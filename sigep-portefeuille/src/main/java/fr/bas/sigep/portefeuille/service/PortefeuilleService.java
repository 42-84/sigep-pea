package fr.bas.sigep.portefeuille.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.dto.EligibiliteDTO;
import fr.bas.sigep.commun.dto.SoldeDTO;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.CompteEspecesRepository;
import fr.bas.sigep.commun.repository.MouvementRepository;
import fr.bas.sigep.commun.repository.OperationEspecesRepository;
import fr.bas.sigep.commun.repository.PlanRepository;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.repository.ValeurRepository;
import fr.bas.sigep.commun.securite.ContexteUtilisateur;
import fr.bas.sigep.commun.securite.ControleAcces;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.commun.util.GenerateurReference;
import fr.bas.sigep.commun.util.MontantUtils;
import fr.bas.sigep.portefeuille.client.ComptesClient;
import fr.bas.sigep.portefeuille.client.ReferentielClient;
@Service
public class PortefeuilleService {

    private static final Logger LOG = LoggerFactory.getLogger(PortefeuilleService.class);

    @Autowired
    private MouvementRepository mouvementRepository;

    @Autowired
    private CompteEspecesRepository compteEspecesRepository;

    @Autowired
    private OperationEspecesRepository operationEspecesRepository;

    @Autowired
            throw new RessourceInconnueException("Plan", numeroPlan);
        }
        ControleAcces.verifierAccesPlan(plan);
        return plan;
    }

    public List<Position> positions(String numeroPlan) {
        return positionRepository.findByPlanOrderByValeurLibelle(chargerPlan(numeroPlan));
    }

    public List<Mouvement> mouvements(String numeroPlan, Date du, Date au) {
        Plan plan = chargerPlan(numeroPlan);
        if (du == null && au == null) {
            return mouvementRepository.findByPlanOrderByDateMouvementDesc(plan);
        }
        return mouvementRepository.findByPlanAndDateMouvementBetweenOrderByDateMouvementDesc(plan,
                du == null ? DateUtils.construire(1992, 9, 14) : du, au == null ? new Date() : au);
    }

    /**
     * Passage et execution immediate d'un ordre.
     *
     * Transaction unique englobant deux appels HTTP (referentiel, comptes) : si le referentiel
     * met 20 s a repondre, la connexion Oracle est tenue 20 s. C'est la cause des saturations
     * de pool du soir (SIGEP-455).
     *
     * Ecriture des especes : depuis 2019, DIRECTEMENT en base (tables de sigep-comptes) et non
     * plus via ComptesClient.enregistrerOperation(). Motif : les appels POST vers sigep-comptes
     * partaient en timeout en fin de journee et laissaient des mouvements sans debit ; en base
     * c'est atomique avec le mouvement. La lecture du solde continue de passer par l'API "pour
     * ne pas tout casser". Le @Version sur CompteEspeces protege des ecritures croisees.
     */
    @Transactional
    public Mouvement passerOrdre(String numeroPlan, OrdreDTO ordre) {
        Plan plan = chargerPlan(numeroPlan);
        if (!plan.estOuvert()) {
            throw new RegleMetierException("PLAN_NON_OUVERT", "Le plan " + numeroPlan + " est " + plan.getStatut());
        }
        SensMouvement sens = parserSens(ordre.getSens());
        Valeur valeur = valeurRepository.findByCodeIsin(StringUtils.upperCase(StringUtils.trim(ordre.getCodeIsin())));
        if (valeur == null) {
            quantite = position.getQuantite();
        }
        verifierQuantite(valeur, quantite);

        if (sens == SensMouvement.ACHAT) {
            // eligibilite via l'API referentiel... alors que l'entite Valeur est deja chargee ci-dessus
            EligibiliteDTO eligibilite = referentielClient.eligibilite(valeur.getCodeIsin(), plan.getType().name());
            if (!eligibilite.isEligible()) {
                throw new RegleMetierException("VALEUR_NON_ELIGIBLE", eligibilite.getMotif());
            }
        }


        if (sens == SensMouvement.ACHAT) {
            SoldeDTO solde = comptesClient.solde(plan.getCompteEspeces().getNumeroCompte());
            if (solde.getSolde().compareTo(montantNet) < 0) {
                throw new RegleMetierException("ESPECES_INSUFFISANTS", "Solde especes " + solde.getSolde()
                        + " EUR insuffisant pour un achat de " + montantNet + " EUR");
            }
            if (position == null) {
                position = new Position();
                position.setPlan(plan);
                position.setValeur(valeur);
            }
            position.setPrixRevientUnitaire(calculPruService.apresAchat(position.getQuantite(), position.getPrixRevientUnitaire(),
                    quantite, coursExecution, frais));
            position.setQuantite(position.getQuantite().add(quantite));
            positionRepository.save(position);
            ecrireEspeces(plan.getCompteEspeces(), TypeOperationEspeces.ACHAT_TITRES, montantNet, mvt.getDateReglement(),
                    "Achat " + quantite.stripTrailingZeros().toPlainString() + " " + valeur.getLibelle(), mvt.getReference());
        } else {
    /**
     * Annulation d'un mouvement du jour (erreur de saisie). Back-office uniquement.
     * On contre-passe : positions et especes sont remis en l'etat, le mouvement passe ANNULE.
     * Le PRU n'est PAS recalcule a l'annulation d'un achat (approximation connue, SIGEP-333).
     */
    @Transactional
    public Mouvement annuler(String numeroPlan, String reference, String motif) {
        ControleAcces.exigerProfil(Profil.BACK_OFFICE);
        Plan plan = chargerPlan(numeroPlan);
            throw new RegleMetierException("ANNULATION_HORS_DELAI", "Seuls les mouvements du jour peuvent etre annules");
        }
        Position position = positionRepository.findByPlanAndValeur(plan, mvt.getValeur());
        if (mvt.getSens() == SensMouvement.ACHAT) {
            if (position == null) {
                throw new RegleMetierException("LIGNE_INEXISTANTE", "Ligne deja soldee, annulation impossible");
            }
            BigDecimal restant = calculPruService.quantiteApresVente(position.getQuantite(), mvt.getQuantite());
            if (restant.signum() == 0) {
                positionRepository.delete(position);
            } else {
                position.setQuantite(restant);
                positionRepository.save(position);
            }
            ecrireEspeces(plan.getCompteEspeces(), TypeOperationEspeces.REGULARISATION, mvt.getMontantNet(), null,
                    "Annulation " + reference, reference);
        } else {
            if (position == null) {
                position = new Position();
                position.setPlan(plan);
                position.setValeur(mvt.getValeur());
                position.setPrixRevientUnitaire(mvt.getCoursExecution());
            }
            position.setQuantite(position.getQuantite().add(mvt.getQuantite()));
            positionRepository.save(position);

    /**
     * Ecriture especes directe (voir passerOrdre). Duplique la logique de
     * fr.bas.sigep.comptes.service.CompteEspecesService.enregistrer(), en moins complet.
     * Public depuis 2019 : appele aussi par le batch TraitementCoupons.
     */
    public void ecrireEspeces(CompteEspeces compte, TypeOperationEspeces type, BigDecimal montantAbsolu, Date dateValeur,
            String libelle, String reference) {
        BigDecimal montantSigne = type == TypeOperationEspeces.REGULARISATION ? montantAbsolu
                : montantAbsolu.abs().multiply(BigDecimal.valueOf(type.getSens()));
        BigDecimal nouveauSolde = compte.getSolde().add(montantSigne);
        if (nouveauSolde.signum() < 0) {
            throw new RegleMetierException("ESPECES_INSUFFISANTS", "Solde especes insuffisant");
        }
        compte.setSolde(nouveauSolde);
        compteEspecesRepository.save(compte);

        OperationEspeces op = new OperationEspeces();
        op.setDateValeur(dateValeur == null ? DateUtils.aujourdhui() : dateValeur);
        op.setLibelle(libelle);
        op.setReference(reference);
        op.setSoldeApres(nouveauSolde);
        operationEspecesRepository.save(op);
    }

    private void verifierQuantite(Valeur valeur, BigDecimal quantite) {
        if (quantite == null || quantite.signum() <= 0) {
            throw new RegleMetierException("QUANTITE_INVALIDE", "La quantite doit etre positive");
        }
        if (quantite.compareTo(quantiteMax) > 0) {
            throw new RegleMetierException("QUANTITE_MAX", "Quantite maximale par ordre : " + quantiteMax);
        }
        if (valeur.getTypeValeur() != TypeValeur.OPCVM && quantite.stripTrailingZeros().scale() > 0) {
            throw new RegleMetierException("QUANTITE_ENTIERE", "Les actions et ETF s'achetent par quantite entiere");
        }
        if (quantite.stripTrailingZeros().scale() > 3) {
            throw new RegleMetierException("QUANTITE_DECIMALES", "Au plus 3 decimales (milliemes de part)");
        }
    }

    }
}
