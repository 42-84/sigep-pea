package fr.bas.sigep.portefeuille.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Position;
import fr.bas.sigep.commun.dto.CoursDTO;
import fr.bas.sigep.commun.dto.PositionDTO;
import fr.bas.sigep.commun.dto.ValorisationDTO;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.util.MontantUtils;
import fr.bas.sigep.portefeuille.client.ReferentielClient;

/**
 * Valorisation d'un plan au dernier cours connu.
 *
 * Un appel HTTP au referentiel par ligne. Pour le batch quotidien (40 000 plans x ~6 lignes)
 * ca represente 240 000 appels chaque soir ; le batch dure environ 40 minutes. Un cache des
 * cours a ete envisage (SIGEP-471) mais le referentiel est sur une autre machine et "on ne sait
 * pas quand il a fini d'importer Fininfo".
 */
@Service
public class ValorisationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValorisationService.class);

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ReferentielClient referentielClient;

    public ValorisationDTO valoriser(Plan plan) {
        ValorisationDTO valo = new ValorisationDTO();
        valo.setNumeroPlan(plan.getNumeroPlan());
        valo.setDateValorisation(new Date());
        valo.setTotalVersements(plan.getTotalVersements());
        // solde especes lu directement sur l'entite (table de sigep-comptes)
        valo.setSoldeEspeces(plan.getCompteEspeces().getSolde());

        List<Position> positions = positionRepository.findByPlanOrderByValeurLibelle(plan);
        BigDecimal totalTitres = BigDecimal.ZERO;
        BigDecimal totalRevient = BigDecimal.ZERO;
        int lignes = 0;
        for (Position position : positions) {
            if (position.getQuantite().signum() == 0) {
                continue; // lignes soldees non purgees (avant 2018 on ne supprimait pas la position)
        valo.setValorisationTitres(MontantUtils.arrondirMontant(totalTitres));
        valo.setPrixRevientTotal(MontantUtils.arrondirMontant(totalRevient));
        valo.setTotal(valo.getValorisationTitres().add(valo.getSoldeEspeces()));
        valo.setPlusValueLatente(valo.getValorisationTitres().subtract(valo.getPrixRevientTotal()));
        return valo;
    }

    public PositionDTO valoriserLigne(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setCodeIsin(position.getValeur().getCodeIsin());
        dto.setPlusValueLatente(dto.getValorisation().subtract(dto.getPrixRevientTotal()));
        if (dto.getPrixRevientTotal().signum() > 0) {
            dto.setPlusValuePourcent(dto.getPlusValueLatente().multiply(MontantUtils.CENT)
                    .divide(dto.getPrixRevientTotal(), 2, RoundingMode.HALF_UP));
        } else {
            dto.setPlusValuePourcent(BigDecimal.ZERO);
        }
        return dto;
    }
}
