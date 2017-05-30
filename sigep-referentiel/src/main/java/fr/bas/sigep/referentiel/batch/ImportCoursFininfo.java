package fr.bas.sigep.referentiel.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.repository.ValeurRepository;
import fr.bas.sigep.commun.util.DateUtils;
import fr.bas.sigep.referentiel.service.CoursService;

/**
 * Import quotidien du fichier de cours depose par le fournisseur de donnees de marche
 * (transfert CFT vers /appli/sigep/fininfo/entree, nom COT_AAAAMMJJ.txt).
 *
 * Planifie a 18h30 du lundi au vendredi. Le fichier arrive normalement vers 18h05. S'il n'est
 * pas la, on ne fait rien et on reessaie a 19h30 (deuxieme cron). Au-dela, c'est l'exploitation
 * qui rejoue via POST /api/cours/import (docs/EXPLOITATION.md §3.2).
 *
 * Le fichier est en ISO-8859-1 (libelles accentues dans l'entete).

    @Value("${sigep.fininfo.repertoire:/appli/sigep/fininfo/entree}")
    private String repertoire;

    @Value("${sigep.fininfo.repertoire-archive:/appli/sigep/fininfo/archive}")
    private String repertoireArchive;

    @Value("${sigep.fininfo.actif:true}")
    private boolean actif;

    private volatile ResultatImport dernierResultat;

    @Scheduled(cron = "${sigep.fininfo.cron:0 30 18 * * MON-FRI}")
    public void importerFichierDuJour() {
        importerPourDate(new Date());
    }

    @Scheduled(cron = "${sigep.fininfo.cron-rattrapage:0 30 19 * * MON-FRI}")
    public void rattrapage() {
        if (dernierResultat != null && DateUtils.memeJour(dernierResultat.getDebut(), new Date())) {
            return; // deja fait aujourd'hui
        }
        importerPourDate(new Date());
    }

    public void importerPourDate(Date date) {
        if (!actif) {
            LOG.info("Import Fininfo desactive (sigep.fininfo.actif=false)");
            return;
        }
        String nom = "COT_" + DateUtils.formaterFichier(date) + ".txt";
        File fichier = new File(repertoire, nom);
        if (!fichier.exists()) {
            LOG.warn("Fichier Fininfo absent : {}", fichier.getAbsolutePath());
            return;
        }
        InputStream flux = null;
        try {
            flux = new FileInputStream(fichier);
            ResultatImport resultat = importer(flux, nom);
            if (resultat.getLignesRejetees() == 0) {
                FileUtils.moveFileToDirectory(fichier, new File(repertoireArchive), true);
            } else {
                LOG.error("Import {} termine avec {} rejet(s), fichier laisse en place", nom, resultat.getLignesRejetees());
            }
        } catch (IOException e) {
            LOG.error("Erreur de lecture du fichier " + nom, e);
        } finally {
            IOUtils.closeQuietly(flux);
        }
    }

    /**
     * Import d'un flux au format Fininfo. Une ligne en erreur n'interrompt pas l'import.
     * Chaque cours est enregistre dans sa propre transaction (CoursService) : un import
     * interrompu laisse la base a moitie a jour, et le rejeu complete.
     */
    public ResultatImport importer(InputStream flux, String nomFichier) throws IOException {
        ResultatImport resultat = new ResultatImport();
        resultat.setFichier(nomFichier);
        LOG.info("Debut import Fininfo {}", nomFichier);

        BufferedReader lecteur = new BufferedReader(new InputStreamReader(flux, ENCODAGE));
        String ligne;
        int numero = 0;
        while ((ligne = lecteur.readLine()) != null) {
            numero++;
            resultat.incrementerLignesLues();
            try {
                traiterLigne(LigneFininfo.parser(ligne), resultat);
            } catch (RuntimeException e) {
                resultat.incrementerLignesRejetees();
                String anomalie = "Ligne " + numero + " rejetee : " + e.getMessage();
                resultat.ajouterAnomalie(anomalie);
                LOG.warn(anomalie);
            }
        }
        resultat.setFin(new Date());
        LOG.info("Fin import {} : {} lues, {} enregistres, {} ignores, {} valeurs inconnues, {} rejets",
                nomFichier, resultat.getLignesLues(), resultat.getCoursEnregistres(), resultat.getCoursIgnores(),
                resultat.getValeursInconnues(), resultat.getLignesRejetees());
        dernierResultat = resultat;
        return resultat;
    }

    private void traiterLigne(LigneFininfo ligne, ResultatImport resultat) {
            resultat.incrementerCoursIgnores();
            return;
        }
        if (!valeur.getDevise().equals(ligne.getDevise())) {
            throw new IllegalStateException("Devise " + ligne.getDevise() + " inattendue pour " + valeur.getCodeIsin());
        }
        boolean enregistre = coursService.enregistrerImport(valeur, DateUtils.tronquer(ligne.getDateCotation()),
                ligne.getCloture(), ligne.getOuverture(), ligne.getPlusHaut(), ligne.getPlusBas(), ligne.getVolume());
        if (enregistre) {
            resultat.incrementerCoursEnregistres();
        } else {
            resultat.incrementerCoursIgnores();
        }
    }

    public ResultatImport getDernierResultat() {
        return dernierResultat;
    }
}
