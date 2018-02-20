package fr.bas.sigep.portefeuille.controleur;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.bas.sigep.commun.domaine.Mouvement;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Position;
import fr.bas.sigep.commun.domaine.ValorisationHisto;
import fr.bas.sigep.commun.dto.PositionDTO;
import fr.bas.sigep.commun.dto.ValorisationDTO;
import fr.bas.sigep.commun.repository.ValorisationHistoRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.portefeuille.dto.HistoriqueValorisationDTO;
import fr.bas.sigep.portefeuille.dto.OrdreDTO;
import fr.bas.sigep.portefeuille.service.PortefeuilleService;
import fr.bas.sigep.portefeuille.service.ValorisationService;

@RestController
@RequestMapping("/api/portefeuilles")
public class PortefeuilleControleur {

    @Autowired
    private PortefeuilleService portefeuilleService;

    @Autowired
    private ValorisationService valorisationService;

    @Autowired
    private ValorisationHistoRepository valorisationHistoRepository;

    /** Positions valorisees. Meme calcul que /valorisation, sans les totaux. */
    @GetMapping("/{numero}/positions")
    public List<PositionDTO> positions(@PathVariable("numero") String numero) {
        List<PositionDTO> dtos = new ArrayList<PositionDTO>();
        for (Position p : portefeuilleService.positions(numero)) {
            if (p.getQuantite().signum() > 0) {
                dtos.add(valorisationService.valoriserLigne(p));
            }
        }
        return dtos;
    }

    @GetMapping("/{numero}/valorisation")
    public ValorisationDTO valorisation(@PathVariable("numero") String numero) {
        Plan plan = portefeuilleService.chargerPlan(numero);
        return valorisationService.valoriser(plan);
    }

    @GetMapping("/{numero}/mouvements")
    public List<Mouvement> mouvements(@PathVariable("numero") String numero,
            @RequestParam(value = "du", required = false) String du,
            @RequestParam(value = "au", required = false) String au) {
        Date dateDu = du == null ? null : DateUtils.parserJour(du);
        Date dateAu = au == null ? null : DateUtils.parserJour(au);
        return portefeuilleService.mouvements(numero, dateDu, dateAu);
    }

    @PostMapping("/{numero}/ordres")
        return portefeuilleService.annuler(numero, reference, motif);
    }

    /** Historique de valorisation (photos quotidiennes du batch) pour le graphique du front. */
        List<HistoriqueValorisationDTO> dtos = new ArrayList<HistoriqueValorisationDTO>();
        for (ValorisationHisto h : histo) {
            dtos.add(new HistoriqueValorisationDTO(h.getDateValorisation(), h.getValorisationTitres(), h.getSoldeEspeces(), h.getTotalVersements()));
        }
        return dtos;
    }
}
