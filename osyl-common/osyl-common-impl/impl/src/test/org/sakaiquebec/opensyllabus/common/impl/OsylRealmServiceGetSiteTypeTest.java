package org.sakaiquebec.opensyllabus.common.impl;


//FIXME: OsylRealmService.getSiteType()'s behaviour is pretty much undefined...It needs clarifications
public final class OsylRealmServiceGetSiteTypeTest extends AbstractOsylRealmServiceTest {
   
    public void testGetTemplateType() {
        OsylRealmServiceImpl service = getService();
        service.setNewRealmName("templateA");
        
        //FIXME: looking at the code, that seems to be what's expected..
        assertEquals(
                "OsylRealmServiceImpl.getSiteType() returned an unexpected Site Type for template<XXX>.",
                service.getSiteType(), "A");
    }
}
