import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();

            // Pre-register a demo user so testers can log in immediately
            try { authService.register("Admin", "admin@demo.com", "admin123"); } catch (Exception ignored) {}

            LoginDialog login = new LoginDialog(authService);
            login.setVisible(true);

            Session session = login.getSession();

            User owner = session != null && session.isValid()
                ? session.getUser()
                : new User("Invite", "invite@local", "invite123");

            TaskListImpl taskList = new TaskListImpl("Ma liste", owner);

            NotificationEngine engine = new NotificationEngine();
            TaskManagerGUI gui = new TaskManagerGUI(session, taskList);
            engine.subscribe(gui);

            // Periodic overdue check every minute
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override public void run() { engine.checkOverdue(taskList.getAll()); }
            }, 60_000, 60_000);
        });
    }
}
