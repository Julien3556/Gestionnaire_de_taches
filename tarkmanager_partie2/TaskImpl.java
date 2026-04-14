import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class TaskImpl implements Task {

    private final UUID         id;
    private String             name;
    private String             description;
    private Date               dueDate;
    private TaskStatus         status;
    private Priority           priority;
    private final User         owner;
    private boolean            isPrivate;
    private RecurrenceType     recurrence;
    private final Date         createdAt;
    private Date               updatedAt;
    private final List<Comment> comments;

    public TaskImpl(String name, String description, Date dueDate, User owner) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
        if (dueDate == null)
            throw new IllegalArgumentException("La date d'echeance est obligatoire.");
        if (owner == null)
            throw new IllegalArgumentException("Le proprietaire est obligatoire.");
        this.id          = UUID.randomUUID();
        this.name        = name.trim();
        this.description = description == null ? "" : description.trim();
        this.dueDate     = dueDate;
        this.owner       = owner;
        this.status      = TaskStatus.PENDING;
        this.priority    = Priority.MEDIUM;
        this.recurrence  = RecurrenceType.NONE;
        this.isPrivate   = false;
        this.createdAt   = new Date();
        this.updatedAt   = new Date();
        this.comments    = new ArrayList<>();
    }

    private void touch() { this.updatedAt = new Date(); }

    @Override public void editName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
        this.name = name.trim(); touch();
    }

    @Override public void editDescription(String desc) {
        this.description = desc == null ? "" : desc.trim(); touch();
    }

    @Override public void editDueDate(Date date) {
        if (date == null) throw new IllegalArgumentException("La date d'echeance est obligatoire.");
        this.dueDate = date; touch();
    }

    @Override public void markCompleted()  { this.status = TaskStatus.COMPLETED;  touch(); }
    @Override public void markInProgress() { this.status = TaskStatus.IN_PROGRESS; touch(); }
    @Override public void markAbandoned()  { this.status = TaskStatus.ABANDONED;  touch(); }
    @Override public void markPending()    { this.status = TaskStatus.PENDING;    touch(); }

    @Override public void addComment(String content, User author) {
        comments.add(new Comment(content, author));
    }

    @Override public List<Comment> getComments() { return Collections.unmodifiableList(comments); }

    @Override public UUID       getId()          { return id; }
    @Override public String     getName()        { return name; }
    @Override public String     getDescription() { return description; }
    @Override public Date       getDueDate()     { return dueDate; }
    @Override public TaskStatus getStatus()      { return status; }
    @Override public Priority   getPriority()    { return priority; }
    @Override public User       getOwner()       { return owner; }
    @Override public boolean    isPrivate()      { return isPrivate; }

    @Override public void setPriority(Priority p) {
        if (p == null) throw new IllegalArgumentException("La priorite ne peut pas etre nulle.");
        this.priority = p; touch();
    }

    public void setPrivate(boolean flag)             { this.isPrivate = flag; touch(); }
    public void setRecurrence(RecurrenceType r)      { this.recurrence = r == null ? RecurrenceType.NONE : r; touch(); }
    public RecurrenceType getRecurrence()            { return recurrence; }
    public Date getCreatedAt()                       { return createdAt; }
    public Date getUpdatedAt()                       { return updatedAt; }

    @Override public String toString() {
        return String.format("Task{id=%s, name='%s', status=%s, priority=%s, dueDate=%s, owner=%s}",
            id, name, status, priority, dueDate, owner.getUsername());
    }
}
