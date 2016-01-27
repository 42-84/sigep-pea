package fr.bas.sigep.commun.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

/**
 * Serialisation JSON commune.
 *
 * Les dates sortent au format dd/MM/yyyy (attente du front et des fichiers de reprise).
 * Le module Hibernate5 evite les LazyInitializationException quand une entite JPA est
 * renvoyee directement par un controleur (ce qui arrive : Client, Valeur, Mouvement...).
 *
 * Piege connu : les colonnes @Temporal(DATE) sortent en java.sql.Date, que Jackson serialise
 * en "yyyy-MM-dd" en ignorant le DateFormat ci-dessous. Les TIMESTAMP sortent bien en dd/MM/yyyy.
 * Le front gere les deux formats (pipe date-fr). Ne pas "corriger" sans revoir les fichiers
 * de releve annuel qui relisent ces dates (SIGEP-176).
 */
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
}
