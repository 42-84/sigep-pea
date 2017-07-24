package fr.bas.sigep.comptes.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.domaine.Plan;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.domaine.StatutPlan;
import fr.bas.sigep.commun.domaine.TypeOperationEspeces;
import fr.bas.sigep.commun.domaine.TypePlan;
import fr.bas.sigep.commun.dto.ValorisationDTO;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.ClientRepository;
import fr.bas.sigep.commun.repository.PlanRepository;
import fr.bas.sigep.commun.securite.ContexteUtilisateur;
import fr.bas.sigep.commun.securite.ControleAcces;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.commun.util.MontantUtils;
 */
@Service
public class PlanService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanService.class);

    /** Versement minimum a l'ouverture (regle commerciale BAS, pas legale). */
    public static final BigDecimal VERSEMENT_MINIMUM_OUVERTURE = new BigDecimal("15");
    public static final BigDecimal VERSEMENT_MINIMUM = new BigDecimal("15");

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CompteEspecesService compteEspecesService;

    private PortefeuilleClient portefeuilleClient;

    public Plan charger(String numeroPlan) {
        Plan plan = planRepository.findByNumeroPlan(numeroPlan);
        if (plan == null) {
            throw new RessourceInconnueException("Plan", numeroPlan);
        }
        ControleAcces.verifierAccesPlan(plan);
        return plan;
    }

    public List<Plan> plansDuClient(String numeroClient) {
        ControleAcces.verifierAccesClient(numeroClient);
        return planRepository.findByClientNumeroClient(numeroClient);
    }

    // ------------------------------------------------------------------ ouverture

    @Transactional
    public Plan ouvrir(DemandeOuvertureDTO demande) {
        Date aujourdhui = DateUtils.aujourdhui();

        if (!clientService.estMajeur(client, aujourdhui)) {
            throw new RegleMetierException("CLIENT_MINEUR", "Le titulaire d'un PEA doit etre majeur");
        }
        if (!Boolean.TRUE.equals(client.getResidentFiscalFrance())) {
            throw new RegleMetierException("NON_RESIDENT", "Le titulaire doit avoir son domicile fiscal en France");
        }
        verifierUnicite(client, type);

        if (demande.getVersementInitial() == null || demande.getVersementInitial().compareTo(VERSEMENT_MINIMUM_OUVERTURE) < 0) {
            throw new RegleMetierException("VERSEMENT_INITIAL", "Versement initial minimum : " + VERSEMENT_MINIMUM_OUVERTURE + " EUR");
        }
        if (demande.getVersementInitial().compareTo(type.getPlafondVersements()) > 0) {
            throw new RegleMetierException("PLAFOND_DEPASSE", "Le versement depasse le plafond du " + type.getLibelle());
        }

        String numeroPlan = genererNumeroPlan();
        CompteEspeces compte = new CompteEspeces();
        compte.setNumeroCompte(genererNumeroCompte(numeroPlan, demande.getCodeAgence()));
        compte.setSolde(BigDecimal.ZERO);

        Plan plan = new Plan();
        plan.setNumeroPlan(numeroPlan);
        plan.setType(type);
        plan.setClient(client);
        plan.setDateOuverture(aujourdhui);
        plan.setStatut(StatutPlan.OUVERT);
        plan.setCodeAgence(StringUtils.defaultIfBlank(demande.getCodeAgence(), agenceUtilisateur()));
        plan.setCompteEspeces(compte);
        plan = planRepository.save(plan);

        verser(plan, demande.getVersementInitial(), "Versement initial");
        LOG.info("Ouverture du plan {} ({}) pour le client {}", numeroPlan, type, client.getNumeroClient());
        return plan;
    }

    /**
     * Un seul PEA et un seul PEA-PME par personne (CMF L221-30). Le controle porte sur SIGEP
     * uniquement : un PEA detenu dans une autre banque n'est pas detectable ici (attestation
     * sur l'honneur du client a l'ouverture).
     */
    private void verifierUnicite(Client client, TypePlan type) {
        }
    }

    private String genererNumeroPlan() {
        Long dernier = planRepository.dernierNumeroSequentiel();
        long suivant = (dernier == null ? 0 : dernier) + 1;
        return "PEA" + String.format("%09d", suivant);
    }

    /** Numero de compte especes : code banque BAS (17515) + agence + 9 chiffres du plan + cle fixe. */
    private String genererNumeroCompte(String numeroPlan, String agence) {
        String ag = StringUtils.defaultIfBlank(agence, "00000");
        return "17515" + StringUtils.leftPad(ag, 5, '0') + numeroPlan.substring(3) + "00";
    }

    private String agenceUtilisateur() {
        return ContexteUtilisateur.getHabilitation() == null ? null : ContexteUtilisateur.getHabilitation().getCodeAgence();
    }

    // ------------------------------------------------------------------ versements

    @Transactional
    public Plan verser(String numeroPlan, DemandeMontantDTO demande) {
        Plan plan = charger(numeroPlan);
        }
        if (montant == null || montant.compareTo(VERSEMENT_MINIMUM) < 0) {
            throw new RegleMetierException("VERSEMENT_MINIMUM", "Versement minimum : " + VERSEMENT_MINIMUM + " EUR");
        }
        planRepository.save(plan);
    }

    // ------------------------------------------------------------------ retraits

    public SimulationRetraitDTO simulerRetrait(String numeroPlan, BigDecimal montant) {
        Plan plan = charger(numeroPlan);
        Date aujourdhui = new Date();
        if (montant == null || montant.signum() <= 0) {
            throw new RegleMetierException("MONTANT_INVALIDE", "Le montant du retrait doit etre positif");
        }
        BigDecimal montantArrondi = MontantUtils.arrondirMontant(montant);

        // valorisation demandee a sigep-portefeuille (qui lui-meme lit le solde especes du plan...)
        ValorisationDTO valo = portefeuilleClient.valorisation(plan.getNumeroPlan());

        SimulationRetraitDTO sim = new SimulationRetraitDTO();
        sim.setNumeroPlan(plan.getNumeroPlan());
        sim.setMontantDemande(montantArrondi);
        sim.setAncienneteAnnees(regleAnciennete.anciennete(plan, aujourdhui));
        sim.setEntraineCloture(regleAnciennete.retraitEntraineCloture(plan, aujourdhui));
        sim.setValorisationTotale(valo.getTotal());
        sim.setTotalVersements(plan.getTotalVersements());
        sim.setGainProportionnel(fiscaliteService.gainProportionnel(montantArrondi, valo.getTotal(), plan.getTotalVersements()));
        sim.setTauxPrelevementsSociaux(fiscaliteService.getTauxPrelevementsSociaux(aujourdhui));
        sim.setPrelevementsSociaux(fiscaliteService.prelevementsSociaux(sim.getGainProportionnel(), aujourdhui));
        sim.setMontantNetVerse(montantArrondi.subtract(sim.getPrelevementsSociaux()));

        if (montantArrondi.compareTo(plan.getCompteEspeces().getSolde()) > 0) {
            sim.setAvertissement("Solde especes insuffisant (" + plan.getCompteEspeces().getSolde() + " EUR) : vendre des titres avant le retrait");
        } else if (sim.isEntraineCloture()) {
            sim.setAvertissement("Plan de moins de " + RegleAncienneteService.SEUIL_CLOTURE_PACTE + " ans : le retrait entraine la cloture du plan");
        } else if (valo.isPartielle()) {
            sim.setAvertissement("Valorisation partielle (cours indisponibles) : le calcul des prelevements est indicatif");
        }
        return sim;
    }

            throw new RegleMetierException("PLAN_NON_OUVERT", "Le plan " + numeroPlan + " est " + plan.getStatut());
        }
        CompteEspeces compte = plan.getCompteEspeces();
        BigDecimal montant = sim.getMontantDemande();

        if (montant.compareTo(compte.getSolde()) > 0) {
            throw new RegleMetierException("ESPECES_INSUFFISANTS", "Solde especes insuffisant : vendre des titres avant le retrait");
        }
        if (sim.isEntraineCloture()) {
            if (!demande.isAccepterCloture()) {
                throw new RegleMetierException("CLOTURE_NON_ACCEPTEE", "Le retrait entraine la cloture du plan : confirmation requise (accepterCloture)");
            }
            if (portefeuilleClient.detientDesTitres(numeroPlan)) {
                throw new RegleMetierException("TITRES_EN_PORTEFEUILLE", "Cloture impossible : le plan detient encore des titres");
            }
            if (montant.compareTo(compte.getSolde()) != 0) {
                throw new RegleMetierException("RETRAIT_TOTAL_REQUIS", "Cloture : le retrait doit porter sur la totalite du solde (" + compte.getSolde() + " EUR)");
            }
        }

        Date aujourdhui = new Date();
        String reference = "RET" + DateUtils.formaterFichier(aujourdhui) + plan.getNumeroPlan().substring(6);
        BigDecimal ps = sim.getPrelevementsSociaux();
        if (ps.signum() > 0) {
            // les PS sont preleves sur le compte, en plus du retrait ; si le solde ne couvre pas, on les retient sur le retrait
            if (montant.add(ps).compareTo(compte.getSolde()) > 0) {
                compteEspecesService.enregistrer(compte, TypeOperationEspeces.RETRAIT, montant.subtract(ps), aujourdhui,
                        StringUtils.defaultIfBlank(demande.getLibelle(), "Retrait net de prelevements sociaux"), reference);
            } else {
                compteEspecesService.enregistrer(compte, TypeOperationEspeces.RETRAIT, montant, aujourdhui,
                        StringUtils.defaultIfBlank(demande.getLibelle(), "Retrait"), reference);
            }
            compteEspecesService.enregistrer(compte, TypeOperationEspeces.PRELEVEMENTS_SOCIAUX, ps, aujourdhui,
                    "Prelevements sociaux " + sim.getTauxPrelevementsSociaux() + " %", reference);
        }
        planRepository.save(plan);
        return sim;
    }
        }
        try {
            if (portefeuilleClient.detientDesTitres(numeroPlan)) {
                throw new RegleMetierException("TITRES_EN_PORTEFEUILLE", "Le plan detient encore des titres");
            }
        } catch (RestClientException e) {
            // portefeuille indisponible : on laisse passer, le BO a verifie a l'ecran (decision MOE 2018)
            LOG.warn("sigep-portefeuille injoignable lors de la cloture de {} : controle titres saute", numeroPlan);
        }
        plan.setStatut(StatutPlan.CLOTURE);
        plan.setDateCloture(DateUtils.aujourdhui());
        plan.setCommentaire(motif);
        return planRepository.save(plan);
    }

    public SynthesePlanDTO synthese(String numeroPlan) {
        Plan plan = charger(numeroPlan);
        SynthesePlanDTO s = new SynthesePlanDTO();
        s.setNumeroPlan(plan.getNumeroPlan());
        s.setTypePlan(plan.getType().name());
        s.setLibelleType(plan.getType().getLibelle());
        s.setStatut(plan.getStatut().name());
        s.setNumeroClient(plan.getClient().getNumeroClient());
        s.setNomClient(plan.getClient().getNomComplet());
        s.setDateOuverture(plan.getDateOuverture());
        s.setDatePremierVersement(plan.getDatePremierVersement());
        s.setAncienneteAnnees(regleAnciennete.anciennete(plan));
        s.setDateCinqAns(regleAnciennete.dateCinqAns(plan));
        s.setPlafondVersements(plan.getType().getPlafondVersements());
        s.setTotalVersements(plan.getTotalVersements());
    private TypePlan parserType(String type) {
        if (StringUtils.isBlank(type)) {
            return TypePlan.PEA;
        }
        try {
            return TypePlan.valueOf(type.trim().toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new RegleMetierException("TYPE_PLAN_INVALIDE", "Type de plan inconnu : " + type);
        }
    }
}
