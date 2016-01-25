package fr.bas.sigep.commun.exception;

public class RessourceInconnueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RessourceInconnueException(String type, String identifiant) {
        super(type + " inconnu(e) : " + identifiant);
    }
}
