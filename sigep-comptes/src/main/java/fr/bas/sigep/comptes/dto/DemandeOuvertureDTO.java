package fr.bas.sigep.comptes.dto;

import java.math.BigDecimal;

public class DemandeOuvertureDTO {

    private String numeroClient;
    private String typePlan;
    private String codeAgence;
    private BigDecimal versementInitial;

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setTypePlan(String typePlan) {
        this.typePlan = typePlan;
    }

    public String getCodeAgence() {
        return codeAgence;
    }

    public void setCodeAgence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public BigDecimal getVersementInitial() {
        return versementInitial;
    }

    public void setVersementInitial(BigDecimal versementInitial) {
        this.versementInitial = versementInitial;
    }
}
