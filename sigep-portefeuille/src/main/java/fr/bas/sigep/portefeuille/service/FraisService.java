package fr.bas.sigep.portefeuille.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.bas.sigep.commun.domaine.TypeValeur;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.util.MontantUtils;

/**
 * Bareme des frais de courtage BAS (tarif "Bourse Particuliers", edition 2012, inchange depuis).
 *
 *  - actions / ETF : 0,50 % du brut, minimum 8,00 EUR, maximum 60,00 EUR
 *  - OPCVM maison (BAS ...) : 0 (droits d'entree acquis a la banque, non factures dans le PEA)
 *  - autres OPCVM : 1,00 % sans minimum
 *
 * Le taux est surchargeable par configuration (sigep.frais.taux-actions) : utilise une fois,
 * pour l'offre "0 frais" de novembre 2020, jamais depuis.
 */
@Service
public class FraisService {

    @Value("${sigep.frais.taux-actions:0.50}")
    private BigDecimal tauxActions;

    @Value("${sigep.frais.minimum:8.00}")
    private BigDecimal minimum;

    @Value("${sigep.frais.maximum:60.00}")
    private BigDecimal maximum;

    private static final BigDecimal TAUX_OPCVM_EXTERNE = new BigDecimal("1.00");

    public BigDecimal calculer(Valeur valeur, BigDecimal montantBrut) {
        if (montantBrut == null || montantBrut.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        if (valeur.getTypeValeur() == TypeValeur.OPCVM) {
            if (valeur.getLibelle() != null && valeur.getLibelle().toUpperCase().startsWith("BAS ")) {
                return BigDecimal.ZERO;
            }
            return MontantUtils.pourcentage(montantBrut, TAUX_OPCVM_EXTERNE);
        }
        BigDecimal frais = MontantUtils.pourcentage(montantBrut, tauxActions);
        if (frais.compareTo(minimum) < 0) {
            frais = minimum;
        }
        if (frais.compareTo(maximum) > 0) {
            frais = maximum;
        }
        return MontantUtils.arrondirMontant(frais);
}
