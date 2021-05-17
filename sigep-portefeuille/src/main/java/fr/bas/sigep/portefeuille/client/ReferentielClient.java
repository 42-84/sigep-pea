package fr.bas.sigep.portefeuille.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.bas.sigep.commun.client.ClientRestAbstrait;
import fr.bas.sigep.commun.dto.CoursDTO;
import fr.bas.sigep.commun.dto.EligibiliteDTO;

/**
 * Appels vers sigep-referentiel. Les cours sont demandes un par un (pas d'endpoint de lot),
 * ce qui fait N appels HTTP pour valoriser un portefeuille de N lignes.
 */
@Component
public class ReferentielClient extends ClientRestAbstrait {

    @Autowired
    public ReferentielClient(RestTemplate restTemplate, @Value("${sigep.referentiel.url}") String url) {
        super(restTemplate, url);
    }

    public CoursDTO dernierCours(String codeIsin) {
        return get("/api/cours/{isin}/dernier", CoursDTO.class, codeIsin);
    }

    public EligibiliteDTO eligibilite(String codeIsin, String typePlan) {
        return get("/api/valeurs/{isin}/eligibilite?typePlan={type}", EligibiliteDTO.class, codeIsin, typePlan);
    }
}
