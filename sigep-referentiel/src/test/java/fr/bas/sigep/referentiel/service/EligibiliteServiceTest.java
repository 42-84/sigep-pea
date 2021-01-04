package fr.bas.sigep.referentiel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fr.bas.sigep.commun.domaine.TypePlan;
import fr.bas.sigep.commun.domaine.TypeValeur;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.dto.EligibiliteDTO;
import fr.bas.sigep.commun.util.DateUtils;

public class EligibiliteServiceTest {

    private EligibiliteService service;

    @Before
    public void init() {
        service = new EligibiliteService();
    }

    private static Valeur action(String isin, String pays, boolean flagPea) {
        Valeur v = new Valeur();
        v.setCodeIsin(isin);
        v.setLibelle("VALEUR TEST");
        v.setTypeValeur(TypeValeur.ACTION);
        v.setCodePaysSiege(pays);
        v.setEligiblePea(flagPea);
        return v;
    }

    @Test
    public void uneActionFrancaiseEstEligible() {
        EligibiliteDTO dto = service.verifier(action("FR0000120073", "FR", true), TypePlan.PEA);

        assertTrue(dto.isEligible());
        assertNull(dto.getMotif());
        assertEquals("PEA", dto.getTypePlan());
    }

    @Test
    public void uneActionDeclareeNonEligibleEstRefusee() {
        EligibiliteDTO dto = service.verifier(action("US0378331005", "US", false), TypePlan.PEA);

        assertFalse(dto.isEligible());
        assertNotNull(dto.getMotif());
    }

    @Test
    public void uneValeurRadieeEstRefusee() {
        Valeur v = action("FR0000120073", "FR", true);
        v.setDateRadiation(DateUtils.construire(2019, 1, 1));

        EligibiliteDTO dto = service.verifier(v, TypePlan.PEA);

        assertFalse(dto.isEligible());
        assertTrue(dto.getMotif().contains("radiee"));
    }

    @Test
    public void uneActionFlaggeeMaisHorsUeEstRefuseeAvecLeMotif() {
        // incoherence flag / pays : le service la signale
        EligibiliteDTO dto = service.verifier(action("US0378331005", "US", true), TypePlan.PEA);

        assertFalse(dto.isEligible());
        assertTrue(dto.getMotif().contains("hors UE"));
    }

    @Test
    public void sansTypeDePlanOnRaisonneEnPea() {
        EligibiliteDTO dto = service.verifier(action("FR0000120073", "FR", true), null);
        assertEquals("PEA", dto.getTypePlan());
    }
}
