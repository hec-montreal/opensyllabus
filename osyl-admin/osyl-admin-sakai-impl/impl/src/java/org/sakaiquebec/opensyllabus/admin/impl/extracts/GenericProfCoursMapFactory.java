package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericProfCoursMapFactory {

	private static Log log = LogFactory.getLog(GenericProfCoursMapFactory.class);

	public static Map<String, ProfCoursMapEntry> buildMap(String completeFileName)
			throws java.io.IOException {

		Map<String, ProfCoursMapEntry> map = new HashMap<String, ProfCoursMapEntry>();

		BufferedReader breader =
				new BufferedReader(new InputStreamReader(new FileInputStream(
						completeFileName), "ISO-8859-1"));

		String buffer;
		String[] token;

		// We remove the first line containing the title
		breader.readLine();

		// fait le tour des lignes du fichier
		while ((buffer = breader.readLine()) != null) {
			token = buffer.split(";");

			String emplId = token[0];
			String catalogNbr = token[1];
			String strm = token[2];
			String sessionCode = token[3];
			String classSection = token[4];
//			Skip acadorg et strmId, les lignes ont parfois trop de colonnes! donc elles ne sont pas fiable.
//			String acadOrg = token[5];
			String role = token[6];
//			String strmId = token[7];

			if (catalogNbr != null) {
				catalogNbr = catalogNbr.trim();
			}

			String key = catalogNbr + strm + sessionCode + classSection;

			ProfCoursMapEntry entry;
			if (map.containsKey(key)) {
				entry = map.get(key);
			} else {
				entry = new ProfCoursMapEntry();
				map.put(key, entry);
			}

			// On ne peut pas se fier a la colonne qui contient le role dans l'extract
			// alors on reconnait juste enseignant
			if ("Enseignant".equals(role)) {
				entry.getInstructors().add(emplId);
			}
			else {
				entry.getCoordinators().add(emplId);
			}
		}

		breader.close();

		return map;
	} // buildMap
}
