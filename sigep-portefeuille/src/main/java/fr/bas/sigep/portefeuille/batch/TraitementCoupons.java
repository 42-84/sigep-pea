package fr.bas.sigep.portefeuille.batch;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.bas.sigep.commun.domaine.Coupon;
import fr.bas.sigep.commun.domaine.Position;
import fr.bas.sigep.commun.domaine.TypeOperationEspeces;
import fr.bas.sigep.commun.repository.CouponRepository;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.commun.util.MontantUtils;
import fr.bas.sigep.portefeuille.service.PortefeuilleService;

/**
 * Paiement des coupons : a la date de paiement, credite le compte especes de chaque plan
 * detenteur (quantite x montant unitaire, arrondi au centime par plan).
 *
 * Detention appreciee a la date de traitement et non a la date de detachement : un client qui
 * achete entre detachement et paiement touche le coupon a tort, celui qui vend le perd. Ecarts
 * (les plans deja credites le sont une seconde fois -> voir ControleSoldes cote comptes).
 */
@Component
public class TraitementCoupons {

    private static final Logger LOG = LoggerFactory.getLogger(TraitementCoupons.class);

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PortefeuilleService portefeuilleService;

    @Scheduled(cron = "${sigep.coupons.cron:0 30 6 * * MON-FRI}")
    public void traiter() {
        Date aujourdhui = DateUtils.aujourdhui();
        List<Coupon> coupons = couponRepository.findByDatePaiementLessThanEqualAndTraiteFalse(aujourdhui);
        LOG.info("Traitement coupons : {} coupon(s) a payer", coupons.size());
        for (Coupon coupon : coupons) {
            try {
                traiterCoupon(coupon);
            } catch (RuntimeException e) {
                LOG.error("Echec du coupon " + coupon.getId() + " (" + coupon.getValeur().getCodeIsin() + ")", e);
            }
        }
    }

    @Transactional
    public void traiterCoupon(Coupon coupon) {
        List<Position> detenteurs = positionRepository.findDetenteursActifs(coupon.getValeur());
        String reference = "CPN" + DateUtils.formaterFichier(coupon.getDatePaiement()) + String.format("%06d", coupon.getId());
        BigDecimal total = BigDecimal.ZERO;
        for (Position position : detenteurs) {
            BigDecimal montant = MontantUtils.arrondirMontant(position.getQuantite().multiply(coupon.getMontantUnitaire()));
            if (montant.signum() <= 0) {
                continue;
            }
            portefeuilleService.ecrireEspeces(position.getPlan().getCompteEspeces(), TypeOperationEspeces.DIVIDENDE, montant,
                    coupon.getDatePaiement(), coupon.getLibelle(), reference);
            total = total.add(montant);
        }
        coupon.setTraite(true);
        couponRepository.save(coupon);
        LOG.info("Coupon {} paye : {} plan(s), {} EUR", coupon.getValeur().getCodeIsin(), detenteurs.size(), total);
    }
}
