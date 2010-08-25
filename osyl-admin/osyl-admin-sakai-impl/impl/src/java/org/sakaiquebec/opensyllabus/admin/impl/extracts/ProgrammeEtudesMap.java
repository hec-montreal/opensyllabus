package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.HashMap;
import java.util.Iterator;

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
 * This map contains all the PE of HEC.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class ProgrammeEtudesMap extends
	HashMap<String, ProgrammeEtudesMapEntry> {
    private static final long serialVersionUID = 1L;

    public void put(ProgrammeEtudesMapEntry entry) {
	put(entry.getAcadCareer(), entry);
    }

    public ProgrammeEtudesMapEntry get(String key) {
	return (ProgrammeEtudesMapEntry) super.get(key);
    }

    public void remove(ProgrammeEtudesMapEntry entry) {
	remove(entry.getAcadCareer());
    }

    public Iterator<ProgrammeEtudesMapEntry> getAllServices() {

	return values().iterator();
    }

}
