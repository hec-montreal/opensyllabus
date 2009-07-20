package org.sakaiquebec.opensyllabus.common.impl;

public final class OsylSecurityServiceGetUserRoleTest extends AbstractOsylSecurityServiceTest {

    public void testGetUserRole() {
        String role = getService().getCurrentUserRole();
        //what to do with that?
    }
}
