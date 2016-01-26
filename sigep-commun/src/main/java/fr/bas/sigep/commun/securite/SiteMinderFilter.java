package fr.bas.sigep.commun.securite;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.bas.sigep.commun.domaine.Habilitation;
import fr.bas.sigep.commun.domaine.Profil;
import fr.bas.sigep.commun.repository.HabilitationRepository;

/**
 * Authentification par en-tete SM_USER pose par le reverse proxy SiteMinder (agent web Apache).
 *
 * Les services ne sont pas exposes directement : le firewall n'autorise que le proxy et les
 * autres serveurs SIGEP. On fait donc confiance a l'en-tete.
 *
 * En dev (sans SiteMinder), sigep.securite.utilisateur-dev permet de forcer un utilisateur,
 * et l'en-tete X-SM-User (envoye par le front Angular en dev) est accepte comme equivalent.
 */
@Component
public class SiteMinderFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(SiteMinderFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (!actif || uri.startsWith("/health") || uri.startsWith("/info") || uri.startsWith("/manage")) {
            chain.doFilter(request, response);
            return;
        }

        String utilisateur = request.getHeader(EN_TETE_SITEMINDER);
        if (StringUtils.isBlank(utilisateur)) {
        }
        if (habilitation == null) {
            LOG.warn("Utilisateur {} sans habilitation SIGEP", utilisateur);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utilisateur non habilite sur SIGEP");
            return;
        }

        ContexteUtilisateur.definir(habilitation);
        try {
            chain.doFilter(request, response);
        } finally {
            ContexteUtilisateur.effacer();
        }
    }

    private Habilitation habilitationInterne() {
        Habilitation h = new Habilitation();
        h.setUtilisateur(UTILISATEUR_INTERNE);
        h.setProfil(Profil.SYSTEME);
        return h;
    }
}
