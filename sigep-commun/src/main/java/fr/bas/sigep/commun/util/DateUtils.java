package fr.bas.sigep.commun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.bas.sigep.commun.exception.SigepException;

/**
 * Utilitaires de dates. Repris de SIGEP v1 (2004) quasiment tels quels.
 *
 * NB : SimpleDateFormat n'est pas thread-safe. Les instances statiques ci-dessous ont
 * provoque des dates fantaisistes en charge (SIGEP-187, 2017) ; le formatage a ete
 * synchronise, le parsing pas encore ("jamais vu de probleme dessus").
 */
public final class DateUtils {

    public static final String FORMAT_JOUR = "dd/MM/yyyy";
    public static final String FORMAT_JOUR_HEURE = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_FICHIER = "yyyyMMdd";

    private static final SimpleDateFormat SDF_JOUR = new SimpleDateFormat(FORMAT_JOUR);
    private static final SimpleDateFormat SDF_FICHIER = new SimpleDateFormat(FORMAT_FICHIER);

    private DateUtils() {
    }

    public static String formaterJour(Date date) {
        if (date == null) {
            return "";
        }
        synchronized (SDF_JOUR) {
            return SDF_JOUR.format(date);
        }
    }

    public static String formaterFichier(Date date) {
        synchronized (SDF_FICHIER) {
            return SDF_FICHIER.format(date);
        }
    }

    public static Date parserJour(String texte) {
        try {
            return SDF_JOUR.parse(texte);
        } catch (ParseException e) {
            throw new SigepException("Date invalide : " + texte, e);
        }
    }

    public static Date parserFichier(String texte) {
        try {
            return SDF_FICHIER.parse(texte);
        } catch (ParseException e) {
            throw new SigepException("Date fichier invalide : " + texte, e);
        }
    }

    /** Date du jour a minuit. */
    public static Date aujourdhui() {
        return tronquer(new Date());
    }

    public static Date tronquer(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date ajouterJours(Date date, int jours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, jours);
        return cal.getTime();
    }

    public static Date ajouterAnnees(Date date, int annees) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, annees);
        return cal.getTime();
    }

    /**
     * Ajoute des jours ouvres (lundi-vendredi). Les jours feries ne sont pas pris en compte :
     * le calendrier Euronext n'a jamais ete integre (TODO depuis 2016).
     */
    public static Date ajouterJoursOuvres(Date date, int jours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int ajoutes = 0;
        while (ajoutes < jours) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            int jour = cal.get(Calendar.DAY_OF_WEEK);
            if (jour != Calendar.SATURDAY && jour != Calendar.SUNDAY) {
                ajoutes++;
     */
    public static int anneesEntre(Date debut, Date fin) {
        Calendar d = Calendar.getInstance();
        d.setTime(debut);
        Calendar f = Calendar.getInstance();
        f.setTime(fin);
        int annees = f.get(Calendar.YEAR) - d.get(Calendar.YEAR);
        if (f.get(Calendar.MONTH) < d.get(Calendar.MONTH)
                || (f.get(Calendar.MONTH) == d.get(Calendar.MONTH) && f.get(Calendar.DAY_OF_MONTH) < d.get(Calendar.DAY_OF_MONTH))) {
            annees--;
        }
        return annees;
    }

    public static boolean memeJour(Date a, Date b) {
        return tronquer(a).equals(tronquer(b));
    }

    public static int annee(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Date construire(int annee, int mois, int jour) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(annee, mois - 1, jour);
        return cal.getTime();
    }
}
