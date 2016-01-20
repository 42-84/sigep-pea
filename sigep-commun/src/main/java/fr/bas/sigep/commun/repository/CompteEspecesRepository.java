package fr.bas.sigep.commun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bas.sigep.commun.domaine.CompteEspeces;

public interface CompteEspecesRepository extends JpaRepository<CompteEspeces, Long> {

    CompteEspeces findByNumeroCompte(String numeroCompte);
}
