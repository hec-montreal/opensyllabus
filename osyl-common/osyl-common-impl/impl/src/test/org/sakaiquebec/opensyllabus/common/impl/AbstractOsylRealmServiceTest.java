package org.sakaiquebec.opensyllabus.common.impl;

import org.sakaiquebec.opensyllabus.common.api.OsylRealmService;

public abstract class AbstractOsylRealmServiceTest extends AbstractServiceTest {

    private OsylRealmService service;
    
    public final OsylRealmService getService() {
        return this.service;
    }
    
    public final void setService(OsylRealmService service) {
        this.service = service;
    }
}
