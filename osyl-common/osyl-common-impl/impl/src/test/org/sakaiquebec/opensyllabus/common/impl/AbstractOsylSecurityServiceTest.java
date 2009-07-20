package org.sakaiquebec.opensyllabus.common.impl;

import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;

public abstract class AbstractOsylSecurityServiceTest extends AbstractServiceTest {

    private OsylSecurityService service;
    
    public final OsylSecurityService getService() {
        return this.service;
    }
    
    public final void setService(OsylSecurityService service) {
        this.service = service;
    }
}
