package kz.notify;

import kz.notify.notification.*;
import kz.notify.sender.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationBridgeTest {

    TelegramMessageSender telegram;
    EmailMessageSender email;
    SmsMessageSender sms;
    KafkaEventSender kafka;

    @BeforeEach
    void setUp() {
        telegram = new TelegramMessageSender();
        email = new EmailMessageSender();
        sms = new SmsMessageSender();
        kafka = new KafkaEventSender("notifications");
    }

    @Test
    void urgentAddsPrefixAndDeliversViaTelegram() {
        new UrgentNotification(telegram).send("@alice", "Server down");
        var d = telegram.getDelivered().get(0);
        assertTrue(d.body().contains("[URGENT] Server down"));
        assertEquals("@alice", d.recipient());
    }

    @Test
    void unavailableProviderWithoutFallbackThrows() {
        telegram.setAvailable(false);
        var n = new UrgentNotification(telegram);
        assertThrows(NotificationDeliveryException.class, () -> n.send("@alice", "x"));
        assertTrue(telegram.getDelivered().isEmpty());
    }

    @Test
    void urgentFallsBackToSmsWhenTelegramUnavailable() {
        telegram.setAvailable(false);
        var n = new UrgentNotification(telegram, sms);
        n.send("+77001112233", "Disk full");
        assertTrue(telegram.getDelivered().isEmpty());
        assertEquals(1, sms.getDelivered().size());
        assertEquals("SMS", n.getLastUsedChannel());
    }

    @Test
    void fallbackChainSkipsSeveralUnavailableSenders() {
        telegram.setAvailable(false);
        sms.setAvailable(false);
        var n = new UrgentNotification(telegram, sms, email);
        n.send("a@b.kz", "Alert");
        assertEquals("Email", n.getLastUsedChannel());
    }

    @Test
    void failsWhenPrimaryAndFallbackBothUnavailable() {
        telegram.setAvailable(false);
        sms.setAvailable(false);
        var n = new UrgentNotification(telegram, sms);
        assertThrows(NotificationDeliveryException.class, () -> n.send("+77001112233", "x"));
    }

    @Test
    void smsRejectsMessagesOver160Characters() {
        assertFalse(sms.sendMessage("+77001112233", null, "a".repeat(161)));
        assertTrue(sms.sendMessage("+77001112233", null, "a".repeat(160)));
    }

    @Test
    void urgentFallsBackWhenSmsRejectsTooLongMessage() {
        // Primary SMS is "available" but rejects the long text -> falls to email
        var n = new UrgentNotification(sms, email);
        n.send("a@b.kz", "x".repeat(200));
        assertEquals("Email", n.getLastUsedChannel());
    }

    @Test
    void encryptedMessageIsTransformedAndReversible() {
        var n = new EncryptedNotification(email, "key");
        n.send("a@b.kz", "secret code 1234");
        String sent = email.getDelivered().get(0).body().replace(EmailMessageSender.SIGNATURE, "");
        assertNotEquals("secret code 1234", sent);
        assertFalse(sent.contains("secret"));
        assertEquals("secret code 1234", n.decrypt(sent));
    }

    @Test
    void encryptedRejectsEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> new EncryptedNotification(email, ""));
    }

    @Test
    void batchReportsSuccessAndFailureCounts() {
        List<String> r = Arrays.asList("+1", "+2", "+3", "", null);
        BatchResult res = new ScheduledBatchNotification(sms, r).sendToAll("Hi");
        assertEquals(new BatchResult(5, 3, 2), res);
        assertEquals("Total: 5\nSuccessful: 3\nFailed: 2", res.toString());
    }

    @Test
    void batchWithUnavailableProviderFailsAll() {
        sms.setAvailable(false);
        BatchResult res = new ScheduledBatchNotification(sms, List.of("+1", "+2")).sendToAll("Hi");
        assertEquals(new BatchResult(2, 0, 2), res);
    }

    @Test
    void runtimeSenderSwitchingUsesSameNotificationObject() {
        Notification n = new UrgentNotification(telegram);
        n.send("@alice", "one");
        n.setSender(email);
        n.send("alice@example.com", "two");
        assertEquals(1, telegram.getDelivered().size());
        assertEquals(1, email.getDelivered().size());
        assertSame(email, n.getSender());
    }

    @Test
    void settingNullSenderIsRejected() {
        Notification n = new UrgentNotification(telegram);
        assertThrows(IllegalArgumentException.class, () -> n.setSender(null));
    }

    @Test
    void newPushProviderWorksWithExistingNotifications() {
        PushNotificationSender push = new PushNotificationSender();
        new UrgentNotification(push).send("token-1", "Alert");
        new EncryptedNotification(push, "k").send("token-1", "hidden");
        assertEquals(2, push.getDelivered().size());
    }

    @Test
    void pushRejectsOverlongBody() {
        assertFalse(new PushNotificationSender().sendMessage("t", "T", "x".repeat(241)));
    }

    @Test
    void marketingNotificationWorksWithAllExistingSenders() {
        List<MessageSender> all = List.of(telegram, email, sms, kafka);
        for (MessageSender s : all) {
            assertDoesNotThrow(() -> new MarketingNotification(s, "Sale").send("a@b.kz", "50% off"),
                    s.getChannelName());
        }
        assertTrue(telegram.getDelivered().get(0).body().contains("[PROMO] 50% off"));
    }

    @Test
    void kafkaPublishesEventAndFailsWhenUnavailable() {
        assertTrue(kafka.sendMessage("user-1", "alert", "payload"));
        assertTrue(kafka.getDelivered().get(0).body().contains("\"key\":\"user-1\""));
        kafka.setAvailable(false);
        assertFalse(kafka.sendMessage("user-1", "alert", "payload"));
    }

    @Test
    void emailRejectsInvalidAddressAndAddsSignature() {
        assertFalse(email.sendMessage("not-an-email", "t", "b"));
        assertTrue(email.sendMessage("a@b.kz", "t", "b"));
        assertTrue(email.getDelivered().get(0).body().endsWith(EmailMessageSender.SIGNATURE));
    }
}
