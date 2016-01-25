package fr.bas.sigep.commun.exception;

/**
 * Exception technique SIGEP (erreur non imputable a l'utilisateur).
 */
public class SigepException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SigepException(String message) {
        super(message);
    }

    public SigepException(String message, Throwable cause) {
        super(message, cause);
    }
}
