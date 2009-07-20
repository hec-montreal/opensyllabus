package org.sakaiquebec.opensyllabus.common.impl;

public final class OsylConfigServiceCreateTest extends AbstractOsylConfigServiceTest {

    public void testInvalidCreate() {        
        try {
            getService().createConfig(null);
            fail("Expected an Exception because of a Null parameter.");            
        }
        //FIXME: it would be better not to have a general exception, rather an IllegalArgumentException or something similar.
        catch (Exception e) {
            //fine
        }
    }
}
