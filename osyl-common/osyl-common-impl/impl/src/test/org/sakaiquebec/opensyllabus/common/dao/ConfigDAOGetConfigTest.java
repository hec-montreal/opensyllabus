package org.sakaiquebec.opensyllabus.common.dao;

import java.util.LinkedList;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;


/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOGetConfigTest extends AbstractConfigDAOTest {
	
		
	public void testGetNullConfig() throws Exception{

		try {
			getConfigDAO().getConfig(null);
			fail("getConfig(): expected an Exception from a NULL id parameter.");
		}		
		catch (Exception e) {
			//fine
		}		
	}
	
	public void testGetBlankConfig() throws Exception{
		try {
			getConfigDAO().getConfig("");
			fail("getConfig(): expected an Exception from an empty id parameter.");
		}		
		catch (Exception e) {
			//fine
		}		
	}

	public void testGetNonExistingConfig() throws Exception{

		try {
			getConfigDAO().getConfig("NonExistingID");
			fail("getConfig(): expected an Exception from a non-existing id parameter.");
		}
		catch (Exception e) {
			//fine
		}		
	}
	
	public void testGetOneConfig() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig();
		configDAO.createConfig(config);
		
		COConfigSerialized otherConfig = configDAO.getConfig(config.getConfigId());
		assertEquals("Configs don't match.", config, otherConfig);		
	}
	
	public void testGetManyConfigs() throws Exception {
		final int SIZE = 10;
		COConfigDao configDAO = getConfigDAO();

		List<COConfigSerialized> configs = new LinkedList<COConfigSerialized>();
		for (int i = 0; i < SIZE; i++) {
			COConfigSerialized config = newConfig();
			configDAO.createConfig(config);
			configs.add(config);
		}
		
		for (COConfigSerialized config: configs) {
			COConfigSerialized otherConfig = configDAO.getConfig(config.getConfigId());
			assertEquals("Configs don't match.", config, otherConfig);
		}
	}
	
}
