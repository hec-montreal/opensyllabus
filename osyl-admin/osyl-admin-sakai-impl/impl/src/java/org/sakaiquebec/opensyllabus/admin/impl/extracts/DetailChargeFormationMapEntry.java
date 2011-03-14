
package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.Vector;




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
 *****************************************************************************/

/**
 * Traitement du fichier charge_formation.dat
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class DetailChargeFormationMapEntry implements java.io.Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String emplId;
    private String role;
    Vector <DetailCoursMapEntry> cours = null;
    
    DetailChargeFormationMapEntry() {
	// empty constructor
	cours = new Vector<DetailCoursMapEntry>();
    }


    public String toString() {
	return "Charge de formation " + getEmplId() ;
    }


    public Vector<DetailCoursMapEntry> getCours() {
        return cours;
    }


    public void setCours(Vector<DetailCoursMapEntry> cours) {
        this.cours = cours;
    }

    public void addCours (DetailCoursMapEntry cours){
	this.cours.add(cours);
    }

    public String getEmplId() {
        return emplId;
    }


    public void setEmplId(String emplId) {
        this.emplId = emplId;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }



}
