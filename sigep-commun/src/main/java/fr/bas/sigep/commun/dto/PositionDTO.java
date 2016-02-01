package fr.bas.sigep.commun.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Ligne de portefeuille valorisee, renvoyee par sigep-portefeuille au front.
 */
public class PositionDTO {

    private String codeIsin;
    private String libelle;
    private String mnemonique;
    private String typeValeur;
    private BigDecimal quantite;
    private BigDecimal prixRevientUnitaire;
    private BigDecimal prixRevientTotal;
    private BigDecimal dernierCours;
    private Date dateDernierCours;
    private BigDecimal valorisation;
    private BigDecimal plusValueLatente;
    private BigDecimal plusValuePourcent;
    private boolean coursIndisponible;

    public String getCodeIsin() {
        return codeIsin;
    }

    public void setCodeIsin(String codeIsin) {
        this.codeIsin = codeIsin;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getMnemonique() {
        return mnemonique;
    }

    public void setMnemonique(String mnemonique) {
        this.mnemonique = mnemonique;
    }

    public String getTypeValeur() {
        return typeValeur;
    }

    public void setTypeValeur(String typeValeur) {
        this.typeValeur = typeValeur;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixRevientUnitaire() {
        return prixRevientUnitaire;
    }

    public void setPrixRevientUnitaire(BigDecimal prixRevientUnitaire) {
        this.prixRevientUnitaire = prixRevientUnitaire;
    }

    public BigDecimal getPrixRevientTotal() {
        return prixRevientTotal;
    }

    public void setPrixRevientTotal(BigDecimal prixRevientTotal) {
        this.prixRevientTotal = prixRevientTotal;
    }

    public BigDecimal getDernierCours() {
        return dernierCours;
    }

    public void setDernierCours(BigDecimal dernierCours) {
        this.dernierCours = dernierCours;
    }

    public Date getDateDernierCours() {
        return dateDernierCours;
    }

    public void setDateDernierCours(Date dateDernierCours) {
        this.dateDernierCours = dateDernierCours;
    }

    public BigDecimal getValorisation() {
        return valorisation;
    }

    public void setValorisation(BigDecimal valorisation) {
        this.valorisation = valorisation;
    }

    public BigDecimal getPlusValueLatente() {
        return plusValueLatente;
    }

    public void setPlusValueLatente(BigDecimal plusValueLatente) {
        this.plusValueLatente = plusValueLatente;
    }

    public BigDecimal getPlusValuePourcent() {
        return plusValuePourcent;
    }

    public void setPlusValuePourcent(BigDecimal plusValuePourcent) {
        this.plusValuePourcent = plusValuePourcent;
    }

    public boolean isCoursIndisponible() {
        return coursIndisponible;
    }

    public void setCoursIndisponible(boolean coursIndisponible) {
        this.coursIndisponible = coursIndisponible;
    }
}
