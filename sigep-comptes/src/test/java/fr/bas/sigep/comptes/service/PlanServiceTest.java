package fr.bas.sigep.comptes.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.domaine.StatutPlan;
import fr.bas.sigep.commun.domaine.TypeOperationEspeces;
import fr.bas.sigep.commun.domaine.TypePlan;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.ClientRepository;
import fr.bas.sigep.commun.repository.PlanRepository;
import fr.bas.sigep.commun.securite.ContexteUtilisateur;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.comptes.client.PortefeuilleClient;
import fr.bas.sigep.comptes.dto.DemandeMontantDTO;

@RunWith(MockitoJUnitRunner.class)
public class PlanServiceTest {

    private static final String NUMERO_PLAN = "PEA000045127";

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private CompteEspecesService compteEspecesService;

    @Mock
    private RegleAncienneteService regleAnciennete;

    @Mock
    private FiscaliteService fiscaliteService;

    @Mock
    private PortefeuilleClient portefeuilleClient;

    @InjectMocks
    private PlanService planService;

    private Plan plan;
    private CompteEspeces compte;

    @Before
    public void init() {
        Habilitation h = new Habilitation();
        h.setUtilisateur("U045123");
        h.setProfil(Profil.CONSEILLER);
        h.setCodeAgence("17001");
        ContexteUtilisateur.definir(h);

        Client client = new Client();
        client.setId(1L);
        client.setNumeroClient("00012345");
        client.setNom("GAUTREAU");

        compte = new CompteEspeces();
        compte.setNumeroCompte("1751517001000004512700");
        compte.setSolde(new BigDecimal("2350.75"));

        plan = new Plan();
        plan.setId(1L);
        plan.setNumeroPlan(NUMERO_PLAN);
        plan.setType(TypePlan.PEA);
        plan.setClient(client);
        plan.setDateOuverture(DateUtils.construire(2009, 3, 12));
        plan.setStatut(StatutPlan.OUVERT);
        plan.setTotalVersements(BigDecimal.ZERO);
        plan.setCompteEspeces(compte);

        when(planRepository.findByNumeroPlan(NUMERO_PLAN)).thenReturn(plan);
    }

    @After
    public void nettoyer() {
        ContexteUtilisateur.effacer();
    }

    private static DemandeMontantDTO demande(String montant) {
        DemandeMontantDTO d = new DemandeMontantDTO();
        d.setMontant(new BigDecimal(montant));
        return d;
    }

    @Test(expected = RessourceInconnueException.class)
    public void doitRefuserUnPlanInconnu() {
        when(planRepository.findByNumeroPlan("PEA999999999")).thenReturn(null);
        planService.charger("PEA999999999");
    }

    @Test
    public void doitChargerUnPlanConnu() {
        assertNotNull(planService.charger(NUMERO_PLAN));
    }

    @Test
    public void doitRefuserUnVersementSurUnPlanCloture() {
        plan.setStatut(StatutPlan.CLOTURE);

        try {
            planService.verser(NUMERO_PLAN, demande("100"));
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("PLAN_NON_OUVERT", e.getCode());
        }
        verify(compteEspecesService, never()).enregistrer(any(CompteEspeces.class), any(TypeOperationEspeces.class),
                any(BigDecimal.class), any(Date.class), any(String.class), any(String.class));
    }

    @Test
    public void doitRefuserUnVersementAuDessusDuPlafond() {
        // PEA : plafond 150 000 ; 149 990 + 20 = 150 010
        plan.setTotalVersements(new BigDecimal("149990"));

        try {
            planService.verser(NUMERO_PLAN, demande("20"));
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("PLAFOND_DEPASSE", e.getCode());
        }
        assertEquals(0, new BigDecimal("149990").compareTo(plan.getTotalVersements()));
    }

    @Test
    public void doitEnregistrerUnVersementValide() {
        Plan resultat = planService.verser(NUMERO_PLAN, demande("100"));

        assertNotNull(resultat);
        verify(compteEspecesService).enregistrer(eq(compte), eq(TypeOperationEspeces.VERSEMENT), eq(new BigDecimal("100.00")),
                (Date) isNull(), eq("Versement"), (String) isNull());
        verify(planRepository).save(plan);
        assertEquals(0, new BigDecimal("100").compareTo(plan.getTotalVersements()));
        assertNotNull(plan.getDatePremierVersement());
    }

    // TODO tests retrait / ouverture (SIGEP-402)
}
