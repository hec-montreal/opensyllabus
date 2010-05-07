package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericDetailCoursMapFactory {

    private static final String DEFAULT_BASE_NAME = "cours";
    
    private static Log log = LogFactory.getLog(GenericDetailCoursMapFactory.class);

    public static DetailCoursMap buildMap(String dataDir)
	    throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME);
    }

    /**
     * Cette methode lit les extracts pour creer les DetailCoursMapEntry en
     * consequence. Si un DetailCoursMapEntry avec la meme cle existe deja il
     * est utilise, et donc potentiellement mis a jour (par exemple changement
     * du coordonnateur...).<br>
     * <br>
     * Ceci est important car lorsqu'on definit un stagiaire dans l'interface
     * prof, cela ajoute des donnees dans le DetailCoursMapEntry du cours (et le
     * MatriculeNomMapEntry du stagiaire). On perdrait ces donnees si on ne
     * procedait pas ainsi.
     */
    public static DetailCoursMap buildMap(String dataDir, String baseName)
	    throws java.io.IOException {

	DetailCoursMap map;
	try {
	    map = getInstance(dataDir);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new DetailCoursMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	InputStreamReader stream = new InputStreamReader(new FileInputStream(
			dataDir + "/" + baseName + ".dat"), "ISO-8859-1");
	BufferedReader breader =
		new BufferedReader(stream);
	String buffer, key, courseId, strm, sessionCode, catalogNbr, section, acadCareer;	
	DetailCoursMapEntry entry;

	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
		String[] tokens = buffer.split(";");
	    int i = 0;
		
	    courseId = tokens[i++];
	    strm = tokens[i++];
	    sessionCode = tokens[i++];
	    catalogNbr = tokens[i++];
	    section = tokens[i++];
	    
	    //Remove empty spaces
	    if (courseId != null)
	    	courseId = courseId.trim();
	    
	    key = DetailCoursMapEntry.getUniqueKey(catalogNbr, (strm + sessionCode), section);
	    // on reprend l'entree existante
	    if (map.containsKey(key)) {
		entry = map.get(key);
	    } else {
		entry = new DetailCoursMapEntry();
		entry.setCourseId(courseId);
		entry.setStrm(strm);
		entry.setSessionCode(sessionCode);
		entry.setCatalogNbr(catalogNbr);
		entry.setSection(section);
	    }
	    entry.setCourseTitleLong(tokens[i++]);

	    try {
		entry.setLangue(tokens[i++]);
	    } catch (ArrayIndexOutOfBoundsException e) {
		// Ce n'est pas grave, on met la langue par defaut
		print("langue manquante pour le cours [" + courseId + "]");
	    }
	    entry.setAcadOrg(tokens[i++]);
	    entry.setStrmId(tokens[i++]);
	    
	    entry.setAcaCareer(tokens[i++]);
	    
	    map.put(entry);
	}

	// ferme le reader
	breader.close();

	return map;
    } // buildMap

 
    public static DetailCoursMap getInstance(String dataDir) throws IOException {

	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    private static DetailCoursMap getInstance(String dataDir, String mapName)
	    throws IOException {

	return new DetailCoursMap();
    }

    protected static void print(String msg) {
	log.info("GenericDetailCoursMapFactory: " + msg);
    }
}
