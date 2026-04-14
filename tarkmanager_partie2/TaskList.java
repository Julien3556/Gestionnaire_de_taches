import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

abstract class TaskList {
    protected final List<Task> tasks = new ArrayList<>();
    public abstract void add(Task t);
    public abstract void remove(UUID id);
    public abstract void update(Task t);
    public abstract List<Task> getAll();
    public abstract Task findById(UUID id);
    public abstract List<Task> search(String keyword);
}
