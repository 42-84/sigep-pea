package fr.bas.sigep.comptes.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.bas.sigep.commun.domaine.Client;
import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.exception.RegleMetierException;
import fr.bas.sigep.commun.repository.ClientRepository;
import fr.bas.sigep.commun.securite.ContexteUtilisateur;
import fr.bas.sigep.commun.util.DateUtils;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Before
    public void contexteConseiller() {
        Habilitation h = new Habilitation();
        h.setUtilisateur("U045123");
        h.setProfil(Profil.CONSEILLER);
        h.setCodeAgence("17001");
        ContexteUtilisateur.definir(h);
    }

    @After
    public void nettoyer() {
        ContexteUtilisateur.effacer();
    }

    @Test
    public void unClientDeVingtAnsEstMajeur() {
        Client client = new Client();
        client.setDateNaissance(DateUtils.construire(2000, 1, 1));
        assertTrue(clientService.estMajeur(client, DateUtils.construire(2020, 6, 1)));
    }

    @Test
    public void unClientDeDixAnsNEstPasMajeur() {
        Client client = new Client();
        client.setDateNaissance(DateUtils.construire(2010, 1, 1));
        assertFalse(clientService.estMajeur(client, DateUtils.construire(2020, 6, 1)));
    }

    @Test
    public void sansDateDeNaissanceLeClientNEstPasConsidereMajeur() {
        assertFalse(clientService.estMajeur(new Client(), DateUtils.construire(2020, 6, 1)));
    }

    @Test
    public void doitRefuserUnNumeroClientASeptChiffres() {
        Client client = new Client();
        client.setNumeroClient("1234567");
        client.setNom("Dupont");

        try {
            clientService.creer(client);
            fail("RegleMetierException attendue");
        } catch (RegleMetierException e) {
            assertEquals("NUM_CLIENT_INVALIDE", e.getCode());
        }
        verify(clientRepository, never()).save(any(Client.class));
    }
}
