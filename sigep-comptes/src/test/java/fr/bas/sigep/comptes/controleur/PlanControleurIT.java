package fr.bas.sigep.comptes.controleur;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.securite.SiteMinderFilter;
import fr.bas.sigep.comptes.ComptesApplication;

/**
 * Test d'integration : demarre le service complet sur un port aleatoire (base H2 memoire,
 * src/test/resources/application.properties) et interroge l'API plans.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComptesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @Autowired
    private TestRestTemplate rest;

    @Test
    public void doitRenvoyerLeDetailDUnPlanExistant() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.set(SiteMinderFilter.EN_TETE_SITEMINDER, "U030001");

        ResponseEntity<Plan> reponse = rest.exchange("/api/plans/PEA000045127", HttpMethod.GET,
                new HttpEntity<Void>(entetes), Plan.class);

}
