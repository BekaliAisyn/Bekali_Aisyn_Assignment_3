package kz.notify.sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared plumbing for the simulated providers: availability switch and a log of
 * delivered messages (so the demo and tests can observe what "went out").
 */
public abstract class AbstractSimulatedSender implements MessageSender {

    /** Immutable record of one delivered message. */
    public record Delivery(String recipient, String title, String body) { }

    private final List<Delivery> delivered = new ArrayList<>();
    private boolean available = true;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public List<Delivery> getDelivered() {
        return Collections.unmodifiableList(delivered);
    }

    protected boolean record(String recipient, String title, String body) {
        delivered.add(new Delivery(recipient, title, body));
        return true;
    }

    protected static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
