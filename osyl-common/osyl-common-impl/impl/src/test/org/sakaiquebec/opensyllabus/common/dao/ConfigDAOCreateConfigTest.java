package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;

/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 * */
public final class ConfigDAOCreateConfigTest extends AbstractConfigDAOTest {

	public void testCreateNull() {
		try {
			getConfigDAO().createConfig(null);
			fail("Should not be able to create a null config.");
		}
		catch (NullPointerException e) {
			fail("Should not receive a NullPointerException: " + e.getLocalizedMessage());
		}
		catch (Exception e) {
			//fine
		}
	}
	
	
	public void testCreateDoubleID() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig();		
		configDAO.createConfig(config);
		
		//Make sure only the ID field has the same value as the first config		
		COConfigSerialized otherConfig = newConfig();		
		otherConfig.setConfigId(config.getConfigId());
		
		try {
			configDAO.createConfig(otherConfig);
			fail("Should not be able to create two configs with the same config ID.");
		}
		catch (Exception e) {
		    //fine, we're actually expecting something like
			//org.springframework.orm.hibernate3.HibernateSystemException: 
			//a different object with the same identifier value was already associated with the session
		}
	}
		
	public void testCreateValid() throws Exception {
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig();		
		configDAO.createConfig(config);
		
		//Make sure we have a new ID
		assertNotNull(
				"Expected a non-null config ID after createConfig().",
				config.getConfigId());		
		
		//Read it back
		COConfigSerialized otherConfig = configDAO.getConfig(config.getConfigId());					
		
		//verify that both objects have equal fields
		//FIXME: should we check for actual equality rather than per-field check?
		assertEquals(config, otherConfig);		
	}
}
