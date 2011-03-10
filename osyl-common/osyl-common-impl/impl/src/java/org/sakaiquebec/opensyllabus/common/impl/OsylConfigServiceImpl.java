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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the {@link OsylConfigService} interface.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylConfigServiceImpl extends Object implements OsylConfigService {

    private static Log log = LogFactory.getLog(OsylConfigServiceImpl.class);

    /**
     * Folder containing the skin file (.css)
     */
    private static final String CONFIGS_SKIN_DIRECTORY = "skin";

    /**
     * File containing the xml representation of the rules or config
     */
    private static final String CONFIG_RULES = "rules.xml";

    /**
     * File containing the rolesList config (in xml)
     */
    private static final String CONFIG_ROLES_LIST = "coRolesList.xml";

    /**
     * File containing the evalTypeList config (in xml)
     */
    private static final String CONFIG_EVAL_TYPE_LIST = "coEvalTypeList.xml";

    /**
     * Item node name for xml parsing
     */
    private static final String ITEM_NODE_NAME = "item";

    /**
     * Value attribute name for xml parsing
     */
    private static final String VALUE_ATTRIBUTE_NAME = "value";

    /**
     * Injection of the ConfigDao
     */
    private COConfigDao coConfigDao;

    private String defaultConfig;

    /**
     * Constructor.
     */
    public OsylConfigServiceImpl() {
	super();
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
	    configsMap.put(config.getConfigId(), config.getConfigRef());
	}
	return configsMap;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COConfigSerialized getCourseOutlineConfig(String configId)
	    throws Exception {
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
	coConfig =
		fillConfig(coConfig, webappDir, coConfig.getConfigRef(),
			getDefautVersionConfig(webappDir + CONFIG_DIR
				+ File.separator + coConfig.getConfigRef()
				+ File.separator));
	return coConfig;
    }

    private static String getDefautVersionConfig(String dir) {
	String folderVersion = "1.0";
	float folderVersionFloat = Float.parseFloat(folderVersion);
	File folder = new File(dir);
	File[] listOfFiles = folder.listFiles();

	for (int i = 0; i < listOfFiles.length; i++) {
	    if (listOfFiles[i].isDirectory()) {
		float directoryVersionFloat =
			Float.parseFloat(listOfFiles[i].getName());
		if (Float.parseFloat(listOfFiles[i].getName()) > folderVersionFloat) {
		    folderVersionFloat = directoryVersionFloat;
		    folderVersion = listOfFiles[i].getName();
		}
	    }
	}
	return folderVersion;
    }

    /** {@inheritDoc} */
    public COConfigSerialized getConfig(String configId, String version,
	    String webappDir) throws Exception {
	COConfigSerialized coConfig = coConfigDao.getConfig(configId);
	coConfig =
		fillConfig(coConfig, webappDir, coConfig.getConfigRef(),
			version);
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
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public Map<String, String> getMessages(String path, String baseFileName,
	    String locale) throws Exception {
	return OsylConfigServiceMessages
		.getMessages(path, baseFileName, locale);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public void initConfigs() throws Exception {
	// Config names are internationalized, we only see keys here:
	initConfig("default");
    }

    private void initConfig(String configref) throws Exception {
	try {
	    coConfigDao.getConfigByRef(configref);
	} catch (Exception e) {
	    log.warn("Could not find config with configref='" + configref
		    + "'. This config will be created");
	    if (log.isInfoEnabled()) {
		log.info("Details:");
		e.printStackTrace();
	    }
	    createConfig(configref);
	}
    }

    /** {@inheritDoc} */
    public COConfigSerialized getConfigByRefAndVersion(String configRef,
	    String version, String webappDir) throws Exception {
	COConfigSerialized coConfig = coConfigDao.getConfigByRef(configRef);
	coConfig = fillConfig(coConfig, webappDir, configRef, version);
	return coConfig;
    }

    /** {@inheritDoc} */
    public String getCurrentLocale() throws Exception {
	return OsylConfigServiceMessages.getCurrentLocale();
    }

    /** {@inheritDoc} */
    public COSerialized fillCo(String webappdir, COSerialized coSerialized)
	    throws Exception {
	if (coSerialized != null) {
	    coSerialized
		    .setMessages(getMessages(getConfigAbsolutePath(webappdir,
			    coSerialized.getOsylConfig().getConfigRef(),
			    coSerialized.getConfigVersion()),
			    OsylConfigService.CONFIG_COMESSAGES, coSerialized
				    .getLang()));
	}
	return coSerialized;
    }

    private void createConfig(String configRef) throws Exception {
	COConfigSerialized coConfig = new COConfigSerialized();
	coConfig.setConfigRef(configRef);
	createConfig(coConfig);
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
    private static COConfigSerialized fillConfig(COConfigSerialized coConfig,
	    String webappdir, String configRef, String version)
	    throws Exception {
	String path = getConfigAbsolutePath(webappdir, configRef, version);
	coConfig.setCascadingStyleSheetPath(getCssPath(path, coConfig));
	coConfig.setCoreBundle(OsylConfigServiceMessages.getMessages(path,
		CONFIG_UIMESSAGES));
	coConfig.setRulesConfig(getRules(path));
	coConfig.setRolesList(getRolesList(path));
	coConfig.setEvalTypeList(getEvalTypeList(path));
	coConfig.setSettings(OsylConfigSettingsService.getSettings(path,
		CONFIG_SETTINGS));
	coConfig.setVersion(version);
	return coConfig;
    }

    private static String getConfigAbsolutePath(String webappdir,
	    String configRef, String version) {
	String dir =
		webappdir + CONFIG_DIR + File.separator + configRef
			+ File.separator;
	if (version == null)
	    version = getDefautVersionConfig(dir);
	return dir + version;
    }

    /**
     * For a given config directory in the webapp, this method reads the
     * rolesList xml file and parse it as a List
     * 
     * @param dir
     * @return List
     */
    private static List<String> getRolesList(String dir) throws Exception {
	File xmlFile = new File(dir, CONFIG_ROLES_LIST);
	List<String> list = parseXmlForList(xmlFile);
	return list;
    }

    /**
     * Parsing xml file (list xml schema) and retrieve list
     * 
     * @param xmlFile
     * @return
     * @throws Exception
     */
    private static List<String> parseXmlForList(File xmlFile) throws Exception {
	List<String> list = new ArrayList<String>();
	DocumentBuilder docBuilder =
		DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document dom = docBuilder.parse(xmlFile);
	Element nodeElement = dom.getDocumentElement();
	NodeList nodeList = nodeElement.getChildNodes();
	if (nodeList != null) {
	    for (int i = 0; i < nodeList.getLength(); i++) {
		Node node = nodeList.item(i);
		if (node.getNodeName().equalsIgnoreCase(ITEM_NODE_NAME)) {
		    String value =
			    node.getAttributes().getNamedItem(
				    VALUE_ATTRIBUTE_NAME).getNodeValue();
		    list.add(value);
		}
	    }
	}
	return list;
    }

    /**
     * For a given config directory in the webapp, this method reads the
     * evalType xml file and parse it as a List
     * 
     * @param dir
     * @return List
     */
    private static List<String> getEvalTypeList(String dir) throws Exception {
	File xmlFile = new File(dir, CONFIG_EVAL_TYPE_LIST);
	List<String> list = parseXmlForList(xmlFile);
	return list;
    }

    /**
     * For a given skin directory in the webapp, this method reads the xml rule
     * file into a String.
     * 
     * @param dir
     * @return String
     * @throws Exception
     */
    private static String getRules(String dir) throws Exception {
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

    private static String getCssPath(String webAppDir,
	    COConfigSerialized coConfig) throws Exception {
	String webAppRelativeDir =
		webAppDir.substring(webAppDir.indexOf("webapps") + 7);
	String relativePath =
		webAppRelativeDir + File.separator + CONFIGS_SKIN_DIRECTORY
			+ File.separator;
	relativePath = relativePath.replaceAll("\\\\", "/");
	return relativePath;
    }

    public String getCssPathFromConfigId(String webAppDir, String configId)
	    throws Exception {
	return getCssPath(webAppDir, coConfigDao.getConfig(configId));
    }

    public String getCssPathFromConfigRef(String webAppDir, String configRef)
	    throws Exception {
	return getCssPath(webAppDir, coConfigDao.getConfigByRef(configRef));
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public Map<String, String> getSettings(String path, String baseFileName)
	    throws Exception {
	return OsylConfigSettingsService.getSettings(path, baseFileName);
    }

    public String getDefaultConfig() {
	if (defaultConfig == null || defaultConfig.length() == 0) {
	    defaultConfig = DEFAULT_CONFIG_REF;
	}
	return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
	this.defaultConfig = defaultConfig;
    }

    public String getXml(COConfigSerialized coConfig, String lang,
	    String webappDir) {
	StringBuilder fileData = new StringBuilder(1000);
	try {

	    BufferedReader reader =
		    getXmlTemplateFileReader(coConfig, lang, webappDir);

	    char[] buf = new char[1024];
	    int numRead = 0;
	    while ((numRead = reader.read(buf)) != -1) {
		fileData.append(buf, 0, numRead);
	    }
	    reader.close();
	} catch (IOException e) {
	    log.error(e.getLocalizedMessage(), e);
	}
	return fileData.toString();
    }

    /**
     * Checks if the file of the co template exists, if not it takes the default
     * template file, and return a buffered reader on the file.
     * 
     * @param webappDir the location of the webapp
     * @return a BufferedReader on the appropriate template file.
     */
    private BufferedReader getXmlTemplateFileReader(
	    COConfigSerialized coConfig, String lang, String webappDir) {
	File coXmlFile = null;
	String coXmlFilePath = null;
	BufferedReader reader = null;
	String templateFileName = "";
	try {
	    templateFileName =
		    CO_CONTENT_TEMPLATE + "_" + lang
			    + OsylSiteService.XML_FILE_EXTENSION;
	    coXmlFilePath =
		    webappDir
			    + OsylConfigService.CONFIG_DIR
			    + File.separator
			    + coConfig.getConfigRef()
			    + File.separator
			    + getDefautVersionConfig(webappDir + CONFIG_DIR
				    + File.separator + coConfig.getConfigRef()
				    + File.separator) + File.separator+ templateFileName;
	    coXmlFile = new File(coXmlFilePath);
	    reader =
		    new BufferedReader(new InputStreamReader(
			    new FileInputStream(coXmlFile), "UTF-8"));

	    log.info("Course outline created with template '"
		    + templateFileName + "' and config '"
		    + coConfig.getConfigRef() + "'");
	} catch (FileNotFoundException e) {
	    try {
		templateFileName =
			CO_CONTENT_TEMPLATE
				+ OsylSiteService.XML_FILE_EXTENSION;
		;
		coXmlFilePath =
			webappDir
				+ OsylConfigService.CONFIG_DIR
				+ File.separator
				+ coConfig.getConfigRef()
				+ File.separator
				+ getDefautVersionConfig(webappDir + CONFIG_DIR
					+ File.separator
					+ coConfig.getConfigRef()
					+ File.separator) + templateFileName;
		coXmlFile = new File(coXmlFilePath);
		reader =
			new BufferedReader(new InputStreamReader(
				new FileInputStream(coXmlFile), "UTF-8"));
		log.info("Course outline created with template '"
			+ templateFileName + "' and config '"
			+ coConfig.getConfigRef() + "'");
	    } catch (Exception e1) {
		try {
		    templateFileName =
			    CO_CONTENT_TEMPLATE
				    + OsylSiteService.XML_FILE_EXTENSION;
		    String defaultConfigRef =
			    getCurrentConfig(webappDir).getConfigRef();
		    coXmlFilePath =
			    webappDir
				    + OsylConfigService.CONFIG_DIR
				    + File.separator
				    + defaultConfigRef
				    + File.separator
				    + getDefautVersionConfig(webappDir
					    + CONFIG_DIR + File.separator
					    + coConfig.getConfigRef()
					    + File.separator)
				    + templateFileName;
		    coXmlFile = new File(coXmlFilePath);
		    reader =
			    new BufferedReader(new InputStreamReader(
				    new FileInputStream(coXmlFile), "UTF-8"));
		    log.info("Course outline created with template '"
			    + templateFileName + "' and config '"
			    + defaultConfigRef + "'");
		} catch (Exception e2) {
		    log.error("Could not created course oultine. "
			    + e2.getLocalizedMessage(), e2);
		}
	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	}
	return reader;
    }

}
