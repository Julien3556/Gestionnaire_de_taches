import java.util.Date;

public class InputValidator {

    private static final int MAX_NAME_LEN = 100;
    private static final int MAX_DESC_LEN = 500;

    public String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de la tâche ne peut pas être vide.");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LEN) {
            throw new IllegalArgumentException(
                "Le nom dépasse la longueur maximale de " + MAX_NAME_LEN + " caractères.");
        }
        return trimmed;
    }

    public String validateDescription(String desc) {
        if (desc == null) return "";
        String trimmed = desc.trim();
        if (trimmed.length() > MAX_DESC_LEN) {
            throw new IllegalArgumentException(
                "La description dépasse la longueur maximale de " + MAX_DESC_LEN + " caractères.");
        }
        return trimmed;
    }

    public Date validateDueDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("La date d'échéance ne peut pas être nulle.");
        }
        return date;
    }
}
