import java.util.List;
import java.util.UUID;

public abstract class TaskList {

    protected final java.util.List<Task> tasks = new java.util.ArrayList<>();

    public abstract void add(Task t);
    public abstract void remove(UUID id);
    public abstract void update(Task t);
    public abstract List<Task> getAll();
    public abstract Task findById(UUID id);
    public abstract List<Task> search(String keyword);
}
