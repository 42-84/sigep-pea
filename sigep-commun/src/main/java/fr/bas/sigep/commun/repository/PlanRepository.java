package fr.bas.sigep.commun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.StatutPlan;
import fr.bas.sigep.commun.domaine.TypePlan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Plan findByNumeroPlan(String numeroPlan);

    List<Plan> findByClient(Client client);

    List<Plan> findByClientNumeroClient(String numeroClient);

    List<Plan> findByStatut(StatutPlan statut);

    // Non utilise : "ne marchait pas sous Oracle" (2017), PlanService fait un findAll() + filtre.
    // A re-tester, le probleme venait probablement de l'ORDINAL du statut. -- JM
    List<Plan> findByClientAndTypeAndStatut(Client client, TypePlan type, StatutPlan statut);

    @Query("select count(p) from Plan p where p.statut = 0")
    long compterPlansOuverts();

    // CAST plutot que TO_NUMBER : H2 (dev) ne connait pas TO_NUMBER meme en MODE=Oracle. Oracle accepte les deux.
    @Query(value = "select max(cast(substr(NUM_PLAN, 4) as decimal(9))) from SGP_PLAN", nativeQuery = true)
    Long dernierNumeroSequentiel();

    @Query("select p from Plan p where p.codeAgence = :agence and p.statut = :statut")
    List<Plan> findParAgence(@Param("agence") String agence, @Param("statut") StatutPlan statut);
}
