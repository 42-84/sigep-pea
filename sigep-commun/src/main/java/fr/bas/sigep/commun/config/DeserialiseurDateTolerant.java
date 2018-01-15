package fr.bas.sigep.commun.config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Accepte les dates dans les formats que SIGEP produit lui-meme : dd/MM/yyyy (format officiel),
 * yyyy-MM-dd (colonnes DATE : java.sql.Date ignore le DateFormat de Jackson), dd/MM/yyyy HH:mm:ss
 * (anciens fichiers de reprise) et le timestamp numerique (front Angular, moment.valueOf()).
 *
 * Ecrit en recette 2016 pour debloquer la synthese plan ; devait etre temporaire.
 */
public class DeserialiseurDateTolerant extends JsonDeserializer<Date> {

    private static final String[] FORMATS = { "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd" };

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken().isNumeric()) {
            return new Date(p.getLongValue());
        }
        String texte = p.getText();
        if (texte == null || texte.trim().isEmpty()) {
            return null;
        }
        texte = texte.trim();
        for (String format : FORMATS) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            try {
                return sdf.parse(texte);
            } catch (ParseException e) {
                // format suivant
            }
        }
        throw ctxt.weirdStringException(texte, Date.class, "aucun format de date SIGEP reconnu");
    }
}
