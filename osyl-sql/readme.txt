Voir ZCII-1101, ZCII-1106, ZCII-1133

Suite a quelques demandes de la DAIP et la biblioth�que, voici 3 scripts qui parse tout les xmls des plans de cours (version �tudiante et publi�) d'une session sp�cifi�.  Le premier liste tout les r�f�rences bibliographique, le deuxi�me liste que les r�f�rences de type articles, et le dernier liste tout les �valuations et leurs d�tails.

Voici la proc�dure pour les rouler avec sqlplus, une fois connect� � la bd:

set serveroutput on size unlimited;
set linesize 15000;
set trimspool on;
spool <nom de fichier output>;
@<path du script>;
spool off;