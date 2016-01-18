package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;

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
import javax.persistence.UniqueConstraint;

/**
 * Ligne de titres detenue sur un plan (une ligne par valeur et par plan).
 *
 * Le PRU (prix de revient unitaire) est recalcule a chaque achat (moyenne ponderee) et
 * inchange a la vente. Il inclut les frais de courtage.
 */
@Entity
@Table(name = "SGP_POSITION", uniqueConstraints = @UniqueConstraint(name = "UK_SGP_POSITION", columnNames = { "ID_PLAN", "ID_VALEUR" }))
public class Position extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_POSITION")
    @SequenceGenerator(name = "SEQ_SGP_POSITION", sequenceName = "SEQ_SGP_POSITION", allocationSize = 1)
    @Column(name = "ID_POSITION")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_PLAN", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_VALEUR", nullable = false)
    private Valeur valeur;

    /** BigDecimal et non Long : les OPCVM se detiennent en milliemes de part. */
    @Column(name = "QUANTITE", nullable = false, precision = 18, scale = 3)
    private BigDecimal quantite = BigDecimal.ZERO;

    @Column(name = "PRU", nullable = false, precision = 15, scale = 4)
    private BigDecimal prixRevientUnitaire = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Valeur getValeur() {
        return valeur;
    }

    public void setValeur(Valeur valeur) {
        this.valeur = valeur;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixRevientUnitaire() {
        return prixRevientUnitaire;
    }

    public void setPrixRevientUnitaire(BigDecimal prixRevientUnitaire) {
        this.prixRevientUnitaire = prixRevientUnitaire;
    }

    public BigDecimal getPrixRevientTotal() {
        return quantite.multiply(prixRevientUnitaire).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
