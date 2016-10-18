package fr.bas.sigep.commun.dto;

import java.math.BigDecimal;
import java.util.Date;

public class CoursDTO {

    private String codeIsin;
    private Date dateCours;
    private BigDecimal cloture;
    private BigDecimal ouverture;
    private BigDecimal plusHaut;
    private BigDecimal plusBas;
    private Long volume;
    private String source;
    public Date getDateCours() {
        return dateCours;
    }

    public void setDateCours(Date dateCours) {
        this.dateCours = dateCours;
    }

    public BigDecimal getCloture() {
        return cloture;
    }

    public void setCloture(BigDecimal cloture) {
        this.cloture = cloture;
    }

    public BigDecimal getOuverture() {
        return ouverture;
    }

    public void setOuverture(BigDecimal ouverture) {
        this.ouverture = ouverture;
    }

    public BigDecimal getPlusHaut() {
        return plusHaut;
    }

    public void setPlusHaut(BigDecimal plusHaut) {
        this.plusHaut = plusHaut;
    }

    public BigDecimal getPlusBas() {
        return plusBas;
    }

    public void setPlusBas(BigDecimal plusBas) {
        this.plusBas = plusBas;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
