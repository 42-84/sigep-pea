package fr.bas.sigep.portefeuille.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.ResourceAccessException;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.dto.ValorisationDTO;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.portefeuille.client.ReferentielClient;

@RunWith(MockitoJUnitRunner.class)
public class ValorisationServiceTest {

    private static final String ISIN = "FR0000120073";

        plan.setNumeroPlan("PEA000045127");
        plan.setType(TypePlan.PEA);
        plan.setClient(client);
        plan.setStatut(StatutPlan.OUVERT);
        valeur.setTypeValeur(TypeValeur.ACTION);

        position = new Position();
        position.setPlan(plan);
        position.setValeur(valeur);
        position.setQuantite(new BigDecimal("40"));
        position.setPrixRevientUnitaire(new BigDecimal("90.3100"));

        List<Position> positions = new ArrayList<Position>();
        positions.add(position);
        when(positionRepository.findByPlanOrderByValeurLibelle(plan)).thenReturn(positions);
    }

    @Test
    public void doitValoriserUnPlanAuDernierCours() {
        CoursDTO cours = new CoursDTO();
        cours.setCodeIsin(ISIN);
        cours.setDateCours(DateUtils.construire(2024, 3, 15));
        cours.setCloture(new BigDecimal("184.1000"));
        when(referentielClient.dernierCours(ISIN)).thenReturn(cours);

        ValorisationDTO valo = service.valoriser(plan);

        assertNotNull(valo);
        assertEquals("PEA000045127", valo.getNumeroPlan());
        assertEquals(1, valo.getNombreLignes());
        assertFalse(valo.isPartielle());
        // 40 x 184,10 = 7364,00 ; PRU total 40 x 90,31 = 3612,40 ; PV latente 3751,60
        assertEquals(new BigDecimal("7364.00"), valo.getValorisationTitres());
        assertEquals(new BigDecimal("3612.40"), valo.getPrixRevientTotal());
        assertEquals(new BigDecimal("3751.60"), valo.getPlusValueLatente());
        assertEquals(0, new BigDecimal("2350.75").compareTo(valo.getSoldeEspeces()));
        assertEquals(0, new BigDecimal("9714.75").compareTo(valo.getTotal()));
        assertEquals(0, new BigDecimal("87500").compareTo(valo.getTotalVersements()));

        PositionDTO ligne = valo.getPositions().get(0);
        assertFalse(ligne.isCoursIndisponible());
        assertEquals(0, new BigDecimal("184.10").compareTo(ligne.getDernierCours()));
        assertEquals(new BigDecimal("103.85"), ligne.getPlusValuePourcent());
    }

    @Test
    public void referentielIndisponibleValoriseAuPruEtMarqueLaValorisationPartielle() {
        assertTrue(valo.getPositions().isEmpty());
    }
}
