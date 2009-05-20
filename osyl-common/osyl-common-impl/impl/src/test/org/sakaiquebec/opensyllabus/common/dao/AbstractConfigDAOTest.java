package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;

abstract class AbstractConfigDAOTest extends AbstractDAOTest {

	private COConfigDao configDAO;//What we're testing
	
	public final void setConfigDAO(COConfigDao configDAO) {
		this.configDAO = configDAO;
	}
	
	protected final COConfigDao getConfigDAO() {
		return this.configDAO;
	}


	protected static final COConfigSerialized newConfig(String id) {
		COConfigSerialized config = new COConfigSerialized();
		
		config.setConfigId(id);		
		config.setCascadingStyleSheetURI("http://acme/stylesheet.css");
		config.setConfigRef("UnitTestConfigRef#" + id);
		config.setRulesConfig("UnitTestRulesConfig#" + id);
		
		return config;
	}
	
	protected static final void assertEquals(COConfigSerialized config, COConfigSerialized otherConfig) {
		assertEquals(
				"CascadingStyleSheetURIs don't match.",
				config.getCascadingStyleSheetURI(),
				otherConfig.getCascadingStyleSheetURI());
		
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
