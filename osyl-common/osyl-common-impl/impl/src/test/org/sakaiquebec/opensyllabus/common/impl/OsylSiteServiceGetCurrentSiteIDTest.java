package org.sakaiquebec.opensyllabus.common.impl;

public final class OsylSiteServiceGetCurrentSiteIDTest extends AbstractOsylSiteServiceTest {
    
    public void testGetCurrentSiteID() throws Exception {       
        String id = getService().getCurrentSiteId();        
        assertEquals(
                "OsylSiteService.getCurrentSiteId() returned an unexpected value.", 
                OsylMockery.SITE_ID, id);        
    }
    
    
}
