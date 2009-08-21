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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.id.api.IdManager;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
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
	 * Css file for skin properties
	 */
	private static final String CONFIG_SKIN = "osylcore.css";

	/**
	 * Package (ie: folder) containing the message files (.properties)
	 */
	private static final String CONFIG_DIR = "osylcoconfigs";

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

	private IdManager idManager;
	
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

	public void setIdManager(IdManager idManager) {
        this.idManager = idManager;
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
	public COConfigSerialized getCurrentConfig(String webappDir) throws Exception {
		List<COConfigSerialized> configs = coConfigDao.getConfigs();
		COConfigSerialized coConfig = (COConfigSerialized) configs.get(0);
		coConfig = fillConfig(coConfig, webappDir, coConfig.getConfigRef(),
				coConfig.getConfigId());
		return coConfig;
	}

	/** {@inheritDoc} */
	public COConfigSerialized getConfig(String configId, String webappDir) throws Exception {
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
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public Map<String, String> getMessages(String path, String baseFileName, String locale) throws Exception {
		return OsylConfigServiceMessages.getMessages(path, baseFileName, locale);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	public void initConfigs() throws Exception {
		if (coConfigDao.getConfigs().size() <= 0) {
			COConfigSerialized coConfig = new COConfigSerialized(idManager
					.createUuid(), CONFIG_DIR + File.separator + "default",
					"Config from HEC Montreal");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(idManager.createUuid(),
					CONFIG_DIR + File.separator + "udem", "Config udem");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(idManager.createUuid(),
					CONFIG_DIR + File.separator + "udemCompetencesComposantes",
					"Config UdeM - Competences Composantes");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(idManager.createUuid(),
					CONFIG_DIR + File.separator + "udemCompetencesSeances",
					"Config UdeM - Competences Seances");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(idManager.createUuid(),
					CONFIG_DIR + File.separator + "udemObjectifsActivites",
					"Config UdeM - Objectifs Activites");
			createConfig(coConfig);

			coConfig = new COConfigSerialized(idManager.createUuid(),
					CONFIG_DIR + File.separator + "udemObjectifsSeances",
					"Config UdeM - Objectifs Seances");
			createConfig(coConfig);
		}
	}

	/** {@inheritDoc} */
	public COConfigSerialized getConfigByRef(String configRef, String webappDir) throws Exception {
		COConfigSerialized coConfig = coConfigDao.getConfigByRef(configRef);
		coConfig = fillConfig(
		        coConfig, 
		        webappDir, 
		        configRef, 
		        coConfig.getConfigId());
		return coConfig;
	}

	/** {@inheritDoc} */	
	public String getCurrentLocale() throws Exception {
		return OsylConfigServiceMessages.getCurrentLocale();
	}

	/** {@inheritDoc} */
	public COSerialized fillCo(String dir, COSerialized coSerialized) throws Exception {	    
		if (coSerialized != null) {
			coSerialized.setMessages(getMessages(
			        dir,
					OsylConfigService.CONFIG_COMESSAGES, 
					coSerialized.getLang()));
		}
		return coSerialized;
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
            String webappdir, String configRef, String configId)
            throws Exception {
        String path = webappdir + configRef;
        coConfig.setCascadingStyleSheetURI(getCascadingStyleSheetURI(path));
        coConfig.setCoreBundle(
                OsylConfigServiceMessages.getMessages(path, CONFIG_UIMESSAGES));
        coConfig.setRulesConfig(getRules(path));
        coConfig.setRolesList(getRolesList(path));
        coConfig.setEvalTypeList(getEvalTypeList(path));
        return coConfig;
    }

    /**
     * For a given config directory in the webapp, this method reads the rolesList xml
     * file and parse it as a List
     * @param dir
     * @return List
     */    
    private static List<String> getRolesList(String dir) throws Exception{
    	File xmlFile = new File(dir, CONFIG_ROLES_LIST);
    	List<String> list = parseXmlForList(xmlFile);
    	return list;
    }
    
    /**
     * Parsing xml file (list xml schema) and retrieve list
     * @param xmlFile
     * @return
     * @throws Exception
     */
    private static List<String> parseXmlForList(File xmlFile) throws Exception{
    	List<String> list = new ArrayList<String>();
    	DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
    	Document dom = docBuilder.parse(xmlFile);
    	Element nodeElement = dom.getDocumentElement();
		NodeList nodeList = nodeElement.getChildNodes();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeName().equalsIgnoreCase(ITEM_NODE_NAME)) {
					String value = node.getAttributes().getNamedItem(
							VALUE_ATTRIBUTE_NAME).getNodeValue();
					list.add(value);
				}
			}
		}
    	return list;
    }
    
    /**
     * For a given config directory in the webapp, this method reads the evalType xml
     * file and parse it as a List
     * @param dir
     * @return List
     */   
    private static List<String> getEvalTypeList(String dir) throws Exception{
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
    
    /**
     * For a given directory returns the String representation of the URI
     * 
     * @param dir
     * @return String
     */
    private static String getCascadingStyleSheetURI(String dir) {
        String relativePath = dir.substring(dir.indexOf("webapps") + 7, dir
                .length());
        relativePath = relativePath + File.separator + CONFIGS_SKIN_DIRECTORY
                + File.separator + CONFIG_SKIN;
        relativePath = relativePath.replaceAll("\\\\", "/");
        return relativePath;
    }

}
