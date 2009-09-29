
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.*;

public class GenericDetailCoursMapFactory {

    private static final String DEFAULT_BASE_NAME = "detail_cours";

    public static DetailCoursMap buildMap(String dataDir)
	throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME);
    }

    /**
     * Cette methode lit les extracts pour creer les DetailCoursMapEntry en
     * consequence. Si un DetailCoursMapEntry avec la meme cle existe deja il
     * est utilise, et donc potentiellement mis a jour (par exemple changement
     * du coordonnateur...).<br><br>
     *
     * Ceci est important car lorsqu'on definit un stagiaire dans l'interface
     * prof, cela ajoute des donnees dans le DetailCoursMapEntry du cours (et le
     * MatriculeNomMapEntry du stagiaire). On perdrait ces donnees si on ne
     * procedait pas ainsi.
     *
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

	BufferedReader breader = new BufferedReader(
			new InputStreamReader(
					new FileInputStream(dataDir + "/" + baseName + ".dat"),"utf8"));
	String buffer, key, numeroHEL, session, periode;
	DetailCoursMapEntry entry;

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
		String[] tokens = buffer.split(";");
		int i = 0;

	    numeroHEL = tokens[i++];
	    session = tokens[i++];
	    periode = tokens[i++];
	    key = DetailCoursMapEntry.getUniqueKey(numeroHEL, session, periode);

	    // on reprend l'entree existante
	    if (map.containsKey(key)) {
			entry = map.get(key);
	    } else {
			entry = new DetailCoursMapEntry();
			entry.setNumeroHEL(numeroHEL);
			entry.setSession(session);
			entry.setPeriode(periode);
	    }
	    entry.setProgramme(tokens[i++]);
	    entry.setNumeroRepertoire(tokens[i++]);
	    entry.setSection(tokens[i++]);
	    // Le token suivant est le coordonnateur, on le gere dans une deuxieme
	    // passe avec setCoordonnateurs, donc on le passe pour le moment.
		i++;
	    entry.setTitreAlt(tokens[i++]);
	    entry.setTitre(tokens[i++]);
		try {
			entry.setLangue(tokens[i++]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// Ce n'est pas grave, on met la langue par defaut
			print ("langue manquante pour le cours [" + numeroHEL + "]");
		}

	    map.put(entry);
	}

	// ferme le reader
	breader.close();

	return map;
    } // buildMap

    public static void setCoordonnateurs(String dataDir, DetailCoursMap map,
										 ProfCoursMap profMap)
		throws java.io.IOException {

		setCoordonnateurs(dataDir, DEFAULT_BASE_NAME, map, profMap);
    }

    private static void setCoordonnateurs(String dataDir, String baseName,
										  DetailCoursMap map, ProfCoursMap profMap)
		throws java.io.IOException {

		if (map == null) {
			throw new IllegalStateException("GenericDetailCoursMapFactory"
											+ ".setCoordonnateurs: l'instance est nulle!");
		}

		if (profMap == null) {
			throw new IllegalStateException("GenericDetailCoursMapFactory"
											+ ".setCoordonnateurs: la ProfCoursMap est nulle!");
		}

		BufferedReader breader  = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(dataDir + "/" + baseName + ".dat")));
		String buffer, key, numeroHEL, session, periode;
		DetailCoursMapEntry entry;

	    // TODO: on ne prend pas en compte le changement ou la suppression
	    // d'un coordonnateur... il faut ajouter ca.

		// fait le tour des lignes du fichier
		while ((buffer = breader.readLine()) != null) {
			String[] tokens = buffer.split(";");
			int i = 0;

			numeroHEL = tokens[i++];
			session = tokens[i++];
			periode = tokens[i++];
			key = DetailCoursMapEntry.getUniqueKey(numeroHEL, session, periode);

			// on reprend l'entree existante
			entry = map.get(key);

			// On saute les parametres suivants
			// Programme
			i++;
			// NumeroRepertoire
			i++;
			// Section
			i++;
			// Le token suivant est le coordonnateur, et il est facultatif, il peut
			// donc etre vide:
			String matricule = tokens[i++].trim();
			if (matricule == null || matricule.length() == 0) {
				continue;
			}
			ProfCoursMapEntry coordonnateur = profMap.get(matricule);
			if (coordonnateur == null) {
				// On arrive ici dans le cas ou un prof coordonne un ou des
				// cours mais n'enseigne aucun cours. Dans ce cas, le
				// GenericProfCoursMapFactory n'a pas cree le ProfCoursMapEntry
				// correspondant. Et le fait qu'il soit defini comme prof dans
				// matricule_nom fait que isProfesseur() retourne vrai, mais
				// ProfCoursMap.get(...) retourne null... Donc on cree le
				// ProfCoursMapEntry maintenant, et on le met dans la
				// ProfCoursMap.
				System.out.println("setCoordonnateurs: creation ProfCoursMapEntry pour " + matricule);
				coordonnateur = new ProfCoursMapEntry(matricule);
				profMap.put(coordonnateur);
			}
			entry.setCoordonnateur(coordonnateur);
			coordonnateur.addCoursCoordonne(entry);
			map.put(entry);
		} // while readline

		// ferme le reader
		breader.close();

	} // setCoordonnateurs


	public static DetailCoursMap getInstance(String dataDir)
		throws IOException {

		return getInstance(dataDir, DEFAULT_BASE_NAME);
	}

	private static DetailCoursMap getInstance(String dataDir, String mapName)
		throws IOException {

		return  new DetailCoursMap() ;
	}

	protected static void print(String msg) {
		System.out.println("GenericDetailCoursMapFactory: " + msg);
	}
}
