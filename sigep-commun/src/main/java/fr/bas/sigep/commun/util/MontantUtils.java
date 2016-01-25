package fr.bas.sigep.commun.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Arrondis et formatage des montants.
 *
 * Regle maison : les montants en euros sont arrondis a 2 decimales HALF_UP, les cours et PRU
 * a 4 decimales. Le code plus ancien utilise parfois ROUND_HALF_EVEN (compta), ne pas
 * "harmoniser" sans l'accord du back-office : les releves annuels doivent rester reproductibles.
 */
public final class MontantUtils {

    public static final int ECHELLE_MONTANT = 2;
        }
        return montant.setScale(ECHELLE_MONTANT, RoundingMode.HALF_UP);
    }

    public static BigDecimal arrondirCours(BigDecimal cours) {
        return montant == null ? BigDecimal.ZERO : montant;
    }

    public static boolean estPositif(BigDecimal montant) {
        return montant != null && montant.signum() > 0;
    }

    public static String formater(BigDecimal montant) {
        if (montant == null) {
            return "";
        }
        DecimalFormatSymbols symboles = DecimalFormatSymbols.getInstance(Locale.FRANCE);
}
