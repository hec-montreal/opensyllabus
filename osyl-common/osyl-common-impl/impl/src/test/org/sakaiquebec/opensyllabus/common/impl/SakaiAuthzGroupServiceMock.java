package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.action.VoidAction;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;

final class SakaiAuthzGroupServiceMock extends AbstractMock<AuthzGroupService>{
	
	private AuthzGroup group;
	protected SakaiAuthzGroupServiceMock(Mockery mockery) {
		super(mockery, AuthzGroupService.class);
		group = mockery.mock(AuthzGroup.class);
	}

	@Override
	protected Expectations getExpectations(final AuthzGroupService auth) {
		return new Expectations() {
            {
                try {
                    allowing(auth).
                    getAuthzGroup(OsylMockery.SESSION_USER);                 
                    will(returnValue(group));
                }
                catch (GroupNotDefinedException e) {}
            }
            {
                allowing(equal(auth)).
                method("getAuthzGroup").  
                with(any(String.class));
                will(throwException(new GroupNotDefinedException("")));
            }
            {
                try {
                    allowing(auth).                
                    removeAuthzGroup(group);
                    will(new VoidAction());
                }
                catch (AuthzPermissionException e) {}
            }
            {
                allowing(equal(auth)).            
                method("addAuthzGroup").
                with(anything());
                will(new VoidAction());
            }
        };        
	}
	
}
