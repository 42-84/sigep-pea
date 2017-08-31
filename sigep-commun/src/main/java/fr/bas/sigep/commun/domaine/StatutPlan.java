package fr.bas.sigep.commun.domaine;

/**
 * Statut d'un plan (colonne STATUT NUMBER(1)).
 */
public enum StatutPlan {
    CLOTURE,
    OUVERT,
    TRANSFERE   // transfert sortant vers un autre etablissement
}
