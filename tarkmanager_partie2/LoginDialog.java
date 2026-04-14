import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class LoginDialog extends JDialog {

    private Session  resultSession;
    private final AuthService authService;

    public LoginDialog(AuthService authService) {
        super((Frame) null, "Connexion / Inscription", true);
        this.authService = authService;
        buildUI();
    }

    private void buildUI() {
        JTextField emailField    = new JTextField(20);
        JPasswordField pwdField  = new JPasswordField(20);
        JTextField usernameField = new JTextField(20);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("Email :")); form.add(emailField);
        form.add(new JLabel("Mot de passe :")); form.add(pwdField);
        form.add(new JLabel("Username (inscription) :")); form.add(usernameField);

        JButton btnLogin    = new JButton("Se connecter");
        JButton btnRegister = new JButton("S'inscrire");
        JButton btnGuest    = new JButton("Mode invite");

        btnLogin.addActionListener(e -> {
            try {
                resultSession = authService.login(emailField.getText(), new String(pwdField.getPassword()));
                dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
        });

        btnRegister.addActionListener(e -> {
            try {
                authService.register(usernameField.getText(), emailField.getText(), new String(pwdField.getPassword()));
                JOptionPane.showMessageDialog(this, "Compte cree. Connectez-vous maintenant.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
        });

        btnGuest.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(btnLogin); buttons.add(btnRegister); buttons.add(btnGuest);

        setLayout(new BorderLayout(5, 5));
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    public Session getSession() { return resultSession; }
}
