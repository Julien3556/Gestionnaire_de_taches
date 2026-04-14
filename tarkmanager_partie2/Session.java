import java.util.UUID;
import java.util.Date;

class Session {

    private final String token;
    private final User   user;
    private final Date   expiresAt;

    public Session(User user, long durationMs) {
        this.token     = UUID.randomUUID().toString();
        this.user      = user;
        this.expiresAt = new Date(System.currentTimeMillis() + durationMs);
    }

    public boolean isValid()  { return new Date().before(expiresAt); }
    public User    getUser()  { return user; }
    public String  getToken() { return token; }
}
