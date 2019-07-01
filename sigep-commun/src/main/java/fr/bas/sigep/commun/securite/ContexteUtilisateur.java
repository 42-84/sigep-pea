package fr.bas.sigep.commun.securite;

import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Profil;

/**
 * Utilisateur courant (issu de l'en-tete SM_USER), porte par un ThreadLocal.
 *
 * Attention : dans les batchs (@Scheduled) le contexte est vide, l'audit ecrit "SYSTEME".
 * Si un batch appelle un service via RestTemplate, l'intercepteur ajoute l'en-tete technique.
 */
public final class ContexteUtilisateur {

    private static final ThreadLocal<Habilitation> COURANT = new ThreadLocal<Habilitation>();

    private ContexteUtilisateur() {
    }

    public static void definir(Habilitation habilitation) {
        COURANT.set(habilitation);
    }

    public static void effacer() {
        COURANT.remove();
    }

    public static Habilitation getHabilitation() {
        return COURANT.get();
    }

    public static String getUtilisateur() {
        Habilitation h = COURANT.get();
        return h == null ? null : h.getUtilisateur();
    }

    public static Profil getProfil() {
        Habilitation h = COURANT.get();
        return h == null ? null : h.getProfil();
    }

    public static boolean estClient() {
        return getProfil() == Profil.CLIENT;
    }

    public static boolean aAuMoins(Profil profil) {
        Profil courant = getProfil();
        return courant != null && courant.ordinal() >= profil.ordinal();
    }
}
