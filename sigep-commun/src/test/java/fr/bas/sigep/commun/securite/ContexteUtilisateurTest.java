package fr.bas.sigep.commun.securite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Profil;

public class ContexteUtilisateurTest {

    @After
    public void nettoyer() {
        ContexteUtilisateur.effacer();
    }

    private static Habilitation habilitation(String utilisateur, Profil profil) {
        Habilitation h = new Habilitation();
        h.setUtilisateur(utilisateur);
        h.setProfil(profil);
        return h;
    }

    @Test
    public void unClientNAtteintPasLeBackOffice() {
        ContexteUtilisateur.definir(habilitation("00012345", Profil.CLIENT));

        assertTrue(ContexteUtilisateur.aAuMoins(Profil.CLIENT));
        assertFalse(ContexteUtilisateur.aAuMoins(Profil.CONSEILLER));
        assertFalse(ContexteUtilisateur.aAuMoins(Profil.BACK_OFFICE));
        assertTrue(ContexteUtilisateur.estClient());
    }

    @Test
    public void leBackOfficeCouvreLesProfilsInferieurs() {
        ContexteUtilisateur.definir(habilitation("U030001", Profil.BACK_OFFICE));

        assertTrue(ContexteUtilisateur.aAuMoins(Profil.CLIENT));
        assertTrue(ContexteUtilisateur.aAuMoins(Profil.CONSEILLER));
        assertTrue(ContexteUtilisateur.aAuMoins(Profil.BACK_OFFICE));
        assertFalse(ContexteUtilisateur.aAuMoins(Profil.ADMIN));
        assertFalse(ContexteUtilisateur.estClient());
    }

    @Test
    public void sansContexteAucunProfilNEstAtteint() {
        assertNull(ContexteUtilisateur.getHabilitation());
        assertNull(ContexteUtilisateur.getUtilisateur());
        assertNull(ContexteUtilisateur.getProfil());
        assertFalse(ContexteUtilisateur.aAuMoins(Profil.CLIENT));
    }

    @Test
    public void effacerVideLeContexte() {
        ContexteUtilisateur.definir(habilitation("U030001", Profil.BACK_OFFICE));
        ContexteUtilisateur.effacer();
        assertNull(ContexteUtilisateur.getUtilisateur());
    }
}
