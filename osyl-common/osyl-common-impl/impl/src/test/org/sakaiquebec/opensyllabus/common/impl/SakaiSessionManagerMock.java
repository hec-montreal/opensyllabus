package org.sakaiquebec.opensyllabus.common.impl;

import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.VoidAction;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

final class SakaiSessionManagerMock extends AbstractMock<SessionManager> {
	private Session sakaiSession;
	private ToolSession toolSession;
	
	private String uuid;
    private String userID;
    
	protected SakaiSessionManagerMock(Mockery mockery) {
		super(mockery, SessionManager.class);
		
		this.sakaiSession = mockery.mock(Session.class);
		this.toolSession = mockery.mock(ToolSession.class);
		
		this.uuid = UUID.randomUUID().toString();
        this.userID = OsylMockery.SESSION_USER;
	}

	@Override
	protected Expectations getExpectations(final SessionManager mock) {
		return new Expectations() {
            {
                allowing(equal(sakaiSession)).
                method("getUser.*").
                withNoArguments();
                will(returnValue(userID));
            }
            {
                allowing(equal(sakaiSession)).
                method("setUser.*");
                with(any(String.class));
                will(new VoidAction() {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        String id = (String)invocation.getParameter(0);
                        userID  = id;
                        return null;
                    }                    
                });
            }
            {
                allowing(equal(sakaiSession)).
                method("getId");
                will(returnValue(uuid));
            }
            {
                allowing(equal(toolSession)).
                method("getUserId");
                will(returnValue(OsylMockery.SESSION_USER));
            }
            {
                allowing(equal(toolSession)).
                method("getPlacementId");
                will(returnValue(OsylMockery.SESSION_PLACEMENT));
            }
            {
                allowing(mock).
                getCurrentToolSession(); 
                will(returnValue(toolSession));
            }
            {
                allowing(mock).
                getCurrentSession(); 
                will(returnValue(sakaiSession));
            }
        };        
	}
}
