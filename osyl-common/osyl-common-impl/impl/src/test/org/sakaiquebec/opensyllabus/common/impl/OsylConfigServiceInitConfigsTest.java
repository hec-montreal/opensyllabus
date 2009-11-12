package org.sakaiquebec.opensyllabus.common.impl;

//This test checks that initConfigs() creates the number of configs it's supposed to (2 according to the javadoc).
public final class OsylConfigServiceInitConfigsTest extends AbstractOsylConfigServiceTest {

	//The OsylServiceConfig interface says:
	//Temporary method to automatically create 2 configs (HEC - UdeM) in the corresponding table.
	//Let's verify that...
    public void testInitConfigs() throws Exception {
    	//DO NOT CALL initConfigs(), the IMPL does it already through init()
    	//(which in itself shows that initConfigs() should not be part of the interface).
    	//We expect 6 configs as the doc says.
    	
    	assertEquals(
    			"OsylServiceConfig.initConfigs() didn't created the number of " + 
    			"COConfigSerialized that the documentation says it does.",
    			7, getService().getConfigs().size());    	
    }
}
