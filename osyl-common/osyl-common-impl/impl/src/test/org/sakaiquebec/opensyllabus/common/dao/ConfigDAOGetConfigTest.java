package org.sakaiquebec.opensyllabus.common.dao;

import java.util.LinkedList;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;


/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOGetConfigTest extends AbstractConfigDAOTest {
	
	//FIXME: there a bug in getConfig() implementation: it does not check the returned result size,	
	//Other tests don't detect it because of the catch all (caused by the DAO methods throwing Exception only).
	//Remove this test when the problem is fixed.
	public void testGetConfigNeedsToBeFixed() {
		try {
			getConfigDAO().getConfig("" + System.currentTimeMillis());
			fail("getConfig(): expected an exception from a non-existing id parameter.");
		}
		catch (IndexOutOfBoundsException e) {
			fail(
					"COConfigDaoImpl.getConfig() has a bug which causes an IndexOutOfBoundsException: " +
					"the result returned from Hibernate is not checked and get(0) is performed on an empty list): " + 
					e.getLocalizedMessage());
		}
		catch (Exception e) {
			//fine
		}		
	}
	
	public void testGetNullConfig() {
		try {
			getConfigDAO().getConfig(null);
			fail("getConfig(): expected an Exception from a NULL id parameter.");
		}		
		catch (Exception e) {
			//fine
		}		
	}
	
	public void testGetBlankConfig() {		
		try {
			getConfigDAO().getConfig("");
			fail("getConfig(): expected an Exception from an empty id parameter.");
		}		
		catch (Exception e) {
			//fine
		}		
	}

	public void testGetNonExistingConfig() {		
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
		
		COConfigSerialized config = newConfig("0");
		configDAO.createConfig(config);
		
		COConfigSerialized otherConfig = configDAO.getConfig(config.getConfigId());
		assertEquals("Configs don't match.", config, otherConfig);		
	}
	
	public void testGetManyConfigs() throws Exception {
		final int SIZE = 10;
		COConfigDao configDAO = getConfigDAO();

		List<COConfigSerialized> configs = new LinkedList<COConfigSerialized>();
		for (int i = 0; i < SIZE; i++) {
			COConfigSerialized config = newConfig("" + i);
			configDAO.createConfig(config);
			configs.add(config);
		}
		
		for (COConfigSerialized config: configs) {
			COConfigSerialized otherConfig = configDAO.getConfig(config.getConfigId());
			assertEquals("Configs don't match.", config, otherConfig);
		}
	}
	
}
