package fr.bas.sigep.commun.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.domaine.OperationEspeces;
import fr.bas.sigep.commun.domaine.TypeOperationEspeces;

public interface OperationEspecesRepository extends JpaRepository<OperationEspeces, Long> {

    List<OperationEspeces> findByCompteOrderByDateOperationDesc(CompteEspeces compte);

    List<OperationEspeces> findByCompteAndDateOperationBetweenOrderByDateOperationDesc(CompteEspeces compte, Date debut, Date fin);

    List<OperationEspeces> findByCompteAndType(CompteEspeces compte, TypeOperationEspeces type);

    @Query("select coalesce(sum(o.montant), 0) from OperationEspeces o where o.compte = :compte")
    BigDecimal sommeOperations(@Param("compte") CompteEspeces compte);

    List<OperationEspeces> findByReference(String reference);
}
