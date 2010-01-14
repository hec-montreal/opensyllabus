package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.util.UUID;

abstract class AbstractConfigDAOTest extends AbstractDAOTest {

	private COConfigDao configDAO;//What we're testing
	
	public final void setConfigDAO(COConfigDao configDAO) {
		this.configDAO = configDAO;
	}
	
	protected final COConfigDao getConfigDAO() {
		return this.configDAO;
	}

	protected static final COConfigSerialized newConfig() {
	    String id = UUID.uuid();
		
	    COConfigSerialized config = new COConfigSerialized();		
		config.setCascadingStyleSheetPath("http://acme/stylesheet.css");
		config.setConfigRef("UnitTestConfigRef#" + id);
		config.setRulesConfig("UnitTestRulesConfig#" + id);
		
		return config;
	}
	
	protected static final void assertEquals(COConfigSerialized config, COConfigSerialized otherConfig) {
		assertEquals(
				"CascadingStyleSheetURIs don't match.",
				config.getCascadingStyleSheetPath(),
				otherConfig.getCascadingStyleSheetPath());
		
		assertEquals(
				"configIDs don't match.",
				config.getConfigId(),
				otherConfig.getConfigId());
		
		assertEquals(
				"configRefs don't match.",
				config.getConfigRef(),
				otherConfig.getConfigRef());
		
		assertEquals(
				"rulesConfigs don't match.",
				config.getRulesConfig(),
				otherConfig.getRulesConfig());
		
		assertEquals(
				"rulesConfigs don't match.",
				config.getI18nMessages(),
				otherConfig.getI18nMessages());
	}
}
