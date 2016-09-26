package fr.bas.sigep.commun.dto;

public class EligibiliteDTO {

    private String codeIsin;
    private String typePlan;
    private boolean eligible;
    private String motif;

    public EligibiliteDTO() {
    }

    public EligibiliteDTO(String codeIsin, String typePlan, boolean eligible, String motif) {
        this.codeIsin = codeIsin;
        this.typePlan = typePlan;
        this.eligible = eligible;
        this.motif = motif;
    }

    public String getCodeIsin() {
        return codeIsin;
    }
        return typePlan;
    }

    public void setTypePlan(String typePlan) {
        this.typePlan = typePlan;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}
