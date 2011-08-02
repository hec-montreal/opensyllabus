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
package org.sakaiquebec.opensyllabus.shared.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CODirectorySite implements java.io.Serializable {

    private static final long serialVersionUID = -5914778051117341406L;

    private String description;

    private String courseNumber;

    private String courseName;

    private String responsible;

    private String program;

    private String credits;

    private String requirements;
    
    private String instructor;

    private Map<String, String> currentSections;
    
    private Map<String,String> archivedSections;

    public CODirectorySite() {
	super();
	currentSections = new HashMap<String, String>();
	archivedSections = new HashMap<String, String>();
    }

    public String getCourseNumber() {
	return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
	this.courseNumber = courseNumber;
    }

    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    public String getResponsible() {
	return responsible;
    }

    public void setResponsible(String responsible) {
	this.responsible = responsible;
    }

    public String getCredits() {
	return credits;
    }

    public void setCredits(String credits) {
	this.credits = credits;
    }

    public String getRequirements() {
	return requirements;
    }

    public void setRequirements(String requirements) {
	this.requirements = requirements;
    }

    public Map<String, String> getCurrentSections() {
	return currentSections;
    }

    public void setCurrentSections(Map<String, String> sections) {
	this.currentSections = sections;
    }

    public void putCurrentSection(String key, String value) {
	currentSections.put(key, value);
    }
    
    public Map<String, String> getArchivedSections() {
	return archivedSections;
    }

    public void setArchivedSections(Map<String, String> sections) {
	this.archivedSections = sections;
    }

    public void putArchivedSection(String key, String value) {
	archivedSections.put(key, value);
    }


    public void setProgram(String program) {
	this.program = program;
    }

    public String getProgram() {
	return program;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getDescription() {
	return description;
    }

    public void setInstructor(String instructor) {
	this.instructor = instructor;
    }

    public String getInstructor() {
	return instructor;
    }

}
