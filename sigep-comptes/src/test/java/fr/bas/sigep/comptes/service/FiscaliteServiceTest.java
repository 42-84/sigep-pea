package fr.bas.sigep.comptes.service;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import fr.bas.sigep.commun.util.DateUtils;

public class FiscaliteServiceTest {

    private FiscaliteService service;

    @Before
    public void init() {
        service = new FiscaliteService();
    }

    @Test
    public void tauxDe2013() {
        assertThat(service.getTauxPrelevementsSociaux(DateUtils.construire(2013, 6, 1)), comparesEqualTo(new BigDecimal("15.5")));
    }

    @Test
    public void tauxDepuis2018() {
        assertThat(service.getTauxPrelevementsSociaux(DateUtils.construire(2018, 1, 15)), comparesEqualTo(new BigDecimal("17.2")));
        // la table s'arrete en 2018 : le dernier taux connu s'applique
        assertThat(service.getTauxPrelevementsSociaux(DateUtils.construire(2024, 3, 15)), comparesEqualTo(new BigDecimal("17.2")));
    }

    @Test
    public void tauxDe2010RetombeSurCeluiDe2009() {
        assertThat(service.getTauxPrelevementsSociaux(DateUtils.construire(2010, 9, 30)), comparesEqualTo(new BigDecimal("12.1")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pasDeTauxAvant2004() {
        service.getTauxPrelevementsSociaux(DateUtils.construire(2003, 12, 31));
    }

    @Test
    public void doitCalculerLaPartDeGainDUnRetraitPartiel() {
        // 1000 x (12000 - 10000) / 12000 = 166,666667 -> 166,67
        BigDecimal gain = service.gainProportionnel(new BigDecimal("1000"), new BigDecimal("12000"), new BigDecimal("10000"));
        assertEquals(new BigDecimal("166.67"), gain);
    }

    @Test
    public void uneMoinsValueNeDonneAucunGain() {
        BigDecimal gain = service.gainProportionnel(new BigDecimal("1000"), new BigDecimal("9000"), new BigDecimal("10000"));
        assertThat(gain, comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void doitCalculerLesPrelevementsSociaux() {
        // 166,67 x 17,2 % = 28,66724 -> 28,67
        BigDecimal ps = service.prelevementsSociaux(new BigDecimal("166.67"), DateUtils.construire(2024, 3, 15));
        assertEquals(new BigDecimal("28.67"), ps);
    }

    @Test
    public void pasDePrelevementsSurUnGainNul() {
        assertThat(service.prelevementsSociaux(BigDecimal.ZERO, DateUtils.construire(2024, 3, 15)), comparesEqualTo(BigDecimal.ZERO));
        assertThat(service.prelevementsSociaux(null, DateUtils.construire(2024, 3, 15)), comparesEqualTo(BigDecimal.ZERO));
    }
}
