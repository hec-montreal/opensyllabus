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
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.shared.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * This POJO rounds up all the informations associated to the course outline and
 * the site that contains that course outline.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class COSite implements java.io.Serializable {

    private static final long serialVersionUID = 7458511802764084049L;

    private String siteId;

    /**
     * The name of the site
     */
    private String siteName;

    /**
     * The short description associated to the site
     */
    private String siteShortDescription;

    /**
     * The long description associated to the site
     */
    private String siteDescription;

    /**
     * The course catalog number associated to the site. The information comes
     * from the course management.
     */
    private String courseNumber;

    /**
     * The name of the course associated to the course outline. The information
     * comes from the course management.
     */
    private String courseName;

    /**
     * The session of the course associated to the site. The information comes
     * from the course management.
     */
    private String courseSession;

    /**
     * The section of the course associated to the site. The information comes
     * from the course management.
     */
    private String courseSection;

    /**
     * The instructors of the course associated to the site. The information
     * comes from the course management.
     */
    private ArrayList<String> courseInstructors;

    /**
     * The coordinator of the course associated to the site. The information
     * comes from the course management.
     */
    private String courseCoordinator;

    /**
     * The id of the site that is associated to this site and the represents its
     * parent in the hierarchy.
     */
    private String parentSite;

    /**
     * The id of the user that has created this site. Can be any person who has
     * access to osyl manager.
     */
    private String siteOwnerName;

    /**
     * The id of the user that has created this site. Can be any person who has
     * access to osyl manager.
     */
    private String siteOwnerLastName;

    /**
     * The last time the course outline was published.
     */
    private Date lastPublicationDate;

    /**
     * The last time the course outline was modified.
     */
    private Date lastModifiedDate;

    /**
     * Empty constructor.
     */
    public COSite() {
	courseInstructors = new ArrayList<String>();
    }

    /**
     * @return the siteName value.
     */
    public String getSiteName() {
	return siteName;
    }

    /**
     * @param siteName the new value of siteName.
     */
    public void setSiteName(String siteName) {
	this.siteName = siteName;
    }

    /**
     * @return the siteShortDescription value.
     */
    public String getSiteShortDescription() {
	return siteShortDescription;
    }

    /**
     * @param siteShortDescription the new value of siteShortDescription.
     */
    public void setSiteShortDescription(String siteShortDescription) {
	this.siteShortDescription = siteShortDescription;
    }

    /**
     * @return the siteDescription value.
     */
    public String getSiteDescription() {
	return siteDescription;
    }

    /**
     * @param siteDescription the new value of siteDescription.
     */
    public void setSiteDescription(String siteDescription) {
	this.siteDescription = siteDescription;
    }

    /**
     * @return the courseNumber value.
     */
    public String getCourseNumber() {
	return courseNumber;
    }

    /**
     * @param courseNumber the new value of courseNumber.
     */
    public void setCourseNumber(String courseNumber) {
	this.courseNumber = courseNumber;
    }

    /**
     * @return the courseName value.
     */
    public String getCourseName() {
	return courseName;
    }

    /**
     * @param courseName the new value of courseName.
     */
    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    /**
     * @return the courseSession value.
     */
    public String getCourseSession() {
	return courseSession;
    }

    /**
     * @param courseSession the new value of courseSession.
     */
    public void setCourseSession(String courseSession) {
	this.courseSession = courseSession;
    }

    /**
     * @return the courseSection value.
     */
    public String getCourseSection() {
	return courseSection;
    }

    /**
     * @param courseSection the new value of courseSection.
     */
    public void setCourseSection(String courseSection) {
	this.courseSection = courseSection;
    }

    /**
     * @return the courseInstructor value.
     */
    public ArrayList<String> getCourseInstructors() {
	return courseInstructors;
    }

    /**
     * @param courseInstructors the new value of courseInstructors.
     */
    public void setCourseInstructors(ArrayList<String> courseInstructors) {
	this.courseInstructors.addAll(courseInstructors);
    }

    /**
     * Add a new instructor to the list.
     * 
     * @param instructor
     */
    public void addCourseInstructor(String instructor) {
	this.courseInstructors.add(instructor);
    }

    /**
     * @return the courseCoordinator value.
     */
    public String getCourseCoordinator() {
	return courseCoordinator;
    }

    /**
     * @param courseCoordinator the new value of courseCoordinator.
     */
    public void setCourseCoordinator(String courseCoordinator) {
	this.courseCoordinator = courseCoordinator;
    }

    /**
     * @return the parentSite value.
     */
    public String getParentSite() {
	return parentSite;
    }

    /**
     * @param parentSite the new value of parentSite.
     */
    public void setParentSite(String parentSite) {
	this.parentSite = parentSite;
    }

    /**
     * @return the siteOwnerName value.
     */
    public String getSiteOwnerName() {
	return siteOwnerName;
    }

    /**
     * @param siteOwnerName the new value of siteOwnerName.
     */
    public void setSiteOwnerName(String siteOwnerName) {
	this.siteOwnerName = siteOwnerName;
    }

    /**
     * @return the siteOwnerLastName value.
     */
    public String getSiteOwnerLastName() {
	return siteOwnerLastName;
    }

    /**
     * @param siteOwnerLastName the new value of siteOwnerLastName.
     */
    public void setSiteOwnerLastName(String siteOwnerLastName) {
	this.siteOwnerLastName = siteOwnerLastName;
    }

    /**
     * @return the lastPublicationDate value.
     */
    public Date getLastPublicationDate() {
	return lastPublicationDate;
    }

    /**
     * @param lastPublicationDate the new value of lastPublicationDate.
     */
    public void setLastPublicationDate(Date lastPublicationDate) {
	this.lastPublicationDate = lastPublicationDate;
    }

    /**
     * @return the lastModifiedDate value.
     */
    public Date getLastModifiedDate() {
	return lastModifiedDate;
    }

    /**
     * @param lastModifiedDate the new value of lastModifiedDate.
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
    }

    public void setSiteId(String siteId) {
	this.siteId = siteId;
    }

    public String getSiteId() {
	return siteId;
    }
}
