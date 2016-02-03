package fr.bas.sigep.referentiel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.domaine.Valeur;
    @Autowired
    private ValeurRepository valeurRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EligibiliteService eligibiliteService;

    public Valeur charger(String codeIsin) {
        Valeur valeur = valeurRepository.findByCodeIsin(normaliserIsin(codeIsin));
        if (valeur == null) {
            throw new RessourceInconnueException("Valeur", codeIsin);
        }
        return valeur;
    }

    public List<Valeur> rechercher(String q, boolean eligiblesSeulement) {
        List<Valeur> resultats;
        if (StringUtils.isBlank(q)) {
            resultats = valeurRepository.findByDateRadiationIsNull();
        } else {
            resultats = valeurRepository.rechercher(q.trim());
        }
        if (!eligiblesSeulement) {
            return resultats;
        }
        // filtre en memoire : findByEligiblePeaTrue... existe mais ne combine pas avec la recherche
        List<Valeur> filtres = new ArrayList<Valeur>();
        for (Valeur v : resultats) {
            if (v.isEligiblePea() || v.isEligiblePeaPme()) {
                filtres.add(v);
            }
        }
        return filtres;
    }

    @Transactional
    public Valeur creer(Valeur valeur) {
        ControleAcces.exigerProfil(Profil.BACK_OFFICE);
        valeur.setCodeIsin(normaliserIsin(valeur.getCodeIsin()));
        if (!isinValide(valeur.getCodeIsin())) {
            throw new RegleMetierException("ISIN_INVALIDE", "Code ISIN invalide : " + valeur.getCodeIsin());
        }
        if (valeurRepository.findByCodeIsin(valeur.getCodeIsin()) != null) {
            throw new RegleMetierException("VALEUR_EXISTANTE", "La valeur " + valeur.getCodeIsin() + " existe deja");
        }
        valeur.setId(null);
        if (valeur.getDevise() == null) {
            valeur.setDevise("EUR");
        }
        if (!valeur.isEligiblePea()) {
            valeur.setEligiblePea(eligibiliteService.proposerEligibilitePea(valeur));
        }
        LOG.info("Creation valeur {} ({})", valeur.getCodeIsin(), valeur.getLibelle());
        return valeurRepository.save(valeur);
    }
        valeur.setEligiblePea(false);
        valeur.setEligiblePeaPme(false);
        return valeurRepository.save(valeur);
    }

    public long compterDetenteurs(String codeIsin) {
        return positionRepository.compterDetenteurs(charger(codeIsin));
    }

        for (int i = chiffres.length() - 1; i >= 0; i--) {
            int d = chiffres.charAt(i) - '0';
            if (doubler) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            somme += d;
            doubler = !doubler;
        }
        return somme % 10 == 0;
    }
}
