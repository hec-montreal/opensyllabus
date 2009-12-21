
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

public class GenericEtudiantCoursMapFactory {

    private static final String DEFAULT_BASE_NAME = "etudiant_cours3";

    public static EtudiantCoursMap buildMap(String dataDir,
					    DetailCoursMap detailCoursMap,
					    DetailSessionsMap detailSessionsMap)
	throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME, detailCoursMap,
			detailSessionsMap);
    }

    /**
     * Cette methode lit les extracts pour creer les EtudiantCoursMapEntry en
     * consequence. Si un EtudiantCoursMapEntry avec le meme matricule existe
     * deja il est reutilise, et potentiellement mis a jour (par exemple ajout /
     * suppression de cours).<br><br>
     *
     * Ceci permet a un etudiant de continuer a acceder a ses forums meme
     * lorsque la session est finie.<br><br>
     *
     * Il y a une subtilite dans le processus. Dans le cas ou un etudiant
     * abandonne un cours, il faut effectivement supprimer ce cours de sa liste
     * de cours pour qu'il ne puisse plus consulter le forum... Cependant il ne
     * faut pas supprimer un cours de l'etudiant simplement parce qu'il n'est
     * plus dans l'extract puisque les cours des sessions passees n'y sont
     * jamais. Il faut donc desinscrire un etudiant du cours uniquement s'il
     * s'agit de la session courante. Le processus utilise consiste a enlever
     * tous les cours de l'etudiant correspondant a la derniere session
     * connue. Pour cette verification on fait appel a
     * DetailSessionsMap.getLatestSession().<br><br>
     *
     * A noter que la derniere session connue dans le systeme est soit la
     * session en cours soit la session qui s'apprete a commencer. Ceci est du
     * au mode de generation des extracts qu'on a mis en place:
     *   1. on exporte toujours la session en cours,
     *   2. on exporte toujours la session precedante sauf quand on est 2
     *      semaines avant le debut d'une session, dans ce cas on exporte les
     *      donnees de cette session afin de permettre aux profs de creer leur
     *      forums avec un peu d'avance.
     *
     * Donc quand on est a 2 semaines ou moins du debut d'une session, on se
     * retrouve a chaque invocation a vider et re-remplir les cours non pas de
     * la session courante mais de la session a venir. C'est exactement ce qu'on
     * veut puisque dans ce cas, la session en cours arrivant a sa fin, il n'y a
     * plus d'inscription ou desinscription a faire pour elle, alors que c'est
     * le cas pour la session a venir.
     *
     */
    public static EtudiantCoursMap buildMap(String dataDir,
					    String baseName,
					    DetailCoursMap detailCoursMap,
					    DetailSessionsMap detailSessionsMap)
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
	    map = getInstance(dataDir);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new EtudiantCoursMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	// Voir le commentaire de methode ci-dessus
//	removeAllCours(map, detailCoursMap,
//		       detailSessionsMap.getLatestSession());

	BufferedReader breader = new BufferedReader(
			new InputStreamReader(
					new FileInputStream(dataDir + "/" + baseName + ".dat"),"utf8"));
	String buffer;
	String[] token;
	int i;

	// We remove the first line containing the title
	breader.readLine();

	
	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    token = buffer.split(";");
	    i=0;
	    
	    EtudiantCoursMapEntry entry;
	    String emplId = token[i++];
	    String catalogNbr = token[i++];
	    String strm   = token[i++];
	    String sessionCode   = token[i++];
	    String classSection  = token[i++];
	    String status  = token[i++];
	    String strmId  = strm + sessionCode;
	    
	    if(map.containsKey(emplId)) {
		entry = map.get(emplId);
	    } else {
		entry = new EtudiantCoursMapEntry(emplId);
		map.put(entry);
	    }
	    
	    
	    DetailCoursMapEntry cours = detailCoursMap.get(
			catalogNbr, strmId, classSection);
	    //TODO: remove for tests purposes, put back after
	    
// 	    if (cours == null) {
// 		throw new IllegalStateException("cours == null pour " + buffer);
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
     *
     */
    private static void removeAllCours(EtudiantCoursMap map,
				       DetailCoursMap detailCoursMap,
				       DetailSessionsMapEntry latestSession) {
	Iterator<EtudiantCoursMapEntry> etudiants = map.values().iterator();
	EtudiantCoursMapEntry etudiant;
	while (etudiants.hasNext()) {
	    etudiant = (EtudiantCoursMapEntry) etudiants.next();
	    etudiant.removeAllCours(latestSession);
	}

	Iterator<DetailCoursMapEntry> coursIterator = detailCoursMap.values().iterator();
	DetailCoursMapEntry cours;
	while (coursIterator.hasNext()) {
	    cours = (DetailCoursMapEntry) coursIterator.next();
	    if (cours.isInSession(latestSession)) {
		cours.removeAllEtudiants();
	    }
	}
    } // removeAllCours


    public static EtudiantCoursMap getInstance(String dataDir)
	throws IOException {

	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    public static EtudiantCoursMap getInstance(String dataDir, String mapName)
	throws IOException {

	return  new EtudiantCoursMap() ;
    }

    protected static void print(String msg) {
	System.out.println("GenericEtudiantCoursMapFactory: " + msg);
    }
}
