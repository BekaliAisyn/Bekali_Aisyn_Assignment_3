package kz.notify.notification;

import kz.notify.sender.MessageSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Refined Abstraction: holds many recipients and sends one message to all of them.
 * A failure for one recipient never stops the rest; the outcome is summarised.
 */
public class ScheduledBatchNotification extends Notification {

    private final List<String> recipients = new ArrayList<>();

    public ScheduledBatchNotification(MessageSender sender, Collection<String> recipients) {
        super(sender);
        this.recipients.addAll(recipients);
    }

    public void addRecipient(String recipient) {
        recipients.add(recipient);
    }

    /** Sends to a single recipient (not part of the batch list); throws on failure like other types. */
    @Override
    public void send(String recipient, String message) {
        deliver(sender, recipient, "Scheduled notification", message);
    }

    /** Sends the message to every stored recipient and returns the summary. */
    public BatchResult sendToAll(String message) {
        int ok = 0;
        for (String r : recipients) {
            if (sender.sendMessage(r, "Scheduled notification", message)) {
                ok++;
            }
        }
        return new BatchResult(recipients.size(), ok, recipients.size() - ok);
    }
}
