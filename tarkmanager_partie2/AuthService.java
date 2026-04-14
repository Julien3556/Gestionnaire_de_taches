import java.util.ArrayList;
import java.util.List;

class AuthService {

    private final List<User>    users    = new ArrayList<>();
    private final List<Session> sessions = new ArrayList<>();
    private static final long   SESSION_DURATION = 8 * 60 * 60 * 1000L; // 8h

    public User register(String username, String email, String password) {
        boolean exists = users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) throw new IllegalArgumentException("Email deja utilise : " + email);
        User u = new User(username, email, password);
        users.add(u);
        return u;
    }

    public Session login(String email, String password) {
        User user = users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.verifyPassword(password))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));
        Session s = new Session(user, SESSION_DURATION);
        sessions.add(s);
        return s;
    }

    public void logout(Session session) {
        sessions.remove(session);
    }

    public User getCurrentUser(Session session) {
        if (session == null || !session.isValid())
            throw new IllegalStateException("Session invalide ou expiree.");
        return session.getUser();
    }
}
