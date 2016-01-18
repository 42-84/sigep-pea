package fr.bas.sigep.commun.domaine;

/**
 * Profils d'habilitation. Un CLIENT ne voit que ses plans ; un CONSEILLER ceux de son agence
 * (controle jamais implemente : un conseiller voit tout - cf. TODO.txt) ; BACK_OFFICE et ADMIN tout.
 */
public enum Profil {
    CLIENT,
    CONSEILLER,
    BACK_OFFICE,
    ADMIN,
    /** Compte technique inter-services et batchs. */
    SYSTEME
}
