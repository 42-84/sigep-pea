package fr.bas.sigep.referentiel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.bas.sigep.commun.domaine.Cours;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.dto.CoursDTO;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.CoursRepository;
import fr.bas.sigep.commun.util.DateUtils;

@RunWith(MockitoJUnitRunner.class)
public class CoursServiceTest {

    private static final String ISIN = "FR0000120073";

    @Mock
    private CoursRepository coursRepository;

    @Mock
    private ValeurService valeurService;

    @InjectMocks
    private CoursService coursService;

    private Valeur valeur;

    @Before
    public void init() {
        valeur = new Valeur();
        valeur.setCodeIsin(ISIN);
        valeur.setLibelle("AIR LIQUIDE");
        when(valeurService.charger(ISIN)).thenReturn(valeur);
    }

    private Cours cours(Date date, String cloture) {
        Cours c = new Cours();
        c.setValeur(valeur);
        c.setDateCours(date);
        c.setCloture(new BigDecimal(cloture));
        return c;
    }

    @Test
    public void doitRenvoyerLeCoursLePlusRecentMemeSiLHistoriqueEstDesordonne() {
        List<Cours> historique = new ArrayList<Cours>();
        historique.add(cours(DateUtils.construire(2024, 3, 13), "182.0000"));
        historique.add(cours(DateUtils.construire(2024, 3, 15), "184.1000"));
        historique.add(cours(DateUtils.construire(2024, 3, 14), "183.2500"));
        when(coursRepository.findByValeur(valeur)).thenReturn(historique);

        CoursDTO dernier = coursService.dernierCours(ISIN);

        assertNotNull(dernier);
        assertEquals(ISIN, dernier.getCodeIsin());
        assertEquals(DateUtils.construire(2024, 3, 15), dernier.getDateCours());
        assertEquals(0, new BigDecimal("184.10").compareTo(dernier.getCloture()));
    }

    @Test(expected = RessourceInconnueException.class)
    public void doitRefuserUneValeurSansAucunCours() {
        when(coursRepository.findByValeur(valeur)).thenReturn(new ArrayList<Cours>());
        coursService.dernierCours(ISIN);
    }
}
