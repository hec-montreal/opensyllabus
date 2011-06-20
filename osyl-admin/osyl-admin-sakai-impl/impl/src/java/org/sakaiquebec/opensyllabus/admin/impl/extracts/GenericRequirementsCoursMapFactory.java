/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class GenericRequirementsCoursMapFactory {
    private static Log log = LogFactory
	    .getLog(GenericDetailCoursMapFactory.class);

    public static RequirementsCoursMap buildMap(String completeFileName)
	    throws java.io.IOException {

	RequirementsCoursMap map;
	try {
	    map = getInstance(completeFileName);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new RequirementsCoursMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	InputStreamReader stream =
		new InputStreamReader(new FileInputStream(completeFileName),
			"ISO-8859-1");
	BufferedReader breader = new BufferedReader(stream);
	String buffer, courseId, catalogNbr, effDate, descrAng, descrFr;
	RequirementsCoursMapEntry entry;

	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    String[] tokens = buffer.split(";", -1);

	    int i = 0;

	    courseId = tokens[i++];
	    catalogNbr = tokens[i++];
	    effDate = tokens[i++];
	    descrAng = tokens[i++];
	    descrFr = tokens[i++];

	    // Remove empty spaces
	    if (courseId != null)
		courseId = courseId.trim();

	    // on reprend l'entree existante
	    if (map.containsKey(courseId)) {
		entry = map.get(courseId);
	    } else {
		entry = new RequirementsCoursMapEntry();
		entry.setCourseId(courseId);
		entry.setCatalogNbr(catalogNbr);
		entry.setEffDate(effDate);
		entry.setDescriptionAng(descrAng);
		entry.setDescriptionFra(descrFr);
	    }
	    map.put(courseId, entry);
	}

	// ferme le reader
	breader.close();

	return map;
    } // buildMap

    public static RequirementsCoursMap getInstance(String completeFileName)
	    throws IOException {

	return new RequirementsCoursMap();
    }

    protected static void print(String msg) {
	log.info("GenericRequirementsCoursMapFactory: " + msg);
    }
}
