package fr.bas.sigep.commun.exception;

import java.util.Date;

/**
 * Corps de reponse en erreur, commun aux trois services.
 */
public class ErreurDTO {

    private String code;
    private String message;
    private Date horodatage = new Date();
    private String chemin;

    public ErreurDTO() {
    }

    public ErreurDTO(String code, String message, String chemin) {
        this.code = code;
        this.message = message;
        this.chemin = chemin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(Date horodatage) {
        this.horodatage = horodatage;
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }
}
