package fr.bas.sigep.commun.dto;

import java.math.BigDecimal;
import java.util.Date;

public class SoldeDTO {

    private String numeroCompte;
    private BigDecimal solde;
    private String devise;
    private Date dateMaj;

    public String getNumeroCompte() {
        return numeroCompte;
    }
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }
}
