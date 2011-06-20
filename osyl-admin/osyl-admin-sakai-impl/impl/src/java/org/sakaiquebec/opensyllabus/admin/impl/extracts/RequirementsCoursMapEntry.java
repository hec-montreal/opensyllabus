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
/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class RequirementsCoursMapEntry {

    
    private static String LANG_FR="fr_CA";
    private static String LANG_ES="es";
    private static String LANG_EN="en";
    
    private String courseId;
    private String catalogNbr;
    private String effDate;
    private String descriptionAng;
    private String descriptionFra;
    
    
    public void setCourseId(String courseId) {
	this.courseId = courseId;
    }
    public String getCourseId() {
	return courseId;
    }
    public void setCatalogNbr(String catalogNbr) {
	this.catalogNbr = catalogNbr;
    }
    public String getCatalogNbr() {
	return catalogNbr;
    }
    public void setEffDate(String effDate) {
	this.effDate = effDate;
    }
    public String getEffDate() {
	return effDate;
    }
    public void setDescriptionAng(String descriptionAng) {
	this.descriptionAng = descriptionAng;
    }
    public String getDescriptionAng() {
	return descriptionAng;
    }
    public void setDescriptionFra(String descriptionFra) {
	this.descriptionFra = descriptionFra;
    }
    public String getDescriptionFra() {
	return descriptionFra;
    }
    
    public String getDescription(String lang){
	if(LANG_FR.equals(lang))
	    return descriptionFra;
	else return descriptionAng;
    }
}

