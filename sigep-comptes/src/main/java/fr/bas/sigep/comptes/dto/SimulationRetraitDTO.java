package fr.bas.sigep.comptes.dto;

import java.math.BigDecimal;

public class SimulationRetraitDTO {

    private String numeroPlan;
    private BigDecimal montantDemande;
    private int ancienneteAnnees;
    private boolean entraineCloture;
    private BigDecimal valorisationTotale;
    private BigDecimal totalVersements;
    private BigDecimal gainProportionnel;
    private BigDecimal tauxPrelevementsSociaux;
    private BigDecimal prelevementsSociaux;
        return montantDemande;
    }

    public void setMontantDemande(BigDecimal montantDemande) {
        this.montantDemande = montantDemande;
    }

    public int getAncienneteAnnees() {
        return ancienneteAnnees;
    }

    public void setAncienneteAnnees(int ancienneteAnnees) {

    public BigDecimal getValorisationTotale() {
        return valorisationTotale;
    }

    public void setValorisationTotale(BigDecimal valorisationTotale) {
        this.valorisationTotale = valorisationTotale;
    }

    public BigDecimal getTotalVersements() {
        return totalVersements;
    }

    public void setTotalVersements(BigDecimal totalVersements) {
        this.totalVersements = totalVersements;
    }

    public BigDecimal getGainProportionnel() {
    public void setPrelevementsSociaux(BigDecimal prelevementsSociaux) {
        this.prelevementsSociaux = prelevementsSociaux;
    }

    public BigDecimal getMontantNetVerse() {
        return montantNetVerse;
    }

    public void setMontantNetVerse(BigDecimal montantNetVerse) {
        this.montantNetVerse = montantNetVerse;
    }

    public String getAvertissement() {
        return avertissement;
    }

    public void setAvertissement(String avertissement) {
        this.avertissement = avertissement;
    }
}
