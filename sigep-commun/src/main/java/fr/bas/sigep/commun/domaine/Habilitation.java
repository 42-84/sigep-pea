package fr.bas.sigep.commun.domaine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Habilitation d'un utilisateur SiteMinder (SM_USER) sur SIGEP.
 *
 * Pour un client, l'identifiant SiteMinder est son numero client (portail BAS en ligne).
 * Pour un agent, c'est son matricule (ex : U045123).
 */
@Entity
@Table(name = "SGP_HABILITATION")
public class Habilitation extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_HABILITATION")
    @SequenceGenerator(name = "SEQ_SGP_HABILITATION", sequenceName = "SEQ_SGP_HABILITATION", allocationSize = 1)
    @Column(name = "ID_HABILITATION")
    private Long id;

    @Column(name = "UTILISATEUR", nullable = false, unique = true, length = 30)
    private String utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROFIL", nullable = false, length = 15)
    private Profil profil;

    /** Renseigne pour le profil CLIENT uniquement. */
    @Column(name = "NUM_CLIENT", length = 8)
    private String numeroClient;

    @Column(name = "AGENCE", length = 5)
    private String codeAgence;

    @Column(name = "ACTIF", nullable = false)
    private boolean actif = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getCodeAgence() {
        return codeAgence;
    }

    public void setCodeAgence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
