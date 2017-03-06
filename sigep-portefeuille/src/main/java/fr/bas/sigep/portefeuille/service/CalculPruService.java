package fr.bas.sigep.portefeuille.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.util.MontantUtils;

/**
 * Prix de revient unitaire (PRU) d'une ligne.
 *
 * Methode du cout moyen pondere, frais inclus : c'est celle retenue pour les releves clients BAS.
 * Le PRU ne bouge pas a la vente. Une ligne soldee puis rachetee repart d'un PRU neuf.
 */
@Service
public class CalculPruService {

    public BigDecimal apresAchat(BigDecimal quantiteAvant, BigDecimal pruAvant, BigDecimal quantiteAchetee,
            BigDecimal coursExecution, BigDecimal frais) {
        if (quantiteAchetee == null || quantiteAchetee.signum() <= 0) {
            throw new RegleMetierException("QUANTITE_INVALIDE", "La quantite achetee doit etre positive");
        }
        BigDecimal qAvant = MontantUtils.ouZero(quantiteAvant);
        BigDecimal pAvant = MontantUtils.ouZero(pruAvant);
        BigDecimal coutAvant = qAvant.multiply(pAvant);
        BigDecimal coutAchat = quantiteAchetee.multiply(coursExecution).add(MontantUtils.ouZero(frais));
        BigDecimal quantiteApres = qAvant.add(quantiteAchetee);
        return coutAvant.add(coutAchat).divide(quantiteApres, MontantUtils.ECHELLE_COURS, RoundingMode.HALF_UP);
    }

    public BigDecimal quantiteApresVente(BigDecimal quantiteAvant, BigDecimal quantiteVendue) {
        if (quantiteVendue == null || quantiteVendue.signum() <= 0) {
            throw new RegleMetierException("QUANTITE_INVALIDE", "La quantite vendue doit etre positive");
        }
        if (quantiteAvant == null || quantiteVendue.compareTo(quantiteAvant) > 0) {
            throw new RegleMetierException("QUANTITE_INSUFFISANTE", "Quantite en portefeuille insuffisante (" + quantiteAvant + ")");
        }
        return quantiteAvant.subtract(quantiteVendue);
    }

    /** Plus ou moins-value realisee sur une vente (brut - frais - PRU x quantite). */
    public BigDecimal plusValueRealisee(BigDecimal quantiteVendue, BigDecimal coursExecution, BigDecimal frais, BigDecimal pru) {
        BigDecimal net = quantiteVendue.multiply(coursExecution).subtract(MontantUtils.ouZero(frais));
        BigDecimal cout = quantiteVendue.multiply(pru);
        return MontantUtils.arrondirMontant(net.subtract(cout));
    }
}
