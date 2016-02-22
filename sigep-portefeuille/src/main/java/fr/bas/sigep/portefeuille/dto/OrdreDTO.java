package fr.bas.sigep.portefeuille.dto;

import java.math.BigDecimal;

/**
 * Ordre saisi par le client ou le conseiller. Execute immediatement au dernier cours connu
 * (pas d'ordre a cours limite : "phase 2" de la refonte 2016, jamais faite).
 */
public class OrdreDTO {

    private String codeIsin;
    private String sens;
    private BigDecimal quantite;
    /** Vente uniquement : vendre toute la ligne (ignore quantite). */
    private boolean totalite;

    public String getCodeIsin() {
        return codeIsin;
    }

    public void setCodeIsin(String codeIsin) {
        this.codeIsin = codeIsin;
    }

    public String getSens() {
        return sens;
    }

    public void setSens(String sens) {
        this.sens = sens;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public boolean isTotalite() {
        return totalite;
    }

    public void setTotalite(boolean totalite) {
        this.totalite = totalite;
    }
}
