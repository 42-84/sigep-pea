package fr.bas.sigep.comptes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Service comptes : clients, plans (ouverture, versements, retraits, cloture), comptes especes, fiscalite.
 * Port 8082.
 */
@SpringBootApplication(scanBasePackages = "fr.bas.sigep")
@EntityScan("fr.bas.sigep.commun.domaine")
@EnableJpaRepositories("fr.bas.sigep.commun.repository")
@EnableScheduling
public class ComptesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComptesApplication.class, args);
    }
}
