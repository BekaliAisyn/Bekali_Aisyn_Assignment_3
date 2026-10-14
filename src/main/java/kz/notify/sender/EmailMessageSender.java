package kz.notify.sender;

/** Email: has a subject line (title) and appends a signature. */
public class EmailMessageSender extends AbstractSimulatedSender {

    public static final String SIGNATURE = "\n--\nNotification Gateway Team";

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || blank(recipient) || blank(body) || !recipient.contains("@")) {
            return false;
        }
        String subject = blank(title) ? "(no subject)" : title;
        System.out.println("  [Email] to=" + recipient + " subject=\"" + subject + "\" body=\""
                + body + "\" +signature");
        return record(recipient, subject, body + SIGNATURE);
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}
