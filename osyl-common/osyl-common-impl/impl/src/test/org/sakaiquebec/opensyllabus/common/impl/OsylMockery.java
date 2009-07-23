package org.sakaiquebec.opensyllabus.common.impl;

import org.hamcrest.Description;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.api.Expectation;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.lib.legacy.ClassImposteriser;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;

public final class OsylMockery {
    
    protected static final String SESSION_USER = "UnitTest";
    protected static final String SESSION_USER_ROLE = "access";
    protected static final String ADMIN_USER_ROLE = "maintain";
    
    protected static final String SESSION_PLACEMENT = "UnitTestPlacement";
    
    protected static final String SITE_ID = "UnitTestSite";
    
    //Sakai's placement context are usually site IDs, 
    //'usually' meaning the Sakai API doesn't guarantee it but is a 'well known' behaviour.
    //We still make a clear distinction between the two by having 2 variables.
    protected static final String PLACEMENT_CONTEXT = SITE_ID;
    
    private Mockery mockery;
    
    public OsylMockery() {
        super();
        this.mockery = new JUnit3Mockery() {
            {
                // Enable mocks of concrete classes
                setImposteriser(ClassImposteriser.INSTANCE);
            }};
    }
    
    public COConfigDao newConfigDAO() {    	
    	return new COConfigDAOMock(mockery).getMock();
    }
    
    public AuthzGroupService newAuthzGroupService() {
       return new SakaiAuthzGroupServiceMock(mockery).getMock();
    }
    
    public ContentHostingService newContentHostingService() {
        return new SakaiContentHostingServiceMock(mockery).getMock();
    }
    
    public SecurityService newSecurityService() {
       return new SakaiSecurityServiceMock(mockery).getMock();        
    }
    
    public SiteService newSiteService() {
    	return new SakaiSiteServiceMock(mockery).getMock(); 
    }
    
    public ToolManager newToolManager() {       
       return new SakaiToolManagerMock(mockery).getMock();
    }
    
    public SessionManager newSessionManager() {
        return new SakaiSessionManagerMock(mockery).getMock();       
    }

    public IdManager newIdManager() {
        return new SakaiIdManagerMock(mockery).getMock();
    }
    
    public FunctionManager newFunctionManager() {
        return new SakaiFunctionManagerMock(mockery).getMock();
    }
    
    protected void addExpectation(Expectation expectation) {
        mockery.addExpectation(expectation);
    }

    protected void assertIsSatisfied() {
        mockery.assertIsSatisfied();
    }

    protected void checking(ExpectationBuilder expectations) {
        mockery.checking(expectations);
    }

    protected void describeTo(Description description) {
        mockery.describeTo(description);
    }

    protected <T> T mock(Class<T> typeToMock) {
        return mockery.mock(typeToMock);
    }

    protected Sequence sequence(String name) {
        return mockery.sequence(name);
    }

    protected States states(String name) {
        return mockery.states(name);
    }
    
    
}
