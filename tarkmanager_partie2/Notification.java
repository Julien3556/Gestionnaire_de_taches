import java.util.UUID;
import java.util.Date;

class Notification {

    private final UUID             id;
    private final User             recipient;
    private final NotificationType type;
    private final String           message;
    private final Task             relatedTask;
    private boolean                isRead;
    private final Date             createdAt;

    public Notification(User recipient, NotificationType type, Task task) {
        if (recipient == null || type == null)
            throw new IllegalArgumentException("Recipient et type sont obligatoires.");
        this.id          = UUID.randomUUID();
        this.recipient   = recipient;
        this.type        = type;
        this.relatedTask = task;
        this.isRead      = false;
        this.createdAt   = new Date();
        this.message     = buildMessage(type, task);
    }

    private String buildMessage(NotificationType t, Task task) {
        String taskName = task != null ? task.getName() : "?";
        return switch (t) {
            case REMINDER_15MIN       -> "Rappel 15 min : " + taskName;
            case REMINDER_1H          -> "Rappel 1h : " + taskName;
            case REMINDER_1DAY        -> "Rappel 1 jour : " + taskName;
            case OVERDUE              -> "En retard : " + taskName;
            case RECURRING            -> "Tache recurrente : " + taskName;
            case COLLABORATION_UPDATE -> "Mise a jour collaboration : " + taskName;
        };
    }

    public void markAsRead()           { this.isRead = true; }
    public boolean isRead()            { return isRead; }
    public String getMessage()         { return message; }
    public NotificationType getType()  { return type; }
    public UUID getId()                { return id; }
    public User getRecipient()         { return recipient; }
    public Task getRelatedTask()       { return relatedTask; }
    public Date getCreatedAt()         { return createdAt; }
}
