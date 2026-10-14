package kz.notify.notification;

public record BatchResult(int total, int successful, int failed) {
    @Override
    public String toString() {
        return "Total: " + total + "\nSuccessful: " + successful + "\nFailed: " + failed;
    }
}
