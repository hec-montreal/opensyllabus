
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericExamensMapFactory {

   private static Log log = LogFactory.getLog(GenericExamensMapFactory.class);
   private static final String DEFAULT_BASE_NAME = "examens";

    public static void addExamens(String dataDir,
								  DetailCoursMap detailCoursMap)
		throws java.io.IOException {

		addExamens(dataDir, DEFAULT_BASE_NAME, detailCoursMap);
    }

    /**
     * Cette methode lit les extracts pour ajouter les ExamensMapEntry aux
     * DetailCoursMapEntry correspondants.<br><br>
     *
     *
     */
    public static void addExamens(String dataDir,
								  String baseName,
								  DetailCoursMap detailCoursMap)
				throws java.io.IOException {

		// Pour faire reference aux cours de chaque examen, on a besoin du
		// details des cours... S'il n'est pas disponible, on ne peut pas
		// continuer
		if (detailCoursMap == null) {
	    throw new IllegalStateException(
				"Erreur: la structure DetailCoursMap doit etre initialisee "
				+ "avant ExamensMap");
		}

		removeAllExams(detailCoursMap);

		/* ExamensMap map;
		try {
			map = getInstance(dataDir);
			print("Mise a jour de la map...");
		} catch (FileNotFoundException e) {
			print("La map n'a pas ete trouvee, on la recree au complet.");
			map = new ExamensMap();
		} catch (IOException e) {
			print("buildMap: exception dans getInstance: " + e);
			throw e;
		}
		*/

		BufferedReader breader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(dataDir + "/" + baseName + ".dat"),"ISO-8859-1"));
		String buffer;
		StringTokenizer tokenizer;

		/*
		  On fait le tour des lignes du fichier. Exemple du contenu:

		  2003;2062;1;2006-07-05;18:30;21:30;PricewaterhouseCoopers;�difice C�te-Sainte-Catherine;TOUS;FIN
		  2004;2062;1;2006-06-01;18:30;20:30;H�l�ne-Desmarais;�difice C�te-Sainte-Catherine;A � HALI;INTR
		  2004;2062;1;2006-06-01;18:30;20:30;Xerox Canada;�difice C�te-Sainte-Catherine;HALJ � Z;INTR
		*/
		String numeroHEL, session, periode, date, hrDebut, hrFin, salle,
			edifice, repartition, type;
		StringBuffer erreurs = new StringBuffer();
		while ((buffer = breader.readLine()) != null) {
			tokenizer = new StringTokenizer(buffer,";");

			try {
				numeroHEL   = tokenizer.nextToken();
				session     = tokenizer.nextToken();
				periode     = tokenizer.nextToken();
				date        = tokenizer.nextToken();
				hrDebut     = tokenizer.nextToken();
				hrFin       = tokenizer.nextToken();
				salle       = tokenizer.nextToken(); // TODO: penser aux entrees des DF1...
				edifice     = tokenizer.nextToken();
				repartition = tokenizer.nextToken();
				type        = tokenizer.nextToken();
			} catch (java.util.NoSuchElementException e) {
				log.info("GenericExamensMapFactory: NoSuchElementException: " + buffer);
				erreurs.append(buffer).append("\n");
				continue;
			}


			/*
			 * 2006-12-18: J'allais creer une map pour les examens comme tel
			 * mais je crois qu'il n'y a pas de besoin comme tel et en plus ca
			 * sera plus facile si les examens sont directement accessibles
			 * depuis les cours. Si le besoin d'une map s'avere alors je
			 * pourrais consid�rer ceci:
			 *
			 * Je pourrais utiliser comme cl�: numeroHEL + session + periode + 1
			 * (si c la premi�re entr�e rencontr�e) et dans ce cas je v�rifie si
			 * on en a d�j� une dans la map et si c la m�me r�partition.

			if(map.containsKey(matricule)) {
				entry = map.get(matricule);
			} else {
				entry = new ExamensMapEntry(matricule);
				map.put(entry);
			}
			*/

			DetailCoursMapEntry cours = detailCoursMap.get(
												numeroHEL, session, periode);
			if (cours == null) {
				throw new IllegalStateException("cours == null pour " + buffer);
			}

			// On cree l'examen avec une reference au cours. Au debut je n'avais
			// pas fait ce lien mais c'est finalement necessaire car il y a
			// plusieurs ExamenMapEntry par DetailCoursMapEntry (meme pour un
			// examen-evenement, a cause des repartitions en plusieurs salles)
			ExamenMapEntry examen = new ExamenMapEntry(cours, date,
						hrDebut, hrFin, salle, edifice, repartition, type);

			// On ajoute l'examen a ce cours
			cours.addExamen(examen);

			// System.out.println(examen.toString());
		}

		if (erreurs.length() > 0) {
			erreurs.insert(0, "Les lignes suivantes ont g�n�r� une NoSuchElementException.\n"
						   + "Les examens correspondants n'ont donc pas �t� cr��.\n\n");
	/*		try {
				ca.hec.mail.Mailer.postMail("remi.saias@hec.ca",
											"Probl�me avec l'extract des examens",
											erreurs.toString(),
											"remi.saias@hec.ca");
			} catch (javax.mail.MessagingException e) {
				print("Impossible d'envoyer le message d'erreur: " + erreurs.toString());
			}
*/		}

		// ferme le tampon
		breader.close();

    } // addExamens

	private static final void removeAllExams(DetailCoursMap detailCoursMap) {

		Iterator<DetailCoursMapEntry> coursIterator = detailCoursMap.values().iterator();
		DetailCoursMapEntry cours;
		while (coursIterator.hasNext()) {
			cours = (DetailCoursMapEntry) coursIterator.next();
			cours.removeAllExamens();
		}

	} // removeAllExams



	/*
    public static ExamensMap getInstance(String dataDir)
	throws IOException {

	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    public static ExamensMap getInstance(String dataDir, String mapName)
	throws IOException {

	return (ExamensMap) MapFactory.deserializeMap(dataDir, mapName);
    }
	*/

    protected static void print(String msg) {
		log.info("GenericExamensMapFactory: " + msg);
    }
}
