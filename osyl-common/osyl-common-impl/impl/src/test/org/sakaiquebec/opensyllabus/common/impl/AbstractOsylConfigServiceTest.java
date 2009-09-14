package org.sakaiquebec.opensyllabus.common.impl;

import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;

public abstract class AbstractOsylConfigServiceTest extends AbstractServiceTest {

    private OsylConfigService service;
    
    public final OsylConfigService getService() {
        return this.service;
    }

    @Override
    protected void onServiceSetUp() throws Exception {
        COConfigDao dao = getMockery().newConfigDAO();
        
        OsylConfigServiceImpl serviceImpl = new OsylConfigServiceImpl();
        
       // serviceImpl.setBaseName("UnitTestBaseName");
        serviceImpl.setConfigDao(dao);
        //FIXME: setContextLocale() has an inner call to ComponentManager.getInstance()...from Sakai
        //This shows a problem with the design of OsylConfigServiceImpl, which should not extend ResourceLoader.        
        //serviceImpl.setContextLocale(Locale.getDefault());
        //This test will fail because of the above
                
        serviceImpl.init();
        this.service = serviceImpl;
    }    
}
