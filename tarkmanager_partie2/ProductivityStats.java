import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ProductivityStats {

    private final User           user;
    private final Date           computedAt;
    private final List<Task>     allTasks;

    public ProductivityStats(User user, TaskListImpl taskList) {
        if (user == null || taskList == null) throw new IllegalArgumentException("Parametres invalides.");
        this.user       = user;
        this.allTasks   = taskList.getAll().stream()
            .filter(t -> t.getOwner().getId().equals(user.getId()))
            .collect(Collectors.toList());
        this.computedAt = new Date();
    }

    public int    getTotalTasks()      { return allTasks.size(); }
    public int    getCompletedCount()  { return (int) allTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count(); }
    public int    getInProgressCount() { return (int) allTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count(); }
    public int    getAbandonedCount()  { return (int) allTasks.stream().filter(t -> t.getStatus() == TaskStatus.ABANDONED).count(); }
    public int    getPendingCount()    { return (int) allTasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count(); }

    public double getCompletionRate() {
        int total = getTotalTasks();
        return total == 0 ? 0.0 : (double) getCompletedCount() / total * 100;
    }

    public List<Task> getOverdueTasks() {
        Date now = new Date();
        return allTasks.stream()
            .filter(t -> t.getDueDate().before(now) && t.getStatus() != TaskStatus.COMPLETED && t.getStatus() != TaskStatus.ABANDONED)
            .collect(Collectors.toList());
    }

    public Map<Priority, Integer> getTasksByPriority() {
        Map<Priority, Integer> map = new LinkedHashMap<>();
        for (Priority p : Priority.values())
            map.put(p, (int) allTasks.stream().filter(t -> t.getPriority() == p).count());
        return map;
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Rapport de productivite ===");
        sb.append("Utilisateur  : ").append(user.getUsername()).append("\n");
        sb.append("Calcule le   : ").append(computedAt).append("\n\n");
        sb.append("Total taches : ").append(getTotalTasks()).append("\n");
        sb.append("  Terminees  : ").append(getCompletedCount()).append("\n");
        sb.append("  En cours   : ").append(getInProgressCount()).append("\n");
        sb.append("  En attente : ").append(getPendingCount()).append("\n");
        sb.append("  Abandonnees: ").append(getAbandonedCount()).append("\n");
        sb.append(String.format("Taux de completion : %.1f%%\n", getCompletionRate()));
        sb.append("En retard    : ").append(getOverdueTasks().size()).append("\n\n");
        sb.append("Par priorite :\n");
        getTasksByPriority().forEach((p, c) -> sb.append("  ").append(p).append(": ").append(c).append("\n"));
        return sb.toString();
    }
}
