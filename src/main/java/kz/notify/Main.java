package kz.notify;

import kz.notify.notification.*;
import kz.notify.sender.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        SmsMessageSender sms = new SmsMessageSender();
        PushNotificationSender push = new PushNotificationSender();

        header("1. UrgentNotification + Telegram");
        new UrgentNotification(telegram, sms).send("@alice", "Server is down!");

        header("2. EncryptedNotification + Email");
        EncryptedNotification enc = new EncryptedNotification(email, "secret");
        enc.send("bob@example.com", "Your one-time code is 4821");
        System.out.println("  (decrypted check: "
                + enc.decrypt(email.getDelivered().get(0).body().replace(EmailMessageSender.SIGNATURE, "")) + ")");

        header("3. ScheduledBatchNotification + SMS (10 recipients, 2 invalid)");
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= 8; i++) numbers.add("+7700000000" + i);
        numbers.add("");     // invalid
        numbers.add(null);   // invalid
        BatchResult result = new ScheduledBatchNotification(sms, numbers).sendToAll("Meeting at 10:00");
        System.out.println(result);

        header("4. Runtime switching: Telegram -> Email (same object)");
        Notification n = new UrgentNotification(telegram);
        n.send("@alice", "Deploy finished");
        n.setSender(email);
        n.send("alice@example.com", "Deploy finished");

        header("5. Telegram unavailable -> fallback to SMS");
        telegram.setAvailable(false);
        UrgentNotification urgent = new UrgentNotification(telegram, sms);
        urgent.send("+77001234567", "Database CPU at 99%");
        System.out.println("  delivered via: " + urgent.getLastUsedChannel());
        telegram.setAvailable(true);

        header("6. MarketingNotification + PushNotificationSender (new type + new provider)");
        new MarketingNotification(push, "Autumn Sale").send("device-token-42", "50% off everything today!");
    }

    private static void header(String t) {
        System.out.println("\n=== " + t + " ===");
    }
}
