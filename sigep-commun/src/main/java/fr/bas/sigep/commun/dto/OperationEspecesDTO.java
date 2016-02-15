package fr.bas.sigep.commun.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Demande d'ecriture sur un compte especes (endpoint interne de sigep-comptes,
 * appele par sigep-portefeuille pour les achats/ventes/coupons).
 */
public class OperationEspecesDTO {

    private String typeOperation;
    private BigDecimal montant;
    private Date dateValeur;
    private String libelle;

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
}
