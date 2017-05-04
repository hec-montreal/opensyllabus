package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.admin.cmjob.impl.OsylAbstractQuartzJobImpl;

import java.io.*;

public class GenericDetailCoursMapFactory {

    private static Log log = LogFactory
	    .getLog(GenericDetailCoursMapFactory.class);

    public static DetailCoursMap buildMap(String completeFileName, String [] debugCourses)
	    throws java.io.IOException {

	DetailCoursMap map;
	try {
	    map = getInstance(completeFileName);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new DetailCoursMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	InputStreamReader stream =
		new InputStreamReader(new FileInputStream(completeFileName),
			"ISO-8859-1");
	BufferedReader breader = new BufferedReader(stream);
	String buffer, key, courseId, strm, sessionCode, catalogNbr, section, acadCareer;
	DetailCoursMapEntry entry;

	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    String[] tokens = buffer.split(";");
	    int i = 0;

	    courseId = tokens[i++];
	    strm = tokens[i++];
	    sessionCode = tokens[i++];
	    catalogNbr = tokens[i++];
	    section = tokens[i++];

	    // Remove empty spaces
	    if (courseId != null)
		courseId = courseId.trim();
	    if (catalogNbr != null)
	    catalogNbr = catalogNbr.trim();

	    key =
		    DetailCoursMapEntry.getUniqueKey(catalogNbr,
			    (strm + sessionCode), section);
	    // on reprend l'entree existante
	    if (map.containsKey(key)) {
		entry = map.get(key);
	    } else {
		entry = new DetailCoursMapEntry();
		entry.setCourseId(courseId);
		entry.setStrm(strm);
		entry.setSessionCode(sessionCode);
		entry.setCatalogNbr(catalogNbr);
		entry.setSection(section);
	    }
	    entry.setCourseTitleLong(tokens[i++]);

	    try {
		entry.setLangue(tokens[i++]);
	    } catch (ArrayIndexOutOfBoundsException e) {
		// Ce n'est pas grave, on met la langue par defaut
		print("langue manquante pour le cours [" + courseId + "]");
	    }
	    entry.setAcadOrg(tokens[i++]);
	    entry.setStrmId(tokens[i++]);

	    entry.setAcaCareer(tokens[i++]);
	    entry.setClassStat(tokens[i++]);
	    entry.setUnitsMinimum(tokens[i++]);
	    try {
		entry.setTypeEvaluation(tokens[i++]);
	    } catch (ArrayIndexOutOfBoundsException e) {
		//on met null si pas de valeur associée au type d'évaluation
		entry.setTypeEvaluation(null);
	    }

	    //-----------------------------------------------------------------------
		//DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG MODE-DEBUG
		if (debugCourses != null && debugCourses.length > 0)
			if (!OsylAbstractQuartzJobImpl.isCourseInDebug(debugCourses, entry.getCatalogNbr()))
				continue;
		//END DEBUG MODE-END DEBUG MODE-END DEBUG MODE-END DEBUG MODE-END DEBUG MODE
		//--------------------------------------------------------------------------
		//ZCII-2783: Do not sync data during and after A2017
		if (OsylAbstractQuartzJobImpl.isBeforeA2017(Integer.parseInt(entry.getStrm())))
	    map.put(entry);
	}

	// ferme le reader
	breader.close();

	return map;
    } // buildMap

    public static DetailCoursMap getInstance(String completeFileName)
	    throws IOException {

	return new DetailCoursMap();
    }

    protected static void print(String msg) {
	log.info("GenericDetailCoursMapFactory: " + msg);
    }
}
