package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;

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
 * This map contains the necessary information to complete the registration of
 * the secretaries.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class SecretairesMap extends HashMap<String, SecretairesMapEntry> {

    public static final long serialVersionUID = 5386630822650707643l;

    public static final String SECRETAIRES_CERTIFICAT = "Agent aux activités";

    public static final String SECRETAIRES_CERTIFICAT_SERV_ENS = "115";

    public static final String SECRETAIRES_ACTIVITES_PROF =
	    "Secrétaire aux activités prof";

    public void put(SecretairesMapEntry entry) {
	String key = entry.getEmplId() + entry.getDeptId();
	put(key, entry);
    }

    public SecretairesMapEntry get(String key) {
	return (SecretairesMapEntry) super.get(key);
    }

    public void remove(SecretairesMapEntry entry) {
	remove(entry.getEmplId());
    }

    /**
     * Liste des secretaires de type agent aux activités. Elles correspondent
     * aux secretaires du certificat.
     * 
     * @return
     */
    public List<String> getSecretairesCertificat() {
	List<String> secretaires = new ArrayList<String>();
	SecretairesMapEntry entry = null;
	String role = null;
	String deptId = null;

	Set<String> keys = this.keySet();

	for (String key : keys) {
	    entry = this.get(key);
	    role = entry.getRole();
	    deptId = entry.getDeptId();
	    if (role.equalsIgnoreCase(SECRETAIRES_CERTIFICAT)
		    && deptId.equalsIgnoreCase(SECRETAIRES_CERTIFICAT_SERV_ENS))
		secretaires.add(entry.getEmplId());
	}

	return secretaires;
    }

    /**
     * Liste des secretaires selon le service d'enseignement. On n'inclue pas
     * les secretaires du certificat.
     * 
     * @param deptId
     * @return
     */
    public List<String> getSecretairesByAcadOrg(int deptId) {
	List<String> secretaires = new ArrayList<String>();
	SecretairesMapEntry entry = null;
	int department = 0;
	Set<String> keys = this.keySet();

	for (String key : keys) {
	    entry = this.get(key);
	    department = Integer.parseInt(entry.getDeptId());
	    if (department == deptId
		    && !entry.getRole()
			    .equalsIgnoreCase(SECRETAIRES_CERTIFICAT))
		secretaires.add(entry.getEmplId());
	}

	return secretaires;
    }

}
