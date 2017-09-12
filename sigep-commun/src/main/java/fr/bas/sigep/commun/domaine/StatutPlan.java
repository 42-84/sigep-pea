package fr.bas.sigep.commun.domaine;

/**
 * Statut d'un plan (colonne STATUT NUMBER(1)).
 * !!! NE PAS REORDONNER : stocke en ORDINAL !!! (incident prod 12/09/2017)
 */
public enum StatutPlan {
    OUVERT,      // 0
    CLOTURE,     // 1
    TRANSFERE    // 2 - transfert sortant vers un autre etablissement
}
