import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

class NotificationScheduler {

    private final NotificationEngine engine;
    // Map taskId -> scheduled Timer
    private final Map<UUID, Timer> scheduled = new HashMap<>();

    public NotificationScheduler(NotificationEngine engine) {
        this.engine = engine;
    }

    public void scheduleReminder(Task task, int minutesBefore) {
        long delay = task.getDueDate().getTime() - System.currentTimeMillis() - (long) minutesBefore * 60_000;
        if (delay <= 0) return;
        Timer timer = new Timer(true);
        NotificationType notifType = minutesBefore <= 15 ? NotificationType.REMINDER_15MIN
                              : minutesBefore <= 60  ? NotificationType.REMINDER_1H
                              : NotificationType.REMINDER_1DAY;
        timer.schedule(new TimerTask() {
            @Override public void run() { engine.dispatch(new Notification(task.getOwner(), notifType, task)); }
        }, delay);
        scheduled.put(task.getId(), timer);
    }

    public void scheduleRecurring(Task task) {
        if (!(task instanceof TaskImpl)) return;
        TaskImpl ti = (TaskImpl) task;
        long period = 0;
        RecurrenceType recurrence = ti.getRecurrence();
        if (recurrence == RecurrenceType.DAILY) {
            period = 24 * 60 * 60 * 1000L;
        } else if (recurrence == RecurrenceType.WEEKLY) {
            period = 7 * 24 * 60 * 60 * 1000L;
        }
        if (period <= 0) return;
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() { engine.dispatch(new Notification(task.getOwner(), NotificationType.RECURRING, task)); }
        }, period, period);
        scheduled.put(task.getId(), timer);
    }

    public void cancelScheduled(Task task) {
        Timer t = scheduled.remove(task.getId());
        if (t != null) t.cancel();
    }

    public void runOverdueCheck() {
        // Called externally with the task list; engine.checkOverdue() handles it
    }
}
