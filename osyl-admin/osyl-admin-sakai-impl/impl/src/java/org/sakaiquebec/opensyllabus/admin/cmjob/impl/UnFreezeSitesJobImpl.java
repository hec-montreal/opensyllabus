package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.UnFreezeSitesJob;

public class UnFreezeSitesJobImpl implements
		UnFreezeSitesJob {

	private static Log log = LogFactory
			.getLog(UnFreezeSitesJobImpl.class);

	private String sessionConfig = null;
	
	private String periodConfig = null;
	
	private String permissionsConfig = null;

	private List<String> functionsToPut;
		
    private List<Site> allSites;    
	

	// ***************** SPRING INJECTION ************************//

	/**
	 * Course management service integration.
	 */
	private CourseManagementService cmService;

	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}
	
    /**
     * Administration ConfigurationService injection
     */
    private ConfigurationService adminConfigService;

    /**
     * @param adminConfigService
     */
    public void setAdminConfigService(ConfigurationService adminConfigService) {
	this.adminConfigService = adminConfigService;
    }
	
    /**
     * The site service used to create new sites: Spring injection
     */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code> needed to create a new site in Sakai.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
    	this.siteService = siteService;
    }
	
	/**
	 * Integration of the AuthzGroupService
	 */
	private AuthzGroupService authzGroupService;

	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	/**
	 * Integration of the EventTrackingService
	 */
	private EventTrackingService eventTrackingService;

	public void setEventTrackingService(
			EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}

	/**
	 * Integration of the UsageSessionService
	 */
    private UsageSessionService usageSessionService;

    public void setUsageSessionService(UsageSessionService usageSessionService) {
	this.usageSessionService = usageSessionService;
    }
    
	/**
	 * Integration of the SessionManager
	 */
	private SessionManager sessionManager;

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
   
	// ***************** END SPRING INJECTION ************************//

	public void execute(JobExecutionContext arg0) throws JobExecutionException {

	long start = System.currentTimeMillis();
	log.info("UnFreezeSitesAfterSessionJobImpl: Starting...");

	loginToSakai();
	
	//-------------------------------------------------------------------
	//Retrieve session and period for frozen sites to replace permissions 
	//to original permissions for each role
	//-------------------------------------------------------------------
	adminConfigService.getFrozenSessionPeriodConfig();
    setSessionConfig(adminConfigService.getFrozenSession());
	setPeriodConfig(adminConfigService.getFrozenPeriod());
	
	log.info("UnFreezeSitesAfterSessionJobImpl: session from xml:" + getSessionConfig());
	log.info("UnFreezeSitesAfterSessionJobImpl: period from xml:" + getPeriodConfig());	


	if (getSessionConfig() != null && getPeriodConfig()!= null) {	
		//-------------------------------------------------------------------		
		//Retrieve list of all site to evaluate	
		//-------------------------------------------------------------------

		allSites =
			siteService.getSites(SiteService.SelectionType.ANY, "course",
				null, null, SiteService.SortType.NONE, null);
	
		log.info("UnFreezeSitesAfterSessionJobImpl: sites to correct:"
				+ allSites.size());
	
		Site site = null;
		AuthzGroup tmplRealm = null;
		AuthzGroup siteRealm = null;		
	    CourseOffering courseOffering = null;
		long cont = 0;
		for (int i = 0; i < allSites.size(); i++) {
			site = allSites.get(i);
			//If the site is frozen 
		    if (getFrozenValue(site)) {
				Section section = cmService.getSection(site.getProviderGroupId());
				String courseOfferingEid = section.getCourseOfferingEid();
				courseOffering = cmService.getCourseOffering(courseOfferingEid);				
			    if (courseOffering != null) {		    	
			    	courseOffering.getAcademicSession().getEid();
				    AcademicSession session = courseOffering.getAcademicSession();
				    //If the site has a session and period from configuration file
		    		if (getSessionConfig().equals(getSession(session)) && getPeriodConfig().equals(getPeriod(session))) {
		        	    try {
								tmplRealm = authzGroupService
										.getAuthzGroup(TEMPLATE_ID);
								siteRealm = authzGroupService
										.getAuthzGroup(REALM_PREFIX
												+ site.getId());		        	    	
	        		    } catch (GroupNotDefinedException e) {
	        		    	log.error(e.getMessage());
	        		    }
	        		    if (tmplRealm != null && siteRealm != null) {
			        		log.info("***The site :" + site.getTitle() + " is valid to process it ***");	        		    	
					    	replacePermission(tmplRealm, siteRealm);								
					    	setFrozenStatus(site, NOT);	
	        		    	cont++;					    	
	        		    } else {
			        		log.info("*** siteRealm or tmplRealm are null***");	        		    	
	        		    }
		    		}
			    }
			}
		}
		log.info("Sites unfrozen : " + cont + "");		
	}//session and period 	
	else {
		log.info("The file unFrozenSitesConfig.xml does not exist in OpenSyllabus Admin Ressources/config.");
	}	

	//-------------------------------------------------------------------
	log.info("UnFreezeSitesAfterSessionJobImpl: completed in "
			+ (System.currentTimeMillis() - start) + " ms");
	logoutFromSakai();
			
    }

/**
 * It returns a true if the site is frozen
 * 
 * @param site
 */	
private boolean getFrozenValue(Site site) {
	ResourcePropertiesEdit rp = site.getPropertiesEdit();
	boolean coIsFrozen = false;
	if (rp.getProperty(PROP_SITE_ISFROZEN)!= null) {
		if (rp.getProperty(PROP_SITE_ISFROZEN).equals("true")) {
			coIsFrozen = true;
			log.info("Site frozen: " + site.getTitle());				
		}
	}	
	return coIsFrozen;
}
	
/**
 * It replaces original permissions to permissions to read and view
 * 
 * @param siteRealm
 */	
  private void replacePermission(AuthzGroup tmplt, AuthzGroup realm) {
	try {
		Set<Role> rolesSite = realm.getRoles();
		Set<Role> rolesTmpl = tmplt.getRoles();		
		
		if (rolesSite != null && rolesTmpl != null) {
			//It just to test
			log.info(" Roles site num: " + rolesSite.size());
			log.info(" Roles template num: " + rolesTmpl.size());
			for (Role roleSite : rolesSite) {
				Role roleToUpdate = realm.getRole(roleSite.getId());
				for (Role roleTmpl : rolesTmpl) {
					Role roleFromTmpl = realm.getRole(roleTmpl.getId());
					if (roleToUpdate.getId().equalsIgnoreCase(roleFromTmpl.getId())) {
						roleToUpdate.disallowAll();
						Set<String> permissions = roleTmpl.getAllowedFunctions();
						roleToUpdate.allowFunctions(permissions);
					    authzGroupService.save(realm);
						log.info("roleToUpdate:...getId(): '" + roleToUpdate.getId()+ "' the permission was applied ***");						
					}
				}
			}			
		} else {
			log.info("Roles are null.");
		}
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
  	}	   

	private void setFrozenStatus(Site site, String value) {
	// Replace property isfrozen to true
	// if sakai_realm.provider_id not null
	if (site.getProviderGroupId() != null) {
		ResourcePropertiesEdit rpr = site.getPropertiesEdit();
		rpr.addProperty("isfrozen", value);
		log.info("*** The setFrozenStatus works.");
		try {
			siteService.save(site);
		} catch (IdUnusedException e) {
			log.info("The site " + site.getTitle() + " does not exist.");
		} catch (PermissionException e) {
			log.info("You are not allowed to update the site "
					+ site.getTitle());
		}
		log.info("The site " + site.getTitle() + " has been upgraded with !isfrozen");
	}
	}  
 
     
    private String getPeriod(AcademicSession session) {
    	String sessionId = session.getEid();
    	String period = sessionId.substring(4, sessionId.length());
    	return period;
    }    
    
    private String getSession(AcademicSession session) {
    	String sessionName = null;
    	String sessionId = session.getEid();
    	Date startDate = session.getStartDate();
    	String year = startDate.toString().substring(0, 4);

    	if ((sessionId.charAt(3)) == '1')
    	    sessionName = WINTER + year;
    	if ((sessionId.charAt(3)) == '2')
    	    sessionName = SUMMER + year;
    	if ((sessionId.charAt(3)) == '3')
    	    sessionName = FALL + year;

    	return sessionName;
   }
    
	/**
	 * Logs in the sakai environment
	 */
	protected void loginToSakai() {
		Session sakaiSession = sessionManager.getCurrentSession();
		sakaiSession.setUserId("admin");
		sakaiSession.setUserEid("admin");

		// establish the user's session
		usageSessionService.startSession("admin", "127.0.0.1", "UnFreezeSitesSync");

		// update the user's externally provided realm definitions
		authzGroupService.refreshUser("admin");

		// post the login event
		eventTrackingService.post(eventTrackingService.newEvent(
				UsageSessionService.EVENT_LOGIN, null, true));
	}

	/**
	 * Logs out of the sakai environment
	 */
	protected void logoutFromSakai() {
		// post the logout event
		eventTrackingService.post(eventTrackingService.newEvent(
				UsageSessionService.EVENT_LOGOUT, null, true));
		usageSessionService.logout();
	}
	
	public String getSessionConfig() {
		return sessionConfig;
	}

	private void setSessionConfig(String sessionConfig) {
		this.sessionConfig = sessionConfig;
	}

	public String getPeriodConfig() {
		return periodConfig;
	}

	private void setPeriodConfig(String periodConfig) {
		this.periodConfig = periodConfig;
	}  

	public String getPermissionsConfig() {
		return permissionsConfig;
	}

	public void setPermissionsConfig(String permissionsConfig) {
		this.permissionsConfig = permissionsConfig;
	}	

	public List<String> getFunctionsToPut() {
	    return functionsToPut;
	}

	public void setFunctionsToPut(List<String> functionsToPut) {
	    this.functionsToPut = functionsToPut;
	}
}