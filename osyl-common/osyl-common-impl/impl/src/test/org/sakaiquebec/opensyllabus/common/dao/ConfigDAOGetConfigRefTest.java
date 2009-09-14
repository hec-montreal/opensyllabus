package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;


/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOGetConfigRefTest extends AbstractConfigDAOTest {
	
	public void testGetNullConfigRef() throws Exception{	    
		try {
			getConfigDAO().getConfigRef(null);
			fail("getConfigRef(): expected an Exception from a NULL id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}
	
	public void testGetBlankConfigRef()  throws Exception{		    
		try {
			getConfigDAO().getConfigRef("");
			fail("getConfigRef(): expected an Exception from an empty id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}

	public void testGetNonExistingConfigRef()  throws Exception{		    
		try {
			getConfigDAO().getConfigRef("NonExistingID");
			//FIXME: if the above does not fail, 
			//then it's inconsistent with the other DAO's method behaviour(s) - some throw exceptions, some don't...
			fail("getConfigRef(): expected an Exception from a non-existing id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}

	public void testGetExistingConfigRef()  throws Exception{				
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig();
	    configDAO.createConfig(config);

        String ref = configDAO.getConfigRef(config.getConfigId());
	
		assertNotNull(
				"Expected a non-null ref returned by getConfigRef()",
				ref);
		
		assertEquals(
				"getConfigRef() returned an unexpected ref.",
				config.getConfigRef(), ref);
	}	
}
