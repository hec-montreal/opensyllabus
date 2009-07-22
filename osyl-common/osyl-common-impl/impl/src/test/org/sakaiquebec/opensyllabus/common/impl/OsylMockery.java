package org.sakaiquebec.opensyllabus.common.impl;

import java.util.UUID;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.api.Expectation;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.lib.legacy.ClassImposteriser;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;

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
    
    public AuthzGroupService newAuthzGroupService() {
        final AuthzGroupService auth = this.mockery.mock(AuthzGroupService.class);
       
        return auth;
    }
    
    public ContentHostingService newContentHostingService() {
        return this.mockery.mock(ContentHostingService.class);
    }
    
    public SecurityService newSecurityService() {
        final SecurityService service = this.mockery.mock(SecurityService.class); 
     
        return service;
        
    }
    
    public SiteService newSiteService() {
        final SiteService siteService  = this.mockery.mock(SiteService.class);
        final Site site = this.mockery.mock(Site.class);
        checking(new Expectations() {
            {
                allowing(site).getId(); 
                will(returnValue(SITE_ID));
            }            
        });
        
        checking(new Expectations() {
            {
                try {
                    allowing(siteService).
                    getSite(SITE_ID);
                    will(returnValue(site));
                }
                catch (IdUnusedException e) {
                    //this is abnormal and should never happen
                    throwException(e);
                }
            }
            {
                allowing(any(SiteService.class)).
                method("getSite").
                with(any(String.class));
                will(throwException(new IdUnusedException("Mocked")));                    
            }
            
        });
        
        
        return siteService;
    }
    
    public ToolManager newToolManager() {       
        final Placement toolPlacement = this.mockery.mock(Placement.class);
        checking(new Expectations() {
            {
                allowing(toolPlacement).getContext(); 
                will(returnValue(PLACEMENT_CONTEXT));
            }
            
        });
        
        final ToolManager toolManager = this.mockery.mock(ToolManager.class);
        checking(new Expectations() {
            {
                allowing(toolManager).getCurrentPlacement(); 
                will(returnValue(toolPlacement));
            }
            
        });
        return toolManager;
    }
    
    public SessionManager newSessionManager() {
        final ToolSession toolSession = this.mockery.mock(ToolSession.class);
        checking(new Expectations() {
            {
                allowing(toolSession).getUserId();
                will(returnValue(SESSION_USER));
            }
            {
                allowing(toolSession).getPlacementId();
                will(returnValue(SESSION_PLACEMENT));
            }
        });
        
        final SessionManager sessionManager = this.mockery.mock(SessionManager.class);
        checking(new Expectations() {
            {
                allowing(sessionManager).getCurrentToolSession(); 
                will(returnValue(toolSession));
            }
        });
        
        return sessionManager;        
    }

    public IdManager newIdManager() {
        final IdManager idManager = this.mockery.mock(IdManager.class);
        checking(new Expectations() {
            {
                allowing(idManager).createUuid();
                will(returnValue(UUID.randomUUID()));
            }
        });
        return idManager;
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
