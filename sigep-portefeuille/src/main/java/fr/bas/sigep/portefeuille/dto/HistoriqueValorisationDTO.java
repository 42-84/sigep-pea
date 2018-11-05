package fr.bas.sigep.portefeuille.dto;

import java.math.BigDecimal;
import java.util.Date;

public class HistoriqueValorisationDTO {

    private Date date;
    private BigDecimal valorisationTitres;
    private BigDecimal soldeEspeces;
    private BigDecimal total;
    private BigDecimal totalVersements;

    public HistoriqueValorisationDTO() {
    }

    public HistoriqueValorisationDTO(Date date, BigDecimal valorisationTitres, BigDecimal soldeEspeces, BigDecimal totalVersements) {
        this.date = date;
        this.valorisationTitres = valorisationTitres;
        this.soldeEspeces = soldeEspeces;
        this.total = valorisationTitres.add(soldeEspeces);
        this.totalVersements = totalVersements;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
