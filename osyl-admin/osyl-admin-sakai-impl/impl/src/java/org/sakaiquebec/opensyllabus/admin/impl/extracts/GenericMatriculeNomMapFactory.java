
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

public class GenericMatriculeNomMapFactory {

    private static final String DEFAULT_BASE_NAME = "matricule_nom";

    public static MatriculeNomMap buildMap(String dataDir)
	throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME);
    }

    /**
     * Cette methode lit les extracts pour creer les MatriculeNomMapEntry en
     * consequence. Si un MatriculeNomMapEntry avec le meme matricule existe
     * deja il est utilise, et donc potentiellement mis a jour (par exemple
     * changement de statut d'etudiant a prof, correction d'une erreur dans le
     * prenom...).<br><br>
     *
     * Ceci est important car lorsqu'on definit un stagiaire dans l'interface
     * prof, cela ajoute des donnees dans le MatriculeNomMapEntry du stagiaire
     * (et le DetailCoursMapEntry du cours correspondant). On perdrait ces
     * donnees si on ne procedait pas ainsi.
     *
     */
    private static MatriculeNomMap buildMap(String dataDir, String baseName)
	throws java.io.IOException {

	MatriculeNomMap map;
//	try {
	    //map = getInstance(dataDir);
		map = new MatriculeNomMap();
	    print("Mise a jour de la map...");
/*	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new MatriculeNomMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}
*/
	    BufferedReader breader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(dataDir + "/" + baseName + ".dat")));
	    
	//BufferedReader breader = MapFactory.getExtractReader(dataDir, baseName);
	String buffer, matricule;
	StringTokenizer tokenizer;
	MatriculeNomMapEntry entry;
	Map<String,String> readItems = new HashMap<String,String>();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    tokenizer = new StringTokenizer(buffer,";");
	    matricule = tokenizer.nextToken();
	    System.out.println(matricule);
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
	    if (! readItems.containsKey(entry.getMatricule())) {
		System.out.println("Suppression de " + entry.toString());
		iterator.remove();
	    }
	}

	return map;

    } // buildMap

    public static MatriculeNomMap getInstance(String dataDir)
	throws IOException {

	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    private static MatriculeNomMap getInstance(String dataDir, String mapName)
	throws IOException {

	return  new MatriculeNomMap();
    }

    protected static void print(String msg) {
	System.out.println("GenericMatriculeNomMapFactory: " + msg);
    }
}
