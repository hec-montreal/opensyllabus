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
 * This class loads of extract file secretaires_serv_ens.dat in the POJO and
 * map.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class GenericSecretairesMapFactory {

    private static final String DEFAULT_BASE_NAME = "secretaires_serv_ens";
    private static Log log =
	    LogFactory.getLog(GenericSecretairesMapFactory.class);

    public static SecretairesMap buildMap(String dataDir)
	    throws java.io.IOException {

	return buildMap(dataDir, DEFAULT_BASE_NAME);
    }

    /**
     * @param dataDir
     * @param baseName
     * @return
     * @throws java.io.IOException
     */
    public static SecretairesMap buildMap(String dataDir, String baseName)
	    throws java.io.IOException {

	SecretairesMap map;
	try {
	    map = getInstance(dataDir);
	    print("Mise a jour de la map...");
	} catch (FileNotFoundException e) {
	    print("La map n'a pas ete trouvee, on la recree au complet.");
	    map = new SecretairesMap();
	} catch (IOException e) {
	    print("buildMap: exception dans getInstance: " + e);
	    throw e;
	}

	InputStreamReader stream =
		new InputStreamReader(new FileInputStream(dataDir + "/"
			+ baseName + ".dat"), "ISO-8859-1");
	BufferedReader breader = new BufferedReader(stream);
	String buffer, emplid, role, deptId;
	String emplStatus, jobCode;

	// We remove the first line containing the title
	breader.readLine();

	// fait le tour des lignes du fichier
	while ((buffer = breader.readLine()) != null) {
	    SecretairesMapEntry entry = new SecretairesMapEntry();
	    String[] tokens = buffer.split(";");
	    int i = 0;

	    emplid = tokens[i++];
	    role = tokens[i++];
	    deptId = tokens[i++];
	    emplStatus = tokens[i++];
	    jobCode = tokens[i++];

	    entry.setEmplId(emplid);
	    entry.setDeptId(deptId);
	    entry.setRole(role);
	    entry.setEmpl_status(emplStatus);
	    entry.setJobCode(jobCode);
	    if (role.equalsIgnoreCase(map.SECRETAIRES_ACTIVITES_PROF)
		    || map.secretaireCertificat(role, deptId)
		    || role.contains(map.SECRETAIRES_PRINCIPALE))
		map.put(entry);
	}

	// ferme le reader
	breader.close();

	return map;
    } // buildMap

    /**
     * @param dataDir
     * @return
     * @throws IOException
     */
    public static SecretairesMap getInstance(String dataDir) throws IOException {

	return getInstance(dataDir, DEFAULT_BASE_NAME);
    }

    /**
     * @param dataDir
     * @param mapName
     * @return
     * @throws IOException
     */
    private static SecretairesMap getInstance(String dataDir, String mapName)
	    throws IOException {

	return new SecretairesMap();
    }

    /**
     * @param msg
     */
    protected static void print(String msg) {
	log.info("GenericSecretairesFactory: " + msg);
    }
}
