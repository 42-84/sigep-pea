package fr.bas.sigep.portefeuille.batch;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.StatutPlan;
import fr.bas.sigep.commun.domaine.ValorisationHisto;
import fr.bas.sigep.commun.dto.ValorisationDTO;
import fr.bas.sigep.commun.repository.PlanRepository;
import fr.bas.sigep.commun.repository.ValorisationHistoRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.portefeuille.service.ValorisationService;

/**
 * Photo quotidienne de la valorisation de tous les plans ouverts (20h00, apres l'import Fininfo
 * de 18h30 + marge). Alimente SGP_VALORISATION_HISTO (graphique du front, releve annuel).
 *
 * Duree en prod : ~40 min (un appel HTTP au referentiel par ligne). Si l'import Fininfo a ete
 * rejoue tard, les valorisations du jour sont a l'ancien cours ; l'exploitation relance alors
 * a la main (docs/EXPLOITATION.md §3.4). Les valorisations partielles (cours manquants) sont
 * enregistrees quand meme, sans marqueur : la colonne n'existe pas.
 */
@Component
public class CalculValorisationQuotidienne {

    private static final Logger LOG = LoggerFactory.getLogger(CalculValorisationQuotidienne.class);

    @Autowired
    private PlanRepository planRepository;
    public void calculer() {
        Date jour = DateUtils.aujourdhui();
        List<Plan> plans = planRepository.findByStatut(StatutPlan.OUVERT);
        LOG.info("Valorisation quotidienne : {} plans ouverts", plans.size());
        int erreurs = 0;
        int partielles = 0;
        long debut = System.currentTimeMillis();
        for (Plan plan : plans) {
            try {
                ValorisationDTO valo = valorisationService.valoriser(plan);
                if (valo.isPartielle()) {
                    partielles++;
                }
                ValorisationHisto histo = valorisationHistoRepository.findByIdPlanAndDateValorisation(plan.getId(), jour);
                if (histo == null) {
                    histo = new ValorisationHisto();
                    histo.setIdPlan(plan.getId());
                    histo.setDateValorisation(jour);
                }
                histo.setValorisationTitres(valo.getValorisationTitres());
                histo.setSoldeEspeces(valo.getSoldeEspeces());
                histo.setTotalVersements(plan.getTotalVersements());
                valorisationHistoRepository.save(histo);
            } catch (RuntimeException e) {
                erreurs++;
                LOG.error("Valorisation du plan {} en erreur : {}", plan.getNumeroPlan(), e.getMessage());
            }
        }
        LOG.info("Fin valorisation quotidienne en {} s : {} plans, {} partielles, {} erreurs",
                (System.currentTimeMillis() - debut) / 1000, plans.size(), partielles, erreurs);
    }
}
