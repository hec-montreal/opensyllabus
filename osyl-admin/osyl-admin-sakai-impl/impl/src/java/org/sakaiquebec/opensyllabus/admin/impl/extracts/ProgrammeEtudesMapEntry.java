package org.sakaiquebec.opensyllabus.admin.impl.extracts;

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
 * This entry represents a PE.
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class ProgrammeEtudesMapEntry implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private String acadCareer;
    private String descFr;
    private String descEng;
    /**
     * Empty constructor.
     */
    public ProgrammeEtudesMapEntry() {
    }

    public String getAcadCareer() {
	return acadCareer;
    }

    public void setAcadCareer(String acadOrg) {
	this.acadCareer = acadOrg;
    }

    public String getDescEng() {
	return descEng;
    }

    public void setDescEng(String deptId) {
	this.descEng = deptId;
    }

    /**
     * @return the descFormal
     */
    public String getDescFr() {
	return descFr;
    }

    /**
     * @param descFormal the descFormal to set
     */
    public void setDescFr(String descFormal) {
	this.descFr = descFormal;
    }

    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	} else if (!o.getClass().getName().equals(
		"ca.hec.peoplesoft.ProgrammeEtudesMapEntry")) {
	    return false;
	} else {
	    if (o.hashCode() == hashCode()) {
		return true;
	    } else {
		return false;
	    }
	}
    } // equals
}
