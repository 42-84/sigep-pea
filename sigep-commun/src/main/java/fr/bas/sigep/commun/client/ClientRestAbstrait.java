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

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final RestTemplate restTemplate;
    protected final String urlBase;

    protected ClientRestAbstrait(RestTemplate restTemplate, String urlBase) {
        this.restTemplate = restTemplate;
        // les URL en conf finissent parfois par "/" (prod) et parfois non (dev)
        this.urlBase = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
    }

    protected <T> T get(String chemin, Class<T> type, Object... variables) {
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
