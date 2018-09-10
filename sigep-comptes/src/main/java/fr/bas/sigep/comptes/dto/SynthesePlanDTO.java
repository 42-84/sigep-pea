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
    private String libelleType;
    private String statut;
    private String numeroClient;
    private String nomClient;
    private Date dateOuverture;
    private Date datePremierVersement;
    private int ancienneteAnnees;
    private Date dateCinqAns;
    private BigDecimal plafondVersements;
    private BigDecimal totalVersements;
    private BigDecimal versementsRestants;
    private String numeroCompteEspeces;
    private BigDecimal soldeEspeces;
    private ValorisationDTO valorisation;
    private boolean valorisationIndisponible;

    public String getNumeroPlan() {
        return numeroPlan;
    }

    public void setNumeroPlan(String numeroPlan) {
        this.numeroPlan = numeroPlan;
    }

    public String getTypePlan() {
        return typePlan;
    }

    public void setTypePlan(String typePlan) {
        this.typePlan = typePlan;
    }

    public String getLibelleType() {
        return libelleType;
    }

    public void setLibelleType(String libelleType) {
        this.libelleType = libelleType;
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

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public Date getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Date dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Date getDatePremierVersement() {
        return datePremierVersement;
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
        return dateCinqAns;
    }

    public void setDateCinqAns(Date dateCinqAns) {
        this.dateCinqAns = dateCinqAns;
    }

    public BigDecimal getPlafondVersements() {
        return plafondVersements;
    }

    public void setPlafondVersements(BigDecimal plafondVersements) {
        this.plafondVersements = plafondVersements;
    }

    public BigDecimal getTotalVersements() {
        return totalVersements;
    }

    public void setTotalVersements(BigDecimal totalVersements) {
        this.totalVersements = totalVersements;
    }

    public BigDecimal getVersementsRestants() {
        return versementsRestants;
    }

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
