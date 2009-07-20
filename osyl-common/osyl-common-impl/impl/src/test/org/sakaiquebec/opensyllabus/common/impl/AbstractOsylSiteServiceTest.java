package org.sakaiquebec.opensyllabus.common.impl;

import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;

public abstract class AbstractOsylSiteServiceTest extends AbstractServiceTest {

    private OsylSiteService service;
    
    public final OsylSiteService getService() {
        return this.service;
    }
    
    public final void setService(OsylSiteService service) {
        this.service = service;
    }
}
