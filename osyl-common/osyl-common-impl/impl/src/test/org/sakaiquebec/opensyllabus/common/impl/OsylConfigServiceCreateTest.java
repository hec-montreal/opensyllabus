package org.sakaiquebec.opensyllabus.common.impl;

import java.util.Map;

import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;

public final class OsylConfigServiceCreateTest extends AbstractOsylConfigServiceTest {

    public void testInvalidCreate() throws Exception {
        try {
            getService().createConfig(null);
            fail("Expected an Exception because of a Null parameter.");            
        }
        //FIXME: it would be better not to have a general exception, rather an IllegalArgumentException or something similar.
        catch (NullPointerException e) {
        	//NPEs are probably not expected
            throw e;
        }
        catch (IllegalArgumentException e) {
            //this is acceptable.
        }
        catch (Exception e) {
            //this is not acceptable.
        	throw e;
        }
    }
    
    public void testValidCreate() throws Exception {    	
    	OsylConfigService service = getService();
    	//We can't assume there's no config - the impl auto creates at least 1.
    	int oldConfigCount = service.getConfigs().size();
    	
    	COConfigSerialized config = new COConfigSerialized();   		
    	service.createConfig(config);   		
   		
    	//FIXME: there is some inconsistency in the API...
    	//We inserted COConfigSerialized but there's no way to get it back
    	//(without internal knowledge that COConfigSerialized should have an ID assigned by us).
    	Map<String, String> configs = service.getConfigs();
   		assertEquals(
   				"Invalid size from OsylConfigService.getConfigs().",
   				oldConfigCount + 1, configs.size());
   		
   		//FIXME: we want to check that the returned config is equal to ours, but..how?
    }
}
