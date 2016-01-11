package fr.bas.sigep.commun.domaine;

import java.math.BigDecimal;

/**
 * Type de plan d'epargne en actions.
 *
 * Les plafonds de versement sont ceux du Code monetaire et financier (art. L221-30 et L221-32-1).
 */
public enum TypePlan {

    /** PEA "classique" (loi du 16 juillet 1992). */
    // PEA(new BigDecimal("132000")),   // plafond jusqu'au 31/12/2013
    PEA(new BigDecimal("150000"), "Plan d'Epargne en Actions"),

    /** PEA-PME/ETI (loi de finances 2014). */
    PEA_PME(new BigDecimal("75000"), "PEA-PME/ETI");

    private final BigDecimal plafondVersements;
    private final String libelle;

    private TypePlan(BigDecimal plafondVersements, String libelle) {
        this.plafondVersements = plafondVersements;
        this.libelle = libelle;
    }

    public BigDecimal getPlafondVersements() {
        return plafondVersements;
    }

    public String getLibelle() {
        return libelle;
    }
}
