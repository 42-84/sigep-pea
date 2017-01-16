package fr.bas.sigep.comptes.controleur;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.bas.sigep.commun.domaine.OperationEspeces;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.comptes.dto.DemandeMontantDTO;
import fr.bas.sigep.comptes.dto.DemandeOuvertureDTO;
 * sur client.getPlans() a la serialisation. Depuis, toutes les requetes HTTP de ce controleur
 * tiennent une transaction pendant les appels REST vers sigep-portefeuille.
 */
@RestController
@RequestMapping("/api/plans")
@Transactional
public class PlanControleur {

    @Autowired
    private PlanService planService;

    @Autowired
    private CompteEspecesService compteEspecesService;

    @GetMapping("/{numero}")
    public Plan detail(@PathVariable("numero") String numero) {
        return planService.charger(numero);
    }

    @GetMapping("/{numero}/synthese")
    public SynthesePlanDTO synthese(@PathVariable("numero") String numero) {
        return planService.synthese(numero);
    }

    @GetMapping("/{numero}/operations")
    public List<OperationEspeces> operations(@PathVariable("numero") String numero,
            @RequestParam(value = "du", required = false) String du,
            @RequestParam(value = "au", required = false) String au) {
        Plan plan = planService.charger(numero);
        Date dateDu = du == null ? null : DateUtils.parserJour(du);
        Date dateAu = au == null ? null : DateUtils.parserJour(au);
    public Plan verser(@PathVariable("numero") String numero, @RequestBody DemandeMontantDTO demande) {
        return planService.verser(numero, demande);
    }

    @GetMapping("/{numero}/retraits/simulation")
    public SimulationRetraitDTO simulerRetrait(@PathVariable("numero") String numero,
            @RequestParam("montant") BigDecimal montant) {
        return planService.simulerRetrait(numero, montant);
    }

    @PostMapping("/{numero}/retraits")
    public SimulationRetraitDTO retirer(@PathVariable("numero") String numero, @RequestBody DemandeMontantDTO demande) {
        return planService.retirer(numero, demande);
    }

    @PostMapping("/{numero}/cloture")
    public Plan cloturer(@PathVariable("numero") String numero,
            @RequestParam(value = "motif", required = false) String motif) {
        return planService.cloturer(numero, motif);
    }
}
