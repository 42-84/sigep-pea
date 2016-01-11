package fr.bas.sigep.commun.domaine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Client particulier titulaire d'un ou plusieurs plans.
 *
 * Le numero client (8 chiffres) est celui du referentiel client central (RCC) de la banque,
 * il est recopie ici a l'ouverture du premier plan. Il n'y a pas de synchronisation
 * avec le RCC : les changements d'adresse sont ressaisis par le back-office.
 */
@Entity
@Table(name = "SGP_CLIENT")
public class Client extends EntiteAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_CLIENT")
    @SequenceGenerator(name = "SEQ_SGP_CLIENT", sequenceName = "SEQ_SGP_CLIENT", allocationSize = 1)
    @Column(name = "ID_CLIENT")
    private Long id;

    @Column(name = "NUM_CLIENT", nullable = false, unique = true, length = 8)
    @Column(name = "ADR_LIGNE1", length = 80)
    private String adresseLigne1;

    @Column(name = "ADR_LIGNE2", length = 80)
    private String adresseLigne2;

    @Column(name = "ADR_CP", length = 10)
    private String codePostal;

    @Column(name = "ADR_VILLE", length = 60)
    private String ville;

    @Column(name = "ADR_PAYS", length = 2)
    private String codePays = "FR";

    @Column(name = "EMAIL", length = 120)
    private String email;

    /** Residence fiscale en France : condition d'ouverture d'un PEA. */
    @Column(name = "RESIDENT_FISCAL_FR")
    private Boolean residentFiscalFrance = Boolean.TRUE;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)

    public String getCivilite() {
        return civilite;
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    }

    public void setAdresseLigne1(String adresseLigne1) {
        this.adresseLigne1 = adresseLigne1;
    }

    public String getAdresseLigne2() {
        return adresseLigne2;
    }

    public void setAdresseLigne2(String adresseLigne2) {
        this.adresseLigne2 = adresseLigne2;
    }

    public String getCodePostal() {
        this.ville = ville;
    }

    public String getCodePays() {
        return codePays;
    }

    public void setCodePays(String codePays) {
        this.codePays = codePays;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getResidentFiscalFrance() {
        return residentFiscalFrance;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

        return sb.toString();
    }
}
