package fr.bas.sigep.referentiel.controleur;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.bas.sigep.commun.domaine.TypePlan;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.dto.EligibiliteDTO;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.referentiel.service.EligibiliteService;
import fr.bas.sigep.referentiel.service.ValeurService;

/**
 * API valeurs. Renvoie l'entite JPA directement (pas de DTO) : le front et sigep-portefeuille
 * consomment le JSON tel quel.
 */
@RestController
@RequestMapping("/api/valeurs")
public class ValeurControleur {

    @Autowired
    private ValeurService valeurService;

    @Autowired
    private EligibiliteService eligibiliteService;

    @GetMapping
    public List<Valeur> rechercher(@RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "eligibles", required = false, defaultValue = "false") boolean eligibles) {
        return valeurService.rechercher(q, eligibles);
    }

    @GetMapping("/{isin}")
    public Valeur detail(@PathVariable("isin") String isin) {
        return valeurService.charger(isin);
    }

    @GetMapping("/{isin}/eligibilite")
    public EligibiliteDTO eligibilite(@PathVariable("isin") String isin,
            @RequestParam(value = "typePlan", required = false, defaultValue = "PEA") String typePlan) {
        Valeur valeur = valeurService.charger(isin);
        return eligibiliteService.verifier(valeur, TypePlan.valueOf(typePlan.toUpperCase()));
    }

    /** Nombre de plans detenant la valeur : lecture directe de SGP_POSITION (table de sigep-portefeuille). */
    @GetMapping("/{isin}/detenteurs")
    public Map<String, Object> detenteurs(@PathVariable("isin") String isin) {
        Map<String, Object> reponse = new HashMap<String, Object>();
        reponse.put("codeIsin", isin);
        reponse.put("nombrePlans", valeurService.compterDetenteurs(isin));
        return reponse;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Valeur creer(@RequestBody Valeur valeur) {
        return valeurService.creer(valeur);
    }

    @PutMapping("/{isin}")
    public Valeur modifier(@PathVariable("isin") String isin, @RequestBody Valeur valeur) {
        return valeurService.modifier(isin, valeur);
    }

    @PutMapping("/{isin}/radiation")
    public Valeur radier(@PathVariable("isin") String isin,
            @RequestParam(value = "date", required = false) String date) {
        Date dateRadiation = date == null ? null : DateUtils.parserJour(date);
        return valeurService.radier(isin, dateRadiation);
    }
}
