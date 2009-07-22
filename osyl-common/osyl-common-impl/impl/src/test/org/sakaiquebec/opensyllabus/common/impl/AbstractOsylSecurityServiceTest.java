package org.sakaiquebec.opensyllabus.common.impl;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;

public abstract class AbstractOsylSecurityServiceTest extends AbstractServiceTest {

    private OsylSecurityService service;  
    
    private AuthzGroupService authService;    
    private ContentHostingService hostingService;
    private SecurityService securityService;
    private SiteService siteService;
    
    private SessionManager sessionManager;
    private ToolManager toolManager;
    
    public final OsylSecurityService getService() {
        return this.service;
    }

    public final void setService(OsylSecurityService service) {
        this.service = service;
    }

    @Override
    protected void onServiceSetUp() {
        this.authService = getMockery().newAuthzGroupService();
        this.hostingService = getMockery().newContentHostingService();
        this.securityService = getMockery().newSecurityService();
        this.siteService = getMockery().newSiteService();
        this.sessionManager = getMockery().newSessionManager();
        this.toolManager = getMockery().newToolManager();
        
        OsylSecurityServiceImpl serviceImpl = new OsylSecurityServiceImpl();
        
        serviceImpl.setAuthzGroupService(authService);
        serviceImpl.setContentHostingService(hostingService);
        serviceImpl.setSecurityService(securityService);
        serviceImpl.setSessionManager(sessionManager);
        serviceImpl.setSiteService(siteService);
        serviceImpl.setToolManager(toolManager);
        serviceImpl.init();
        
        this.service = serviceImpl;        
    }

    protected final AuthzGroupService getAuthzGroupService() {
        return authService;
    }

    protected final ContentHostingService getHostingService() {
        return hostingService;
    }

    protected final SecurityService getSecurityService() {
        return securityService;
    }

    protected final SiteService getSiteService() {
        return siteService;
    }

    protected final SessionManager getSessionManager() {
        return sessionManager;
    }

    protected final ToolManager getToolManager() {
        return toolManager;
    }

}
