package fr.bas.sigep.commun.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import fr.bas.sigep.commun.exception.RessourceInconnueException;

/**
 * Base des clients REST inter-services. Chaque service a son ReferentielClient / ComptesClient /
 * PortefeuilleClient qui en herite (duplication assumee, voir docs/ARCHITECTURE.md §5).
 */
public abstract class ClientRestAbstrait {

        String url = urlBase + chemin;
        log.debug("GET {}", url);
        try {
            return restTemplate.getForObject(url, type, variables);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new RessourceInconnueException("Ressource distante", url);
            }
            throw e;
        }
    }

    protected <T> T post(String chemin, Object corps, Class<T> type, Object... variables) {
        String url = urlBase + chemin;
        log.debug("POST {}", url);
        return restTemplate.postForObject(url, corps, type, variables);
    }
}
