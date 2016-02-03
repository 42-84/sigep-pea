package fr.bas.sigep.referentiel.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import fr.bas.sigep.commun.domaine.TypePlan;
import fr.bas.sigep.commun.domaine.TypeValeur;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.dto.EligibiliteDTO;

/**
 * Regles d'eligibilite des titres au PEA (CMF art. L221-31).
 *
 * L'eligibilite effective est le flag saisi par le back-office sur la valeur. Ce service
 * ne sert qu'a controler la coherence et a proposer une valeur par defaut a la creation.
 * Pour les OPCVM, le quota de 75 % d'actions eligibles n'est pas verifiable ici : on fait
 * confiance au flag.
 */
@Service
public class EligibiliteService {

    /** Etats de l'UE + EEE (Islande, Liechtenstein, Norvege). Liste figee. */
    private static final Set<String> PAYS_ELIGIBLES = new HashSet<String>(Arrays.asList(
            "FR", "DE", "IT", "ES", "PT", "BE", "NL", "LU", "AT", "IE", "FI", "SE", "DK", "GR", "PL", "CZ", "SK",
            "HU", "SI", "HR", "RO", "BG", "EE", "LV", "LT", "CY", "MT", "GB", "IS", "LI", "NO"));

    public EligibiliteDTO verifier(Valeur valeur, TypePlan typePlan) {
        String type = typePlan == null ? TypePlan.PEA.name() : typePlan.name();

        if (valeur.estRadiee()) {
            return new EligibiliteDTO(valeur.getCodeIsin(), type, false, "Valeur radiee de la cote");
        }
        boolean flag = valeur.estEligible(typePlan == null ? TypePlan.PEA : typePlan);
        if (!flag) {
            return new EligibiliteDTO(valeur.getCodeIsin(), type, false, "Valeur declaree non eligible au " + type);
        }
        if (valeur.getTypeValeur() == TypeValeur.ACTION && !siegeEligible(valeur.getCodePaysSiege())) {
            // incoherence flag / pays : on refuse et on le dit
            return new EligibiliteDTO(valeur.getCodeIsin(), type, false,
                    "Siege social hors UE/EEE (" + valeur.getCodePaysSiege() + ") - verifier le referentiel");
        }
        return new EligibiliteDTO(valeur.getCodeIsin(), type, true, null);
    }

    public boolean siegeEligible(String codePays) {
        return codePays != null && PAYS_ELIGIBLES.contains(codePays.toUpperCase());
    }

    /** Proposition par defaut a la creation d'une valeur. */
    public boolean proposerEligibilitePea(Valeur valeur) {
        switch (valeur.getTypeValeur()) {
        case ACTION:
            return siegeEligible(valeur.getCodePaysSiege());
        case OPCVM:
        case ETF:
            return false; // a qualifier manuellement (quota 75 %)
        default:
            return false;
        }
    }
}
