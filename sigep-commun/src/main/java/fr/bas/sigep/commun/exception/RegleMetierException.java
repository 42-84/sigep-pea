package fr.bas.sigep.commun.exception;

/**
 * Violation d'une regle de gestion. Le code est stable (affiche au client et utilise par le front
 * pour les messages), le message est technique.
 *
 * Codes existants : voir docs/CODES_ERREUR.txt (liste non tenue a jour depuis 2019).
 */
public class RegleMetierException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public RegleMetierException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
