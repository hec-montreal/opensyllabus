package org.sakaiquebec.opensyllabus.common.impl;

import java.util.UUID;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;


//FIXME: this entire test should not exist (or not in that form): 
//The OsylSiteService should not have a getSite() method that returns a Sakai model object.
//The service should return an OSYL model object, avoiding coupling with Sakai and exposing Sakai objects to the Osyl API.
//In other words: there should not be a Sakai import in this test.
public final class OsylSiteServiceGetSiteTest extends AbstractOsylSiteServiceTest {
    
     
    //While the API is being refactored, test existing methods
    public void testValidGetSite() throws Exception {
        OsylSiteService service = getService();
        
        //FIXME: the reason why getCurrentSiteId() throws an exception is unclear.
        String siteID = service.getCurrentSiteId();
        
        assertNotNull(
                "OsylSiteService.getCurrentSiteId() must return a non-null for this test to work.",
                siteID);
        
        try {
            Site site = service.getSite(siteID);
            assertNotNull(
                    "OsylSiteService.getSite() returned NULL for a valid site ID '" + siteID + "'.", 
                    site);
            //TODO: add some field checking.
        }
        catch (IdUnusedException e) {
            fail("Unexpected IdUnusedException for a valid siteID.");
        }
    }
    
    public void testInvalidGetSite() {
        OsylSiteService service = getService();        
        try {
            service.getSite("" + UUID.randomUUID());
            fail("Expected an IdUnusedException because of a non-existing site ID.");
        }
        catch (IdUnusedException e) {
            //fine
        }        
    }
}
