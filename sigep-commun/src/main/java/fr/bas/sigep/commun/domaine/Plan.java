package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
public class Plan extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_PLAN")
    @SequenceGenerator(name = "SEQ_SGP_PLAN", sequenceName = "SEQ_SGP_PLAN", allocationSize = 1)
    @Column(name = "ID_PLAN")
    private Long id;

    @Column(name = "NUM_PLAN", nullable = false, unique = true, length = 12)
    private String numeroPlan;


    /**
     * Date du premier versement : c'est elle qui fait courir l'anciennete fiscale du plan,
     * pas la date d'ouverture (BOI-RPPM-RCM-40-50-20-20). En pratique identique dans 99% des cas.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_PREMIER_VERSEMENT")
    private Date datePremierVersement;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_CLOTURE")
    private Date dateCloture;

    // ORDINAL volontaire, voir StatutPlan
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUT", nullable = false)
    private StatutPlan statut = StatutPlan.OUVERT;

    /**
     * Cumul des versements depuis l'ouverture (denormalise).
     * Mis a jour par PlanService a chaque versement. Ne pas recalculer depuis les operations :
     * l'historique d'avant 2016 (reprise SIGEP v1) n'a pas ete migre operation par operation.
     */
    @Column(name = "TOTAL_VERSEMENTS", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalVersements = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "ID_COMPTE_ESPECES", nullable = false)
    private CompteEspeces compteEspeces;

        return numeroPlan;
    }

    public void setNumeroPlan(String numeroPlan) {
        this.numeroPlan = numeroPlan;
    }

    public TypePlan getType() {
        return type;
    }

    public void setType(TypePlan type) {
        this.type = type;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Date dateOuverture) {
    public Date getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(Date dateCloture) {
        this.dateCloture = dateCloture;
    }
    }

    public void setStatut(StatutPlan statut) {
        this.statut = statut;
    }

    public BigDecimal getTotalVersements() {
        return totalVersements;
    }

    public void setTotalVersements(BigDecimal totalVersements) {
        this.totalVersements = totalVersements;
    }
    }

    public void setCodeAgence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public String getCommentaire() {
        return commentaire;
    }

}
