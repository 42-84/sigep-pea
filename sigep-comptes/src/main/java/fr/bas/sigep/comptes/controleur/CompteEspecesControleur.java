package fr.bas.sigep.comptes.controleur;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.bas.sigep.commun.domaine.OperationEspeces;
import fr.bas.sigep.commun.dto.OperationEspecesDTO;
import fr.bas.sigep.commun.dto.SoldeDTO;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.comptes.service.CompteEspecesService;

/**
 * API comptes especes. Le POST est un endpoint INTERNE (appel de sigep-portefeuille) :
 * il n'y a pas de controle d'acces particulier, tout utilisateur habilite peut l'appeler.
 * Le pare-feu est cense empecher un appel direct depuis le poste client (jamais verifie).
 */
@RestController
@RequestMapping("/api/comptes-especes")
@CrossOrigin("*")
public class CompteEspecesControleur {

    @Autowired
    private CompteEspecesService compteEspecesService;

    @GetMapping("/{numero}/solde")
    public SoldeDTO solde(@PathVariable("numero") String numero) {
        return compteEspecesService.solde(numero);
    }

    @GetMapping("/{numero}/operations")
    public List<OperationEspeces> operations(@PathVariable("numero") String numero,
            @RequestParam(value = "du", required = false) String du,
            @RequestParam(value = "au", required = false) String au) {
        Date dateDu = du == null ? null : DateUtils.parserJour(du);
        Date dateAu = au == null ? null : DateUtils.parserJour(au);
        return compteEspecesService.operations(numero, dateDu, dateAu);
    }

    /** Appel interne SIGEP-PORTEFEUILLE (achat, vente, coupon, frais). */
    @PostMapping("/{numero}/operations")
    @ResponseStatus(HttpStatus.CREATED)
    public OperationEspeces enregistrer(@PathVariable("numero") String numero, @RequestBody OperationEspecesDTO demande) {
        return compteEspecesService.enregistrer(numero, demande);
    }
}
