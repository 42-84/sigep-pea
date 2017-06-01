package fr.bas.sigep.commun.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bas.sigep.commun.domaine.ValorisationHisto;

public interface ValorisationHistoRepository extends JpaRepository<ValorisationHisto, Long> {

    List<ValorisationHisto> findByIdPlanOrderByDateValorisationAsc(Long idPlan);

    ValorisationHisto findByIdPlanAndDateValorisation(Long idPlan, Date date);

    List<ValorisationHisto> findByIdPlanAndDateValorisationAfterOrderByDateValorisationAsc(Long idPlan, Date depuis);
}
