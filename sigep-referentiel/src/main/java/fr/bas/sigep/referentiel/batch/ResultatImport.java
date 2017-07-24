package fr.bas.sigep.referentiel.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultatImport {

    private String fichier;
    private Date debut = new Date();
    private Date fin;
    private int lignesLues;
    private int coursEnregistres;
    private int coursIgnores;
    private int valeursInconnues;
    private int lignesRejetees;
    private List<String> anomalies = new ArrayList<String>();

    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
        this.fichier = fichier;
    }

    public Date getDebut() {
        return debut;
    }

    public void setDebut(Date debut) {
        this.debut = debut;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public int getLignesLues() {
        return lignesLues;
    }

    public void incrementerLignesLues() {
        this.lignesLues++;
    }

    public int getCoursEnregistres() {
        return coursEnregistres;
    }

    public void incrementerCoursEnregistres() {
        this.coursEnregistres++;
    }

    public int getValeursInconnues() {
        return valeursInconnues;
    }

    public void incrementerValeursInconnues() {
        this.valeursInconnues++;
    }

    public int getLignesRejetees() {
        return lignesRejetees;
    }

    public void incrementerLignesRejetees() {
        this.lignesRejetees++;
    }

    public List<String> getAnomalies() {
        return anomalies;
    }

    public void ajouterAnomalie(String anomalie) {
        // on garde les 50 premieres, le reste est dans les logs
        if (anomalies.size() < 50) {
            anomalies.add(anomalie);
        }
    }
}
