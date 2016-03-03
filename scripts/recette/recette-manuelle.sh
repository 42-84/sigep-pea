#!/bin/bash
# Recette manuelle SIGEP - enchainement d'appels curl sur les 3 services.
# Ecrit pour la recette de la refonte (03/2016), complete au fil des versions.
# Usage : ./recette-manuelle.sh [hote] (defaut localhost). Les 3 services doivent etre demarres.
#
# NB : ne remet pas la base en etat. A jouer sur une base H2 fraiche (supprimer ~/sigep-dev/sigep.*).
#      Sur la recette Oracle, jouer une seule fois par jour (references MVT du jour).

HOTE=${1:-localhost}
REF=http://$HOTE:8081
CPT=http://$HOTE:8082
PTF=http://$HOTE:8083

CLIENT="-H SM_USER:00012345"      # M. Gautreau, plan PEA000045127
CONSEILLER="-H SM_USER:U045123"
BO="-H SM_USER:U030001"
JSON="-H Content-Type:application/json"

etape() { echo; echo "### $1"; }

etape "Sante des services"
for u in $REF $CPT $PTF; do curl -s $u/health; echo " <- $u"; done

etape "Referentiel : valeur, dernier cours, eligibilite"
curl -s $CLIENT $REF/api/valeurs/FR0000120073 | cut -c1-200; echo
curl -s $CLIENT $REF/api/cours/FR0000120073/dernier; echo
curl -s $CLIENT "$REF/api/valeurs/US0378331005/eligibilite?typePlan=PEA"; echo

etape "Comptes : plans du client, synthese (appelle portefeuille qui appelle referentiel)"
curl -s $CLIENT $CPT/api/clients/00012345/plans | cut -c1-200; echo
curl -s $CLIENT $CPT/api/plans/PEA000045127/synthese | cut -c1-400; echo

etape "Portefeuille : positions valorisees"
curl -s $CLIENT $PTF/api/portefeuilles/PEA000045127/positions | cut -c1-400; echo

etape "Ordre d'achat 5 TotalEnergies (attendu : 201, statut EXECUTE)"
curl -s -w " [%{http_code}]\n" $CLIENT $JSON -X POST -d '{"codeIsin":"FR0000120271","sens":"ACHAT","quantite":5}' $PTF/api/portefeuilles/PEA000045127/ordres | cut -c1-300

etape "Ordre sur valeur non eligible (attendu : 422 VALEUR_NON_ELIGIBLE)"
curl -s -w " [%{http_code}]\n" $CLIENT $JSON -X POST -d '{"codeIsin":"US0378331005","sens":"ACHAT","quantite":1}' $PTF/api/portefeuilles/PEA000045127/ordres

etape "Simulation de retrait 1 000 EUR"
curl -s $CLIENT "$CPT/api/plans/PEA000045127/retraits/simulation?montant=1000"; echo

etape "Acces au plan d'un autre client (attendu : 403)"
curl -s -o /dev/null -w "%{http_code}\n" $CLIENT $CPT/api/plans/PEA000412077

etape "Ouverture d'un second PEA (attendu : 422 PLAN_EXISTANT)"
curl -s -w " [%{http_code}]\n" $CONSEILLER $JSON -X POST -d '{"numeroClient":"00012345","typePlan":"PEA","versementInitial":500}' $CPT/api/plans

etape "Import manuel d'un fichier Fininfo (attendu : 2 enregistres, 1 ignore, 1 inconnue)"
curl -s $BO -F "fichier=@$(dirname "$0")/COT_20240318.txt" $REF/api/cours/import; echo

# TODO : retrait avec cloture, annulation BO, coupons (jamais ecrit, on teste a la main)
echo; echo "Fin de la recette manuelle."
