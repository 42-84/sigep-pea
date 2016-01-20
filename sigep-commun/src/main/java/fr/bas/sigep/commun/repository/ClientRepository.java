package fr.bas.sigep.commun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByNumeroClient(String numeroClient);

    List<Client> findByNomIgnoreCaseStartingWithOrderByNomAscPrenomAsc(String nom);

    @Query("select c from Client c where upper(c.nom) like upper(concat(:nom, '%')) and upper(c.prenom) like upper(concat(:prenom, '%'))")
    List<Client> rechercher(@Param("nom") String nom, @Param("prenom") String prenom);
}
