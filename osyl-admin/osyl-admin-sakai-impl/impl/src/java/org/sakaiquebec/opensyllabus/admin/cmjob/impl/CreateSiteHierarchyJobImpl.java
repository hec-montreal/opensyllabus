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


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.coursemanagement.api.AcademicCareer;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CreateSiteHierarchyJob;

/**
* Create a hierarchy for HEC course site
* 
* This job add custom properties to every course site (into database table SAKAI_SITE_PROPERTY)
* 
* It add the following three property needed b the delegated access tool to every site
* 
* From sakai.properties :
* 
* delegatedaccess.hierarchy.site.properties.1
* delegatedaccess.hierarchy.site.properties.3
* delegatedaccess.hierarchy.site.properties.3
* 
* 
*
*
* @author <a href="mailto:philippe.rancourt@hec.ca">Philippe Rancourt</a>
* @version $Id: $
*/
public class CreateSiteHierarchyJobImpl extends OsylAbstractQuartzJobImpl  implements CreateSiteHierarchyJob{
	
    private static final String HIERARCHY_LEVEL1 = "delegatedaccess.hierarchy.site.properties.1";
    private static final String HIERARCHY_LEVEL2 = "delegatedaccess.hierarchy.site.properties.2";
    private static final String HIERARCHY_LEVEL3 = "delegatedaccess.hierarchy.site.properties.3";
      
    private static final String CERTIFICAT_CODE = "CERT";
    
    private static final String CERTIFICAT = "Certificats";
    private static final String SERVICE_ENSEIGNEMENT = "Services d'enseignement et programmes";
   
    private List<AcademicCareer> allAcademicCareer = null;

    
    /**
     * Our logger
     */
    private static Log log = LogFactory
	    .getLog(CreateSiteHierarchyJobImpl.class);

    
    
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("CreateSiteHierarchyJobImpl: starting");

		
	List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, "course",
			null, null, SiteService.SortType.NONE, null);

	Site site = null;
	String [] providerIds = null;

	log.info("CreateSiteHierarchyJobImpl: sites to add properties:"
		+ allSites.size());

	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);


		if (site.getTitle().indexOf(".E2017") == -1) {
			continue;
		}
	    if (site.getProviderGroupId() != null) {
		
		 Section section = null;

		 providerIds = site.getProviderGroupId().split("\\+");

		 for (String providerId: providerIds) {
			 try {
				 section = cmService.getSection(providerId);
			 } catch (Exception ex) {
				 log.error("No section for site: " + site.getTitle());
			 }


			 if (section != null) {

				 String acadDepartment = cmService.getSectionCategoryDescription(section.getCategory());


				 CourseOffering co = null;

				 try {
					 co = cmService.getCourseOffering(section.getCourseOfferingEid());
				 } catch (Exception ex) {
					 log.error("No course offering for site: " + site.getTitle());
				 }


				 String acadCareer = null;

				 if (co != null) {
					 acadCareer = getAcademicCareer(co.getAcademicCareer());
				 }

				 if (!propertiesExist(site, acadDepartment, acadCareer)) {

					 addHierarchyProperties(site, acadDepartment, acadCareer);

				 }


			 }//end if section!=null
		 }
	    }//end if providerGroupId!=null
	    else{
		log.error("No provider group id for site: "+site.getTitle());
	    }
	    
	    //remove the reference from the list to free up memory
	    allSites.set(i,null);
	    
	}//end for all sites

	log.info("CreateSiteHierarchyJobImpl: completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	
	logoutFromSakai();

    }

    
    
    private void addHierarchyProperties(Site site, String acadDepartment, String acadCareer){
	
	site.loadAll();
	
	ResourcePropertiesEdit siteProperties = site.getPropertiesEdit();

	if(CERTIFICAT_CODE.equalsIgnoreCase(acadCareer)){
	    
	    siteProperties.addProperty(getHierarchyLevel1(), CERTIFICAT);
	    siteProperties.addProperty(getHierarchyLevel2(), acadDepartment);
	}
	else{
	    
	    siteProperties.addProperty(getHierarchyLevel1(), SERVICE_ENSEIGNEMENT);
	    siteProperties.addProperty(getHierarchyLevel2(), acadDepartment);
	    
	    if(acadCareer!=null){
		siteProperties.addProperty(getHierarchyLevel3(), acadCareer);
		
		//PR - patch pour certains cours du MBA
		if(acadCareer.equalsIgnoreCase("MBA")){
			if(acadDepartment==null || acadDepartment.equals("")){
			    siteProperties.addProperty(getHierarchyLevel2(), "Programme de MBA"); 
			}
	    	}//fin de la patch
	    }
	}
	
	
	try{
	    siteService.save(site);
	    
	    log.info("Success saving site: "+site.getTitle());
	}
	catch(Exception ex){
	    log.error("Unable to save site: "+site.getTitle()+" cause: "+ex.toString());
	}
    }
    
    
    private boolean propertiesExist(Site site, String acadDepartment, String acadCareer){
	
	boolean propExist = true;
	
	ResourcePropertiesEdit siteProperties = site.getPropertiesEdit();
	
	String level1 = siteProperties.getProperty(getHierarchyLevel1());
	String level2 = siteProperties.getProperty(getHierarchyLevel2());
	String level3 = siteProperties.getProperty(getHierarchyLevel3());
	
	String prop1=null;
	String prop2=null;
	String prop3=null;
	
	if(CERTIFICAT_CODE.equalsIgnoreCase(acadCareer)){	    
	    prop1 = CERTIFICAT;
	    prop2 = acadDepartment;
	}
	else{
	    prop1 = SERVICE_ENSEIGNEMENT;
	    prop2 = acadDepartment;
	    prop3 = acadCareer;
	}
	
	level1 = (level1==null?"":level1);
	level2 = (level2==null?"":level2);
	level3 = (level3==null?"":level3);
	
	prop3 = (prop3==null?"":prop3);

	
	if(!level1.equals(prop1)){
	    propExist = false;
	}	
	else if(!level2.equals(prop2)){
	    propExist = false;
	}
	else if(!level3.equals(prop3)){
	    propExist = false;
	}
	
	return propExist;
    }
    
    
    private String getAcademicCareer(String acadCareerFromSite){
	
	String acadCareerCode = null;
	
	if(allAcademicCareer==null){
	    allAcademicCareer = cmService.getAcademicCareers();
	}
	
	for(AcademicCareer ac : allAcademicCareer){
	    
	    if(ac.getEid().equalsIgnoreCase(acadCareerFromSite) ||
		ac.getDescription().equalsIgnoreCase(acadCareerFromSite) ||
		ac.getDescription_fr_ca().equalsIgnoreCase(acadCareerFromSite))
	    {
		acadCareerCode = ac.getEid();
		break;
	    }
		    		
	}
	
	return acadCareerCode;
    }
    
    
    private String getHierarchyLevel1(){
	return serverConfigService.getString(HIERARCHY_LEVEL1);
    }
    
    private String getHierarchyLevel2(){
	return serverConfigService.getString(HIERARCHY_LEVEL2);
    }
    
    private String getHierarchyLevel3(){
	return serverConfigService.getString(HIERARCHY_LEVEL3);
    }
    
    
    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	super.loginToSakai("CreateSiteHierarchyJobImpl");
    }


}

