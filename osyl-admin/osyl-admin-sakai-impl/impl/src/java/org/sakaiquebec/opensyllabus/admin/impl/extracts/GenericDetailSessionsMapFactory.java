package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.admin.cmjob.impl.OsylAbstractQuartzJobImpl;

import java.io.*;

public class GenericDetailSessionsMapFactory {

    private static Log log = LogFactory.getLog(DetailSessionsMap.class);

    public static DetailSessionsMap buildMap(String completeFileName)
	    throws java.io.IOException {

	DetailSessionsMap map;
	try {
	    map = getInstance(completeFileName);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new DetailSessionsMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	BufferedReader breader =
		new BufferedReader(new InputStreamReader(new FileInputStream(
			completeFileName), "ISO-8859-1"));
	String buffer;

	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	String delimeter = ";";
	String[] token;
	while ((buffer = breader.readLine()) != null) {
	    token = buffer.split(delimeter);
	    DetailSessionsMapEntry entry = new DetailSessionsMapEntry();
	    entry.setAcadCareer(token[0]);
	    entry.setStrm(token[1]);
	    entry.setDescFrancais(token[2]);
	    entry.setDescAnglais(token[3]);
	    entry.setDescShortFrancais(token[4]);
	    entry.setDescShortAnglais(token[5]);
	    entry.setBeginDate(token[6]);
	    entry.setEndDate(token[7]);
	    entry.setSessionCode(token[8]);
	    entry.setStrmId(token[9]);

		//ZCII-2783: Do not sync data after A2017
		if (OsylAbstractQuartzJobImpl.isBeforeA2017(Integer.parseInt(entry.getStrm())))
	    map.put(entry);
	}

	// ferme le tampon
	breader.close();

	return map;
    } // buildMap

    public static DetailSessionsMap getInstance(String completeFileName)
	    throws IOException {
	return new DetailSessionsMap();
    }

    protected static void print(String msg) {
	log.info("GenericDetailSessionsMapFactory: " + msg);
    }
}
