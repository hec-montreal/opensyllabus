package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericEtudiantCoursMapFactory {

    private static Log log = LogFactory
	    .getLog(GenericEtudiantCoursMapFactory.class);

    public static EtudiantCoursMap buildMap(String completeFileName,
	    DetailCoursMap detailCoursMap, DetailSessionsMap detailSessionsMap)
	    throws java.io.IOException {

	// Pour faire reference aux cours de chaque etudiant, on a besoin du
	// details des cours... Sinon on ne peut pas continuer.
	if (detailCoursMap == null) {
	    throw new IllegalStateException(
		    "Erreur: la structure DetailCoursMap doit etre initialisee "
			    + "avant EtudiantCoursMap");
	}

	EtudiantCoursMap map;
	try {
	    map = getInstance(completeFileName);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new EtudiantCoursMap();
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

	    EtudiantCoursMapEntry entry;
	    String emplId = token[i++];
	    String catalogNbr = token[i++];
	    String strm = token[i++];
	    String sessionCode = token[i++];
	    String classSection = token[i++];
	    String status = token[i++];
	    String strmId = strm + sessionCode;

	    if (catalogNbr != null)
		catalogNbr = catalogNbr.trim();

	    if (map.containsKey(emplId)) {
		entry = map.get(emplId);
	    } else {
		entry = new EtudiantCoursMapEntry(emplId);
		map.put(entry);
	    }

	    DetailCoursMapEntry cours =
		    detailCoursMap.get(catalogNbr, strmId, classSection);
	    // TODO: remove for tests purposes, put back after

	    // if (cours == null) {
	    // throw new IllegalStateException("cours == null pour " + buffer);
	    // }
	    if (cours != null) {

		// On ajoute le cours a cet etudiant uniquement s'il ne l'a pas
		// deja
		// (ce qui est le cas pour tous ses cours d'anciennes sessions).
		if (!entry.containsCours(cours)) {
		    entry.addCours(cours);
		}

		// et la contrepartie dans le cours...
		if (!cours.containsEtudiant(entry)) {
		    cours.addEtudiant(entry);
		}
	    }
	}

	// ferme le tampon
	breader.close();

	return map;
    } // buildMap

    /**
     * Enleve tous les cours correspondant a la session specifiee dans chaque
     * etudiant. L'operation complementaire, c'est a dire enlever les etudiants
     * des cours correspondants est aussi effectuee afin de maintenir
     * l'integrite.
     */
    private static void removeAllCours(EtudiantCoursMap map,
	    DetailCoursMap detailCoursMap, DetailSessionsMapEntry latestSession) {
	Iterator<EtudiantCoursMapEntry> etudiants = map.values().iterator();
	EtudiantCoursMapEntry etudiant;
	while (etudiants.hasNext()) {
	    etudiant = (EtudiantCoursMapEntry) etudiants.next();
	    etudiant.removeAllCours(latestSession);
	}

	Iterator<DetailCoursMapEntry> coursIterator =
		detailCoursMap.values().iterator();
	DetailCoursMapEntry cours;
	while (coursIterator.hasNext()) {
	    cours = (DetailCoursMapEntry) coursIterator.next();
	    if (cours.isInSession(latestSession)) {
		cours.removeAllEtudiants();
	    }
	}
    } // removeAllCours

    public static EtudiantCoursMap getInstance(String completeFileName)
	    throws IOException {

	return new EtudiantCoursMap();
    }

    protected static void print(String msg) {
	log.info("GenericEtudiantCoursMapFactory: " + msg);
    }
}
