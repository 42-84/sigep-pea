package fr.bas.sigep.commun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.Valeur;
    @Query("select v from Valeur v where (upper(v.libelle) like upper(concat('%', :q, '%')) "
            + "or upper(v.codeIsin) like upper(concat(:q, '%')) or upper(v.mnemonique) like upper(concat(:q, '%'))) "
            + "and v.dateRadiation is null order by v.libelle")
    List<Valeur> rechercher(@Param("q") String q);

    List<Valeur> findByEligiblePeaTrueAndDateRadiationIsNullOrderByLibelle();

    List<Valeur> findByDateRadiationIsNull();
}
