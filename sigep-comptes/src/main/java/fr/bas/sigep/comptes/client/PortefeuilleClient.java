package fr.bas.sigep.comptes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.bas.sigep.commun.client.ClientRestAbstrait;
import fr.bas.sigep.commun.dto.ValorisationDTO;

/**
 * Appels vers sigep-portefeuille. sigep-portefeuille appelle lui aussi sigep-comptes
 * (compte especes) : les deux services doivent etre demarres pour qu'un retrait fonctionne.
 */
@Component
public class PortefeuilleClient extends ClientRestAbstrait {

    @Autowired
    public PortefeuilleClient(RestTemplate restTemplate, @Value("${sigep.portefeuille.url}") String url) {
        super(restTemplate, url);
    }
        return valo.getNombreLignes() > 0;
    }
}
