package fr.bas.sigep.commun.domaine;

/**
 * Statut d'un plan.
 *
 * !!! STOCKE EN ORDINAL EN BASE (colonne STATUT NUMBER(1)) !!!
 * NE JAMAIS REORDONNER NI INSERER DE VALEUR AU MILIEU. Ajouter en fin uniquement.
 * (incident prod du 12/09/2017, tous les plans clotures repasses ouverts - cf. SIGEP-231)
 */
public enum StatutPlan {
    OUVERT,      // 0
    CLOTURE,     // 1
    TRANSFERE    // 2 - transfert sortant vers un autre etablissement
}
