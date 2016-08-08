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
    private String reference;

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
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
}
