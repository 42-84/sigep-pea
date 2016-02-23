package fr.bas.sigep.portefeuille.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import fr.bas.sigep.commun.domaine.TypeValeur;
import fr.bas.sigep.commun.domaine.Valeur;

/**
 * Bareme "Bourse Particuliers" 2012 : 0,50 % min 8 EUR max 60 EUR sur actions, 1 % OPCVM externes, 0 OPCVM BAS.
 */
public class FraisServiceTest {

    private FraisService service;

    @Before
    public void init() {
        service = new FraisService();
        // les @Value ne sont pas injectes hors contexte Spring
        ReflectionTestUtils.setField(service, "tauxActions", new BigDecimal("0.50"));
        ReflectionTestUtils.setField(service, "minimum", new BigDecimal("8.00"));
        ReflectionTestUtils.setField(service, "maximum", new BigDecimal("60.00"));
    }

    private static Valeur valeur(TypeValeur type, String libelle) {
        Valeur v = new Valeur();
        v.setCodeIsin("FR0000120073");
        v.setTypeValeur(type);
        v.setLibelle(libelle);
        return v;
    }

    @Test
    public void actionAuTauxProportionnel() {
        // 3594,40 x 0,50 % = 17,972 -> 17,97
        BigDecimal frais = service.calculer(valeur(TypeValeur.ACTION, "AIR LIQUIDE"), new BigDecimal("3594.40"));
        assertEquals(new BigDecimal("17.97"), frais);
    }

    @Test
    public void actionAuMinimum() {
        // 500 x 0,50 % = 2,50 < 8
        BigDecimal frais = service.calculer(valeur(TypeValeur.ACTION, "AIR LIQUIDE"), new BigDecimal("500"));
        assertEquals(new BigDecimal("8.00"), frais);
    }

    @Test
    public void actionAuMaximum() {
        // 50 000 x 0,50 % = 250 > 60
        BigDecimal frais = service.calculer(valeur(TypeValeur.ACTION, "AIR LIQUIDE"), new BigDecimal("50000"));
        assertEquals(new BigDecimal("60.00"), frais);
    }

    @Test
    public void etfSuitLeBaremeActions() {
        BigDecimal frais = service.calculer(valeur(TypeValeur.ETF, "LYXOR CAC 40"), new BigDecimal("3594.40"));
        assertEquals(new BigDecimal("17.97"), frais);
    }

    @Test
    public void opcvmMaisonSansFrais() {
        BigDecimal frais = service.calculer(valeur(TypeValeur.OPCVM, "BAS ACTIONS FRANCE C"), new BigDecimal("1000"));
        assertEquals(0, BigDecimal.ZERO.compareTo(frais));
    }

    @Test
    public void opcvmExterneAUnPourCent() {
        // 1000 x 1 % = 10,00
        BigDecimal frais = service.calculer(valeur(TypeValeur.OPCVM, "CARMIGNAC INVESTISSEMENT A"), new BigDecimal("1000"));
        assertEquals(new BigDecimal("10.00"), frais);
    }

    @Test
    public void pasDeFraisSurUnMontantNul() {
        assertEquals(0, BigDecimal.ZERO.compareTo(service.calculer(valeur(TypeValeur.ACTION, "AIR LIQUIDE"), BigDecimal.ZERO)));
        assertEquals(0, BigDecimal.ZERO.compareTo(service.calculer(valeur(TypeValeur.ACTION, "AIR LIQUIDE"), null)));
    }
}
