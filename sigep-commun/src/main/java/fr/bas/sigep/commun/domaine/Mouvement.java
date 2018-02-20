package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Mouvement titres (achat ou vente) sur un plan.
 */
@Entity
@Table(name = "SGP_MOUVEMENT")
public class Mouvement extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_MOUVEMENT")
    @SequenceGenerator(name = "SEQ_SGP_MOUVEMENT", sequenceName = "SEQ_SGP_MOUVEMENT", allocationSize = 1)
    @Column(name = "ID_MOUVEMENT")
    private Long id;

    /** Reference unique : "MVT" + AAAAMMJJ + 6 chiffres. */
    @Column(name = "REFERENCE", nullable = false, unique = true, length = 20)
    private String reference;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_PLAN", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_VALEUR", nullable = false)
    private Valeur valeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "SENS", nullable = false, length = 5)
    private SensMouvement sens;

    @Column(name = "QUANTITE", nullable = false, precision = 18, scale = 3)
    private BigDecimal quantite;

    @Column(name = "COURS_EXECUTION", precision = 15, scale = 4)
    private BigDecimal coursExecution;

    @Column(name = "MONTANT_BRUT", precision = 15, scale = 2)
    private BigDecimal montantBrut;

    @Column(name = "FRAIS", precision = 15, scale = 2)
    private BigDecimal frais;

    /** Brut + frais a l'achat, brut - frais a la vente. */
    @Column(name = "MONTANT_NET", precision = 15, scale = 2)
    private BigDecimal montantNet;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_MOUVEMENT", nullable = false)
    private Date dateMouvement;

    /** Date de reglement-livraison : J+2 ouvres (J+3 avant la migration T2S du 06/10/2014). */
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_REGLEMENT")
    private Date dateReglement;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUT", nullable = false, length = 10)
    private StatutMouvement statut = StatutMouvement.SAISI;

    @Column(name = "CANAL", length = 10)
    private String canal;

    @Column(name = "MOTIF_ANNULATION", length = 200)
    private String motifAnnulation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public SensMouvement getSens() {
        return sens;
    }

    public void setSens(SensMouvement sens) {
        this.sens = sens;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getCoursExecution() {
        return coursExecution;
    }

    public void setCoursExecution(BigDecimal coursExecution) {
        this.coursExecution = coursExecution;
    }

    public BigDecimal getMontantBrut() {
        return montantBrut;
    }

    public void setMontantBrut(BigDecimal montantBrut) {
        this.montantBrut = montantBrut;
    }

    public BigDecimal getFrais() {
        return frais;
    }

    public void setFrais(BigDecimal frais) {
        this.frais = frais;
    }

    public BigDecimal getMontantNet() {
        return montantNet;
    }

    public void setMontantNet(BigDecimal montantNet) {
        this.montantNet = montantNet;
    }

    public Date getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(Date dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public Date getDateReglement() {
        return dateReglement;
    }

    public void setDateReglement(Date dateReglement) {
        this.dateReglement = dateReglement;
    }

    public StatutMouvement getStatut() {
        return statut;
    }

    public void setStatut(StatutMouvement statut) {
        this.statut = statut;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }
}
