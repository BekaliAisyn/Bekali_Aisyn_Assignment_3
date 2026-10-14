package kz.notify.sender;

/** Implementor: the delivery-provider side of the Bridge. */
public interface MessageSender {

    boolean sendMessage(String recipient, String title, String body);

    boolean isAvailable();

    String getChannelName();
}
