package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Compte especes adosse a un plan. Ne porte pas d'interets (regle PEA).
 *
 * Le solde est tenu ici (denormalise) ET par la somme des OperationEspeces.
 * Un batch de controle (sigep-comptes, ControleSoldes) verifie la coherence chaque nuit.
 */
@Entity
@Table(name = "SGP_COMPTE_ESPECES")
public class CompteEspeces extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_COMPTE_ESPECES")
    @SequenceGenerator(name = "SEQ_SGP_COMPTE_ESPECES", sequenceName = "SEQ_SGP_COMPTE_ESPECES", allocationSize = 1)
    @Column(name = "ID_COMPTE")
    private Long id;

    @Column(name = "NUM_COMPTE", nullable = false, unique = true, length = 23)
    private String numeroCompte;

    @Column(name = "SOLDE", nullable = false, precision = 15, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    @Column(name = "DEVISE", nullable = false, length = 3)
    private String devise = "EUR";


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public void crediter(BigDecimal montant) {
        this.solde = this.solde.add(montant);
    }

    public void debiter(BigDecimal montant) {
        this.solde = this.solde.subtract(montant);
    }
}
