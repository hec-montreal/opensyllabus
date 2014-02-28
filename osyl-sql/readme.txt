Voir ZCII-1101, ZCII-1106, ZCII-1133

Suite a quelques demandes de la DAIP et la bibliothèque, voici 3 scripts qui parse tout les xmls des plans de cours (version étudiante et publié) d'une session spécifié.  Le premier liste tout les références bibliographique, le deuxième liste que les références de type articles, et le dernier liste tout les évaluations et leurs détails.

Voici la procédure pour les rouler avec sqlplus, une fois connecté à la bd:

set serveroutput on size unlimited;
set linesize 15000;
set trimspool on;
spool <nom de fichier output>;
@<path du script>;
spool off;