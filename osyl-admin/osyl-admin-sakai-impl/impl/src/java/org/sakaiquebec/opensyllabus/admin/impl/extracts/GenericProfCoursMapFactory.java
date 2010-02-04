package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericProfCoursMapFactory {

	private static Log log = LogFactory.getLog(GenericProfCoursMapFactory.class);
	private static final String DEFAULT_BASE_NAME = "prof_cours3";

	public static ProfCoursMap buildMap(String dataDir,
			DetailCoursMap detailCoursMap, DetailSessionsMap detailSessionsMap)
			throws java.io.IOException {

		return buildMap(dataDir, DEFAULT_BASE_NAME, detailCoursMap,
				detailSessionsMap);
	}

	/**
	 * Cette methode lit les extracts pour creer les ProfCoursMapEntry en
	 * consequence. Si un ProfCoursMapEntry avec le meme matricule existe deja
	 * il est reutilise, et potentiellement mis a jour (par exemple ajout /
	 * suppression de cours).<br>
	 * <br>
	 *
	 * Ceci permet a un prof de continuer a acceder a ses forums meme lorsque la
	 * session est finie.<br>
	 * <br>
	 *
	 * On utilise le meme processus que pour les etudiants, meme s'il peut
	 * sembler moins pertinent. Dans le cas ou un prof cesse de donner un cours
	 * (par exemple: changement de personnel au debut de la session, ou juste
	 * avant), il faut effectivement supprimer ce cours de sa liste de cours
	 * pour qu'il ne puisse plus consulter/modifier le forum... Cependant il ne
	 * faut pas supprimer un cours du prof simplement parce qu'il n'est plus
	 * dans l'extract puisque les cours des sessions passees n'y sont jamais. Il
	 * faut donc desinscrire un prof du cours uniquement s'il s'agit de la
	 * session courante. Le processus utilise consiste a enlever tous les cours
	 * du prof correspondant a la derniere session connue. Pour cette
	 * verification on fait appel a DetailSessionsMap.getLatestSession(). <br>
	 * <br>
	 *
	 * A noter que la derniere session connue dans le systeme est soit la
	 * session en cours soit la session qui s'apprete a commencer. Ceci est du
	 * au mode de generation des extracts qu'on a mis en place: 1. on exporte
	 * toujours la session en cours, 2. on exporte toujours la session
	 * precedante sauf quand on est 2 semaines avant le debut d'une session,
	 * dans ce cas on exporte les donnees de cette session afin de permettre aux
	 * profs de creer leur forums avec un peu d'avance.
	 *
	 * Donc quand on est a 2 semaines ou moins du debut d'une session, on se
	 * retrouve a chaque invocation a vider et re-remplir les cours non pas de
	 * la session courante mais de la session a venir. C'est exactement ce qu'on
	 * veut puisque dans ce cas, la session en cours arrivant a sa fin, il n'y a
	 * plus d'inscription ou desinscription a faire pour elle, alors que c'est
	 * le cas pour la session a venir.
	 *
	 */
	public static ProfCoursMap buildMap(String dataDir, String baseName,
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
			map = getInstance(dataDir);
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

		BufferedReader breader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataDir + "/" + baseName + ".dat"), "ISO-8859-1"));
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
			String unitMinimum = token[i++];
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
			DetailCoursMapEntry cours = detailCoursMap.get(catalogNbr, strmId,
					section);

			//TODO: removed for the purpose of the tests, put back when done
//			if (cours == null) {
//				// throw new IllegalStateException("cours == null pour " +
//				// buffer);
//			}

			entry.setUnitMinimum(unitMinimum);
			entry.setAcadOrg(acadOrg);
			entry.setRole(role);
			entry.setStrmId(strmId);
			if (cours != null && "Enseignant".equalsIgnoreCase(role.trim())) {
				// On ajoute le cours a cet etudiant uniquement s'il ne l'a pas
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
		}

		// ferme le tampon
		breader.close();

		return map;
	} // buildMap

	/**
	 * Enleve tous les cours correspondant a la session specifiee dans chaque
	 * prof. L'operation complementaire, c'est a dire enlever les profs des
	 * cours correspondants est aussi effectuee afin de maintenir l'integrite.
	 *
	 */
	private static void removeAllCours(ProfCoursMap map,
			DetailCoursMap detailCoursMap, DetailSessionsMapEntry latestSession) {
		Iterator<ProfCoursMapEntry> profs = map.values().iterator();
		ProfCoursMapEntry prof;
		while (profs.hasNext()) {
			prof = (ProfCoursMapEntry) profs.next();
			prof.removeAllCours(latestSession);
		}

		Iterator<DetailCoursMapEntry> coursIterator = detailCoursMap.values()
				.iterator();
		DetailCoursMapEntry cours;
		while (coursIterator.hasNext()) {
			cours = (DetailCoursMapEntry) coursIterator.next();
			if (cours.isInSession(latestSession)) {
				cours.removeAllProfs();
			}
		}
	} // removeAllCours

	public static ProfCoursMap getInstance(String dataDir) throws IOException {

		return getInstance(dataDir, DEFAULT_BASE_NAME);
	}

	public static ProfCoursMap getInstance(String dataDir, String mapName)
			throws IOException {

		return new ProfCoursMap();
	}

	protected static void print(String msg) {
		log.info("GenericProfCoursMapFactory: " + msg);
	}
}
