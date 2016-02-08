package fr.bas.sigep.referentiel.batch;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import fr.bas.sigep.commun.util.DateUtils;

/**
 * Une ligne de detail du fichier quotidien de cours (format "COTATIONS V2", largeur fixe).
 *
 * Description du format (extrait de la doc fournisseur de 2009, docs/specs/FORMAT_FININFO.txt) :
 * <pre>
 *  pos   long  champ
 *  1     1     type enregistrement : 'E' entete, 'D' detail, 'F' fin
 *  2     12    code valeur fournisseur
 *  14    12    code ISIN
 *  26    8     date de cotation AAAAMMJJ
 *  94    12    volume
 *  106   3     devise
 *  109   1     indicateur de suspension ('S') ou blanc
 * </pre>
 * Total : 109 caracteres. Les lignes plus courtes (fichiers d'avant 2012, sans l'indicateur) sont acceptees.
 */
    private final Date dateCotation;
    private final BigDecimal cloture;
    private final BigDecimal ouverture;
    private final BigDecimal plusHaut;
    private final BigDecimal plusBas;
    private final Long volume;
    private final String devise;
    private final boolean suspendue;

    private LigneFininfo(String ligne) {
        this.codeFournisseur = ligne.substring(1, 13).trim();
        this.codeIsin = ligne.substring(13, 25).trim();
        this.dateCotation = DateUtils.parserFichier(ligne.substring(25, 33));
        this.cloture = montant(ligne.substring(33, 48));
        this.ouverture = montant(ligne.substring(48, 63));
        this.plusHaut = montant(ligne.substring(63, 78));
        this.plusBas = montant(ligne.substring(78, 93));
        String vol = ligne.substring(93, 105).trim();
        this.volume = vol.isEmpty() ? null : Long.valueOf(vol);
        this.devise = ligne.substring(105, 108).trim();
        this.suspendue = ligne.length() > 108 && ligne.charAt(108) == 'S';
    }

    public static boolean estDetail(String ligne) {
        return ligne != null && ligne.length() >= LONGUEUR_MINIMALE && ligne.charAt(0) == 'D';
    }
    public String getCodeFournisseur() {
        return codeFournisseur;
    }

    public String getCodeIsin() {
        return codeIsin;
    }

    public Date getDateCotation() {
        return dateCotation;
    }

    public BigDecimal getPlusHaut() {
        return plusHaut;
    }

    public BigDecimal getPlusBas() {
        return plusBas;
    }

    public Long getVolume() {
        return volume;
    }

    public String getDevise() {
        return devise;
    }

    public boolean isSuspendue() {
        return suspendue;
}
