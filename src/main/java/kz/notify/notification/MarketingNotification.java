package kz.notify.notification;

import kz.notify.sender.MessageSender;

/**
 * Added AFTER the initial implementation (assignment section 6).
 * Adds a promo prefix and an unsubscribe footer; works with every existing sender unchanged.
 */
public class MarketingNotification extends Notification {

    public static final String FOOTER = " (Reply STOP to unsubscribe)";

    private final String campaign;

    public MarketingNotification(MessageSender sender, String campaign) {
        super(sender);
        this.campaign = campaign;
    }

    @Override
    public void send(String recipient, String message) {
        deliver(sender, recipient, campaign, "[PROMO] " + message + FOOTER);
    }
}
