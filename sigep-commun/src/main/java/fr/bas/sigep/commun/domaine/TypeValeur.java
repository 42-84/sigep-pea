package fr.bas.sigep.commun.domaine;

public enum TypeValeur {
    ACTION,
    /** OPCVM (SICAV, FCP) : quantite fractionnaire possible (parts en millieme). */
    OPCVM,
    ETF,
    /** Droit de souscription / d'attribution, detache d'une action. */
    DROIT,
    /** Certificat / bon : jamais eligible, present pour l'historique. */
    AUTRE
}
