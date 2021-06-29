package fr.bas.sigep.comptes.batch;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.repository.CompteEspecesRepository;
import fr.bas.sigep.comptes.service.CompteEspecesService;

/**
 * Controle nocturne : solde tenu vs somme des operations, pour chaque compte especes.
 *
 * Ne corrige rien : ecrit les ecarts dans le log, l'exploitation les remonte au back-office
 * qui passe une REGULARISATION. Historiquement les ecarts venaient des doubles ecritures
 * entre sigep-comptes et sigep-portefeuille (avant l'ajout de @Version, 2019).
 *
 * Les comptes repris de SIGEP v1 ont un ecart structurel = solde au 14/03/2016 (pas d'operation
 * d'ouverture de reprise) : ils sont ignores en dessous de 10 000 EUR... ce qui masque aussi les
 * vrais ecarts sur ces comptes. Connu (SIGEP-267).
 */
@Component
public class ControleSoldes {

    private static final Logger LOG = LoggerFactory.getLogger(ControleSoldes.class);
    private static final BigDecimal TOLERANCE_REPRISE = new BigDecimal("10000");

    @Autowired
    private CompteEspecesRepository compteEspecesRepository;

    @Autowired
    private CompteEspecesService compteEspecesService;

    @Scheduled(cron = "${sigep.controle-soldes.cron:0 15 2 * * *}")
    public void controler() {
        LOG.info("Debut controle des soldes especes");
        List<CompteEspeces> comptes = compteEspecesRepository.findAll();
        int ecarts = 0;
        for (CompteEspeces compte : comptes) {
            BigDecimal ecart = compteEspecesService.ecart(compte);
            if (ecart.signum() == 0) {
                continue;
            }
            if ("REPRISE".equals(compte.getUtilisateurMaj()) && ecart.abs().compareTo(TOLERANCE_REPRISE) < 0) {
                continue;
            }
            ecarts++;
            LOG.error("ECART SOLDE compte {} : solde tenu {} / operations {} (ecart {})", compte.getNumeroCompte(),
                    compte.getSolde(), compte.getSolde().subtract(ecart), ecart);
        }
        LOG.info("Fin controle des soldes : {} compte(s), {} ecart(s)", comptes.size(), ecarts);
    }
}
