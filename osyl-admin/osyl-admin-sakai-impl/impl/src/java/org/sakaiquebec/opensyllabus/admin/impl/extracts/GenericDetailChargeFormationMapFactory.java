package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.*;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericDetailChargeFormationMapFactory {

    private static Log log = LogFactory.getLog(DetailChargeFormationMap.class);

    public static DetailChargeFormationMap buildMap(String completeFileName,
	    DetailCoursMap detailCoursMap) throws java.io.IOException {


	DetailChargeFormationMap map;
	try {
	    map = getInstance(completeFileName);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new DetailChargeFormationMap();
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
	String role, emplId, catalogNbr, strm, sessionCode, classSection;

	while ((buffer = breader.readLine()) != null) {
	    token = buffer.split(delimeter);
	    DetailChargeFormationMapEntry entry;
	    emplId = token[0];
	    role = token[1];
	    catalogNbr = token[2];
	    strm = token[3];
	    sessionCode = token[4];
	    classSection = token[5];

	    if (map.containsKey(emplId)) {
		entry = map.get(emplId);
	    } else {
		entry = new DetailChargeFormationMapEntry();
		entry.setEmplId(emplId);
		entry.setRole(role);
		entry.setCours(new Vector<DetailCoursMapEntry>());
	    }

	    DetailCoursMapEntry cours =
		    detailCoursMap.get(catalogNbr, strm + sessionCode, classSection);

	    if (cours != null)
		entry.addCours(cours);

	    map.put(entry);
	}

	// ferme le tampon
	breader.close();

	return map;
    } // buildMap

    public static DetailChargeFormationMap getInstance(String completeFileName)
	    throws IOException {
	return new DetailChargeFormationMap();
    }

    protected static void print(String msg) {
	log.info("GenericDetailChargeFormationMapFactory: " + msg);
    }
}
