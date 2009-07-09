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
	
	//FIXME: this test should fail as we should not have to set internal IDs to database objects.
	public void testCreateWithoutID() throws Exception {
		testCreateImpl(null);//this test should not fail..		
	}

	public void testCreateWithID() throws Exception {
		testCreateImpl("0");		
	}
	
	//FIXME: the behaviour of createConfig() with 2 configs and the same config ref is unspecified...	
	public void testCreateDoubleRef() {
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig("0");	
		try{
		    configDAO.createConfig(config);
		}catch (Exception e) {
		   fail("Unable to create a config");
		}
		
		COConfigSerialized otherConfig = newConfig("1");
		otherConfig.setConfigRef(config.getConfigRef());
		try {
			//since COConfigDao.getConfigRef(String) has a unique parameter and return value,
			//we must assume that the config ref is also a unique identifier,
			//so this should throw an error
			configDAO.createConfig(otherConfig);
			
			//but it does not...
			fail("Should not be able to create two configs with the same config ref.");
		}
		catch (Exception e) {
			//fine
		}
	}

	
	public void testCreateDoubleID() {
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig("0");		
		try{
		    configDAO.createConfig(config);
		}catch (Exception e) {
		   fail("Unable to create a config");
		}
		
		//Make sure only the ID field has the same value as the first config		
		COConfigSerialized otherConfig = newConfig("1");
		otherConfig.setConfigId("0");		
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
	
	
	private void testCreateImpl(String id) throws Exception {
		COConfigDao configDAO = getConfigDAO();
		
		COConfigSerialized config = newConfig(id);		
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
