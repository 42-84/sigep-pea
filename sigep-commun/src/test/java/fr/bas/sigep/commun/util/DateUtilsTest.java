package fr.bas.sigep.commun.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void doitCompterLesAnneesEntieresQuandLAnniversaireEstPasse() {
        Date debut = DateUtils.construire(2010, 3, 15);
        Date fin = DateUtils.construire(2020, 7, 1);
        assertEquals(10, DateUtils.anneesEntre(debut, fin));
    }

    @Test
    public void doitCompterLesAnneesEntieresLeJourAnniversaire() {
        Date debut = DateUtils.construire(2010, 3, 15);
        Date fin = DateUtils.construire(2020, 3, 15);
        assertEquals(10, DateUtils.anneesEntre(debut, fin));
    }

    @Test
    public void doitRetirerUneAnneeSiLAnniversaireNEstPasEncorePasse() {
        Date debut = DateUtils.construire(2010, 3, 15);
        Date fin = DateUtils.construire(2020, 3, 14);
        assertEquals(9, DateUtils.anneesEntre(debut, fin));
    }

    @Test
    public void doitSauterLeWeekEndEnAjoutantDesJoursOuvres() {
        // vendredi 15/03/2024 + 2 jours ouvres = mardi 19/03/2024
        Date vendredi = DateUtils.construire(2024, 3, 15);
        Date resultat = DateUtils.ajouterJoursOuvres(vendredi, 2);
        assertEquals(DateUtils.construire(2024, 3, 19), resultat);

        Calendar cal = Calendar.getInstance();
        cal.setTime(resultat);
        assertEquals(Calendar.TUESDAY, cal.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void doitTronquerLHeure() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2021, Calendar.NOVEMBER, 8, 14, 37, 22);
        cal.set(Calendar.MILLISECOND, 512);

        Date tronquee = DateUtils.tronquer(cal.getTime());

        assertEquals(DateUtils.construire(2021, 11, 8), tronquee);
    }

    @Test
    public void doitReconnaitreLeMemeJourMalgreDesHeuresDifferentes() {
        Calendar matin = Calendar.getInstance();
        matin.clear();
        matin.set(2021, Calendar.NOVEMBER, 8, 8, 0, 0);
        Calendar soir = Calendar.getInstance();
        soir.clear();
        soir.set(2021, Calendar.NOVEMBER, 8, 23, 59, 59);

        assertTrue(DateUtils.memeJour(matin.getTime(), soir.getTime()));
        assertFalse(DateUtils.memeJour(matin.getTime(), DateUtils.construire(2021, 11, 9)));
    }

    @Test
    public void doitFormaterEtParserUneDateFichier() {
        Date date = DateUtils.construire(2024, 3, 15);
        assertEquals("20240315", DateUtils.formaterFichier(date));
        assertEquals(date, DateUtils.parserFichier("20240315"));
    }

    @Test
    @Ignore("echoue le 29 fevrier - SIGEP-198")
    public void doitAjouterDesAnnees() {
        Date depart = DateUtils.construire(2020, 2, 29);
        Date resultat = DateUtils.ajouterAnnees(depart, 1);
        assertEquals(DateUtils.construire(2021, 2, 29), resultat);
    }
}
