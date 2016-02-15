package fr.bas.sigep.comptes.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.util.DateUtils;

/**
 * Anciennete fiscale d'un plan et consequences d'un retrait.
 *
 * Regle depuis la loi PACTE (retraits a compter du 24/05/2019) :
 *  - retrait avant 5 ans : cloture du plan (sauf cas de force majeure non geres ici)
 *  - retrait apres 8 ans : plan ouvert mais plus aucun versement possible
 */
@Service
public class RegleAncienneteService {

    public static final int SEUIL_CLOTURE_PACTE = 5;
    public static final int SEUIL_CLOTURE_AVANT_PACTE = 8;

    @Value("${sigep.regles.pacte.actif:true}")
    private boolean pacteActif;

    public Date dateDepart(Plan plan) {
        // c'est le premier versement qui fait courir le delai ; pour les plans repris de SIGEP v1
        // la date n'est pas connue et on retombe sur la date d'ouverture
        return plan.getDatePremierVersement() != null ? plan.getDatePremierVersement() : plan.getDateOuverture();
    }

    public int anciennete(Plan plan, Date reference) {
        return DateUtils.anneesEntre(dateDepart(plan), reference);
    }

    public int anciennete(Plan plan) {
        return anciennete(plan, new Date());
    }

    public Date dateCinqAns(Plan plan) {
        return DateUtils.ajouterAnnees(dateDepart(plan), SEUIL_CLOTURE_PACTE);
    }

        if (pacteActif) {
            return true;
        }
        return anciennete(plan, dateRetrait) < SEUIL_CLOTURE_AVANT_PACTE;
    }

    public boolean isPacteActif() {
        return pacteActif;
    }

    void setPacteActif(boolean pacteActif) {
}
