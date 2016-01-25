package fr.bas.sigep.commun.util;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class MontantUtilsTest {

    @Test
    public void doitArrondirUnMontantADeuxDecimales() {
        assertEquals(new BigDecimal("12.35"), MontantUtils.arrondirMontant(new BigDecimal("12.345")));
        assertEquals(new BigDecimal("12.34"), MontantUtils.arrondirMontant(new BigDecimal("12.344")));
        assertEquals(2, MontantUtils.arrondirMontant(new BigDecimal("100")).scale());
    }

    @Test
    public void doitRenvoyerNullPourUnMontantNull() {
        assertNull(MontantUtils.arrondirMontant(null));
        assertNull(MontantUtils.arrondirCours(null));
    }

    @Test
    public void doitCalculerUnPourcentage() {
        // 100 x 17,2 % = 17,20
        BigDecimal resultat = MontantUtils.pourcentage(new BigDecimal("100"), new BigDecimal("17.2"));
        assertEquals(new BigDecimal("17.20"), resultat);
    }

    @Test
    public void doitRemplacerNullParZero() {
        assertThat(MontantUtils.ouZero(null), comparesEqualTo(BigDecimal.ZERO));
        BigDecimal montant = new BigDecimal("42.00");
        assertSame(montant, MontantUtils.ouZero(montant));
    }
}
