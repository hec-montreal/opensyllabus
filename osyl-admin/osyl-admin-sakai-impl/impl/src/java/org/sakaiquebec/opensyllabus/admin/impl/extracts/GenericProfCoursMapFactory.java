package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.admin.cmjob.impl.OsylAbstractQuartzJobImpl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
public class GenericProfCoursMapFactory {

	private static Log log = LogFactory.getLog(GenericProfCoursMapFactory.class);

	public static Map<String, ProfCoursMapEntry> buildMap(String completeFileName, String [] debugCourses)
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
				//-----------------------------------------------------------------------
				//DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG
				if (debugCourses != null && debugCourses.length > 0)
					if (!OsylAbstractQuartzJobImpl.isCourseInDebug(debugCourses, catalogNbr))
						continue;
				//END DEBUG MODE-END DEBUG MODE-END DEBUG MODE-END DEBUG MODE-END DEBUG MODE
				//--------------------------------------------------------------------------
			}

			String key = catalogNbr + strm + sessionCode + classSection;

			ProfCoursMapEntry entry;
			if (map.containsKey(key)) {
				entry = map.get(key);
			} else {
				entry = new ProfCoursMapEntry();
				//ZCII-2783: Do not sync data during and after A2017
				if (OsylAbstractQuartzJobImpl.isAfterA2017Limite(Integer.parseInt(strm)))
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
