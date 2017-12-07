-- ce script permet d'imprimer la liste des événements qui ont été créé par le compte admin (probablement la job de création)
-- mais qui ne sont pas lié à la table HEC_EVENT
-- NB: n'oubliez pas de changer la session dans le select plus bas 
DECLARE
  l_var VARCHAR2(32767); -- max length
BEGIN

FOR rec IN (SELECT aa.EVENT_ID, aa.CALENDAR_ID, aa.XML FROM calendar_event aa, hec_event bb WHERE aa.EVENT_ID = bb.EVENT_ID (+) and bb.EVENT_ID IS NULL AND CALENDAR_ID LIKE '%H2018%' ORDER BY CALENDAR_ID) LOOP
  l_var := rec.XML;
  IF l_var LIKE '%<property enc="BASE64" name="CHEF:creator" value="YWRtaW4="/>%' THEN
    dbms_output.put_line('ID:' || rec.EVENT_ID || ' CALENDAR:' || rec.CALENDAR_ID);
  END IF;
END LOOP;
dbms_output.put_line('end.');
END;