package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
 *****************************************************************************/

/**
 * This class loads the content of the extract file service_enseignement.dat in
 * the POJO and map.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class GenericProgrammeEtudesMapFactory {

    private static final String DEFAULT_BASE_NAME = "programme_etudes";
    private static Log log =
	    LogFactory.getLog(GenericProgrammeEtudesMapFactory.class);

    public static ProgrammeEtudesMap buildMap(String dataDir)
	    throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME);
    }

    public static ProgrammeEtudesMap buildMap(String dataDir,
	    String baseName) throws java.io.IOException {

	ProgrammeEtudesMap map;
	try {
	    map = getInstance(dataDir);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new ProgrammeEtudesMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	InputStreamReader stream =
		new InputStreamReader(new FileInputStream(dataDir + "/"
			+ baseName + ".dat"), "ISO-8859-1");
	BufferedReader breader = new BufferedReader(stream);
	String buffer, acadCareer, descFr, descEng;

	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    ProgrammeEtudesMapEntry entry =
		    new ProgrammeEtudesMapEntry();
	    String[] tokens = buffer.split(";");
	    int i = 0;

	    acadCareer = tokens[i++];
	    descFr = tokens[i++];
	    descEng = tokens[i++];

	    entry.setAcadCareer(acadCareer);
	    entry.setDescFr(descFr);
	    entry.setDescEng(descEng);
	    map.put(entry);
	}

	// ferme le reader
	breader.close();

	return map;
    } // buildMap

    public static ProgrammeEtudesMap getInstance(String dataDir)
	    throws IOException {

	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    private static ProgrammeEtudesMap getInstance(String dataDir,
	    String mapName) throws IOException {

	return new ProgrammeEtudesMap();
    }

    protected static void print(String msg) {
	log.info("GenericServiceEnseignementFactory: " + msg);
    }
}
