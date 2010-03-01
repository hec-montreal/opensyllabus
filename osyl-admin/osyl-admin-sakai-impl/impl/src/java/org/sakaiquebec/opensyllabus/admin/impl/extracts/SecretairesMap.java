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
 * This map contains the necessary information to complete the registration of the
 * secretaries.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class SecretairesMap extends HashMap<String, SecretairesMapEntry> {

    public static final long serialVersionUID = 5386630822650707643l;

    public void put(SecretairesMapEntry entry) {
	put(entry.getEmplId(), entry);
    }

    public SecretairesMapEntry get(String key) {
	return (SecretairesMapEntry) super.get(key);
    }

    public void remove(SecretairesMapEntry entry) {
	remove(entry.getEmplId());
    }

}
