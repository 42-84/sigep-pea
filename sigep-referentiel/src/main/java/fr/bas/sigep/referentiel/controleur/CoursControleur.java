package fr.bas.sigep.referentiel.controleur;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import fr.bas.sigep.commun.dto.CoursDTO;
import fr.bas.sigep.commun.securite.ControleAcces;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.referentiel.batch.ImportCoursFininfo;
import fr.bas.sigep.referentiel.batch.ResultatImport;
import fr.bas.sigep.referentiel.service.CoursService;

@RestController
@RequestMapping("/api/cours")
public class CoursControleur {

    @Autowired
    private CoursService coursService;

    @Autowired
    private ImportCoursFininfo importCoursFininfo;

    @GetMapping("/{isin}/dernier")
    public CoursDTO dernier(@PathVariable("isin") String isin) {
        return coursService.dernierCours(isin);
    }

    @GetMapping("/{isin}")
    public List<CoursDTO> historique(@PathVariable("isin") String isin,
            @RequestParam(value = "du", required = false) String du,
            @RequestParam(value = "au", required = false) String au) {
        Date dateDu = du == null ? null : DateUtils.parserJour(du);
        Date dateAu = au == null ? null : DateUtils.parserJour(au);
        return coursService.historique(isin, dateDu, dateAu);
    }

    @PostMapping("/saisie")
    public CoursDTO saisir(@RequestBody CoursDTO cours) {
        return coursService.saisir(cours);
    }

    /**
     * Import manuel d'un fichier Fininfo (rejeu apres incident batch). Meme format que le batch.
     */
    @PostMapping("/import")
        Map<String, Object> etat = new HashMap<String, Object>();
        etat.put("derniereDateCotation", coursService.derniereDateCotation());
        etat.put("dernierImport", importCoursFininfo.getDernierResultat());
        return etat;
    }
}
