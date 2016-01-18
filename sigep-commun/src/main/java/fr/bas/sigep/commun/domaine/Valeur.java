package fr.bas.sigep.commun.domaine;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Valeur mobiliere du referentiel (action, OPCVM, ETF...).
 *
 * Alimente par l'import Fininfo quotidien pour les cours, saisi par le back-office pour
 * la creation et l'eligibilite. Le code Fininfo est conserve pour le rapprochement fichier.
 */
@Entity
@Table(name = "SGP_VALEUR")
public class Valeur extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_VALEUR")
    @SequenceGenerator(name = "SEQ_SGP_VALEUR", sequenceName = "SEQ_SGP_VALEUR", allocationSize = 1)
    /** Code MIC de la place de cotation (XPAR = Euronext Paris). */
    @Column(name = "PLACE_COTATION", length = 4)
    private String placeCotation;

    /** Pays du siege social de l'emetteur (ISO 3166-1 alpha-2). Determine l'eligibilite. */
    @Column(name = "PAYS_SIEGE", length = 2)
    private String codePaysSiege;

    @Column(name = "DEVISE", nullable = false, length = 3)
    private String devise = "EUR";

    /** Eligibilite PEA, saisie par le back-office (l'algorithme EligibiliteService ne fait que proposer). */
    @Column(name = "ELIGIBLE_PEA", nullable = false)
    private boolean eligiblePea;

    @Column(name = "ELIGIBLE_PEA_PME", nullable = false)
    private boolean eligiblePeaPme;

    /** Code valeur chez le fournisseur de donnees de marche (fichier quotidien). */
    @Column(name = "CODE_FININFO", length = 12)
    private String codeFininfo;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_RADIATION")
    private Date dateRadiation;
    public void setMnemonique(String mnemonique) {
        this.mnemonique = mnemonique;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public TypeValeur getTypeValeur() {
        return typeValeur;
    }

    public void setTypeValeur(TypeValeur typeValeur) {
        this.typeValeur = typeValeur;
    }

    public String getPlaceCotation() {
        return placeCotation;
    }

    public void setPlaceCotation(String placeCotation) {
        this.placeCotation = placeCotation;
    }

    public String getCodePaysSiege() {

    public String getDevise() {
        return devise;
    }
        return eligiblePea;
    }

    public void setEligiblePea(boolean eligiblePea) {
        this.eligiblePea = eligiblePea;
    }

    public boolean isEligiblePeaPme() {
        return eligiblePeaPme;
    }

    public void setEligiblePeaPme(boolean eligiblePeaPme) {
    public Date getDateRadiation() {
        return dateRadiation;
    }

    public void setDateRadiation(Date dateRadiation) {
        this.dateRadiation = dateRadiation;
    }

    public boolean estRadiee() {
        return dateRadiation != null && !dateRadiation.after(new Date());
    }

    public boolean estEligible(TypePlan typePlan) {
        if (typePlan == TypePlan.PEA_PME) {
            return eligiblePeaPme;
        }
        return eligiblePea;
    }
}
