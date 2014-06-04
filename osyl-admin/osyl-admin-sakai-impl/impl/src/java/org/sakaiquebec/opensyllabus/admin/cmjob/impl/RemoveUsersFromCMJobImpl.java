/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2012 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.admin.cmjob.impl;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.RemoveUsersFromCMJob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * Remove users from sakai course management
 *
 * The user is removed based on his id (matricule) and his role in the site (Library staff, secretary, etc.)
 *
 * @author <a href="mailto:philippe.rancourt@hec.ca">Philippe Rancourt</a>
 * @version $Id: $
 */
public class RemoveUsersFromCMJobImpl extends OsylAbstractQuartzJobImpl  implements RemoveUsersFromCMJob{


	/**
	 * Our logger
	 */
	private static Log log = LogFactory
			.getLog(RemoveUsersFromCMJobImpl.class);


	//map containing the user to be removed from a site for a particular role (including sites to exclude)
	private HashMap<String, HashMap> removeUsersMap = new HashMap<String, HashMap>();



	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		loginToSakai();

		long start = System.currentTimeMillis();
		log.info("RemoveUsersFromCMJobImpl: starting");

		loadConfigFile();

		Set<String> usersToRemove = getUsersToRemove();


		for(String userEid : usersToRemove){

			HashMap<String, String> parameters = removeUsersMap.get(userEid);
			String userRole = parameters.get("role");

			String excludeSitesString = parameters.get("excludeSites");
			List<String> excludeSites = null;

			if (excludeSitesString != null) {
				excludeSites = Arrays.asList(excludeSitesString.split(","));

				//trim the sites to exclude
				for(int i=0; i < excludeSites.size(); i++) {
					String s = excludeSites.get(i);
					excludeSites.set(i, s.trim());
				}
			}

			if (userRole == null) {
				log.error("Role is a required parameter.");
				continue;
			}

			log.debug("userEid:"+userEid);
			log.debug("userRole:"+userRole);
			log.debug("excludeSites: " + excludeSites.toString());

			// remove membership from course offerings
			Map<String, String> courseOfferingRoleMap = cmService.findCourseOfferingRoles(userEid);
			Set<String> courseOfferings = courseOfferingRoleMap.keySet();

			for(String courseOfferingEid : courseOfferings){

				String courseOfferingRole = courseOfferingRoleMap.get(courseOfferingEid);
				log.debug("courseOfferingEid: " + courseOfferingEid + " courseOfferingRole: " + courseOfferingRole);

				if(userRole.equals(courseOfferingRole)){
					// only try if the site should not be excluded
					CourseOffering courseOffering = cmService.getCourseOffering(courseOfferingEid);
					if (excludeSites != null && isSiteExcluded(courseOffering, excludeSites)) {
						log.debug("exclude CO: "+courseOfferingEid);
						continue;
					}

					if(cmAdmin.removeCourseOfferingMembership(userEid, courseOfferingEid)){
						log.info("SUCCESS removing user:"+userEid+" from CO:"+courseOfferingEid);
					}
					else{
						log.info("FAILED removing user:"+userEid+" from CO:"+courseOfferingEid);
					}
				}

			}//end for

			// remove membership from course sections
			Map<String, String> courseSectionRoleMap = cmService.findSectionRoles(userEid);
			Set<String> courseSections = courseSectionRoleMap.keySet();

			for(String courseSectionEid : courseSections){
				String courseSectionRole = courseSectionRoleMap.get(courseSectionEid);
				log.debug("courseSectionEid:" + courseSectionEid+" courseSectionRole: "+courseSectionRole);

				if(userRole.equals(courseSectionRole)){
					// only try if the site should not be excluded
					Section section = cmService.getSection(courseSectionEid);
					if (excludeSites != null && isSiteExcluded(section, excludeSites)) {
						log.debug("exclude Section: "+courseSectionEid);
						continue;
					}

					if(cmAdmin.removeSectionMembership(userEid, courseSectionEid)){
						log.info("SUCCESS removing user:"+userEid+" from Section:"+courseSectionEid);
					}
					else{
						log.info("FAILED removing user:"+userEid+" from Section:"+courseSectionEid);
					}
				}
			}//end for

			// Remove user as official instructor
			if (userRole.equals("I")) {
				Set<Section> officialInstructorsSet = cmService.findInstructingSections(userEid);

				for (Section section : officialInstructorsSet) {
					EnrollmentSet enrollment = section.getEnrollmentSet();

					Set<String> instructors = enrollment.getOfficialInstructors();
					instructors.remove(userEid);
					enrollment.setOfficialInstructors(instructors);

					if (excludeSites != null && isSiteExcluded(section, excludeSites)) {
						log.debug("exclude Section: "+section.getEid());
						continue;
					}

					cmAdmin.updateEnrollmentSet(enrollment);
					log.info("SUCCESS removing instructor:"+userEid+" from Section:"+section.getEid());
				}
			}
		}//end for

		log.info("RemoveUsersFromCMJobImpl: completed in "
				+ (System.currentTimeMillis() - start) + " ms");

		logoutFromSakai();

	}



	private boolean isSiteExcluded(Section section, List<String> excludeSites) {
		String courseOfferingEid = section.getCourseOfferingEid();
		CourseOffering co = cmService.getCourseOffering(courseOfferingEid);

		return isSiteExcluded(co, excludeSites);
	}




	private boolean isSiteExcluded(CourseOffering courseOffering, List<String> excludeSites) {
		AcademicSession session = courseOffering.getAcademicSession();
		String sessionName = "";

		Date startDate = session.getStartDate();
		String year = startDate.toString().substring(0, 4);

		if ((session.getEid().charAt(3)) == '1')
		    sessionName = "H" + year;
		else if ((session.getEid().charAt(3)) == '2')
		    sessionName = "E" + year;
		else if ((session.getEid().charAt(3)) == '3')
		    sessionName = "A" + year;

		return excludeSites.contains(courseOffering.getCanonicalCourseEid()) ||
				excludeSites.contains(courseOffering.getCanonicalCourseEid() + "." + sessionName);
	}




	private Set<String> getUsersToRemove(){

		return removeUsersMap.keySet();
	}



	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	private void loadConfigFile(){

		String fileName = ConfigurationService.CONFIG_FOLDER + "removeUsersFromCMConfig.xml";

		Reference reference = entityManager.newReference(fileName);

		if (reference != null) {

			ContentResource resource = null;

			try{
				resource = contentHostingService.getResource(reference.getId());

				if(resource!=null){
					Document document = parseXmlFromStream(resource.streamContent());

					if(document!=null){
						parseConfig(document);
					}
				}
			}
			catch(Exception ex){
				log.error("Error loading config file:"+fileName+"  ex:"+ex.toString());
			}
		}
	}


	private void parseConfig(Document doc){

		NodeList userList = doc.getElementsByTagName("user");


		for(int i=0;i<userList.getLength();i++) {

			Node user = userList.item(i);

			if(user.getNodeType() == Node.ELEMENT_NODE){

				Element userElement = (Element)user;

				String id = null;
				HashMap<String, String> parameters = new HashMap<String, String>();

				NodeList idList = userElement.getElementsByTagName("id");
				if(idList!=null && idList.getLength()!=0){

					Element idElement = (Element)idList.item(0);

					NodeList textIDList = idElement.getChildNodes();
					id = ((Node)textIDList.item(0)).getNodeValue().trim();
				}

				NodeList roleList = userElement.getElementsByTagName("role");
				if(roleList!=null && roleList.getLength()!=0){
					Element roleElement = (Element)roleList.item(0);

					NodeList textRoleList = roleElement.getChildNodes();
					String role = ((Node)textRoleList.item(0)).getNodeValue().trim();
					parameters.put("role", role);
				}

				NodeList excludeSitesList = userElement.getElementsByTagName("excludeSites");
				if(excludeSitesList!=null && excludeSitesList.getLength()!=0){
					Element excludeSitesElement = (Element)excludeSitesList.item(0);

					NodeList textExcludeSitesList = excludeSitesElement.getChildNodes();
					String excludeSites = ((Node)textExcludeSitesList.item(0)).getNodeValue().trim();
					parameters.put("excludeSites", excludeSites);
				}

				removeUsersMap.put(id, parameters);
			}

		}//end for userlist

	}

	/**
	 * Parse an XML resource
	 *
	 * @param filename The filename (or URI) to parse
	 * @return DOM Document (null if parse fails)
	 */
	private Document parseXmlFromStream(InputStream stream) {
		try {
			DocumentBuilderFactory factory;

			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);

			DocumentBuilder documentBuilder = factory.newDocumentBuilder();

			if (documentBuilder != null) {
				return documentBuilder.parse(stream);
			}
		} catch (Exception exception) {
			log.warn("XML parse on \"" + stream + "\" failed: " + exception);
		}
		return null;
	}


	/**
	 * Logs in the sakai environment
	 */
	protected void loginToSakai() {
		super.loginToSakai("RemoveUsersFromCMJobImpl");
	}


}

