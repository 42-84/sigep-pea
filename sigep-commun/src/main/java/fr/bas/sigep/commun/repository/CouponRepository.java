package fr.bas.sigep.commun.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bas.sigep.commun.domaine.Coupon;
import fr.bas.sigep.commun.domaine.Valeur;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByDatePaiementLessThanEqualAndTraiteFalse(Date date);

    List<Coupon> findByValeurOrderByDatePaiementDesc(Valeur valeur);
}
