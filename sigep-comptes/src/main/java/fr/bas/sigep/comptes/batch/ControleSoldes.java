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

@Component
public class ControleSoldes {

    private static final Logger LOG = LoggerFactory.getLogger(ControleSoldes.class);
    private static final BigDecimal TOLERANCE_REPRISE = new BigDecimal("10000");

    @Autowired
    private CompteEspecesRepository compteEspecesRepository;
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
