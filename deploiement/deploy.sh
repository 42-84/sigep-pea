#!/bin/bash
#
# SIGEP - deploiement des trois services
# Usage : deploiement/deploy.sh <recette|prod>
#
# A lancer depuis la racine du projet apres "./gradlew clean build" (les jars sont pris dans
# */build/libs). Copie les jars, redemarre les services dans l'ordre referentiel -> comptes
# -> portefeuille avec 30 s d'attente entre chaque, puis verifie /health.
#
# Prerequis : cle SSH du compte de deploiement (sigep-deploy) autorisee sur les serveurs cibles,
# sudo sans mot de passe pour "systemctl restart sigep-*" et "cp" dans /appli/sigep.
#
# FD - 03/2018. Modifie 05/2019 : sauvegarde du jar precedent en .prec
#

ENV=$1
VERSION=$(grep '^version=' gradle.properties | cut -d= -f2)

if [ "$ENV" = "recette" ]; then
    HOTE_REF=srv-sigep-rec-01.bas.intra
    HOTE_CPT=srv-sigep-rec-01.bas.intra
    HOTE_PTF=srv-sigep-rec-01.bas.intra
    PROFIL=recette
elif [ "$ENV" = "prod" ]; then
    HOTE_REF=srv-sigep-ref-01.bas.intra
    HOTE_CPT=srv-sigep-cpt-01.bas.intra
    HOTE_PTF=srv-sigep-ptf-01.bas.intra
    PROFIL=prod
    echo "*** DEPLOIEMENT EN PRODUCTION de la version $VERSION ***"
    echo "Les scripts SQL de la version ont-ils ete passes par les DBA ? (db/scripts) [o/N]"
    read REPONSE
    if [ "$REPONSE" != "o" ]; then
        echo "Abandon."
        exit 1
    fi
else
    echo "Usage : $0 <recette|prod>"
    exit 1
fi

UTILISATEUR=sigep-deploy
CIBLE=/appli/sigep
ATTENTE=30

echo "Deploiement SIGEP $VERSION sur $ENV"
echo "  referentiel  -> $HOTE_REF"
echo "  comptes      -> $HOTE_CPT"
echo "  portefeuille -> $HOTE_PTF"
echo

# ---------------------------------------------------------------- copie des jars
# Le nom du jar contient la version (sigep-referentiel-2.7.4-SNAPSHOT.jar) : on le renomme sans
# version sur la cible pour que l'unite systemd ne change pas.

echo "[1/3] Copie des jars"
scp sigep-referentiel/build/libs/sigep-referentiel-$VERSION.jar   $UTILISATEUR@$HOTE_REF:/tmp/sigep-referentiel.jar
scp sigep-comptes/build/libs/sigep-comptes-$VERSION.jar           $UTILISATEUR@$HOTE_CPT:/tmp/sigep-comptes.jar
scp sigep-portefeuille/build/libs/sigep-portefeuille-$VERSION.jar $UTILISATEUR@$HOTE_PTF:/tmp/sigep-portefeuille.jar

# ---------------------------------------------------------------- redemarrage dans l'ordre

redemarrer() {
    HOTE=$1
    SERVICE=$2
    PORT=$3
    echo "[2/3] $SERVICE sur $HOTE"
    ssh $UTILISATEUR@$HOTE "sudo cp $CIBLE/$SERVICE.jar $CIBLE/$SERVICE.jar.prec 2>/dev/null; \
                            sudo cp /tmp/$SERVICE.jar $CIBLE/$SERVICE.jar && \
                            sudo chown sigep:sigep $CIBLE/$SERVICE.jar && \
                            sudo systemctl restart $SERVICE"
    echo "      attente $ATTENTE s..."
    sleep $ATTENTE
    echo "[3/3] verification http://$HOTE:$PORT/health"
    curl -s --max-time 10 http://$HOTE:$PORT/health
    echo
}

redemarrer $HOTE_REF sigep-referentiel  8081
redemarrer $HOTE_CPT sigep-comptes      8082
redemarrer $HOTE_PTF sigep-portefeuille 8083

echo
echo "Deploiement termine. Versions deployees :"
curl -s --max-time 5 http://$HOTE_REF:8081/info
echo
curl -s --max-time 5 http://$HOTE_CPT:8082/info
echo
curl -s --max-time 5 http://$HOTE_PTF:8083/info
echo

# TODO : arreter si un scp echoue (aujourd'hui on redemarre avec l'ancien jar sans le dire)
# TODO : verifier "status":"UP" dans la reponse /health au lieu de l'afficher
# TODO : le front (frontend/sigep-web/dist) n'est pas deploye par ce script -> a la main sur srv-sigep-web-01
