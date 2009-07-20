package org.sakaiquebec.opensyllabus.common.impl;

public final class OsylRealmServiceGetTypeTest extends AbstractOsylRealmServiceTest {

    public void testGetType() {
        String type = getService().getSiteType();
        
        assertNotNull(
                "OsylRealmService.getSiteType() cannot return a null value.", 
                type);
        
        assertTrue(
                "OsylRealmService.getSiteType() cannot return a blank value.", 
                type.trim().length() > 0);
    }
}
