package org.sakaiquebec.opensyllabus.common.impl;

import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;

public abstract class AbstractOsylConfigServiceTest extends AbstractServiceTest {

    private OsylConfigService service;
    
    public final OsylConfigService getService() {
        return this.service;
    }
    
    public final void setService(OsylConfigService service) {
        this.service = service;
    }     
}
