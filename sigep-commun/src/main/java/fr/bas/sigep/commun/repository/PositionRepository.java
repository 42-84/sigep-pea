package fr.bas.sigep.commun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Position;
import fr.bas.sigep.commun.domaine.Valeur;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByPlan(Plan plan);

    List<Position> findByPlanOrderByValeurLibelle(Plan plan);
    long compterDetenteurs(@Param("valeur") Valeur valeur);
}
