-- instructions pour rouler ce script dans SQL PLUS:
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
    AND site_id LIKE '%._201_.%'
    ORDER BY site_id;
    
  -- Retrieve citations modified since this date, inclusive.  Can be null
  since_date CONSTANT DATE := null; --TO_DATE('2014-09-01', 'YYYY-MM-DD');

  -- xml parser variables  
  parser xmlparser.Parser;
  xmldoc xmldom.DOMDocument;
  xmlnode xmldom.DOMNode;
  asmContextList xmldom.DOMNodeList;
  asmContext xmldom.DOMNode;
  asmResource xmldom.DOMNode;
  asmUnit xmldom.DOMNode;
  
  plancours_clob CLOB;
  site_id VARCHAR(50 CHAR);
  title VARCHAR(700 CHAR);
  asmUnitLabel VARCHAR(700 CHAR);
  author VARCHAR(700 CHAR);
  year VARCHAR(700 CHAR);
  journal VARCHAR(700 CHAR);
  volume VARCHAR(100);
  issue VARCHAR(100);
  output VARCHAR(4000 CHAR);
  isn VARCHAR(200 CHAR);
  library_url VARCHAR(4000 CHAR);
  other_url VARCHAR(4000 CHAR);
  resourceType VARCHAR(700 CHAR);
  level VARCHAR(700 CHAR);
  modifiedDate VARCHAR(100 CHAR);
  
BEGIN
  dbms_output.enable(NULL);

  -- headers
  dbms_output.put_line('sigle,id,titre de la page,type de référence,niveau exigence,référence,isn,url,url manuel');

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
      asmContextList := xslprocessor.selectNodes(xmlnode, '//asmContext[@xsi:type="BiblioContext"]', 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance');

      IF xmldom.getLength(asmContextList) > 0 THEN

      -- for each document in the cours outline  
      FOR j in 1..xmldom.getLength(asmContextList) LOOP
        asmContext := xmldom.item(asmContextList, j-1);
        level := xslprocessor.valueOf(asmContext, 'level');
        asmResource := xslprocessor.selectSingleNode(asmContext, 'asmResource');
        asmUnit :=  xslprocessor.selectSingleNode(asmContext, '../../..');
        asmUnitLabel := xslprocessor.valueOf(asmUnit, 'label');
        author := xslprocessor.valueOf(asmResource, 'author');
        title := replace(replace(xslprocessor.valueOf(asmResource, 'title'),chr(10),''),chr(13),'');
        year := xslprocessor.valueOf(asmResource, 'year');
        journal := xslprocessor.valueOf(asmResource, 'journal');
        volume := xslprocessor.valueOf(asmResource, 'volume');
        issue := xslprocessor.valueOf(asmResource, 'issue');
        library_url := xslprocessor.valueOf(asmResource, 'identifier[@type="library"]');
        other_url := xslprocessor.valueOf(asmResource, 'identifier[@type="other_link"]');
        isn := xslprocessor.valueOf(asmResource, 'identifier[@type="isn"]');
        resourceType := xslprocessor.valueOf(asmResource, 'resourceType');
        modifiedDate := xslprocessor.valueOf(asmResource, 'modified');

        if (since_date is null or since_date <= TO_DATE(SUBSTR(modifiedDate, 1, 10), 'YYYY-MM-DD')) then 
          dbms_output.put('"' || REPLACE(SUBSTR(site_id, 0, INSTR(site_id, '.') - 1), '-', NULL) || '",'); 
          dbms_output.put('"' || site_id || '",');
          dbms_output.put('"' || asmUnitLabel || '",');
          dbms_output.put('"' || resourceType || '",');
          dbms_output.put('"' || level || '",');
        
          dbms_output.put('"');
          if author is not NULL then
            dbms_output.put(author || ' ');
          end if;
          if year is not NULL then
            dbms_output.put('(' || year || ')');
          end if;
          if author is not NULL or year is not NULL then
            dbms_output.put('. ');
          end if;
          dbms_output.put(REPLACE(title, '"'));
		      if journal is not NULL then 
			      dbms_output.put(', ' || journal);
		      end if;
          if volume is not NULL then
            dbms_output.put(', vol. ' || volume);
          end if;
          if issue is not NULL then
            dbms_output.put(', iss. ' || issue);
          end if;
          dbms_output.put_line('","' || isn || '","' || library_url || '","' || other_url || '"');
        end if; --check the date
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
