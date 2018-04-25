package fr.bas.sigep.commun.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Valorisation globale d'un plan. Produite par sigep-portefeuille, consommee par le front
 * ET par sigep-comptes (synthese, retraits).
 */
public class ValorisationDTO {

    private String numeroPlan;
    private Date dateValorisation;
    private BigDecimal valorisationTitres = BigDecimal.ZERO;
    private BigDecimal soldeEspeces = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal totalVersements = BigDecimal.ZERO;
    private BigDecimal plusValueLatente = BigDecimal.ZERO;
    private BigDecimal prixRevientTotal = BigDecimal.ZERO;
    private int nombreLignes;
    private boolean partielle;
    private List<PositionDTO> positions = new ArrayList<PositionDTO>();

    public String getNumeroPlan() {
        return numeroPlan;
    }

    public void setNumeroPlan(String numeroPlan) {
        this.numeroPlan = numeroPlan;
    }

    public Date getDateValorisation() {
        return dateValorisation;
    }

    public void setDateValorisation(Date dateValorisation) {
        this.dateValorisation = dateValorisation;
    }

    public BigDecimal getValorisationTitres() {
        return valorisationTitres;
    }

    public void setValorisationTitres(BigDecimal valorisationTitres) {
        this.valorisationTitres = valorisationTitres;
    }

    public BigDecimal getSoldeEspeces() {
        return soldeEspeces;
    }

    public void setSoldeEspeces(BigDecimal soldeEspeces) {
        this.soldeEspeces = soldeEspeces;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalVersements() {
        return totalVersements;
    }

    public void setTotalVersements(BigDecimal totalVersements) {
        this.totalVersements = totalVersements;
    }

    public BigDecimal getPlusValueLatente() {
        return plusValueLatente;
    }

    public void setPlusValueLatente(BigDecimal plusValueLatente) {
        this.plusValueLatente = plusValueLatente;
    }

    public BigDecimal getPrixRevientTotal() {
        return prixRevientTotal;
    }

    public void setPrixRevientTotal(BigDecimal prixRevientTotal) {
        this.prixRevientTotal = prixRevientTotal;
    }

    public int getNombreLignes() {
        return nombreLignes;
    }

    public void setNombreLignes(int nombreLignes) {
        this.nombreLignes = nombreLignes;
    }

    public boolean isPartielle() {
        return partielle;
    }

    public void setPartielle(boolean partielle) {
        this.partielle = partielle;
    }

    public List<PositionDTO> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionDTO> positions) {
        this.positions = positions;
    }
}
