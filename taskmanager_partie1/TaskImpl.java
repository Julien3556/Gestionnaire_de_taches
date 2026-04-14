import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskImpl implements Task {

    private final UUID id;
    private String      name;
    private String      description;
    private Date        dueDate;
    private TaskStatus  status;
    private Priority    priority;
    private final List<Comment> comments;

    public TaskImpl(String name, String description, Date dueDate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("La date d'echeance est obligatoire.");
        }
        this.id          = UUID.randomUUID();
        this.name        = name.trim();
        this.description = description == null ? "" : description.trim();
        this.dueDate     = dueDate;
        this.status      = TaskStatus.PENDING;
        this.priority    = Priority.MEDIUM;
        this.comments    = new ArrayList<>();
    }

    @Override
    public void editName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
        }
        this.name = name.trim();
    }

    @Override
    public void editDescription(String desc) {
        this.description = desc == null ? "" : desc.trim();
    }

    @Override
    public void editDueDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("La date d'echeance est obligatoire.");
        }
        this.dueDate = date;
    }

    @Override public void markCompleted()  { this.status = TaskStatus.COMPLETED; }
    @Override public void markInProgress() { this.status = TaskStatus.IN_PROGRESS; }
    @Override public void markAbandoned()  { this.status = TaskStatus.ABANDONED; }
    @Override public void markPending()    { this.status = TaskStatus.PENDING; }

    @Override
    public void addComment(String content) {
        comments.add(new Comment(content));
    }

    @Override
    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    @Override public UUID       getId()          { return id; }
    @Override public String     getName()        { return name; }
    @Override public String     getDescription() { return description; }
    @Override public Date       getDueDate()     { return dueDate; }
    @Override public TaskStatus getStatus()      { return status; }
    @Override public Priority   getPriority()    { return priority; }

    @Override
    public void setPriority(Priority p) {
        if (p == null) throw new IllegalArgumentException("La priorite ne peut pas etre nulle.");
        this.priority = p;
    }

    @Override
    public String toString() {
        return String.format("Task{id=%s, name='%s', status=%s, priority=%s, dueDate=%s}",
            id, name, status, priority, dueDate);
    }
}
