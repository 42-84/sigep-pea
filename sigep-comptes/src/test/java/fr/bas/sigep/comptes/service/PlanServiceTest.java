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

        when(planRepository.findByNumeroPlan(NUMERO_PLAN)).thenReturn(plan);
    }

    @After
    }

    @Test(expected = RessourceInconnueException.class)
    public void doitRefuserUnPlanInconnu() {
        when(planRepository.findByNumeroPlan("PEA999999999")).thenReturn(null);
        planService.charger("PEA999999999");
            planService.verser(NUMERO_PLAN, demande("100"));
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("PLAN_NON_OUVERT", e.getCode());
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
}
