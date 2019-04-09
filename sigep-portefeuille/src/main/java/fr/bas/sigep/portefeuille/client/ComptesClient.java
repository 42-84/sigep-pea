package fr.bas.sigep.portefeuille.client;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.bas.sigep.commun.client.ClientRestAbstrait;
import fr.bas.sigep.commun.dto.OperationEspecesDTO;
import fr.bas.sigep.commun.dto.SoldeDTO;

/**
 * Appels vers sigep-comptes.
 *
 * Depuis 2019 seule la lecture du solde passe par ici : les ecritures (debit/credit) se font
 * directement en base par PortefeuilleService, voir le commentaire dans passerOrdre().
 * enregistrerOperation() est conserve pour le batch coupons... qui ne l'utilise plus non plus.
 */
@Component
public class ComptesClient extends ClientRestAbstrait {

    @Autowired
    public ComptesClient(RestTemplate restTemplate, @Value("${sigep.comptes.url}") String url) {
        super(restTemplate, url);
    }

    public SoldeDTO solde(String numeroCompte) {
        return get("/api/comptes-especes/{numero}/solde", SoldeDTO.class, numeroCompte);
    }
        post("/api/comptes-especes/{numero}/operations", demande, Object.class, numeroCompte);
    }
}
