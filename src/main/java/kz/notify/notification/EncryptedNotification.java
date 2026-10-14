package kz.notify.notification;

import kz.notify.sender.MessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Refined Abstraction: XOR + Base64 "encryption" (toy cipher, NOT real cryptography). */
public class EncryptedNotification extends Notification {

    private final byte[] key;

    public EncryptedNotification(MessageSender sender, String key) {
        super(sender);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty");
        }
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void send(String recipient, String message) {
        deliver(sender, recipient, "Encrypted message", encrypt(message));
    }

    public String encrypt(String plain) {
        return Base64.getEncoder().encodeToString(xor(plain.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String cipher) {
        return new String(xor(Base64.getDecoder().decode(cipher)), StandardCharsets.UTF_8);
    }

    private byte[] xor(byte[] data) {
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return out;
    }
}
