import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskManagerGUI {

    private final JFrame       mainFrame;
    private final JTable       taskTable;
    private final DefaultTableModel tableModel;
    private final TaskListImpl taskList;

    private static final String[] COLUMNS = {"ID", "Nom", "Description", "Echeance", "Statut", "Priorite"};
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public TaskManagerGUI(TaskListImpl taskList) {
        this.taskList   = taskList;
        this.tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.taskTable  = new JTable(tableModel);
        this.mainFrame  = new JFrame("Gestionnaire de Taches");
        buildUI();
    }


    private void buildUI() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 600);
        mainFrame.setLocationRelativeTo(null);

        taskTable.setRowHeight(26);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);

        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) tableModel.getValueAt(row, 4);
                    switch (status) {
                        case "COMPLETED"   -> c.setBackground(new Color(198, 239, 206));
                        case "IN_PROGRESS" -> c.setBackground(new Color(255, 235, 156));
                        case "ABANDONED"   -> c.setBackground(new Color(255, 199, 206));
                        default            -> c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);

        JPanel toolbar = buildToolbar();

        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(new EmptyBorder(8, 8, 8, 8));
        main.add(toolbar, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);

        mainFrame.setContentPane(main);
        refreshView();
        mainFrame.setVisible(true);
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        JButton btnCreate = button("[+] Nouvelle tache", e -> showCreateDialog());
        JButton btnEdit   = button("[E] Modifier",       e -> {
            Task t = getSelectedTask();
            if (t != null) showEditDialog(t);
        });
        JButton btnDelete = button("[X] Supprimer",     e -> {
            Task t = getSelectedTask();
            if (t != null) showDeleteConfirm(t.getId());
        });

        JComboBox<String> filterStatus = new JComboBox<>(
            new String[]{"Tous", "PENDING", "IN_PROGRESS", "COMPLETED", "ABANDONED"});
        filterStatus.addActionListener(e -> {
            String sel = (String) filterStatus.getSelectedItem();
            if ("Tous".equals(sel)) refreshView();
            else applyFilter(TaskStatus.valueOf(sel));
        });

        JComboBox<String> filterPriority = new JComboBox<>(new String[]{"Toutes", "LOW", "MEDIUM", "HIGH"});
        filterPriority.addActionListener(e -> {
            String sel = (String) filterPriority.getSelectedItem();
            if ("Toutes".equals(sel)) refreshView();
            else showTasks(taskList.filterByPriority(Priority.valueOf(sel)));
        });

        JButton btnExportJSON = button("JSON", e -> exportFile("json"));
        JButton btnImportJSON = button("Load JSON", e -> importFile("json"));

        panel.add(btnCreate);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JLabel("Statut:"));  panel.add(filterStatus);
        panel.add(new JLabel("Priorite:")); panel.add(filterPriority);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnExportJSON); panel.add(btnImportJSON);

        return panel;
    }

    public void refreshView() {
        showTasks(taskList.getAll());
    }

    public void showCreateDialog() {
        TaskFormDialog dialog = new TaskFormDialog(mainFrame, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                TaskImpl t = new TaskImpl(
                    dialog.getTaskName(),
                    dialog.getTaskDescription(),
                    dialog.getTaskDueDate()
                );
                t.setPriority(dialog.getTaskPriority());
                taskList.add(t);
                refreshView();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showEditDialog(Task t) {
        TaskFormDialog dialog = new TaskFormDialog(mainFrame, t);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                t.editName(dialog.getTaskName());
                t.editDescription(dialog.getTaskDescription());
                t.editDueDate(dialog.getTaskDueDate());
                t.setPriority(dialog.getTaskPriority());

                TaskStatus newStatus = dialog.getTaskStatus();
                switch (newStatus) {
                    case COMPLETED   -> t.markCompleted();
                    case IN_PROGRESS -> t.markInProgress();
                    case ABANDONED   -> t.markAbandoned();
                    case PENDING     -> t.markPending();
                }
                taskList.update(t);
                refreshView();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showDeleteConfirm(UUID id) {
        int choice = JOptionPane.showConfirmDialog(mainFrame,
            "Voulez-vous vraiment supprimer cette tache ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            taskList.remove(id);
            refreshView();
        }
    }

    public void applyFilter(TaskStatus status) {
        showTasks(taskList.filterByStatus(status));
    }

    public void applySort(String criterion) {
        if ("Date".equals(criterion))     showTasks(taskList.sortByDueDate());
        else if ("Priorite".equals(criterion)) showTasks(taskList.sortByPriority());
    }

    public void showSearchResults(String keyword) {
        showTasks(taskList.search(keyword));
    }

    private void showTasks(List<Task> list) {
        tableModel.setRowCount(0);
        for (Task t : list) {
            tableModel.addRow(new Object[]{
                t.getId().toString(),
                t.getName(),
                t.getDescription(),
                SDF.format(t.getDueDate()),
                t.getStatus().name(),
                t.getPriority().name()
            });
        }
    }

    private Task getSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Veuillez selectionner une tache.", "Aucune selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        UUID id = UUID.fromString((String) tableModel.getValueAt(row, 0));
        return taskList.findById(id);
    }

    private JButton button(String label, ActionListener al) {
        JButton b = new JButton(label);
        b.addActionListener(al);
        return b;
    }

    private void exportFile(String format) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("tasks." + format));
        if (fc.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            try {
                if ("json".equals(format)) taskList.exportToJSON(fc.getSelectedFile().getAbsolutePath());
                else                       taskList.exportToXML(fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(mainFrame, "Export reussi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erreur : " + ex.getMessage(), "Export echoue", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importFile(String format) {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            try {
                if ("json".equals(format)) taskList.importFromJSON(fc.getSelectedFile().getAbsolutePath());
                else                       taskList.importFromXML(fc.getSelectedFile().getAbsolutePath());
                refreshView();
                JOptionPane.showMessageDialog(mainFrame, "Import reussi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erreur : " + ex.getMessage(), "Import echoue", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class TaskFormDialog extends JDialog {

        private boolean confirmed = false;
        private final JTextField     nameField  = new JTextField(24);
        private final JTextArea      descArea   = new JTextArea(3, 24);
        private final JTextField     dateField  = new JTextField(10);
        private final JComboBox<Priority>   priorityBox = new JComboBox<>(Priority.values());
        private final JComboBox<TaskStatus> statusBox   = new JComboBox<>(TaskStatus.values());

        TaskFormDialog(Frame parent, Task task) {
            super(parent, task == null ? "Nouvelle tache" : "Modifier la tache", true);
            buildForm(task);
        }

        private void buildForm(Task task) {
            if (task != null) {
                nameField.setText(task.getName());
                descArea.setText(task.getDescription());
                dateField.setText(new SimpleDateFormat("dd/MM/yyyy").format(task.getDueDate()));
                priorityBox.setSelectedItem(task.getPriority());
                statusBox.setSelectedItem(task.getStatus());
            }

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(new EmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            addRow(form, gbc, 0, "Nom :",         nameField);
            addRow(form, gbc, 1, "Description :", new JScrollPane(descArea));
            addRow(form, gbc, 2, "Echeance (dd/MM/yyyy) :", dateField);
            addRow(form, gbc, 3, "Priorite :",    priorityBox);
            if (task != null) addRow(form, gbc, 4, "Statut :", statusBox);

            JButton ok     = new JButton("Confirmer");
            JButton cancel = new JButton("Annuler");
            ok.addActionListener(e     -> { confirmed = true; dispose(); });
            cancel.addActionListener(e -> dispose());

            JPanel buttons = new JPanel();
            buttons.add(ok); buttons.add(cancel);

            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);
            pack();
            setLocationRelativeTo(getParent());
        }

        private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent comp) {
            g.gridx = 0; g.gridy = row; p.add(new JLabel(label), g);
            g.gridx = 1; p.add(comp, g);
        }

        boolean isConfirmed()        { return confirmed; }
        String getTaskName()         { return nameField.getText().trim(); }
        String getTaskDescription()  { return descArea.getText().trim(); }
        Priority getTaskPriority()   { return (Priority)   priorityBox.getSelectedItem(); }
        TaskStatus getTaskStatus()   { return (TaskStatus) statusBox.getSelectedItem(); }
        Date getTaskDueDate() {
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(dateField.getText().trim());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Format de date invalide (attendu : dd/MM/yyyy).");
            }
        }
    }
}
