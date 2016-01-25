package fr.bas.sigep.commun.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Traduction des exceptions en reponses HTTP. Charge par les trois services via le scan
 * du package fr.bas.sigep.
 */
@ControllerAdvice
public class GestionnaireErreurs {

    private static final Logger LOG = LoggerFactory.getLogger(GestionnaireErreurs.class);

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ErreurDTO> regleMetier(RegleMetierException e, HttpServletRequest req) {
        LOG.info("Regle metier {} : {}", e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErreurDTO(e.getCode(), e.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(RessourceInconnueException.class)

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ErreurDTO> refuse(AccesRefuseException e, HttpServletRequest req) {
        LOG.warn("Acces refuse sur {} : {}", req.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErreurDTO("ACCES_REFUSE", e.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErreurDTO> verrou(ObjectOptimisticLockingFailureException e, HttpServletRequest req) {
        LOG.warn("Conflit de version sur {} ({})", req.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErreurDTO("CONFLIT", "Operation concurrente, merci de reessayer", req.getRequestURI()));
    }

    /** Un service partenaire (referentiel, comptes, portefeuille) a repondu en erreur. */
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErreurDTO> servicePartenaire(HttpStatusCodeException e, HttpServletRequest req) {
        LOG.error("Erreur service partenaire {} : {}", e.getStatusCode(), e.getResponseBodyAsString());
        // on relaie le corps tel quel : c'est deja un ErreurDTO dans 99% des cas
}
