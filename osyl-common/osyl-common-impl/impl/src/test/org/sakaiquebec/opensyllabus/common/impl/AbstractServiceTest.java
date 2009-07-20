package org.sakaiquebec.opensyllabus.common.impl;

import org.springframework.test.AbstractTransactionalSpringContextTests;

abstract class AbstractServiceTest extends  AbstractTransactionalSpringContextTests {
    
    protected void onServiceSetUp() {}
    
    protected void onServiceTearDown() {}
        
    @Override
    protected String[] getConfigLocations() {
        return new String[]{
                "classpath:service-test.xml"};
    }
    
    @Override
    protected final void onSetUp() {
        onServiceSetUp();
    }
    
    @Override
    protected final void onTearDown() {
        onServiceTearDown();
    }    
}
