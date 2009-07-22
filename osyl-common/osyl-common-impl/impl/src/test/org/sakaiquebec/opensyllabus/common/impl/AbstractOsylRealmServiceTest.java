package org.sakaiquebec.opensyllabus.common.impl;


public abstract class AbstractOsylRealmServiceTest extends AbstractServiceTest {

    private OsylRealmServiceImpl service;
    
    public final OsylRealmServiceImpl getService() {
        return this.service;
    }

    @Override
    protected void onServiceSetUp() {
        OsylRealmServiceImpl serviceImpl = new OsylRealmServiceImpl();
        
        serviceImpl.setAutoDdl(true);
        serviceImpl.setRecreate(true);
        serviceImpl.setNewRealmName("UnitTest");
        serviceImpl.setParentRealm("ParentRealm");
                
        //FIXME: This will fail as long as Sakai coupling is not removed (XXXX.getInstance()) from the impl.
        serviceImpl.init();
        this.service = serviceImpl;
    }
    
}
