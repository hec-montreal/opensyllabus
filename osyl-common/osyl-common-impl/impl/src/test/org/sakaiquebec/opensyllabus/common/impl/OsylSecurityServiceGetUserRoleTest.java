package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;

public final class OsylSecurityServiceGetUserRoleTest extends AbstractOsylSecurityServiceTest {
        
    @Override
    protected void onServiceSetUp() {
        super.onServiceSetUp();         
        
        final AuthzGroupService auth = getAuthzGroupService();         
        checking(new Expectations() {
            {
                allowing(auth).
                getUserRole(OsylMockery.SESSION_USER, "/site/" + OsylMockery.PLACEMENT_CONTEXT);
                will(returnValue(OsylMockery.SESSION_USER_ROLE));
            }
        });
    }

    /** Checks that the user role is 'access' for a user that is a not an admin.*/
    public void testAccessGetUserRole() {
        final SecurityService securityService = getSecurityService();
        checking(new Expectations() {
            {
                allowing(securityService).isSuperUser();
                will(returnValue(false));
            }
        });        
        assertEquals(
                "Returned user role is incorrect.",
                OsylMockery.SESSION_USER_ROLE, getService().getCurrentUserRole());
    }
    
    /** Checks that the user role is 'maintain' for a user that is an admin.*/
    public void testAdminGetUserRole() {        
        final SecurityService securityService = getSecurityService();
        checking(new Expectations() {
            {
                allowing(securityService).isSuperUser();
                will(returnValue(true));
            }
        });
        assertEquals(
                "Returned user role is incorrect.",
                OsylMockery.ADMIN_USER_ROLE, getService().getCurrentUserRole());
    }
}
