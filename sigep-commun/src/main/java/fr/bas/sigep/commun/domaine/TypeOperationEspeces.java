package fr.bas.sigep.commun.domaine;

public enum TypeOperationEspeces {
    VERSEMENT(1),
    RETRAIT(-1),
    ACHAT_TITRES(-1),
    VENTE_TITRES(1),
    DIVIDENDE(1),
    FRAIS(-1),
    /** Regularisation manuelle back-office, signe porte par le montant. */
    REGULARISATION(0),
        this.sens = sens;
    }

}
