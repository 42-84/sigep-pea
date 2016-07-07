package fr.bas.sigep.portefeuille.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.domaine.StatutPlan;
import fr.bas.sigep.commun.domaine.TypePlan;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.CompteEspecesRepository;
import fr.bas.sigep.commun.repository.MouvementRepository;
import fr.bas.sigep.commun.repository.OperationEspecesRepository;
import fr.bas.sigep.commun.repository.PlanRepository;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.repository.ValeurRepository;
import fr.bas.sigep.commun.securite.ContexteUtilisateur;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.portefeuille.client.ComptesClient;
import fr.bas.sigep.portefeuille.client.ReferentielClient;
import fr.bas.sigep.portefeuille.dto.OrdreDTO;

@RunWith(MockitoJUnitRunner.class)
public class PortefeuilleServiceTest {

    private static final String NUMERO_PLAN = "PEA000045127";

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ValeurRepository valeurRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private MouvementRepository mouvementRepository;

    @Mock
    private CompteEspecesRepository compteEspecesRepository;

    @Mock
    private OperationEspecesRepository operationEspecesRepository;

    @Mock
    private ReferentielClient referentielClient;

    @Mock
    private ComptesClient comptesClient;

    @Mock
    private FraisService fraisService;

    @Mock
    private CalculPruService calculPruService;

    @InjectMocks
    private PortefeuilleService service;

    private Plan plan;

    @Before
    public void init() {
        ReflectionTestUtils.setField(service, "quantiteMax", new BigDecimal("100000"));

        Habilitation h = new Habilitation();
        h.setUtilisateur("U045123");
        h.setProfil(Profil.CONSEILLER);
        ContexteUtilisateur.definir(h);

        Client client = new Client();
        client.setNumeroClient("00012345");
        client.setNom("GAUTREAU");

        CompteEspeces compte = new CompteEspeces();
        compte.setNumeroCompte("1751517001000004512700");
        compte.setSolde(new BigDecimal("2350.75"));

        plan = new Plan();
        plan.setNumeroPlan(NUMERO_PLAN);
        plan.setType(TypePlan.PEA);
        plan.setClient(client);
        plan.setDateOuverture(DateUtils.construire(2009, 3, 12));
        plan.setStatut(StatutPlan.OUVERT);
        plan.setCompteEspeces(compte);

        when(planRepository.findByNumeroPlan(NUMERO_PLAN)).thenReturn(plan);
    }

    @After
    public void nettoyer() {
        ContexteUtilisateur.effacer();
    }

    @Test(expected = RessourceInconnueException.class)
    public void doitRefuserUnPlanInconnu() {
        when(planRepository.findByNumeroPlan("PEA999999999")).thenReturn(null);
        service.chargerPlan("PEA999999999");
    }

    @Test
    public void doitRefuserUnOrdreSurUnPlanCloture() {
        plan.setStatut(StatutPlan.CLOTURE);
        OrdreDTO ordre = new OrdreDTO();
        ordre.setCodeIsin("FR0000120073");
        ordre.setSens("ACHAT");
        ordre.setQuantite(new BigDecimal("10"));

        try {
            service.passerOrdre(NUMERO_PLAN, ordre);
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("PLAN_NON_OUVERT", e.getCode());
        }
    }

    // desactives : trop de mocks a maintenir (F. Dubois, 2020)
    /*
    @Test
    public void doitExecuterUnAchatAuDernierCours() {
        Valeur valeur = valeur("FR0000120073", TypeValeur.ACTION);
        when(valeurRepository.findByCodeIsin("FR0000120073")).thenReturn(valeur);
        when(positionRepository.findByPlanAndValeur(plan, valeur)).thenReturn(null);
        when(referentielClient.eligibilite("FR0000120073", "PEA")).thenReturn(new EligibiliteDTO("FR0000120073", "PEA", true, null));
        when(referentielClient.dernierCours("FR0000120073")).thenReturn(cours("184.1000"));
        when(fraisService.calculer(eq(valeur), any(BigDecimal.class))).thenReturn(new BigDecimal("9.21"));
        when(calculPruService.apresAchat(any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("185.0210"));
        when(comptesClient.solde("1751517001000004512700")).thenReturn(solde("2350.75"));
        when(mouvementRepository.save(any(Mouvement.class))).thenAnswer(retourneArgument());

        Mouvement mvt = service.passerOrdre(NUMERO_PLAN, ordre("FR0000120073", "ACHAT", "10"));

        assertEquals(SensMouvement.ACHAT, mvt.getSens());
        assertEquals(new BigDecimal("1841.00"), mvt.getMontantBrut());
        assertEquals(new BigDecimal("1850.21"), mvt.getMontantNet());
        assertEquals(StatutMouvement.EXECUTE, mvt.getStatut());
        verify(positionRepository).save(any(Position.class));
        verify(operationEspecesRepository).save(any(OperationEspeces.class));
    }

    @Test
    public void doitRefuserUnAchatSansEspecesSuffisants() {
        Valeur valeur = valeur("FR0000120073", TypeValeur.ACTION);
        when(valeurRepository.findByCodeIsin("FR0000120073")).thenReturn(valeur);
        when(referentielClient.eligibilite("FR0000120073", "PEA")).thenReturn(new EligibiliteDTO("FR0000120073", "PEA", true, null));
        when(referentielClient.dernierCours("FR0000120073")).thenReturn(cours("184.1000"));
        when(fraisService.calculer(eq(valeur), any(BigDecimal.class))).thenReturn(new BigDecimal("36.82"));
        when(comptesClient.solde("1751517001000004512700")).thenReturn(solde("2350.75"));

        try {
            service.passerOrdre(NUMERO_PLAN, ordre("FR0000120073", "ACHAT", "40"));
            fail();
        } catch (RegleMetierException e) {
            assertEquals("ESPECES_INSUFFISANTS", e.getCode());
        }
    }

    @Test
    public void doitSupprimerLaPositionApresVenteTotale() {
        Valeur valeur = valeur("FR0000120073", TypeValeur.ACTION);
        Position position = position(valeur, "40", "90.3100");
        when(valeurRepository.findByCodeIsin("FR0000120073")).thenReturn(valeur);
        when(positionRepository.findByPlanAndValeur(plan, valeur)).thenReturn(position);
        when(referentielClient.dernierCours("FR0000120073")).thenReturn(cours("184.1000"));
        when(fraisService.calculer(eq(valeur), any(BigDecimal.class))).thenReturn(new BigDecimal("36.82"));
        when(calculPruService.quantiteApresVente(new BigDecimal("40"), new BigDecimal("40"))).thenReturn(BigDecimal.ZERO);
        when(mouvementRepository.save(any(Mouvement.class))).thenAnswer(retourneArgument());

        OrdreDTO ordre = ordre("FR0000120073", "VENTE", null);
        ordre.setTotalite(true);
        service.passerOrdre(NUMERO_PLAN, ordre);

        verify(positionRepository).delete(position);
    }
    */
}
