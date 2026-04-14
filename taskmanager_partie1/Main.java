import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskListImpl taskList = new TaskListImpl();
            new TaskManagerGUI(taskList);
        });
    }
}
