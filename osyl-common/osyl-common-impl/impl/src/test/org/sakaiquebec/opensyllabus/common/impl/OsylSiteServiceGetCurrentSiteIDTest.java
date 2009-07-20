package org.sakaiquebec.opensyllabus.common.impl;

public final class OsylSiteServiceGetCurrentSiteIDTest extends AbstractOsylSiteServiceTest {
    
    public void testGetCurrentSiteID() throws Exception {
        String id = getService().getCurrentSiteId();
        
        assertNotNull(
                "OsylSiteService.getCurrentSiteId() cannot return a null value.", 
                id);
        
        assertTrue(
                "OsylSiteService.getCurrentSiteId() cannot return a blank value.", 
                id.trim().length() > 0);
    }
}
