DECLARE
  CURSOR course_outline_cur is
    SELECT site_id, content FROM osyl_co
    WHERE "ACCESS" = 'attendee' 
    AND published = '1'
    AND site_id LIKE '%.A2013.%'
    ORDER BY site_id;

  -- xml parser variables  
  parser xmlparser.Parser;
  xmldoc xmldom.DOMDocument;
  xmlnode xmldom.DOMNode;
  citations xmldom.DOMNodeList;
  
  plancours_clob CLOB;
  site_id VARCHAR(25 CHAR);
  title VARCHAR(700 CHAR);
  author VARCHAR(700 CHAR);
  year VARCHAR(700 CHAR);
  journal VARCHAR(700 CHAR);
  volume VARCHAR(100);
  issue VARCHAR(100);
  output VARCHAR(4000 CHAR);
  isn VARCHAR(200 CHAR);
  library_url VARCHAR(700 CHAR);  
  
BEGIN
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
      citations := xslprocessor.selectNodes(xmlnode, '//asmResource[@xsi:type="BiblioResource"][resourceType="article"]', 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance');

      IF xmldom.getLength(citations) > 0 THEN

      dbms_output.put_line('<tr><th>');
      dbms_output.put_line(REPLACE(SUBSTR(site_id, 0, INSTR(site_id, '.')), '-', NULL) || ' ' || site_id);      
      dbms_output.put_line('</th></tr>');

      -- for each document in the cours outline  
      FOR j in 1..xmldom.getLength(citations) LOOP
        author := xslprocessor.valueOf(xmldom.item(citations, j-1), 'author');
        title := xslprocessor.valueOf(xmldom.item(citations, j-1), 'title');
        year := xslprocessor.valueOf(xmldom.item(citations, j-1), 'year');
        journal := xslprocessor.valueOf(xmldom.item(citations, j-1), 'journal');
        volume := xslprocessor.valueOf(xmldom.item(citations, j-1), 'volume');
        issue := xslprocessor.valueOf(xmldom.item(citations, j-1), 'issue');
        library_url := xslprocessor.valueOf(xmldom.item(citations, j-1), 'identifier[@type="library"]');
        isn := xslprocessor.valueOf(xmldom.item(citations, j-1), 'identifier[@type="isn"]');

        dbms_output.put('- ');
        if author is not NULL then
          dbms_output.put(author || ' ');
        end if;
        if year is not NULL then
          dbms_output.put('(' || year || ')');
        end if;
        if author is not NULL or year is not NULL then
          dbms_output.put('. ');
        end if;
        dbms_output.put('"' || title || '"');
        dbms_output.put(', ' || journal);
        if volume is not NULL then
          dbms_output.put(', vol. ' || volume);
        end if;
        if issue is not NULL then
          dbms_output.put(', iss. ' || issue);
        end if;
        dbms_output.new_line();
        
        if isn is not NULL then
          dbms_output.put_line('* ISN: ' || isn);
        end if;

        if library_url is not NULL then
          dbms_output.put_line('* ' || library_url);
        end if;

      END LOOP;
        dbms_output.put_line('----------');
      END IF;
      
      -- free memory
      xmlparser.freeParser(parser);
      xmldom.freeDocument(xmldoc);

    END;
  END LOOP;
  CLOSE course_outline_cur;
END;
/
