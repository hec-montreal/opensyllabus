package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericProfCoursMapFactory {

	private static Log log = LogFactory.getLog(GenericProfCoursMapFactory.class);

	public static Map<String, List<ProfCoursMapEntry>> buildMap(String completeFileName)
			throws java.io.IOException {

		Map<String, List<ProfCoursMapEntry>> map = new HashMap<String, List<ProfCoursMapEntry>>();

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

			// terrible hack because the number of columns per line is not consistent
			if (!"Enseignant".equals(role))
				role = "Coordonnateur";

			if (catalogNbr != null) {
				catalogNbr = catalogNbr.trim();
			}

			List<ProfCoursMapEntry> listProfs;
			String key = catalogNbr + strm + sessionCode + classSection;
			if (map.containsKey(key)) {
				listProfs = map.get(key);
			} else {
				listProfs = new ArrayList<ProfCoursMapEntry>();
				map.put(key, listProfs);
			}
			listProfs.add(new ProfCoursMapEntry(emplId, catalogNbr, strm, sessionCode, classSection, null, role, null));
		}

		breader.close();

		return map;
	} // buildMap
}
