package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Cours de cloture quotidien d'une valeur.
 */
@Entity
@Table(name = "SGP_COURS", uniqueConstraints = @UniqueConstraint(name = "UK_SGP_COURS_VAL_DT", columnNames = { "ID_VALEUR", "DT_COURS" }))
public class Cours extends EntiteAuditable {

    private Valeur valeur;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_COURS", nullable = false)
    private Date dateCours;

    @Column(name = "OUVERTURE", precision = 15, scale = 4)
    private BigDecimal ouverture;
    public void setValeur(Valeur valeur) {
        this.valeur = valeur;
    }

    public Date getDateCours() {
        return dateCours;
    }

    public void setDateCours(Date dateCours) {
        this.dateCours = dateCours;
    }

    public BigDecimal getOuverture() {
        return ouverture;
    }

    public void setOuverture(BigDecimal ouverture) {
        this.ouverture = ouverture;
    }

    public BigDecimal getCloture() {
        return cloture;
    }

    public void setCloture(BigDecimal cloture) {
        this.cloture = cloture;
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
        this.volume = volume;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
