package fr.bas.sigep.commun.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.Mouvement;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.StatutMouvement;

public interface MouvementRepository extends JpaRepository<Mouvement, Long> {

    Mouvement findByReference(String reference);

    List<Mouvement> findByPlanOrderByDateMouvementDesc(Plan plan);

    List<Mouvement> findByPlanAndDateMouvementBetweenOrderByDateMouvementDesc(Plan plan, Date debut, Date fin);

    List<Mouvement> findByStatut(StatutMouvement statut);

    @Query("select count(m) from Mouvement m where m.dateMouvement >= :debut and m.dateMouvement < :fin")
    long compterDuJour(@Param("debut") Date debut, @Param("fin") Date fin);
}
