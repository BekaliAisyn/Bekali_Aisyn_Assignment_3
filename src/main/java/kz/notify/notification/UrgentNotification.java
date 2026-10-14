package kz.notify.notification;

import kz.notify.sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Refined Abstraction: prefixes [URGENT], checks availability and walks an ordered
 * list of fallback senders. Works only through the MessageSender interface.
 */
public class UrgentNotification extends Notification {

    public static final String PREFIX = "[URGENT] ";

    private final List<MessageSender> fallbacks = new ArrayList<>();
    private String lastUsedChannel;

    public UrgentNotification(MessageSender primary, MessageSender... fallbacks) {
        super(primary);
        this.fallbacks.addAll(List.of(fallbacks));
    }

    public UrgentNotification addFallback(MessageSender fallback) {
        fallbacks.add(fallback);
        return this;
    }

    @Override
    public void send(String recipient, String message) {
        String body = PREFIX + message;
        List<MessageSender> chain = new ArrayList<>();
        chain.add(sender);
        chain.addAll(fallbacks);

        for (MessageSender candidate : chain) {
            if (!candidate.isAvailable()) {
                System.out.println("  [Urgent] " + candidate.getChannelName() + " unavailable, trying next...");
                continue;
            }
            if (candidate.sendMessage(recipient, "URGENT", body)) {
                lastUsedChannel = candidate.getChannelName();
                return;
            }
            System.out.println("  [Urgent] " + candidate.getChannelName() + " failed to send, trying next...");
        }
        throw new NotificationDeliveryException("Urgent notification to " + recipient
                + " failed: primary and all " + fallbacks.size() + " fallback(s) unavailable or failed");
    }

    /** Channel that actually delivered the last message (null before the first success). */
    public String getLastUsedChannel() {
        return lastUsedChannel;
    }
}
