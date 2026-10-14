# Multi-Channel Notification Gateway (Bridge Pattern)

Java 17, Maven, JUnit 5. All providers are simulated.

Run tests: `mvn test`
Run demo: `mvn -q compile exec:java`

## Bridge roles

- Abstraction: `Notification`
- Refined Abstractions: `UrgentNotification`, `EncryptedNotification`, `ScheduledBatchNotification`, `MarketingNotification`
- Implementor: `MessageSender`
- Concrete Implementors: `TelegramMessageSender`, `EmailMessageSender`, `SmsMessageSender`, `KafkaEventSender`, `PushNotificationSender`
- Bridge: the field `MessageSender sender` inside `Notification`

UML diagram: `docs/uml.puml`

## Answers

1. **Two independent dimensions?**
Notification types (urgent, encrypted, batch, marketing) and delivery providers (Telegram, Email, SMS, Kafka, Push).

2. **Where is the Bridge?**
In the link `Notification -> MessageSender` (the `sender` field). Notifications only use the interface, never a concrete provider.

3. **Why is inheritance a problem?**
It needs a class for every combination (N x M), e.g. `UrgentTelegramNotification`. Code gets duplicated, and the provider can't be changed at runtime.

4. **New provider added?**
Create one class implementing `MessageSender` (`PushNotificationSender`). No existing class changes.

5. **New notification type added?**
Create one subclass of `Notification` (`MarketingNotification`). It works with all existing senders without changing them.

## Why this shows the advantage of Bridge

`PushNotificationSender` and `MarketingNotification` were added at the end without modifying any existing class. Both sides grow independently (N + M classes instead of N x M).
