package fr.bas.sigep.comptes.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.util.DateUtils;

public class RegleAncienneteServiceTest {

    private RegleAncienneteService service;
    private Date aujourdhui;

    @Before
    public void init() {
        service = new RegleAncienneteService();
        ReflectionTestUtils.setField(service, "pacteActif", true);
        aujourdhui = DateUtils.aujourdhui();
    }

    private Plan planAvecPremierVersementIlYA(int annees) {
        Plan plan = new Plan();
        plan.setNumeroPlan("PEA000045127");
        plan.setDateOuverture(DateUtils.ajouterAnnees(aujourdhui, -annees));
        plan.setDatePremierVersement(DateUtils.ajouterAnnees(aujourdhui, -annees));
        return plan;
    }

    @Test
    public void unRetraitAvantCinqAnsEntraineLaCloture() {
        assertTrue(service.retraitEntraineCloture(planAvecPremierVersementIlYA(4), aujourdhui));
    }

    @Test
    public void unRetraitApresCinqAnsNEntrainePasLaCloture() {
        assertFalse(service.retraitEntraineCloture(planAvecPremierVersementIlYA(6), aujourdhui));
    }

    @Test
    public void avantPacteLeSeuilEtaitDeHuitAns() {
        ReflectionTestUtils.setField(service, "pacteActif", false);
        assertTrue(service.retraitEntraineCloture(planAvecPremierVersementIlYA(6), aujourdhui));
        assertFalse(service.versementsAutorisesApresRetrait(planAvecPremierVersementIlYA(9), aujourdhui));
    }

    @Test
    public void laDateDeDepartRetombeSurLOuvertureSansPremierVersement() {
        Plan plan = new Plan();
        plan.setDateOuverture(DateUtils.construire(2009, 3, 12));
        plan.setDatePremierVersement(null);

        assertEquals(DateUtils.construire(2009, 3, 12), service.dateDepart(plan));
        assertEquals(DateUtils.construire(2014, 3, 12), service.dateCinqAns(plan));
    }

    @Test
    public void laDateDeDepartEstLePremierVersementQuandIlEstConnu() {
        Plan plan = new Plan();
        plan.setDateOuverture(DateUtils.construire(2019, 6, 3));
        plan.setDatePremierVersement(DateUtils.construire(2019, 6, 10));

        assertEquals(DateUtils.construire(2019, 6, 10), service.dateDepart(plan));
    }

    @Test
    @Ignore("depend de la date du jour, casse chaque annee")
    public void ancienneteDuPlanRepris() {
        Plan plan = new Plan();
        plan.setDateOuverture(DateUtils.construire(2009, 3, 12));
        // ecrit en 2024...
        assertEquals(15, service.anciennete(plan));
    }
}
