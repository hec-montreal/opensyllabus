package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericMatriculeNomMapFactory {

    private static Log log = LogFactory
	    .getLog(GenericMatriculeNomMapFactory.class);

    public static MatriculeNomMap buildMap(String completeFileName)
	    throws java.io.IOException {

	MatriculeNomMap map;
	// try {
	// map = getInstance(dataDir);
	map = new MatriculeNomMap();
	print("Mise a jour de la map...");
	/*
	 * } catch (FileNotFoundException e) {
	 * print("La map n'a pas ete trouvee, on la recree au complet."); map =
	 * new MatriculeNomMap(); } catch (IOException e) {
	 * print("buildMap: exception dans getInstance: " + e); throw e; }
	 */
	BufferedReader breader =
		new BufferedReader(new InputStreamReader(new FileInputStream(
			completeFileName), "ISO-8859-1"));

	// BufferedReader breader = MapFactory.getExtractReader(dataDir,
	// baseName);
	String buffer, matricule;
	StringTokenizer tokenizer;
	MatriculeNomMapEntry entry;
	Map<String, String> readItems = new HashMap<String, String>();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    tokenizer = new StringTokenizer(buffer, ";");
	    matricule = tokenizer.nextToken();
	    // on reprend l'entree existante
	    if (map.containsKey(matricule)) {
		entry = map.get(matricule);
	    } else {
		entry = new MatriculeNomMapEntry();
		entry.setMatricule(matricule);
		map.put(entry);
	    }
	    entry.setLastName(tokenizer.nextToken());
	    entry.setFirstName(tokenizer.nextToken());
	    entry.setStatus(tokenizer.nextToken());

	    // On garde trace des matricules qu'on a lus
	    readItems.put(matricule, "");
	}

	// ferme le tampon
	breader.close();

	// On supprime les entrees qui sont dans la map mais pas dans les
	// extracts:
	Iterator<MatriculeNomMapEntry> iterator = map.values().iterator();
	while (iterator.hasNext()) {
	    entry = (MatriculeNomMapEntry) iterator.next();
	    if (!readItems.containsKey(entry.getMatricule())) {
		log.info("Suppression de " + entry.toString());
		iterator.remove();
	    }
	}

	return map;

    } // buildMap

    public static MatriculeNomMap getInstance(String completeFileName)
	    throws IOException {

	return new MatriculeNomMap();
    }

    protected static void print(String msg) {
	log.info("GenericMatriculeNomMapFactory: " + msg);
    }
}
