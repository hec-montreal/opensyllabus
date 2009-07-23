package org.sakaiquebec.opensyllabus.common.impl;


public abstract class AbstractOsylRealmServiceTest extends AbstractServiceTest {

    private boolean autoDDL;
    private boolean autoGroupCreate;
    
    private OsylRealmServiceImpl service;
    
    //We need to test with and without those 2 options, 
    //so enforce this by constructor requirement.
    public AbstractOsylRealmServiceTest(boolean autoDDL, boolean autoGroupCreate) {
        super();
        this.autoDDL = autoDDL;
        this.autoGroupCreate = autoGroupCreate;
    }
    
    public final OsylRealmServiceImpl getService() {
        return this.service;
    }

    @Override
    protected void onServiceSetUp() {
        OsylRealmServiceImpl serviceImpl = new OsylRealmServiceImpl();
        
        serviceImpl.setAutoDdl(autoDDL);
        serviceImpl.setRecreate(autoGroupCreate);
        serviceImpl.setNewRealmName("UnitTest");
        serviceImpl.setParentRealm("ParentRealm");
                
        serviceImpl.setAuthzGroupService(getMockery().newAuthzGroupService());
        serviceImpl.setFunctionManager(getMockery().newFunctionManager());
        serviceImpl.setSessionManager(getMockery().newSessionManager());

        //FIXME: init() needs a specific batch of tests
        serviceImpl.init();
        this.service = serviceImpl;
    }        
}
