-- instructions pour rouler ce script:
-- set serveroutput on size unlimited;
-- set linesize 15000;
-- set trimspool on;
-- spool <nom de fichier output>;
-- @<path du fichier>
-- spool off;
DECLARE
  CURSOR citation_url_cur is
  SELECT PROPERTY_VALUE FROM citation_citation 
  WHERE to_char(PROPERTY_VALUE) LIKE 'https://elib.hec.ca/%' 
  AND rownum < 100
  FOR UPDATE;

  citation_url CLOB;
  searchdata VARCHAR2(500);
  
BEGIN
  dbms_output.enable(NULL);

  OPEN citation_url_cur;

  LOOP
    FETCH citation_url_cur INTO citation_url;
    EXIT WHEN citation_url_cur%NOTFOUND;
    
    startindex := INSTR(citation_url, 'searchdata1')+12;
    endindex := INSTR(citation_url, '&', startindex);
   
    searchdata := SUBSTR(citation_url, startindex);

    dbms_output.put_line(startindex || ' ' || endindex || ' ' || searchdata);
  END LOOP;
  CLOSE citation_url_cur;
END;
/
