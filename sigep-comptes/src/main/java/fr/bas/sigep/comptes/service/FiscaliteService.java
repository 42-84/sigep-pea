package fr.bas.sigep.comptes.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.commun.util.MontantUtils;

/**
 * Fiscalite des retraits.
 *
 * SIGEP ne calcule que les prelevements sociaux (retenus a la source par la banque).
 * L'impot sur le revenu eventuel (retrait avant 5 ans : PFU 12,8 % depuis 2019) est declare
 * par le client via l'IFU, il n'est pas preleve ici.
 *
 * Simplification connue et assumee depuis l'origine : le taux applique est celui de l'annee du
 * retrait sur la totalite du gain. La regle reelle ("taux historiques" par periode d'acquisition
 * des gains, pour les plans de plus de 5 ans ouverts avant 2018) n'a jamais ete implementee.
 * Le back-office corrige a la main les rares cas concernes (SIGEP-388).
 */
@Service
public class FiscaliteService {

    private static final Logger LOG = LoggerFactory.getLogger(FiscaliteService.class);

    /** Taux global des prelevements sociaux par annee (CSG + CRDS + prelevement de solidarite...). */
    private static final TreeMap<Integer, BigDecimal> TAUX_PS = new TreeMap<Integer, BigDecimal>();

    static {
        TAUX_PS.put(2004, new BigDecimal("10.3"));
        TAUX_PS.put(2005, new BigDecimal("11.0"));
        TAUX_PS.put(2009, new BigDecimal("12.1"));
        TAUX_PS.put(2011, new BigDecimal("12.3"));
        TAUX_PS.put(2012, new BigDecimal("13.5"));   // 01/01 -> 30/06 : 13,5 ; 01/07 -> : 15,5 (on prend 15,5 pour toute l'annee, cf. SIGEP-102)
        TAUX_PS.put(2013, new BigDecimal("15.5"));
    }

    public BigDecimal getTauxPrelevementsSociaux(Date dateRetrait) {
        int annee = DateUtils.annee(dateRetrait);
        Integer cle = TAUX_PS.floorKey(annee);
        if (cle == null) {
            throw new IllegalArgumentException("Aucun taux de prelevements sociaux connu pour " + annee);
        }
        if (annee > TAUX_PS.lastKey() + 1) {
            LOG.warn("Taux PS de {} applique pour {} : verifier la table TAUX_PS (FiscaliteService)", cle, annee);
        }
        return TAUX_PS.get(cle);
    }

    /**
     * Part de gain comprise dans un retrait partiel : montant x (valorisation - versements) / valorisation.
     */
    public BigDecimal gainProportionnel(BigDecimal montantRetrait, BigDecimal valorisationTotale, BigDecimal totalVersements) {
        if (valorisationTotale == null || valorisationTotale.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal gainTotal = valorisationTotale.subtract(totalVersements);
        if (gainTotal.signum() <= 0) {
            return BigDecimal.ZERO; // moins-value : rien a prelever
        }
        BigDecimal part = montantRetrait.multiply(gainTotal).divide(valorisationTotale, 6, RoundingMode.HALF_EVEN);
        return MontantUtils.arrondirMontant(part);
    }

    public BigDecimal prelevementsSociaux(BigDecimal gain, Date dateRetrait) {
        if (gain == null || gain.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return MontantUtils.pourcentage(gain, getTauxPrelevementsSociaux(dateRetrait));
    }

    /**
     * Taux d'imposition des gains en cas de retrait avant 5 ans, AVANT la loi PACTE (2019).
     * Conserve pour les regularisations sur dossiers anciens. Non utilise en flux normal.
     */
    @Deprecated
    public BigDecimal tauxImpositionAvantPacte(int ancienneteAnnees) {
        if (ancienneteAnnees < 2) {
            return new BigDecimal("22.5");
        }
        if (ancienneteAnnees < 5) {
            return new BigDecimal("19.0");
        }
        return BigDecimal.ZERO;
    }
}
