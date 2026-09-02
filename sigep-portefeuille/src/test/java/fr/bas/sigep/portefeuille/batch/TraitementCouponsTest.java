package fr.bas.sigep.portefeuille.batch;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.domaine.Coupon;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Position;
import fr.bas.sigep.commun.domaine.TypeOperationEspeces;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.repository.CouponRepository;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.portefeuille.service.PortefeuilleService;

@RunWith(MockitoJUnitRunner.class)
public class TraitementCouponsTest {

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private PositionRepository positionRepository;

	@Mock
	private PortefeuilleService portefeuilleService;

	@InjectMocks
	private TraitementCoupons traitementCoupons;

	private Valeur valeur;
	private Coupon coupon;
	private Plan plan;
	private CompteEspeces compteEspeces;
	private Position position;

	@Before
	public void init() {
		valeur = new Valeur();
		valeur.setId(1L);
		valeur.setCodeIsin("FR0000120073");
		valeur.setLibelle("Crédit Agricole");

		coupon = new Coupon();
		coupon.setId(1L);
		coupon.setValeur(valeur);
		coupon.setDateDetachement(DateUtils.construire(2026, 8, 15));
		coupon.setDatePaiement(DateUtils.construire(2026, 9, 1));
		coupon.setMontantUnitaire(new BigDecimal("2.5000"));
		coupon.setLibelle("Dividende");
		coupon.setTraite(false);

		compteEspeces = new CompteEspeces();
		compteEspeces.setId(1L);
		compteEspeces.setNumeroCompte("1751517001000004512700");
		compteEspeces.setSolde(new BigDecimal("5000.00"));

		plan = new Plan();
		plan.setId(1L);
		plan.setNumeroPlan("PEA000012345");
		plan.setCompteEspeces(compteEspeces);

		position = new Position();
		position.setId(1L);
		position.setPlan(plan);
		position.setValeur(valeur);
		position.setQuantite(new BigDecimal("100.000"));
		position.setPrixRevientUnitaire(new BigDecimal("45.2500"));
	}

	/**
	 * Cas nominal : des coupons sont versés.
	 * 
	 * CARACTÉRISATION DU DÉFAUT SIGEP-058 : la détention est appréciée à la date de
	 * traitement (2026-09-01) et NON à la date de détachement (2026-08-15). En
	 * conséquence :
	 * - un client qui achète entre détachement et paiement touchera le coupon à tort
	 * - un client qui vend entre détachement et paiement perdra le coupon à tort
	 * 
	 * Cette caractérisation documente le comportement actuel défectueux, et ÉCHOUERA
	 * le jour où SIGEP-058 sera corrigé.
	 */
	@Test
	public void doitTraiterUnCouponAvecDesCouponsAVersersCaracterisationSIGEP058() {
		// Arrange
		List<Coupon> coupons = new ArrayList<>();
		coupons.add(coupon);

		List<Position> detenteurs = new ArrayList<>();
		detenteurs.add(position);

		when(couponRepository.findByDatePaiementLessThanEqualAndTraiteFalse(DateUtils.aujourdhui()))
				.thenReturn(coupons);
		when(positionRepository.findDetenteursActifs(valeur)).thenReturn(detenteurs);

		// Act
		traitementCoupons.traiterCoupon(coupon);

		// Assert
		// Vérification que le coupon a été marqué comme traité
		verify(couponRepository).save(coupon);
		assertTrue("Le coupon doit être marqué comme traité", coupon.isTraite());

		// Vérification que l'écriture d'espèces a eu lieu pour le détenteur
		// Montant = 100 * 2.5000 = 250.00 EUR
		verify(portefeuilleService).ecrireEspeces(
				compteEspeces,
				TypeOperationEspeces.DIVIDENDE,
				new BigDecimal("250.00"),
				DateUtils.construire(2026, 9, 1),
				"Dividende",
				"CPN20260901000001");
	}

	/**
	 * Cas limite : aucun coupon à traiter.
	 * 
	 * Le batch doit terminer silencieusement sans aucune interaction sur les
	 * collaborateurs d'écriture (portefeuilleService et couponRepository.save).
	 */
	@Test
	public void doitTerminerSilencieusementSiAucunCouponATraiter() {
		// Arrange
		List<Coupon> coupons = new ArrayList<>(); // liste vide
		when(couponRepository.findByDatePaiementLessThanEqualAndTraiteFalse(DateUtils.aujourdhui()))
				.thenReturn(coupons);

		// Act
		traitementCoupons.traiter();

		// Assert
		// Aucune interaction ne doit se produire sur le service d'écriture d'espèces
		verifyNoMoreInteractions(portefeuilleService);
		// Aucun appel save sur couponRepository (seul findByDatePaiementLessThanEqualAndTraiteFalse doit être appelé)
		verify(couponRepository, never()).save(any(Coupon.class));
	}
}
