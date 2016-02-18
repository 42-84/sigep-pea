package fr.bas.sigep.commun.domaine;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fr.bas.sigep.commun.securite.ContexteUtilisateur;

/**
 * Colonnes techniques communes a toutes les tables SGP_*.
 *
 * Reprise du modele historique (SIGEP v1, 2004) : DT_CREATION / DT_MAJ / USR_MAJ.
 */
@MappedSuperclass
public abstract class EntiteAuditable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREATION", nullable = false, updatable = false)
    private Date dateCreation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_MAJ")
    private Date dateMaj;

    @Column(name = "USR_MAJ", length = 30)
    private String utilisateurMaj;

    @PrePersist
    protected void avantCreation() {
        Date maintenant = new Date();
        this.dateCreation = maintenant;
        this.dateMaj = maintenant;
        this.utilisateurMaj = utilisateurCourant();
    }

    @PreUpdate
    protected void avantMiseAJour() {
        this.dateMaj = new Date();
        this.utilisateurMaj = utilisateurCourant();
    }

    private String utilisateurCourant() {
        String utilisateur = ContexteUtilisateur.getUtilisateur();
        if (utilisateur == null) {
            // batchs, tests, appels internes sans en-tete SM_USER
            return "SYSTEME";
        }
        // USR_MAJ fait 30 en base (Oracle), les identifiants SiteMinder peuvent depasser
        return utilisateur.length() > 30 ? utilisateur.substring(0, 30) : utilisateur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    public String getUtilisateurMaj() {
        return utilisateurMaj;
    }

    public void setUtilisateurMaj(String utilisateurMaj) {
        this.utilisateurMaj = utilisateurMaj;
    }
}
