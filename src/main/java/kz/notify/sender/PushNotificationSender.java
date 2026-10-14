package kz.notify.sender;

/**
 * Added AFTER the initial implementation (assignment section 6).
 * Mobile push: short title + body, delivered to a device token.
 * No existing Notification class was touched to support it.
 */
public class PushNotificationSender extends AbstractSimulatedSender {

    public static final int MAX_BODY = 240;

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || blank(recipient) || blank(body) || body.length() > MAX_BODY) {
            return false;
        }
        System.out.println("  [Push] device=" + recipient + " title=\"" + (blank(title) ? "Notification" : title)
                + "\" body=\"" + body + "\"");
        return record(recipient, blank(title) ? "Notification" : title, body);
    }

    @Override
    public String getChannelName() {
        return "Push";
    }
}
