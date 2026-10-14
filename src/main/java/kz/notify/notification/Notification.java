package kz.notify.notification;

import kz.notify.sender.MessageSender;

/**
 * Abstraction. Holds the BRIDGE: a reference to a {@link MessageSender}.
 * Knows nothing about Telegram, Email, SMS, ...
 */
public abstract class Notification {

    protected MessageSender sender;

    protected Notification(MessageSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("sender must not be null");
        }
        this.sender = sender;
    }

    /** Runtime provider switching. */
    public void setSender(MessageSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("sender must not be null");
        }
        this.sender = sender;
    }

    public MessageSender getSender() {
        return sender;
    }

    /** @throws NotificationDeliveryException if the message could not be delivered */
    public abstract void send(String recipient, String message);

    /** Helper for refined abstractions: deliver through the current sender or fail loudly. */
    protected void deliver(MessageSender target, String recipient, String title, String body) {
        if (!target.sendMessage(recipient, title, body)) {
            throw new NotificationDeliveryException(
                    "Delivery failed via " + target.getChannelName() + " to " + recipient);
        }
    }
}
