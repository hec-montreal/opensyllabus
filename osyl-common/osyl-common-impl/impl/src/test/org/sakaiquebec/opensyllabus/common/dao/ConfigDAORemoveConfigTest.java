package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;

/**
 * @see org.sakaiquebec.opensyllabus.common.dao.COConfigDaoImpl
 */
public final class ConfigDAORemoveConfigTest extends AbstractConfigDAOTest {

    public void testRemoveNull() throws Exception {
	COConfigSerialized config = newConfig("0");
	getConfigDAO().createConfig(config);
	try {
	    getConfigDAO().removeConfig(null);
	    fail("removeConfig(): expected an Exception from a NULL id parameter.");
	} catch (Exception e) {
	    // fine
	}
    }

    public void testRemoveBlank() throws Exception {
	COConfigSerialized config = newConfig("0");
	getConfigDAO().createConfig(config);
	try {
	    getConfigDAO().removeConfig("");
	    fail("removeConfig(): expected an Exception from an empty id parameter.");
	} catch (Exception e) {
	    // fine
	}
    }

    public void testRemoveNonExisting() throws Exception {
	COConfigSerialized config = newConfig("0");
	getConfigDAO().createConfig(config);
	try {
	    getConfigDAO().removeConfig("NonExistingID");
	    fail("removeConfig(): expected an Exception from a non-existing id parameter.");
	} catch (Exception e) {
	    // fine
	}
    }

    public void testRemoveExisting() throws Exception {
	COConfigDao configDAO = getConfigDAO();

	COConfigSerialized config = newConfig("0");
	configDAO.createConfig(config);

	// just making sure it really exists...
	config = configDAO.getConfig(config.getConfigId());
	assertNotNull("getConfig() returned a null config for configId '"
		+ config.getConfigId() + "'.", config);

	configDAO.removeConfig(config.getConfigId());
	try {
	    config = configDAO.getConfig(config.getConfigId());
	    fail("Expected an Exception because of a removed config ID '"
		    + config.getConfigId() + "': config not removed.");
	} catch (Exception e) {
	    // fine
	}
    }
}
