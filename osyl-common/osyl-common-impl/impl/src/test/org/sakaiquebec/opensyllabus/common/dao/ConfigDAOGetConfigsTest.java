package org.sakaiquebec.opensyllabus.common.dao;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;


/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOGetConfigsTest extends AbstractConfigDAOTest {
	
	public void testGetNoConfig() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		List<COConfigSerialized> configs = configDAO.getConfigs();
		
		//We've not added any data yet
		assertEquals(
				"getConfigs() returned " + configs.size() + " configs. This test requires an empty database.",
				0, configs.size());
	}
	
	public void testGetOneConfig() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		configDAO.createConfig(newConfig("0"));
		List<COConfigSerialized> configs = configDAO.getConfigs();
		assertEquals(
				"One COConfigSerialized was created.",
				1, configs.size());
	}
	
	public void testGetManyConfigs() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		List<COConfigSerialized> configs = configDAO.getConfigs();
				
		//a little overkill...
		final int SIZE = 10;
		for (int i = 0; i < SIZE; i++) {
			configDAO.createConfig(newConfig("" + i));
		}
		configs = configDAO.getConfigs();
		assertEquals(
				"Invalid number of configs returned by getConfigs().",
				SIZE, configs.size());
		
		//still works if I delete one?
		configDAO.removeConfig("0");
		configs = configDAO.getConfigs();
		assertEquals(
				"Invalid number of configs returned by getConfigs().",
				SIZE - 1, configs.size());		
	}
	
}
