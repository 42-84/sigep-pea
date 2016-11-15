package fr.bas.sigep.referentiel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.domaine.TypeValeur;
import fr.bas.sigep.commun.domaine.Valeur;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.exception.RessourceInconnueException;
import fr.bas.sigep.commun.repository.PositionRepository;
import fr.bas.sigep.commun.repository.ValeurRepository;
import fr.bas.sigep.commun.securite.ContexteUtilisateur;

@RunWith(MockitoJUnitRunner.class)
public class ValeurServiceTest {

    @Mock
    private ValeurRepository valeurRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EligibiliteService eligibiliteService;

    @InjectMocks
    private ValeurService valeurService;

    @Before
    public void contexteBackOffice() {
        Habilitation h = new Habilitation();
        h.setUtilisateur("U030001");
        h.setProfil(Profil.BACK_OFFICE);
        ContexteUtilisateur.definir(h);
    }

    @After
    public void nettoyer() {
        ContexteUtilisateur.effacer();
    }

    // ------------------------------------------------------------------ cle ISIN

    @Test
    public void doitAccepterDesIsinValides() {
        assertTrue(ValeurService.isinValide("FR0000120073")); // Air Liquide
        assertTrue(ValeurService.isinValide("FR0000131104")); // BNP Paribas
        assertTrue(ValeurService.isinValide("US0378331005")); // Apple
    }

    @Test
    public void doitRefuserUnIsinDeMauvaiseLongueur() {
        assertFalse(ValeurService.isinValide("FR000012007"));
        assertFalse(ValeurService.isinValide(null));
    }

    @Test
    public void doitRefuserUnIsinAvecUnCaractereInterdit() {
        assertFalse(ValeurService.isinValide("FR00-0120073"));
    }

    @Test
    public void doitRefuserUnIsinAvecUneCleFausse() {
        assertFalse(ValeurService.isinValide("FR0000120074"));
    }

    @Test
    public void doitNormaliserUnIsin() {
        assertEquals("FR0000120073", ValeurService.normaliserIsin("  fr0000120073 "));
        assertNull(ValeurService.normaliserIsin(null));
    }

    // ------------------------------------------------------------------ service

    @Test(expected = RessourceInconnueException.class)
    public void doitRefuserUneValeurInconnue() {
        when(valeurRepository.findByCodeIsin("FR0000000000")).thenReturn(null);
        valeurService.charger("FR0000000000");
    }

    @Test
    public void doitRefuserLaCreationDUneValeurDejaExistante() {
        Valeur existante = new Valeur();
        existante.setCodeIsin("FR0000120073");
        when(valeurRepository.findByCodeIsin("FR0000120073")).thenReturn(existante);

        Valeur nouvelle = new Valeur();
        nouvelle.setCodeIsin("fr0000120073");
        nouvelle.setLibelle("AIR LIQUIDE");
        nouvelle.setTypeValeur(TypeValeur.ACTION);
        nouvelle.setCodePaysSiege("FR");

        try {
            valeurService.creer(nouvelle);
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("VALEUR_EXISTANTE", e.getCode());
        }
        verify(valeurRepository, never()).save(any(Valeur.class));
    }
}
