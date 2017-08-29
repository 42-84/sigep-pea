package fr.bas.sigep.comptes.controleur;

import java.util.List;

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

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.comptes.service.ClientService;
import fr.bas.sigep.comptes.service.PlanService;

@RestController
@RequestMapping("/api/clients")
public class ClientControleur {

    @Autowired
    private ClientService clientService;

    @Autowired
    private PlanService planService;

    @GetMapping
    public List<Client> rechercher(@RequestParam("nom") String nom,
            @RequestParam(value = "prenom", required = false) String prenom) {
        return clientService.rechercher(nom, prenom);
    }

    @GetMapping("/{numero}")
    public Client detail(@PathVariable("numero") String numero) {
        return clientService.charger(numero);
    }

    @GetMapping("/{numero}/plans")
    public List<Plan> plans(@PathVariable("numero") String numero) {
        return planService.plansDuClient(numero);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
}
