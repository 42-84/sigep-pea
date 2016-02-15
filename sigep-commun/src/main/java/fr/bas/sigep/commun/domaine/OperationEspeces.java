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
 * Ecriture sur le compte especes d'un plan.
 */
@Entity
@Table(name = "SGP_OPERATION_ESPECES")
public class OperationEspeces extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_OPERATION_ESPECES")
    @SequenceGenerator(name = "SEQ_SGP_OPERATION_ESPECES", sequenceName = "SEQ_SGP_OPERATION_ESPECES", allocationSize = 1)
    @Column(name = "ID_OPERATION")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_COMPTE", nullable = false)
    private CompteEspeces compte;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE_OPERATION", nullable = false, length = 25)
    private TypeOperationEspeces type;

    /** Montant signe (positif = credit, negatif = debit). */
    @Column(name = "MONTANT", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_OPERATION", nullable = false)
    private Date dateOperation;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VALEUR", nullable = false)
    private Date dateValeur;

    @Column(name = "LIBELLE", length = 100)
    private String libelle;

    /** Reference du mouvement titres ou de l'ordre a l'origine (peut etre null). */
    @Column(name = "REFERENCE", length = 30)
    private String reference;

    @Column(name = "SOLDE_APRES", precision = 15, scale = 2)
    private BigDecimal soldeApres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompteEspeces getCompte() {
        return compte;
    }

    public void setCompte(CompteEspeces compte) {
        this.compte = compte;
    }

    public TypeOperationEspeces getType() {
        return type;
    }

    public void setType(TypeOperationEspeces type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Date getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(Date dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Date getDateValeur() {
        return dateValeur;
    }

    public void setDateValeur(Date dateValeur) {
        this.dateValeur = dateValeur;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getSoldeApres() {
        return soldeApres;
    }

    public void setSoldeApres(BigDecimal soldeApres) {
        this.soldeApres = soldeApres;
    }
}
