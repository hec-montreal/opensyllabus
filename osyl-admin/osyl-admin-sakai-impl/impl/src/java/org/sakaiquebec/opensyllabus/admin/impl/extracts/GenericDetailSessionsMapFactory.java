
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericDetailSessionsMapFactory {

    private static Log log = LogFactory.getLog(DetailSessionsMap.class);

    private static final String DEFAULT_BASE_NAME = "session";

    public static DetailSessionsMap buildMap(String dataDir)
	throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME);
    }

    /**
     * Cette methode lit les extracts pour creer les DetailSessionsMapEntry en
     * consequence. Les donnees sont cumulees d'une lecture des extracts a
     * l'autre afin de conserver pendant plusieurs sessions les
     * informations. Par contre etant donne qu'aucune information sur les
     * sessions n'est modifiee via Jive, et qu'on considere l'extract comme
     * "master" (dans le sens ou la bonne information vient toujours de la), on
     * ecrase toujours les informations existante dans la map.<br><br>
     *
     */
    public static DetailSessionsMap buildMap(String dataDir, String baseName)
	throws java.io.IOException {
    

	DetailSessionsMap map;
	try {
	    map = getInstance(dataDir);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new DetailSessionsMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	BufferedReader breader  = new BufferedReader(
			new InputStreamReader(
					new FileInputStream(dataDir + File.separator + baseName + ".dat"),"ISO-8859-1"));
	String buffer;

	//We remove the first line containing the title
	breader.readLine();
	
	// fait le tour des lignes du fichier
	String delimeter = ";";
	String [] token;
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
	    
	    map.put(entry);
	}

	// ferme le tampon
	breader.close();

	return map;
    } // buildMap

    public static DetailSessionsMap getInstance(String dataDir)
	throws IOException {
	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    private static DetailSessionsMap getInstance(String dataDir, String mapName)
	throws IOException {

	return new DetailSessionsMap() ;
    }

    protected static void print(String msg) {
	log.info("GenericDetailSessionsMapFactory: " + msg);
    }
}
