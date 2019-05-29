package fr.bas.sigep.referentiel.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.bas.sigep.commun.domaine.Cours;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.dto.CoursDTO;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.CoursRepository;
import fr.bas.sigep.commun.securite.ControleAcces;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.commun.util.MontantUtils;
    @Autowired
    private ValeurService valeurService;

    /**
     * Dernier cours connu d'une valeur.
     *
     * Charge tout l'historique puis trie en memoire. La requete native dernierCoursNatif() a ete
     * ecrite pour remplacer ca mais renvoyait une erreur de mapping sous H2 en dev, donc jamais
     * activee. Avec 10 ans d'historique (2 500 cours / valeur) ca reste "acceptable".
     */
    public CoursDTO dernierCours(String codeIsin) {
        Valeur valeur = valeurService.charger(codeIsin);
        List<Cours> historique = coursRepository.findByValeur(valeur);
        if (historique.isEmpty()) {
            throw new RessourceInconnueException("Cours", codeIsin);
        }
        Collections.sort(historique, new Comparator<Cours>() {
            @Override
            public int compare(Cours a, Cours b) {
                return a.getDateCours().compareTo(b.getDateCours());
            }
        });
        return versDto(historique.get(historique.size() - 1));
    }

    public List<CoursDTO> historique(String codeIsin, Date du, Date au) {
        Valeur valeur = valeurService.charger(codeIsin);
        if (au == null) {
            au = new Date();
        }
        if (du == null) {
            du = DateUtils.ajouterAnnees(au, -1);
        }
        List<Cours> cours = coursRepository.findByValeurAndDateCoursBetweenOrderByDateCoursAsc(valeur, du, au);
        List<CoursDTO> dtos = new ArrayList<CoursDTO>();
        for (Cours c : cours) {
            dtos.add(versDto(c));
        }
        return dtos;
    }

    /**
     * Saisie manuelle d'un cours (valeur non couverte par Fininfo : OPCVM maison, valeurs radiees).
     */
        if (cours == null) {
            cours = new Cours();
            cours.setValeur(valeur);
            cours.setDateCours(date);
        } else {
            LOG.info("Ecrasement du cours du {} pour {} (source {})", DateUtils.formaterJour(date), valeur.getCodeIsin(), cours.getSource());
        }
        cours.setCloture(MontantUtils.arrondirCours(saisie.getCloture()));
        cours.setOuverture(MontantUtils.arrondirCours(saisie.getOuverture()));
        cours.setPlusHaut(MontantUtils.arrondirCours(saisie.getPlusHaut()));
        cours.setPlusBas(MontantUtils.arrondirCours(saisie.getPlusBas()));
        cours.setVolume(saisie.getVolume());
        cours.setSource(Cours.SOURCE_SAISIE);
        return versDto(coursRepository.save(cours));
    }

    /**
     * Enregistrement d'un cours issu du fichier Fininfo. Ne remplace jamais une saisie manuelle.
     */
    @Transactional
    public boolean enregistrerImport(Valeur valeur, Date date, BigDecimal cloture, BigDecimal ouverture,
            BigDecimal plusHaut, BigDecimal plusBas, Long volume) {
        Cours existant = coursRepository.findByValeurAndDateCours(valeur, date);
        if (existant != null) {
            if (Cours.SOURCE_SAISIE.equals(existant.getSource())) {
                LOG.debug("Cours saisi manuellement conserve pour {} au {}", valeur.getCodeIsin(), DateUtils.formaterJour(date));
                return false;
            }
            existant.setCloture(cloture);
            existant.setOuverture(ouverture);
            existant.setPlusHaut(plusHaut);
            existant.setPlusBas(plusBas);
            existant.setVolume(volume);
            coursRepository.save(existant);
            return true;
        }
        Cours cours = new Cours();
        cours.setValeur(valeur);
        cours.setDateCours(date);
        cours.setCloture(cloture);
        cours.setOuverture(ouverture);
        cours.setPlusHaut(plusHaut);
        cours.setPlusBas(plusBas);
        cours.setVolume(volume);
        cours.setSource(Cours.SOURCE_FININFO);
        coursRepository.save(cours);
        return true;
    }

    public Date derniereDateCotation() {
        return coursRepository.derniereDateCotation();
    }

    public static CoursDTO versDto(Cours c) {
        CoursDTO dto = new CoursDTO();
        dto.setCodeIsin(c.getValeur().getCodeIsin());
        dto.setDateCours(c.getDateCours());
        dto.setCloture(c.getCloture());
        dto.setOuverture(c.getOuverture());
        dto.setPlusHaut(c.getPlusHaut());
        dto.setPlusBas(c.getPlusBas());
        dto.setVolume(c.getVolume());
        dto.setSource(c.getSource());
        return dto;
    }
}
