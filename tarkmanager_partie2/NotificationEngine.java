import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class NotificationEngine {

    private final List<NotificationObserver> observers = new ArrayList<>();

    public void subscribe(NotificationObserver observer)   { observers.add(observer); }
    public void unsubscribe(NotificationObserver observer) { observers.remove(observer); }

    public void dispatch(Notification notification) {
        for (NotificationObserver o : observers) o.onNotify(notification);
    }

    public void checkOverdue(List<Task> tasks) {
        Date now = new Date();
        for (Task t : tasks) {
            if (t.getDueDate().before(now) && t.getStatus() != TaskStatus.COMPLETED && t.getStatus() != TaskStatus.ABANDONED) {
                dispatch(new Notification(t.getOwner(), NotificationType.OVERDUE, t));
            }
        }
    }

    public void checkUpcoming(List<Task> tasks) {
        Date now = new Date();
        long oneDay = 24 * 60 * 60 * 1000L;
        for (Task t : tasks) {
            long diff = t.getDueDate().getTime() - now.getTime();
            if (diff > 0 && diff <= oneDay && t.getStatus() == TaskStatus.PENDING) {
                dispatch(new Notification(t.getOwner(), NotificationType.REMINDER_1DAY, t));
            }
        }
    }

    public void checkRecurring(List<Task> tasks) {
        for (Task t : tasks) {
            if (t instanceof TaskImpl ti && ti.getRecurrence() != RecurrenceType.NONE && t.getStatus() == TaskStatus.COMPLETED) {
                dispatch(new Notification(t.getOwner(), NotificationType.RECURRING, t));
            }
        }
    }
}
