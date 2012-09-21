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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.RemoveUsersFromSiteJob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* Remove users from a HEC course site 
* 
* The user is removed based on his id (matricule) and his role in the site (Library staff, secretary, etc.)
*
* @author <a href="mailto:philippe.rancourt@hec.ca">Philippe Rancourt</a>
* @version $Id: $
*/
public class RemoveUsersFromSiteJobImpl extends OsylAbstractQuartzJobImpl  implements RemoveUsersFromSiteJob{
	
    
    /**
     * Our logger
     */
    private static Log log = LogFactory
	    .getLog(RemoveUsersFromSiteJobImpl.class);

    
    //map containing the user to be removed from a site for a particular role
    private HashMap<String, String> userRoleMap = new HashMap<String, String>();
    
    
    
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("RemoveUsersFromSiteJobImpl: starting");

	loadConfigFile();
	
	Set<String> usersToRemove = getUsersToRemove();
	
	List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, null,
			null, null, SiteService.SortType.NONE, null);

	Site site = null;

	log.info("RemoveUsersFromSiteJobImpl: sites to browse:"
		+ allSites.size());
	
	
	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);
	    
	    boolean siteModified = false;
	    	    
	    for(String userId : usersToRemove){
			        	    
    	    	Member member = site.getMember(userId);
    	    
    	    	if(member!=null){
    		
    	    	    Role memberRole = member.getRole();
    		
    	    	   // log.info("USER: "+userId+" FOUND IN SITE: "+site.getTitle()+" WITH ROLE:"+memberRole.getId());
    		
    	    	    String roleToRemove = userRoleMap.get(userId);
    	    	    
    	    	    if(memberRole!=null && roleToRemove.equalsIgnoreCase(memberRole.getId())){
    		    
    	    		site.removeMember(userId);
    		    
    	    		//log.info("USER: "+userId+" REMOVED FROM SITE: "+site.getTitle());
    		   
    	    		siteModified = true;
    	    	    }
    	    	}
	    }//end for userToRemove

	    if(siteModified){
		
		//le site ne doit pas être un site Espace (i.e. Espace CAT, Espace Secrétaire, Espace Bibliothèque)
		if(!site.getTitle().startsWith("Espace")){		
		    saveSite(site);
		}
	    }
	    
	    //remove the reference from the list to free up memory
	    allSites.set(i,null);
	    
	}//end for all sites
	

	log.info("RemoveUsersFromSiteJobImpl: completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	
	logoutFromSakai();

    }

    
    
    private String getUserId(String eid){
	
	String userId = "";
        		 
	try{
	    userId = userDirectoryService.getUserId(eid);
	}
	catch(Exception ex){}
	
	return userId;
    }
    
    
    private void saveSite(Site site){
	
	site.loadAll();
	
	try{
	    siteService.save(site);
	    
	    log.info("Success saving site: "+site.getTitle());
	}
	catch(Exception ex){
	    log.error("Unable to save site: "+site.getTitle()+" cause: "+ex.toString());
	}

    }
    
    
    private Set<String> getUsersToRemove(){
	
	return userRoleMap.keySet();
    }
    
    
    
    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    
    private void loadConfigFile(){
	
	String fileName = ConfigurationService.CONFIG_FOLDER + "removeUsersFromSitesConfig.xml";
	
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

                NodeList idList = userElement.getElementsByTagName("id");
                
                if(idList!=null && idList.getLength()!=0){
                
                    Element idElement = (Element)idList.item(0);
                
                    NodeList textIDList = idElement.getChildNodes();
                    String id = ((Node)textIDList.item(0)).getNodeValue().trim();
                                             
                    NodeList roleList = userElement.getElementsByTagName("role");
                
                    if(roleList!=null && roleList.getLength()!=0){
                    
                	Element roleElement = (Element)roleList.item(0);
                    
                	NodeList textRoleList = roleElement.getChildNodes();
                	String role = ((Node)textRoleList.item(0)).getNodeValue().trim();
                    
                	userRoleMap.put(getUserId(id), role);
                    }
                }
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
	super.loginToSakai("RemoveUsersFromSiteJobImpl");
    }


}

