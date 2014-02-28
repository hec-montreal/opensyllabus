-- instructions pour rouler ce script:
-- set serveroutput on size unlimited;
-- set linesize 15000;
-- set trimspool on;
-- spool <nom de fichier output>;
-- @<path du fichier>
-- spool off;
DECLARE
  CURSOR course_outline_cur is
    SELECT site_id, content FROM osyl_co
    WHERE "ACCESS" = 'attendee' 
    AND published = '1'
    AND site_id LIKE '%A2013%'
    ORDER BY site_id;

  -- xml parser variables  
  parser xmlparser.Parser;
  xmldoc xmldom.DOMDocument;
  xmlnode xmldom.DOMNode;
  asmUnitList xmldom.DOMNodeList;
  asmUnit xmldom.DOMNode;
  asmResource xmldom.DOMNode;
  
  plancours_clob CLOB;
  site_id VARCHAR(25 CHAR);
  assignment_number VARCHAR(10 CHAR);
  title VARCHAR(1000 CHAR);
  assignment_type VARCHAR(150 CHAR);
  weight VARCHAR(25 CHAR);
  assignment_mode VARCHAR(150 CHAR);
  start_date VARCHAR(150 CHAR);
  assignment_location VARCHAR(150 CHAR);
  modality VARCHAR(150 CHAR);
  submission_type VARCHAR(150 CHAR);
  
BEGIN

  -- headers
  dbms_output.put_line('Sigle;Id du site;Numéro;Nom;Type d''évaluation;Pondération;Mode de travail;Date de l''évaluation;Localisation;Modalité;Mode de remise');

  OPEN course_outline_cur;

  LOOP
    FETCH course_outline_cur INTO site_id, plancours_clob;
    EXIT WHEN course_outline_cur%NOTFOUND;
    
    -- create a new parser
    parser := xmlparser.newParser;
  
    BEGIN
      -- parse the xml
      xmlparser.ParseCLOB(parser, plancours_clob);  
      xmldoc := xmlparser.getDocument(parser);
      xmlnode := xmldom.makeNode(xmldoc);
  
      -- get course details and documents
      asmUnitList := xslprocessor.selectNodes(xmlnode, '//asmUnit[@xsi:type="AssessmentUnit"]', 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance');

      -- for each document in the cours outline  
      FOR j in 1..xmldom.getLength(asmUnitList) LOOP
        asmUnit := xmldom.item(asmUnitList, j-1);
        
        assignment_number := xslprocessor.valueOf(asmUnit, 'prefix');
        title := xslprocessor.valueOf(asmUnit, 'label');
        assignment_type := xslprocessor.valueOf(asmUnit, 'assessmentType');
        weight := xslprocessor.valueOf(asmUnit, 'weight');
        assignment_mode := xslprocessor.valueOf(asmUnit, 'mode');
        start_date := xslprocessor.valueOf(asmUnit, 'date-start');
        assignment_location := xslprocessor.valueOf(asmUnit, 'location');
        modality := xslprocessor.valueOf(asmUnit, 'modality');
        submission_type := xslprocessor.valueOf(asmUnit, 'submition_type');
        
        dbms_output.put('"' || REPLACE(SUBSTR(site_id, 0, INSTR(site_id, '.') - 1), '-', NULL) || '";'); 
        dbms_output.put('"' || site_id || '";');
        dbms_output.put('"' || assignment_number || '";'); --no
        dbms_output.put('"' || title || '";'); 
        dbms_output.put('"' || assignment_type || '";'); --type
        dbms_output.put('"' || weight || '";'); --pondération
        dbms_output.put('"' || assignment_mode || '";'); --mode de travail
        dbms_output.put('"' || start_date || '";'); --date 
        dbms_output.put('"' || assignment_location || '";'); --localisation
        dbms_output.put('"' || modality || '";'); --modalité
        dbms_output.put('"' || submission_type || '"'); --mode de remise
        
        dbms_output.new_line;
        
      END LOOP;
      
      -- free memory
      xmlparser.freeParser(parser);
      xmldom.freeDocument(xmldoc);

    END;
  END LOOP;
  CLOSE course_outline_cur;
END;
/
