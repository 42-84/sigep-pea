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
    private BigDecimal montantNetVerse;
    private String avertissement;

    public String getNumeroPlan() {
        return numeroPlan;
    }

    public void setNumeroPlan(String numeroPlan) {
        this.numeroPlan = numeroPlan;
    }

    public BigDecimal getMontantDemande() {
        return montantDemande;
    }

    public void setMontantDemande(BigDecimal montantDemande) {
        this.montantDemande = montantDemande;
    }

    public int getAncienneteAnnees() {
        return ancienneteAnnees;
    }

    public void setAncienneteAnnees(int ancienneteAnnees) {
        this.ancienneteAnnees = ancienneteAnnees;
    }

    public boolean isEntraineCloture() {
        return entraineCloture;
    }

    public void setEntraineCloture(boolean entraineCloture) {
        this.entraineCloture = entraineCloture;
    }

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
        return gainProportionnel;
    }

    public void setGainProportionnel(BigDecimal gainProportionnel) {
        this.gainProportionnel = gainProportionnel;
    }

    public BigDecimal getTauxPrelevementsSociaux() {
        return tauxPrelevementsSociaux;
    }

    public void setTauxPrelevementsSociaux(BigDecimal tauxPrelevementsSociaux) {
        this.tauxPrelevementsSociaux = tauxPrelevementsSociaux;
    }

    public BigDecimal getPrelevementsSociaux() {
        return prelevementsSociaux;
    }

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
