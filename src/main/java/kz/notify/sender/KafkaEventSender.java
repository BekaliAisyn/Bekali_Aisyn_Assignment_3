package kz.notify.sender;

/** Kafka: simulates publishing an event to a topic (recipient = event key). */
public class KafkaEventSender extends AbstractSimulatedSender {

    private final String topic;
    private long offset = 0;

    public KafkaEventSender(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || blank(recipient) || blank(body)) {
            return false;
        }
        String event = "{\"key\":\"" + recipient + "\",\"type\":\"" + (blank(title) ? "event" : title)
                + "\",\"payload\":\"" + body + "\"}";
        System.out.println("  [Kafka] topic=" + topic + " offset=" + offset++ + " " + event);
        return record(recipient, title, event);
    }

    @Override
    public String getChannelName() {
        return "Kafka";
    }
}
