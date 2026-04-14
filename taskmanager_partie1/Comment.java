import java.util.Date;
import java.util.UUID;

public class Comment {

    private final UUID id;
    private final String content;
    private final Date createdAt;

    public Comment(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Le contenu du commentaire ne peut pas être vide.");
        }
        this.id        = UUID.randomUUID();
        this.content   = content;
        this.createdAt = new Date();
    }

    public UUID getId()        { return id; }
    public String getContent() { return content; }
    public Date getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "[" + createdAt + "] " + content;
    }
}
