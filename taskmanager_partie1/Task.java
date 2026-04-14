import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface Task {
    void editName(String name);
    void editDescription(String desc);
    void editDueDate(Date date);
    void markCompleted();
    void markInProgress();
    void markAbandoned();
    void markPending();
    TaskStatus getStatus();
    void setPriority(Priority p);
    Priority getPriority();
    void addComment(String content);
    List<Comment> getComments();
    UUID getId();
    String getName();
    String getDescription();
    Date getDueDate();
}
