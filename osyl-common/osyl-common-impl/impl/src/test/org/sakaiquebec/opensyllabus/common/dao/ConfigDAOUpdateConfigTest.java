package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;


/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOUpdateConfigTest extends AbstractConfigDAOTest {
		
	public void testUpdateNull() throws Exception {
		try {
			getConfigDAO().updateConfig(null);
			fail("updateConfig(): expected an Exception from a NULL config parameter.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
	public void testUpdateNonExisting() throws Exception {
		COConfigSerialized config = newConfig();
		try {
			getConfigDAO().updateConfig(config);
			fail("updateConfig(): expected an Exception from a non-existing config parameter.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
	//Example with only one tested field (do a test for each field)
	public void testUpdateRulesConfig() throws Exception {
		final String REF = "testUpdateRulesConfig()"+ System.currentTimeMillis();
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig();		
		configDAO.createConfig(config);
		
		config = configDAO.getConfig(config.getConfigId());
		config.setRulesConfig(REF);		
		configDAO.updateConfig(config);
		
		COConfigSerialized updated = configDAO.getConfig(config.getConfigId());
		
		assertEquals(
				"configRefs don't match (updateConfig() did not update).",
				REF,
				updated.getRulesConfig());		
	}	
	
	//TODO: ADD MORE
}
