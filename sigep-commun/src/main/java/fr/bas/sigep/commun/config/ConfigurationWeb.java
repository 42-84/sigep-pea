package fr.bas.sigep.commun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * CORS ouvert : ajoute pour le debug du front Angular (ng serve sans proxy) en 2018,
 * jamais retire. En prod le front et les API sont derriere le meme reverse proxy,
 * donc "ca ne sert a rien mais ca ne gene pas".
 */
@Configuration
public class ConfigurationWeb extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
