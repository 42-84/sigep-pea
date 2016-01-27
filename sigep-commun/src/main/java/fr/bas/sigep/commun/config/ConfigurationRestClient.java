package fr.bas.sigep.commun.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.bas.sigep.commun.securite.ContexteUtilisateur;
import fr.bas.sigep.commun.securite.SiteMinderFilter;

/**
 * RestTemplate partage pour les appels inter-services.
 *
 * Propage l'utilisateur SiteMinder courant (pour que l'audit USR_MAJ du service appele soit
 * le bon), ou l'identifiant technique SIGEP-INTERNE hors requete HTTP (batchs).
 *
 * Le RestTemplate doit utiliser le MEME ObjectMapper que les controleurs (dates dd/MM/yyyy),
 */
@Configuration
public class ConfigurationRestClient {

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        RestTemplate rt = new RestTemplate();
        List<HttpMessageConverter<?>> convertisseurs = rt.getMessageConverters();
        for (int i = 0; i < convertisseurs.size(); i++) {
        }
        rt.getInterceptors().add(new PropagationUtilisateur());
        return rt;
    }

    static class PropagationUtilisateur implements ClientHttpRequestInterceptor {

            String utilisateur = ContexteUtilisateur.getUtilisateur();
            if (utilisateur == null) {
                utilisateur = SiteMinderFilter.UTILISATEUR_INTERNE;
            }
            request.getHeaders().set(SiteMinderFilter.EN_TETE_SITEMINDER, utilisateur);
            return execution.execute(request, body);
        }
    }
}
