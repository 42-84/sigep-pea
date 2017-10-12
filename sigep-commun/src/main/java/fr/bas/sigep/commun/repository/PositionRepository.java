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

    Position findByPlanAndValeur(Plan plan, Valeur valeur);

    List<Position> findByValeur(Valeur valeur);

    @Query("select p from Position p where p.valeur = :valeur and p.quantite > 0 and p.plan.statut = 0")
    List<Position> findDetenteursActifs(@Param("valeur") Valeur valeur);

    @Query("select count(distinct p.plan) from Position p where p.valeur = :valeur and p.quantite > 0")
    long compterDetenteurs(@Param("valeur") Valeur valeur);
}
