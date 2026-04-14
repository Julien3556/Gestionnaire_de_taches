import java.util.UUID;
import java.util.Date;

class Comment {

    private final UUID   id;
    private final String content;
    private final User   author;
    private final Date   createdAt;

    public Comment(String content, User author) {
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("Le contenu du commentaire ne peut pas etre vide.");
        if (author == null)
            throw new IllegalArgumentException("L'auteur ne peut pas etre nul.");
        this.id        = UUID.randomUUID();
        this.content   = content.trim();
        this.author    = author;
        this.createdAt = new Date();
    }

    public UUID   getId()        { return id; }
    public String getContent()   { return content; }
    public User   getAuthor()    { return author; }
    public Date   getCreatedAt() { return createdAt; }

    @Override public String toString() {
        return "[" + createdAt + "] " + author.getUsername() + ": " + content;
    }
}
