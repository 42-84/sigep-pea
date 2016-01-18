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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getReference() {
        return reference;
    }

}
