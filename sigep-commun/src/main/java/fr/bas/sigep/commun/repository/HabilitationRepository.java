package fr.bas.sigep.commun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bas.sigep.commun.domaine.Habilitation;

public interface HabilitationRepository extends JpaRepository<Habilitation, Long> {

    Habilitation findByUtilisateurAndActifTrue(String utilisateur);
}
