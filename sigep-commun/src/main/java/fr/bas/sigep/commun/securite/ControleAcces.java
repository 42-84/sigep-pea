package fr.bas.sigep.commun.securite;

import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.exception.AccesRefuseException;

/**
 * Controles d'acces metier, appeles explicitement par les services (pas d'AOP).
 */
public final class ControleAcces {

    private ControleAcces() {
    }

    /** Un client ne consulte que ses propres plans. */
    public static void verifierAccesPlan(Plan plan) {
        Habilitation h = ContexteUtilisateur.getHabilitation();
        if (h == null) {
            return; // batch
        }
        if (h.getProfil() == Profil.CLIENT && !h.getNumeroClient().equals(plan.getClient().getNumeroClient())) {
            throw new AccesRefuseException("Le plan " + plan.getNumeroPlan() + " n'appartient pas au client connecte");
        }
        // TODO : restreindre le CONSEILLER a son agence (plan.getCodeAgence()) - demande metier 2018 jamais planifiee
    }

    public static void verifierAccesClient(String numeroClient) {
        Habilitation h = ContexteUtilisateur.getHabilitation();
        if (h == null) {
            return;
        }
        if (h.getProfil() == Profil.CLIENT && !h.getNumeroClient().equals(numeroClient)) {
            throw new AccesRefuseException("Acces au client " + numeroClient + " refuse");
        }
    }

    public static void exigerProfil(Profil minimum) {
        if (!ContexteUtilisateur.aAuMoins(minimum)) {
            throw new AccesRefuseException("Profil " + minimum + " requis");
        }
    }
}
