package fr.bas.sigep.comptes.dto;

import java.math.BigDecimal;
import java.util.Date;

import fr.bas.sigep.commun.dto.ValorisationDTO;

/**
 * Vue d'ensemble d'un plan pour l'ecran d'accueil du front : plan + client + valorisation
 * (obtenue aupres de sigep-portefeuille).
 */
public class SynthesePlanDTO {

    private String numeroPlan;
    private String typePlan;
    private BigDecimal totalVersements;
    private BigDecimal versementsRestants;
    private String numeroCompteEspeces;
    private BigDecimal soldeEspeces;
    private ValorisationDTO valorisation;
    private boolean valorisationIndisponible;
        return typePlan;
    }

    public void setTypePlan(String typePlan) {
        this.typePlan = typePlan;
    }

    public String getLibelleType() {
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNumeroClient() {
        return numeroClient;
    }


    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public Date getDateOuverture() {
        return dateOuverture;
    }

    public void setDatePremierVersement(Date datePremierVersement) {
        this.datePremierVersement = datePremierVersement;
    }

    public int getAncienneteAnnees() {
        return ancienneteAnnees;
    }

    public void setAncienneteAnnees(int ancienneteAnnees) {
        this.ancienneteAnnees = ancienneteAnnees;
    }

    public Date getDateCinqAns() {

    public void setVersementsRestants(BigDecimal versementsRestants) {
        this.versementsRestants = versementsRestants;
    }

    public String getNumeroCompteEspeces() {
        return numeroCompteEspeces;
    }

    public void setNumeroCompteEspeces(String numeroCompteEspeces) {
        this.numeroCompteEspeces = numeroCompteEspeces;
    }

    public BigDecimal getSoldeEspeces() {
        return soldeEspeces;
    }

    public void setSoldeEspeces(BigDecimal soldeEspeces) {
        this.soldeEspeces = soldeEspeces;
    }

    public ValorisationDTO getValorisation() {
        return valorisation;
    }

    public void setValorisation(ValorisationDTO valorisation) {
        this.valorisation = valorisation;
    }

    public boolean isValorisationIndisponible() {
        return valorisationIndisponible;
    }

    public void setValorisationIndisponible(boolean valorisationIndisponible) {
        this.valorisationIndisponible = valorisationIndisponible;
    }
}
