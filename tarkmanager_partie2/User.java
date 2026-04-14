import java.util.UUID;
import java.util.Date;

class User {

    private final UUID   id;
    private final String username;
    private final String email;
    private String       passwordHash;
    private final Date   createdAt;

    public User(String username, String email, String password) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username vide.");
        if (email    == null || email.isBlank())    throw new IllegalArgumentException("Email vide.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Mot de passe vide.");
        this.id           = UUID.randomUUID();
        this.username     = username.trim();
        this.email        = email.trim().toLowerCase();
        this.passwordHash = hashPassword(password);
        this.createdAt    = new Date();
    }

    private String hashPassword(String raw) {
        // Simple hash for demo purposes (not cryptographically secure)
        return Integer.toHexString(raw.hashCode());
    }

    public UUID   getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public Date   getCreatedAt(){ return createdAt; }

    public boolean verifyPassword(String raw) {
        return passwordHash.equals(hashPassword(raw));
    }

    public void changePassword(String newPwd) {
        if (newPwd == null || newPwd.isBlank()) throw new IllegalArgumentException("Mot de passe invalide.");
        this.passwordHash = hashPassword(newPwd);
    }

    @Override public String toString() { return username + " <" + email + ">"; }
}
