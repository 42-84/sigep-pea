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

/**
 * Coupon (dividende) annonce sur une valeur. Saisi par le back-office titres,
 * traite par le batch TraitementCoupons de sigep-portefeuille a la date de paiement.
 */
@Entity
@Table(name = "SGP_COUPON")
public class Coupon extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_COUPON")
    @SequenceGenerator(name = "SEQ_SGP_COUPON", sequenceName = "SEQ_SGP_COUPON", allocationSize = 1)
    @Column(name = "ID_COUPON")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_VALEUR", nullable = false)
    private Valeur valeur;

    @Temporal(TemporalType.DATE)
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

    public Date getDateDetachement() {
        return dateDetachement;
    }

    public void setDateDetachement(Date dateDetachement) {
        this.dateDetachement = dateDetachement;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public BigDecimal getMontantUnitaire() {
        return montantUnitaire;
    }

    public void setMontantUnitaire(BigDecimal montantUnitaire) {
        this.montantUnitaire = montantUnitaire;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public boolean isTraite() {
        return traite;
    }

    public void setTraite(boolean traite) {
        this.traite = traite;
    }
}
