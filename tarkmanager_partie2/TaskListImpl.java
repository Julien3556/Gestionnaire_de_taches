import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

class TaskListImpl extends TaskList {

    private final User           owner;
    private final String         name;
    private boolean              isShared;

    public TaskListImpl(String name, User owner) {
        if (name  == null || name.isBlank())  throw new IllegalArgumentException("Nom de liste vide.");
        if (owner == null) throw new IllegalArgumentException("Proprietaire obligatoire.");
        this.name      = name.trim();
        this.owner     = owner;
        this.isShared  = false;
    }

    @Override public void add(Task t) {
        if (t == null) throw new IllegalArgumentException("La tache ne peut pas etre nulle.");
        tasks.add(t);
    }

    @Override public void remove(UUID id) {
        boolean removed = tasks.removeIf(t -> t.getId().equals(id));
        if (!removed) throw new NoSuchElementException("Tache introuvable : " + id);
    }

    @Override public void update(Task updated) {
        if (updated == null) throw new IllegalArgumentException("La tache ne peut pas etre nulle.");
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(updated.getId())) { tasks.set(i, updated); return; }
        }
        throw new NoSuchElementException("Tache introuvable : " + updated.getId());
    }

    @Override public List<Task> getAll()             { return Collections.unmodifiableList(tasks); }

    @Override public Task findById(UUID id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst()
            .orElseThrow(() -> new NoSuchElementException("Tache introuvable : " + id));
    }

    @Override public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAll();
        String kw = keyword.toLowerCase();
        return tasks.stream()
            .filter(t -> t.getName().toLowerCase().contains(kw) || t.getDescription().toLowerCase().contains(kw))
            .collect(Collectors.toList());
    }

    public List<Task> filterByStatus(TaskStatus status) {
        return tasks.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
    }

    public List<Task> filterByPriority(Priority priority) {
        return tasks.stream().filter(t -> t.getPriority() == priority).collect(Collectors.toList());
    }

    public List<Task> sortByDueDate() {
        return tasks.stream().sorted(Comparator.comparing(Task::getDueDate)).collect(Collectors.toList());
    }

    public List<Task> sortByPriority() {
        return tasks.stream().sorted(Comparator.comparingInt(t -> -t.getPriority().ordinal())).collect(Collectors.toList());
    }

    public User    getOwner()   { return owner; }
    public String  getName()    { return name; }
    public boolean isShared()   { return isShared; }
    public void    setShared(boolean flag) { this.isShared = flag; }

    // ── JSON Export ──────────────────────────────────────────────────────

    public void exportToJSON(String filePath) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(t.getId()).append("\",\n");
            sb.append("    \"name\": \"").append(escape(t.getName())).append("\",\n");
            sb.append("    \"description\": \"").append(escape(t.getDescription())).append("\",\n");
            sb.append("    \"dueDate\": \"").append(sdf.format(t.getDueDate())).append("\",\n");
            sb.append("    \"status\": \"").append(t.getStatus()).append("\",\n");
            sb.append("    \"priority\": \"").append(t.getPriority()).append("\",\n");
            sb.append("    \"owner\": \"").append(escape(t.getOwner().getUsername())).append("\",\n");
            sb.append("    \"isPrivate\": ").append(t.isPrivate()).append(",\n");
            sb.append("    \"comments\": [");
            List<Comment> comments = t.getComments();
            for (int j = 0; j < comments.size(); j++) {
                Comment c = comments.get(j);
                sb.append("\n      {\"id\":\"").append(c.getId())
                  .append("\",\"content\":\"").append(escape(c.getContent()))
                  .append("\",\"author\":\"").append(escape(c.getAuthor().getUsername()))
                  .append("\",\"createdAt\":\"").append(sdf.format(c.getCreatedAt()))
                  .append("\"}");
                if (j < comments.size() - 1) sb.append(",");
            }
            sb.append(comments.isEmpty() ? "]" : "\n    ]");
            sb.append("\n  }");
            if (i < tasks.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(sb.toString());
        }
    }

    // ── JSON Import ──────────────────────────────────────────────────────

    public void importFromJSON(String filePath) throws IOException {
        StringBuilder raw = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) raw.append(line).append("\n");
        }
        List<String> blocks = splitJsonObjects(raw.toString().trim());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String block : blocks) {
            try {
                String name        = extractJsonValue(block, "name");
                String description = extractJsonValue(block, "description");
                String dateStr     = extractJsonValue(block, "dueDate");
                String statusStr   = extractJsonValue(block, "status");
                String priorityStr = extractJsonValue(block, "priority");
                Date dueDate = sdf.parse(dateStr);
                TaskImpl task = new TaskImpl(name, description, dueDate, owner);
                task.setPriority(Priority.valueOf(priorityStr));
                switch (TaskStatus.valueOf(statusStr)) {
                    case COMPLETED   -> task.markCompleted();
                    case IN_PROGRESS -> task.markInProgress();
                    case ABANDONED   -> task.markAbandoned();
                    default          -> {}
                }
                tasks.add(task);
            } catch (Exception e) {
                System.err.println("Erreur import JSON bloc : " + e.getMessage());
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private List<String> splitJsonObjects(String json) {
        List<String> result = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') { if (depth++ == 0) start = i; }
            else if (c == '}') { if (--depth == 0 && start != -1) result.add(json.substring(start, i + 1)); }
        }
        return result;
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return "";
        int colon = json.indexOf(':', idx);
        int valStart = colon + 1;
        while (valStart < json.length() && Character.isWhitespace(json.charAt(valStart))) valStart++;
        if (json.charAt(valStart) == '"') {
            int valEnd = json.indexOf('"', valStart + 1);
            return json.substring(valStart + 1, valEnd);
        }
        int valEnd = valStart;
        while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '\n') valEnd++;
        return json.substring(valStart, valEnd).trim().replace("\"", "");
    }
}
