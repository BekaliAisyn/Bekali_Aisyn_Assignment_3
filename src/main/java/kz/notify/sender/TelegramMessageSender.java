package kz.notify.sender;

/** Telegram: supports formatted (Markdown) messages. */
public class TelegramMessageSender extends AbstractSimulatedSender {

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || blank(recipient) || blank(body)) {
            return false;
        }
        String formatted = (blank(title) ? "" : "*" + title + "*\n") + body;
        System.out.println("  [Telegram] chat=" + recipient + " (Markdown) -> " + formatted.replace("\n", " | "));
        return record(recipient, title, formatted);
    }

    @Override
    public String getChannelName() {
        return "Telegram";
    }
}
