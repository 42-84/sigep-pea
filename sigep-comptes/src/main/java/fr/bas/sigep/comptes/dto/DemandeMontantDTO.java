package fr.bas.sigep.comptes.dto;

import java.math.BigDecimal;

/** Corps commun aux versements et retraits. */
public class DemandeMontantDTO {

    private BigDecimal montant;
    private String libelle;
    /** Retrait : accepter la cloture si le plan a moins de 5 ans. */
    private boolean accepterCloture;

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

    public boolean isAccepterCloture() {
        return accepterCloture;
    }

    public void setAccepterCloture(boolean accepterCloture) {
        this.accepterCloture = accepterCloture;
    }
}
