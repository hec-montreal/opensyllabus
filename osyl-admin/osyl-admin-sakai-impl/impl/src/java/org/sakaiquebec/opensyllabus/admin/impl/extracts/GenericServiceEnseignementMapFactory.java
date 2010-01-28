package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericServiceEnseignementMapFactory {

	private static final String DEFAULT_BASE_NAME = "service_enseignement";
	private static Log log = LogFactory.getLog(GenericServiceEnseignementMapFactory.class);

	public static ServiceEnseignementMap buildMap(String dataDir)
			throws java.io.IOException {

		return buildMap(dataDir, DEFAULT_BASE_NAME);
	}

	/**
	 * Cette methode lit les extracts pour creer les ServiceEnseignementMapEntry en
	 * consequence. Si un ServiceEnseignementMapEntry avec la meme cle existe deja il
	 * est utilise, et donc potentiellement mis a jour (par exemple changement
	 * du coordonnateur...).<br>
	 * <br>
	 */
	public static ServiceEnseignementMap buildMap(String dataDir,
			String baseName) throws java.io.IOException {

		ServiceEnseignementMap map;
		try {
			map = getInstance(dataDir);
			print("Mise a jour de la map...");
		} catch (FileNotFoundException e) {
			print("La map n'a pas ete trouvee, on la recree au complet.");
			map = new ServiceEnseignementMap();
		} catch (IOException e) {
			print("buildMap: exception dans getInstance: " + e);
			throw e;
		}

		InputStreamReader stream = new InputStreamReader(new FileInputStream(
				dataDir + "/" + baseName + ".dat"), "ISO-8859-1");
		BufferedReader breader = new BufferedReader(stream);
		String buffer, acadOrg, descFormal, deptId;

		// We remove the first line containing the title
		breader.readLine();

		// fait le tour des lignes du fichier
		while ((buffer = breader.readLine()) != null) {
			ServiceEnseignementMapEntry entry = new ServiceEnseignementMapEntry();
			String[] tokens = buffer.split(";");
			int i = 0;

			acadOrg = tokens[i++];
			descFormal = tokens[i++];
			deptId = tokens[i++];

			entry.setAcadOrg(acadOrg);
			entry.setDeptId(deptId);
			entry.setDescFormal(descFormal);
			map.put(entry);
		}

		// ferme le reader
		breader.close();

		return map;
	} // buildMap

	public static ServiceEnseignementMap getInstance(String dataDir)
			throws IOException {

		return getInstance(dataDir, DEFAULT_BASE_NAME);
	}

	private static ServiceEnseignementMap getInstance(String dataDir,
			String mapName) throws IOException {

		return new ServiceEnseignementMap();
	}

	protected static void print(String msg) {
		log.info("GenericServiceEnseignementFactory: " + msg);
	}
}
