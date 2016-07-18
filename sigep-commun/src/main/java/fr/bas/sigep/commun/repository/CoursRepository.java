package fr.bas.sigep.commun.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.Cours;
import fr.bas.sigep.commun.domaine.Valeur;

public interface CoursRepository extends JpaRepository<Cours, Long> {

    List<Cours> findByValeur(Valeur valeur);

    List<Cours> findByValeurOrderByDateCoursDesc(Valeur valeur);

    Cours findByValeurAndDateCours(Valeur valeur, Date dateCours);

    List<Cours> findByValeurAndDateCoursBetweenOrderByDateCoursAsc(Valeur valeur, Date debut, Date fin);

    // Requete Oracle (ROWNUM). Marche sous H2 uniquement avec MODE=Oracle dans l'URL JDBC.
    @Query(value = "select * from (select c.* from SGP_COURS c where c.ID_VALEUR = :idValeur order by c.DT_COURS desc) where ROWNUM <= 1", nativeQuery = true)
    Cours dernierCoursNatif(@Param("idValeur") Long idValeur);

    @Query("select c from Cours c where c.dateCours = :date")
    List<Cours> findParDate(@Param("date") Date date);

    @Query("select max(c.dateCours) from Cours c")
    Date derniereDateCotation();
}
