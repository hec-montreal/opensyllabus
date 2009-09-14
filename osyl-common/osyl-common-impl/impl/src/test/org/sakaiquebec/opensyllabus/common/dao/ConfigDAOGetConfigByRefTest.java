package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;


/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOGetConfigByRefTest extends AbstractConfigDAOTest {
	
	public void testGetNullConfigByRef() throws Exception{	    
		try {
			getConfigDAO().getConfigByRef(null);
			fail("getConfigByRef(): expected an Exception from a NULL id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}
	
	public void testGetBlankConfigByRef() throws Exception{		    	    
		try {
			getConfigDAO().getConfigByRef("");
			fail("getConfigByRef(): expected an Exception from an empty id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}

	public void testGetNonExistingConfigByRef() throws Exception{    			
		try {
			getConfigDAO().getConfigByRef("NonExistingID");
			fail("getConfigByRef(): expected an Exception from a non-existing id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}

	public void testGetConfigByRef() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		
		//Create a new config, get it back and check that it's the same config.
		COConfigSerialized config = newConfig();		
		configDAO.createConfig(config);
		
		COConfigSerialized otherConfig = configDAO.getConfigByRef(config.getConfigRef());
		assertEquals(config, otherConfig);		
	}
	
}
