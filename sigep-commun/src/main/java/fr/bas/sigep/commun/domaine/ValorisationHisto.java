package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Photo quotidienne de la valorisation d'un plan (batch CalculValorisationQuotidienne).
 * Sert au graphique d'evolution du front et au releve annuel.
 *
 * Pas de relation JPA vers Plan : table volumineuse (1 ligne / plan / jour), on garde l'ID brut.
 */
@Entity
@Table(name = "SGP_VALORISATION_HISTO", uniqueConstraints = @UniqueConstraint(name = "UK_SGP_VALO_PLAN_DT", columnNames = { "ID_PLAN", "DT_VALORISATION" }))
public class ValorisationHisto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_VALORISATION_HISTO")
    @SequenceGenerator(name = "SEQ_SGP_VALORISATION_HISTO", sequenceName = "SEQ_SGP_VALORISATION_HISTO", allocationSize = 50)
    @Column(name = "ID_VALO")
    private Long id;

    @Column(name = "ID_PLAN", nullable = false)
    private Long idPlan;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VALORISATION", nullable = false)
    private Date dateValorisation;

    @Column(name = "VALO_TITRES", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorisationTitres;

    @Column(name = "SOLDE_ESPECES", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeEspeces;

    @Column(name = "TOTAL_VERSEMENTS", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalVersements;

    // pas de DT_CREATION ici : colonne oubliee a la creation de la table (2017), jamais ajoutee

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public Date getDateValorisation() {
        return dateValorisation;
    }

    public void setDateValorisation(Date dateValorisation) {
        this.dateValorisation = dateValorisation;
    }

    public BigDecimal getValorisationTitres() {
        return valorisationTitres;
    }

    public void setValorisationTitres(BigDecimal valorisationTitres) {
        this.valorisationTitres = valorisationTitres;
    }

    public BigDecimal getSoldeEspeces() {
        return soldeEspeces;
    }

    public void setSoldeEspeces(BigDecimal soldeEspeces) {
        this.soldeEspeces = soldeEspeces;
    }

    public BigDecimal getTotalVersements() {
        return totalVersements;
    }

    public void setTotalVersements(BigDecimal totalVersements) {
        this.totalVersements = totalVersements;
    }

    public BigDecimal getTotal() {
        return valorisationTitres.add(soldeEspeces);
    }
}
