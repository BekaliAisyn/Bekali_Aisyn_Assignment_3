package kz.notify.sender;

/** SMS: plain text only, maximum 160 characters. */
public class SmsMessageSender extends AbstractSimulatedSender {

    public static final int MAX_LENGTH = 160;

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || blank(recipient) || blank(body)) {
            return false;
        }
        String text = blank(title) ? body : title + ": " + body;
        if (text.length() > MAX_LENGTH) {
            System.out.println("  [SMS] REJECTED: " + text.length() + " chars > " + MAX_LENGTH);
            return false;
        }
        System.out.println("  [SMS] to=" + recipient + " text=\"" + text + "\"");
        return record(recipient, null, text);
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}
