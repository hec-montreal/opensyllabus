package org.sakaiquebec.opensyllabus.common.impl;


//FIXME: OsylRealmService.getSiteType()'s behaviour is pretty much undefined...It needs clarifications
public final class OsylRealmServiceGetSiteTypeTest extends AbstractOsylRealmServiceTest {
    
    public OsylRealmServiceGetSiteTypeTest() {
        super(false, false);
    }
    
    //FIXME: this uncovers a bug in OsylRealmServiceImpl.getSiteType()
    public void testGetTemplateSiteTypeIsBroken() {
        OsylRealmServiceImpl service = getService();
        try {
            service.setNewRealmName("template");
            service.getSiteType();
        }
        catch (StringIndexOutOfBoundsException e) {
            fail("Caught a StringIndexOutOfBoundsException from OsylRealmServiceImpl.getSiteType() because of a bug in this method. Please fix.");
        }
    }
    
    public void testGetTemplateSiteType() {
        OsylRealmServiceImpl service = getService();
        service.setNewRealmName("templateA");
        
        //FIXME: looking at the code, that seems to be what's expected (strip 'template' from the realm name).
        //In this case, the char count is wrong in getSiteType().
        assertEquals(
                "OsylRealmServiceImpl.getSiteType() returned an unexpected Site Type for template<XXX>.",
                "A", service.getSiteType());
    }
    
    public void testGetSiteType() {
        OsylRealmServiceImpl service = getService();
        service.setNewRealmName("A");
        
        assertEquals(
                "OsylRealmServiceImpl.getSiteType() returned an unexpected Site Type for A.",
                "A", service.getSiteType());
    }
}
