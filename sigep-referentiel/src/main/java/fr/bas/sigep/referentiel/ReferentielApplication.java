package fr.bas.sigep.referentiel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Service referentiel : valeurs mobilieres, cours, eligibilite PEA.
 * Port 8081.
 */
@SpringBootApplication(scanBasePackages = "fr.bas.sigep")
@EntityScan("fr.bas.sigep.commun.domaine")
@EnableJpaRepositories("fr.bas.sigep.commun.repository")
@EnableScheduling
public class ReferentielApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReferentielApplication.class, args);
    }
}
