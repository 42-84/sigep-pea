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
