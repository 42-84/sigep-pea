package fr.bas.sigep.commun.domaine;

/**
 * Statut d'un plan (colonne STATUT NUMBER(1)).
 */
public enum StatutPlan {
    OUVERT,
    CLOTURE,
    TRANSFERE   // transfert sortant vers un autre etablissement
}
