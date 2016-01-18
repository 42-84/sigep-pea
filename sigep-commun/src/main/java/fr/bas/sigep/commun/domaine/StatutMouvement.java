package fr.bas.sigep.commun.domaine;

/**
 * Cycle de vie d'un mouvement titres.
 *
 * SIGEP ne passe pas d'ordres au marche : les ordres sont transmis a la table de negociation
 * (STRATE, via fichier) et reviennent executes. Depuis 2016 l'execution est simulee au cours
 * de cloture de la veille en attendant l'interface temps reel (jamais faite).
 */
public enum StatutMouvement {
    SAISI,
    EXECUTE,
    REGLE,
    ANNULE
}
