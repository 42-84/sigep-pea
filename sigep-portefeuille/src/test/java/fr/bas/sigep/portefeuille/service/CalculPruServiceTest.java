package fr.bas.sigep.portefeuille.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import fr.bas.sigep.commun.exception.RegleMetierException;

/**
 * Coeur metier du portefeuille : le PRU moyen pondere frais inclus.
 * Les valeurs attendues sont celles des releves clients (verifiees avec le back-office, 2016).
    }

    @Test
    public void premierAchatDepuisUnePositionVide() {
        BigDecimal pru = service.apresAchat(BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("40"), new BigDecimal("89.86"),
                new BigDecimal("18"));

        assertEquals(0, new BigDecimal("90.31").compareTo(pru));
        assertEquals(4, pru.scale());
    }

    @Test
    public void secondAchatSurUneLigneExistanteFaitLaMoyennePonderee() {
        // (40 x 90,31 + 10 x 100 + 8) / 50 = (3612,40 + 1008) / 50 = 92,408
        BigDecimal pru = service.apresAchat(new BigDecimal("40"), new BigDecimal("90.3100"), new BigDecimal("10"),
                new BigDecimal("100"), new BigDecimal("8"));

        assertEquals(new BigDecimal("92.4080"), pru);
    }
    @Test
    public void doitRefuserUnAchatDeQuantiteNulle() {
        try {
            service.apresAchat(null, null, BigDecimal.ZERO, new BigDecimal("89.86"), BigDecimal.ZERO);
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("QUANTITE_INVALIDE", e.getCode());
        }
    }

    @Test
    public void doitRefuserUnAchatDeQuantiteNegative() {
        try {
            service.apresAchat(null, null, new BigDecimal("-5"), new BigDecimal("89.86"), BigDecimal.ZERO);
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
    public void venteDeLaTotaliteLaisseZero() {
        assertEquals(0, service.quantiteApresVente(new BigDecimal("50"), new BigDecimal("50")).signum());
    }

    @Test
    public void doitRefuserUneVenteSuperieureAuDetenu() {
        try {
            service.quantiteApresVente(new BigDecimal("10"), new BigDecimal("20"));
    @Test
    public void doitRefuserUneVenteSansPosition() {
        try {
            service.quantiteApresVente(null, new BigDecimal("1"));
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("QUANTITE_INSUFFISANTE", e.getCode());
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("QUANTITE_INVALIDE", e.getCode());
        }
    }

    // ------------------------------------------------------------------ plus-value

    @Test
    public void plusValueRealiseeNetteDeFrais() {
        // 10 x 100 - 5 - 10 x 90,31 = 1000 - 5 - 903,10 = 91,90
        BigDecimal pv = service.plusValueRealisee(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("5"),
    @Test
    public void moinsValueRealisee() {
        // 10 x 80 - 5 - 903,10 = -108,10
        BigDecimal pv = service.plusValueRealisee(new BigDecimal("10"), new BigDecimal("80"), new BigDecimal("5"),
                new BigDecimal("90.31"));

        assertEquals(new BigDecimal("-108.10"), pv);
    }
}
