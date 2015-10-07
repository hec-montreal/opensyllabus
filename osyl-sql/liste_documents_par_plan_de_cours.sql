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
    AND site_id LIKE '%A2015.%'
    ORDER BY site_id;

  -- xml parser variables  
  parser xmlparser.Parser;
  xmldoc xmldom.DOMDocument;
  xmlnode xmldom.DOMNode;
  asmContextList xmldom.DOMNodeList;
  asmContext xmldom.DOMNode;
  asmResource xmldom.DOMNode;
  
  plancours_clob CLOB;
  site_id VARCHAR(25 CHAR);
  department VARCHAR(50 CHAR);
  program VARCHAR(25 CHAR);
  access VARCHAR(50 CHAR);
  uri VARCHAR(700 CHAR);
  title VARCHAR(500 CHAR);
  comment VARCHAR2(4001 CHAR);
  resourceType VARCHAR(700 CHAR);
  copyright VARCHAR(100 CHAR);
  important VARCHAR(10 CHAR);
  level VARCHAR(700 CHAR);
BEGIN

  -- headers
  dbms_output.put_line('Cours,Departement,Cheminement academique,Nom de fichier,Titre,Type,Statut de droits d''auteur,Niveau de diffusion,Niveau d''exigence,Importance,Commentaire');

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
      asmContextList := xslprocessor.selectNodes(xmlnode, '//asmContext[asmResource[@xsi:type="Document"]]', 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance');
      department := xslprocessor.valueOf(xmlnode, '//department');
      program := xslprocessor.valueOf(xmlnode, '//program');
      
      IF xmldom.getLength(asmContextList) > 0 THEN

      -- for each document in the cours outline  
      FOR j in 1..xmldom.getLength(asmContextList) LOOP
        asmContext := xmldom.item(asmContextList, j-1);
        access := xslprocessor.valueOf(asmContext, '@access');
        level := xslprocessor.valueOf(asmContext, 'level');
        important := xslprocessor.valueOf(asmContext, 'importance');
        BEGIN
          --faut utiliser la procedure, qui n'est pas limité à 4000 chars
          comment := xslprocessor.valueOf(asmContext, 'comment');
        EXCEPTION WHEN OTHERS THEN
          comment := 'erreur: commentaire trop long';
        END;
        title := xslprocessor.valueOf(asmContext, 'label');
        asmResource := xslprocessor.selectSingleNode(asmContext, 'asmResource');
        resourceType := xslprocessor.valueOf(asmResource, 'asmResourceType');
        uri := xslprocessor.valueOf(asmResource, 'identifier[@type="uri"]');
        copyright := xslprocessor.valueOf(asmResource, 'license');

        dbms_output.put('"' || site_id || '",'); 
        dbms_output.put('"' || department || '",');
        dbms_output.put('"' || program || '",');
--        dbms_output.put('"' || REPLACE(SUBSTR(site_id, 0, INSTR(site_id, '.') - 1), '-', NULL) || '",'); 
        -- nom du fichier sans le path
        dbms_output.put('"' || SUBSTR(uri, INSTR(uri,'/', -1, 1)+1) || '",');
        dbms_output.put('"' || replace(title, '"', '') || '",'); 
        dbms_output.put('"' || resourceType || '",');
        dbms_output.put('"' || copyright || '",');
        dbms_output.put('"' || access || '",');
        dbms_output.put('"' || level || '",');
        dbms_output.put('"' || important || '",');
        dbms_output.put('"' || replace(replace(comment, '"', ''),CHR(10),'') || '"');
                
        dbms_output.put_line('');
      END LOOP;
      END IF;
      
      -- free memory
      xmlparser.freeParser(parser);
      xmldom.freeDocument(xmldoc);

    END;
  END LOOP;
  CLOSE course_outline_cur;
END;
/
