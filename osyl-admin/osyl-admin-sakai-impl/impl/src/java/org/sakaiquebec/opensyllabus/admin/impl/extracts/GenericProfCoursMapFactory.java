package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericProfCoursMapFactory {

    private static Log log = LogFactory
	    .getLog(GenericProfCoursMapFactory.class);

    public static ProfCoursMap buildMap(String completeFileName,
	    DetailCoursMap detailCoursMap, DetailSessionsMap detailSessionsMap)
	    throws java.io.IOException {

	// Pour faire reference aux cours de chaque prof, on a besoin du details
	// des cours... S'il n'est pas disponible, on ne peut pas continuer
	if (detailCoursMap == null) {
	    throw new IllegalStateException(
		    "Erreur: la structure DetailCoursMap doit etre initialisee "
			    + "avant ProfCoursMap");
	}

	ProfCoursMap map;
	try {
	    map = getInstance(completeFileName);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new ProfCoursMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	// Voir le commentaire de methode ci-dessus
	// removeAllCours(map, detailCoursMap,
	// detailSessionsMap.getLatestSession());

	BufferedReader breader =
		new BufferedReader(new InputStreamReader(new FileInputStream(
			completeFileName), "ISO-8859-1"));
	String buffer;
	String[] token;
	int i;
	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    token = buffer.split(";");
	    i = 0;
	    ProfCoursMapEntry entry;
	    String emplId = token[i++];
	    String catalogNbr = token[i++];
	    String strm = token[i++];
	    String sessionCode = token[i++];
	    String section = token[i++];
	    String acadOrg = token[i++];
	    String role = token[i++];
	    String strmId = strm + sessionCode;

	    if (catalogNbr != null)
		catalogNbr = catalogNbr.trim();
	    if (map.containsKey(emplId)) {
		entry = map.get(emplId);
	    } else {
		entry = new ProfCoursMapEntry(emplId);
		map.put(entry);
	    }

	    // TODO: removed for the purpose of the tests, put back when done
	    // if (cours == null) {
	    // // throw new IllegalStateException("cours == null pour " +
	    // // buffer);
	    // }

	    DetailCoursMapEntry cours =
		    detailCoursMap.get(catalogNbr, strmId, section);

	    entry.setAcadOrg(acadOrg);
	    entry.setRole(role);
	    entry.setStrmId(strmId);
	    if (cours != null && "Enseignant".equalsIgnoreCase(role.trim())) {
		// On ajoute le cours a cet prof uniquement s'il ne l'a pas
		// deja
		// (ce qui est le cas pour tous ses cours d'anciennes sessions).
		if (!entry.containsCours(cours)) {
		    entry.addCours(cours);
		}

		// et la contrepartie dans le cours...
		if (!cours.containsProfesseur(entry)) {
		    cours.addProfesseur(entry);
		}

	    }

	    // Si c'est un coordonnateur on l'ajoute comme tel dans le cours
	    if (!entry.isEnseignant() && cours != null) {
		cours.setCoordonnateur(entry);
	    }
	}

	// ferme le tampon
	breader.close();

	return map;
    } // buildMap

    /**
     * Enleve tous les cours correspondant a la session specifiee dans chaque
     * prof. L'operation complementaire, c'est a dire enlever les profs des
     * cours correspondants est aussi effectuee afin de maintenir l'integrite.
     */
    private static void removeAllCours(ProfCoursMap map,
	    DetailCoursMap detailCoursMap, DetailSessionsMapEntry latestSession) {
	Iterator<ProfCoursMapEntry> profs = map.values().iterator();
	ProfCoursMapEntry prof;
	while (profs.hasNext()) {
	    prof = (ProfCoursMapEntry) profs.next();
	    prof.removeAllCours(latestSession);
	}

	Iterator<DetailCoursMapEntry> coursIterator =
		detailCoursMap.values().iterator();
	DetailCoursMapEntry cours;
	while (coursIterator.hasNext()) {
	    cours = (DetailCoursMapEntry) coursIterator.next();
	    if (cours.isInSession(latestSession)) {
		cours.removeAllProfs();
	    }
	}
    } // removeAllCours

    public static ProfCoursMap getInstance(String completeFileName)
	    throws IOException {

	return new ProfCoursMap();
    }

    protected static void print(String msg) {
	log.info("GenericProfCoursMapFactory: " + msg);
    }
}
