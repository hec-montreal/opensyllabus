/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.common.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * Implementation of the {@link OsylConfigService} interface.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
// FIXME: extending a class (ResourceLoader) should really be avoided here.
// It makes it impossible to have a hierarchy of service-oriented classes.
public class OsylConfigServiceImpl extends ResourceLoader implements OsylConfigService {

	private static Log log = LogFactory.getLog(OsylConfigServiceImpl.class);

	/**
	 * Default locale considered
	 */
	private static String DEFAULT_LOCALE = "fr_CA";

	/**
	 * Folder containing the file messages (.properties)
	 */
	protected static final String CONFIG_UIMESSAGES_DIR = "bundle";

	/**
	 * Folder containing the skin file (.css)
	 */
	protected static final String CONFIGS_SKIN_DIRECTORY = "skin";

	/**
	 * File containing the xml representation of the rules or config
	 */
	protected static final String CONFIG_RULES = "rules.xml";

	/**
	 * Css file for skin properties
	 */
	protected static final String CONFIG_SKIN = "osylcore.css";

	/**
	 * Message file in french
	 */
	protected static final String PROPERTIES_EXTENSION = ".properties";

	/**
	 * Package (ie: folder) containing the message files (.properties)
	 */
	protected static final String CONFIG_DIR = "osylcoconfigs";

	/**
	 * Folder containing the file messages (.properties)
	 */
	protected static final String MESSAGES_DIRECTORY = "bundle";

	/**
	 * Injection of the ConfigDao
	 */
	private COConfigDao coConfigDao;

	/**
	 * Constructor.
	 */
	public OsylConfigServiceImpl() {
		super(CONFIG_UIMESSAGES);
	}

	/**
	 * Sets the {@link COConfigDao}.
	 * 
	 * @param courseOutlineConfigDao
	 */
	public void setConfigDao(COConfigDao courseOutlineConfigDao) {
		this.coConfigDao = courseOutlineConfigDao;
	}

	/**
	 * Init method called at initialization of the bean.
	 */
	public void init() throws Exception {
		initConfigs();
		log.info("INIT from Config service");
	}

	/**
	 * Destroy method called at destruction of the bean.
	 */
	public void destroy() {
		log.info("DESTROY from Config service");
	}

	/** {@inheritDoc} */
	public void createConfig(COConfigSerialized coConfig) throws Exception {
		coConfigDao.createConfig(coConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getConfigs() throws Exception {
		Map<String, String> configsMap = new HashMap<String, String>();
		List<COConfigSerialized> list = coConfigDao.getConfigs();
		for (Iterator<COConfigSerialized> iter = list.iterator(); iter
				.hasNext();) {
			COConfigSerialized config = iter.next();
			configsMap.put(config.getConfigId(), config.getDescription());
		}
		return configsMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public COConfigSerialized getCourseOutlineConfig(String configId) throws Exception {
		return coConfigDao.getConfig(configId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	public COConfigSerialized getCurrentConfig(String webappDir)
			throws Exception {
		List<COConfigSerialized> configs = coConfigDao.getConfigs();
		COConfigSerialized coConfig = (COConfigSerialized) configs.get(0);
		coConfig = fillConfig(coConfig, webappDir, coConfig.getConfigRef(),
				coConfig.getConfigId());
		return coConfig;
	}

	/** {@inheritDoc} */
	public COConfigSerialized getConfig(String configId, String webappDir)
			throws Exception {
		COConfigSerialized coConfig = coConfigDao.getConfig(configId);
		coConfig = fillConfig(coConfig, webappDir, coConfig.getConfigRef(),
				configId);
		return coConfig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public void removeConfig(String configId) throws Exception {
		coConfigDao.removeConfig(configId);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public void updateConfig(COConfigSerialized coConfig) throws Exception {
		coConfigDao.updateConfig(coConfig);
	}

	/**
	 * Private method used to fill out some variables of a COConfigSerialiazed:
	 * the css URI, the String representation of the rules and a map
	 * representation of the messages.
	 * 
	 * @param coConfig
	 * @param dir
	 * @param configId
	 * @return Filled COConfigSerialized
	 * @throws Exception
	 */
	private COConfigSerialized fillConfig(COConfigSerialized coConfig,
			String webappdir, String configRef, String configId)
			throws Exception {
		String path = webappdir + configRef;
		coConfig.setCascadingStyleSheetURI(getCascadingStyleSheetURI(path));
		coConfig.setCoreBundle(getMessages(path, CONFIG_UIMESSAGES,
				getCurrentLocale()));
		coConfig.setRulesConfig(getRules(path));
		return coConfig;
	}

	/**
	 * For a given skin directory in the webapp, this method reads the xml rule
	 * file into a String.
	 * 
	 * @param dir
	 * @return String
	 * @throws Exception
	 */
	private String getRules(String dir) throws Exception {
		String result = "";
		File rules = new File(dir, CONFIG_RULES);
		BufferedReader readFile;
		String line;

		try {
			readFile = new BufferedReader(new FileReader(rules));
			while ((line = readFile.readLine()) != null) {
				result += line;
			}
			readFile.close();

		} catch (FileNotFoundException e) {
			log.error("Unable to find file config files", e);
			throw new Exception(e);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public Map<String, String> getMessages(String path, String baseFileName,
			String locale) throws Exception {
		try {

			String key;
			String dir = path + File.separator + MESSAGES_DIRECTORY
					+ File.separator;

			File messagesFile = new File(dir, baseFileName + "_" + locale
					+ PROPERTIES_EXTENSION);

			// The file is not found for this locale, use default.
			if (!messagesFile.exists()) {
				messagesFile = new File(dir, baseFileName
						+ PROPERTIES_EXTENSION);
				log.info("Cannot load property file: " + baseFileName
						+ " for locale " + locale);
			}

			FileInputStream inputStream = new FileInputStream(messagesFile);
			PropertyResourceBundle bundle = new PropertyResourceBundle(
					inputStream);

			// We transform our ResourceBundle to a Map to return it.
			Map<String, String> messages = new HashMap<String, String>();
			Enumeration<String> enu = bundle.getKeys();
			while (enu.hasMoreElements()) {
				key = enu.nextElement();
				messages.put(key, bundle.getString(key));
			}

			return messages;

		} catch (Exception e) {
			log.warn("Unable to retrieve messages", e);
			return new HashMap<String, String>();
		}
	}

	/**
	 * For a given directory returns the String representation of the URI
	 * 
	 * @param dir
	 * @return String
	 */
	private String getCascadingStyleSheetURI(String dir) {
		String relativePath = dir.substring(dir.indexOf("webapps") + 7, dir
				.length());
		relativePath = relativePath + File.separator + CONFIGS_SKIN_DIRECTORY
				+ File.separator + CONFIG_SKIN;
		relativePath = relativePath.replaceAll("\\\\", "/");
		return relativePath;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public void initConfigs() throws Exception {
		if (coConfigDao.getConfigs().size() <= 0) {
			COConfigSerialized coConfig = new COConfigSerialized(IdManager
					.createUuid(), CONFIG_DIR + File.separator + "default",
					"Config from HEC Montreal");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(IdManager.createUuid(),
					CONFIG_DIR + File.separator + "udem", "Config udem");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(IdManager.createUuid(),
					CONFIG_DIR + File.separator + "udemCompetencesComposantes",
					"Config UdeM - Competences Composantes");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(IdManager.createUuid(),
					CONFIG_DIR + File.separator + "udemCompetencesSeances",
					"Config UdeM - Competences Seances");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(IdManager.createUuid(),
					CONFIG_DIR + File.separator + "udemObjectifsActivites",
					"Config UdeM - Objectifs Activites");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(IdManager.createUuid(),
					CONFIG_DIR + File.separator + "udemObjectifsSeances",
					"Config UdeM - Objectifs Seances");
			createConfig(coConfig);
		}
	}

	/** {@inheritDoc} */
	public COConfigSerialized getConfigByRef(String configRef, String webappDir)
			throws Exception {
		COConfigSerialized coConfig = coConfigDao.getConfigByRef(configRef);
		coConfig = fillConfig(coConfig, webappDir, configRef, coConfig
				.getConfigId());
		return coConfig;
	}

	/** {@inheritDoc} */
	public String getCurrentLocale() throws Exception {

		String locale = "";
		ResourceLoader rb = new ResourceLoader();
		try {
			Locale sessionLocale = rb.getLocale();
			locale = sessionLocale.toString();
		} catch (NullPointerException e) {
			locale = DEFAULT_LOCALE;
		} catch (Exception e) {
			log.error("Unable to retrieve current locale", e);
			throw new Exception(e);
		}

		return locale;
	}

	/** {@inheritDoc} */
	public COSerialized fillCo(String dir, COSerialized coSerialized)
			throws Exception {
		if (coSerialized != null)
			coSerialized
					.setMessages(getMessages(dir,
							OsylConfigService.CONFIG_COMESSAGES, coSerialized
									.getLang()));
		return coSerialized;
	}

}
