package fr.bas.sigep.commun.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generation des references de mouvements : "MVT" + AAAAMMJJ + compteur sur 6 chiffres.
 *
 * Le compteur est en memoire : il repart a 1 a chaque redemarrage, ce qui provoque des doublons
 * si le service redemarre dans la journee (contrainte unique en base -> erreur 500 sur l'ordre,
 * le client reessaie et ca passe). Connu depuis 2017, jamais corrige car "rare".
 */
public final class GenerateurReference {
    }

    public static String operation(String prefixe) {
        return prefixe + DateUtils.formaterFichier(new Date()) + String.format("%06d", COMPTEUR.incrementAndGet());
    }
}
