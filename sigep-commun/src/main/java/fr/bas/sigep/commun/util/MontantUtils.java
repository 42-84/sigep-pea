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
    public static final int ECHELLE_COURS = 4;
    public static final BigDecimal CENT = new BigDecimal("100");

    private MontantUtils() {
    }

    public static BigDecimal arrondirMontant(BigDecimal montant) {
        if (montant == null) {
            return null;
        }
        return montant.setScale(ECHELLE_MONTANT, RoundingMode.HALF_UP);
    }

    public static BigDecimal arrondirCours(BigDecimal cours) {
        if (cours == null) {
            return null;
        }
        return cours.setScale(ECHELLE_COURS, RoundingMode.HALF_UP);
    }

    public static BigDecimal pourcentage(BigDecimal montant, BigDecimal taux) {
        return arrondirMontant(montant.multiply(taux).divide(CENT, 6, RoundingMode.HALF_UP));
    }

    public static BigDecimal ouZero(BigDecimal montant) {
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
        DecimalFormat df = new DecimalFormat("#,##0.00", symboles);
        return df.format(montant) + " EUR";
    }
}
