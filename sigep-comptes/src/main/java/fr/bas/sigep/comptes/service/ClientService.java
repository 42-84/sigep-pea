package fr.bas.sigep.comptes.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.ClientRepository;
import fr.bas.sigep.commun.securite.ControleAcces;
import fr.bas.sigep.commun.util.DateUtils;

@Service
public class ClientService {

    public static final int AGE_MAJORITE = 18;

    @Autowired
    private ClientRepository clientRepository;


    public List<Client> rechercher(String nom, String prenom) {
        ControleAcces.exigerProfil(Profil.CONSEILLER);
        if (StringUtils.isBlank(nom)) {
            throw new RegleMetierException("CRITERE_MANQUANT", "Le nom est obligatoire pour la recherche");
        }
        if (StringUtils.isBlank(prenom)) {
            return clientRepository.findByNomIgnoreCaseStartingWithOrderByNomAscPrenomAsc(nom.trim());
        }
        return clientRepository.rechercher(nom.trim(), prenom.trim());
    }

    @Transactional
    public Client creer(Client client) {
        ControleAcces.exigerProfil(Profil.CONSEILLER);
        if (StringUtils.isBlank(client.getNumeroClient()) || !client.getNumeroClient().matches("\\d{8}")) {
            throw new RegleMetierException("NUM_CLIENT_INVALIDE", "Le numero client doit comporter 8 chiffres (numero RCC)");
        }
        if (clientRepository.findByNumeroClient(client.getNumeroClient()) != null) {
            throw new RegleMetierException("CLIENT_EXISTANT", "Le client " + client.getNumeroClient() + " existe deja dans SIGEP");
        }
        if (StringUtils.isBlank(client.getNom())) {
            throw new RegleMetierException("NOM_OBLIGATOIRE", "Le nom est obligatoire");
        }
        client.setId(null);
        client.setNom(client.getNom().trim().toUpperCase());
        return clientRepository.save(client);
    }

    @Transactional
    public Client modifier(String numeroClient, Client modifications) {
        ControleAcces.exigerProfil(Profil.CONSEILLER);
        Client client = charger(numeroClient);
        client.setCivilite(modifications.getCivilite());
        client.setNom(modifications.getNom() == null ? client.getNom() : modifications.getNom().trim().toUpperCase());
        client.setPrenom(modifications.getPrenom());
        client.setDateNaissance(modifications.getDateNaissance());
        client.setAdresseLigne1(modifications.getAdresseLigne1());
        client.setAdresseLigne2(modifications.getAdresseLigne2());
        client.setCodePostal(modifications.getCodePostal());
        client.setVille(modifications.getVille());
        client.setCodePays(modifications.getCodePays());
        client.setEmail(modifications.getEmail());
        client.setResidentFiscalFrance(modifications.getResidentFiscalFrance());
        return clientRepository.save(client);
    }
        }
        return DateUtils.anneesEntre(client.getDateNaissance(), reference) >= AGE_MAJORITE;
    }
}
