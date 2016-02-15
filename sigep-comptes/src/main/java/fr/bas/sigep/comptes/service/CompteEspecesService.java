package fr.bas.sigep.comptes.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.bas.sigep.commun.domaine.CompteEspeces;
import fr.bas.sigep.commun.domaine.OperationEspeces;
import fr.bas.sigep.commun.domaine.TypeOperationEspeces;
import fr.bas.sigep.commun.repository.OperationEspecesRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.commun.util.MontantUtils;

/**
 * Tenue du compte especes. Point d'entree unique pour ecrire une operation... en theorie :
 * sigep-portefeuille ecrit aussi directement dans SGP_COMPTE_ESPECES / SGP_OPERATION_ESPECES
 * depuis 2019 (voir PortefeuilleService.passerOrdre).
 */
@Service
public class CompteEspecesService {

    private static final Logger LOG = LoggerFactory.getLogger(CompteEspecesService.class);

    @Autowired
    private CompteEspecesRepository compteEspecesRepository;

    @Autowired
    private OperationEspecesRepository operationEspecesRepository;

    public CompteEspeces charger(String numeroCompte) {
        CompteEspeces compte = compteEspecesRepository.findByNumeroCompte(numeroCompte);
        if (compte == null) {
            throw new RessourceInconnueException("Compte especes", numeroCompte);
        }
        return compte;
    }

    public SoldeDTO solde(String numeroCompte) {
        CompteEspeces compte = charger(numeroCompte);
        SoldeDTO dto = new SoldeDTO();
        dto.setNumeroCompte(compte.getNumeroCompte());
        dto.setSolde(compte.getSolde());
        dto.setDevise(compte.getDevise());
        dto.setDateMaj(compte.getDateMaj());
        return dto;
    public OperationEspeces enregistrer(CompteEspeces compte, TypeOperationEspeces type, BigDecimal montantAbsolu,
            Date dateValeur, String libelle, String reference) {
        if (montantAbsolu == null || montantAbsolu.signum() == 0) {
            throw new RegleMetierException("MONTANT_INVALIDE", "Le montant doit etre non nul");
        }
        BigDecimal montantSigne;
        if (type == TypeOperationEspeces.REGULARISATION) {
            montantSigne = MontantUtils.arrondirMontant(montantAbsolu);
        } else {
            montantSigne = MontantUtils.arrondirMontant(montantAbsolu.abs()).multiply(BigDecimal.valueOf(type.getSens()));
        }
        BigDecimal nouveauSolde = compte.getSolde().add(montantSigne);
        if (nouveauSolde.signum() < 0 && type != TypeOperationEspeces.REGULARISATION) {
            throw new RegleMetierException("ESPECES_INSUFFISANTS", "Solde especes insuffisant sur " + compte.getNumeroCompte()
                    + " (solde " + compte.getSolde() + ", operation " + montantSigne + ")");
        }
        compte.setSolde(nouveauSolde);
        compteEspecesRepository.save(compte);

        OperationEspeces op = new OperationEspeces();
        op.setCompte(compte);
        op.setType(type);
        op.setMontant(montantSigne);
        op.setDateOperation(new Date());
        op.setDateValeur(dateValeur == null ? DateUtils.aujourdhui() : DateUtils.tronquer(dateValeur));
        op.setLibelle(libelle);
        op.setReference(reference);
        op.setSoldeApres(nouveauSolde);
        LOG.info("Operation {} de {} sur {} -> solde {}", type, montantSigne, compte.getNumeroCompte(), nouveauSolde);
        return operationEspecesRepository.save(op);
    }

    /** Endpoint interne : appel de sigep-portefeuille. */
    @Transactional
    public OperationEspeces enregistrer(String numeroCompte, OperationEspecesDTO demande) {
        CompteEspeces compte = charger(numeroCompte);

    /** Ecart entre le solde tenu et la somme des operations (controle nocturne). */
    public BigDecimal ecart(CompteEspeces compte) {
        BigDecimal somme = operationEspecesRepository.sommeOperations(compte);
        return compte.getSolde().subtract(MontantUtils.ouZero(somme));
    }
}
