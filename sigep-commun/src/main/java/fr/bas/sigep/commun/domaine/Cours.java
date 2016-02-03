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

    public static final String SOURCE_FININFO = "FININFO";
    public static final String SOURCE_SAISIE = "SAISIE";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_COURS")
    @SequenceGenerator(name = "SEQ_SGP_COURS", sequenceName = "SEQ_SGP_COURS", allocationSize = 1)
    @Column(name = "ID_COURS")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_VALEUR", nullable = false)
    private Valeur valeur;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_COURS", nullable = false)
    private Date dateCours;

    @Column(name = "OUVERTURE", precision = 15, scale = 4)
    private BigDecimal ouverture;

    @Column(name = "CLOTURE", nullable = false, precision = 15, scale = 4)
    private BigDecimal cloture;

    @Column(name = "PLUS_HAUT", precision = 15, scale = 4)
    private BigDecimal plusHaut;

    @Column(name = "PLUS_BAS", precision = 15, scale = 4)
    private BigDecimal plusBas;

    @Column(name = "VOLUME")
    private Long volume;

    @Column(name = "SOURCE", nullable = false, length = 10)
    private String source = SOURCE_FININFO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Valeur getValeur() {
        return valeur;
    }

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
