
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

public class GenericDetailSessionsMapFactory {

    private static final String DEFAULT_BASE_NAME = "detail_sessions";

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
					new FileInputStream(dataDir + "/" + baseName + ".dat"),"utf8"));
	String buffer;
	StringTokenizer tokenizer;

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    DetailSessionsMapEntry entry = new DetailSessionsMapEntry();
	    tokenizer = new StringTokenizer(buffer,";");
	    entry.setNumero(tokenizer.nextToken());
	    entry.setLongForm(tokenizer.nextToken());
	    entry.setShortForm(tokenizer.nextToken());
	    entry.setStartDate(tokenizer.nextToken());
	    entry.setEndDate(tokenizer.nextToken());

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
	System.out.println("GenericDetailSessionsMapFactory: " + msg);
    }
}
