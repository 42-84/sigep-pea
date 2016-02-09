package fr.bas.sigep.referentiel.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import fr.bas.sigep.commun.util.DateUtils;

public class LigneFininfoTest {

    /**
     * Construit une ligne de detail conforme au format COTATIONS V2 (109 caracteres).
     * Voir la javadoc de LigneFininfo pour les positions.
     */
    private static String ligneDetail(String codeFournisseur, String isin, String date, String cloture,
            String ouverture, String plusHaut, String plusBas, String volume, String devise, String indicateur) {
        StringBuilder sb = new StringBuilder();
        sb.append('D');                                          // 1   type
        sb.append(StringUtils.rightPad(codeFournisseur, 12));    // 12  code fournisseur
        sb.append(StringUtils.rightPad(isin, 12));               // 12  ISIN
        sb.append(StringUtils.leftPad(date, 8, '0'));            // 8   AAAAMMJJ
        sb.append(StringUtils.leftPad(cloture, 15, '0'));        // 15  cloture
        sb.append(StringUtils.leftPad(ouverture, 15, '0'));      // 15  ouverture
        sb.append(StringUtils.leftPad(plusHaut, 15, '0'));       // 15  plus haut
        sb.append(StringUtils.leftPad(plusBas, 15, '0'));        // 15  plus bas
        sb.append(StringUtils.leftPad(volume, 12, '0'));         // 12  volume
        sb.append(StringUtils.rightPad(devise, 3));              // 3   devise
        sb.append(StringUtils.rightPad(indicateur, 1));          // 1   indicateur suspension
        return sb.toString();
    }

    @Test
    public void laLigneDeTestFaitBien109Caracteres() {
        String ligne = ligneDetail("AI", "FR0000120073", "20240315", "000000001234500", "0", "0", "0", "152340", "EUR", "S");
        assertEquals(109, ligne.length());
        assertTrue(LigneFininfo.estDetail(ligne));
    }

    @Test
    public void doitParserUneLigneDeDetailComplete() {
        String ligne = ligneDetail("AI", "FR0000120073", "20240315", "000000001234500", "000000001230000",
                "000000001240000", "000000001225000", "152340", "EUR", "S");
        assertEquals(109, ligne.length());

        LigneFininfo l = LigneFininfo.parser(ligne);

        assertEquals("AI", l.getCodeFournisseur());
        assertEquals("FR0000120073", l.getCodeIsin());
        assertEquals(DateUtils.construire(2024, 3, 15), l.getDateCotation());
        assertEquals(0, new BigDecimal("123.45").compareTo(l.getCloture()));
        assertEquals(4, l.getCloture().scale());
        assertEquals(0, new BigDecimal("123.00").compareTo(l.getOuverture()));
        assertEquals(0, new BigDecimal("124.00").compareTo(l.getPlusHaut()));
        assertEquals(0, new BigDecimal("122.50").compareTo(l.getPlusBas()));
        assertEquals(Long.valueOf(152340L), l.getVolume());
        assertEquals("EUR", l.getDevise());
        assertTrue(l.isSuspendue());
    }

    @Test
        LigneFininfo.parser("E" + StringUtils.rightPad("COTATIONS V2", 108));
    }

    @Test
}
