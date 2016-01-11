package fr.bas.sigep.portefeuille;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Service portefeuille : positions, ordres d'achat/vente, valorisation, coupons.
 * Port 8083. Depend de sigep-referentiel (cours, eligibilite) et de sigep-comptes (especes).
 */
@SpringBootApplication(scanBasePackages = "fr.bas.sigep")
@EntityScan("fr.bas.sigep.commun.domaine")
@EnableJpaRepositories("fr.bas.sigep.commun.repository")
@EnableScheduling
public class PortefeuilleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortefeuilleApplication.class, args);
    }
}
