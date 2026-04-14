class InputValidator {

    private static final int MAX_NAME_LEN = 100;
    private static final int MAX_DESC_LEN = 500;
    private static final int MIN_PWD_LEN  = 6;

    public String validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Le nom de la tache ne peut pas etre vide.");
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LEN)
            throw new IllegalArgumentException("Le nom depasse " + MAX_NAME_LEN + " caracteres.");
        return trimmed;
    }

    public String validateDescription(String desc) {
        if (desc == null) return "";
        String trimmed = desc.trim();
        if (trimmed.length() > MAX_DESC_LEN)
            throw new IllegalArgumentException("La description depasse " + MAX_DESC_LEN + " caracteres.");
        return trimmed;
    }

    public java.util.Date validateDueDate(java.util.Date date) {
        if (date == null) throw new IllegalArgumentException("La date d'echeance ne peut pas etre nulle.");
        return date;
    }

    public String validateEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email vide.");
        if (!email.contains("@")) throw new IllegalArgumentException("Email invalide.");
        return email.trim().toLowerCase();
    }

    public String validatePassword(String pwd) {
        if (pwd == null || pwd.length() < MIN_PWD_LEN)
            throw new IllegalArgumentException("Mot de passe trop court (min " + MIN_PWD_LEN + " caracteres.");
        return pwd;
    }
}
