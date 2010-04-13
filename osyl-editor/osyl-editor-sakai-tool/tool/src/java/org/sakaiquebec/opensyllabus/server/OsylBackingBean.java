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

package org.sakaiquebec.opensyllabus.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.api.OsylService;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;

/**
 * A bean that acts as a concentrator for OSYL services, to be used in JSP pages
 * and in the servlet.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public class OsylBackingBean {

    /** Our logger */
    private static Log log = LogFactory.getLog(OsylBackingBean.class);

    /** Init method to be called by Spring */
    public void init() {
	log.debug("INIT Osyl Backing Bean");
    }

    /** The default constructor */
    public OsylBackingBean() {
	log.debug("Constructor Osyl Backing Bean");
    }

    /**
     * The security service to be injected by spring
     * @uml.property  name="osylSecurityService"
     * @uml.associationEnd  
     */
    private OsylSecurityService osylSecurityService;

    /**
     * Sets the  {@link OsylSiteService} .
     * @param  osylSiteService
     * @uml.property  name="osylSiteService"
     */
    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    /**
     * @return  the osylSecurityService.
     * @uml.property  name="osylSecurityService"
     */
    public OsylSecurityService getOsylSecurityService() {
	return osylSecurityService;
    }

    /**
     * The Site service to be injected by spring
     * @uml.property  name="osylSiteService"
     * @uml.associationEnd  
     */
    private OsylSiteService osylSiteService;

    /**
     * @return  the osylSiteService.
     * @uml.property  name="osylSiteService"
     */
    public OsylSiteService getOsylSiteService() {
	return osylSiteService;
    }

    /**
     * Sets the  {@link OsylSecurityService} .
     * @param  osylSecurityService
     * @uml.property  name="osylSecurityService"
     */
    public void setOsylSecurityService(OsylSecurityService osylSecurityService) {
	this.osylSecurityService = osylSecurityService;
    }

    /**
     * The config service to be injected by spring
     * @uml.property  name="osylConfigService"
     * @uml.associationEnd  
     */
    private OsylConfigService osylConfigService;

    /**
     * Sets the  {@link OsylConfigService} .
     * @param  osylConfigService
     * @uml.property  name="osylConfigService"
     */
    public void setOsylConfigService(OsylConfigService osylConfigService) {
	this.osylConfigService = osylConfigService;
    }

    /**
     * @return  the osylConfigService.
     * @uml.property  name="osylConfigService"
     */
    public OsylConfigService getOsylConfigService() {
	return osylConfigService;
    }

    /**
     * @uml.property  name="osylService"
     * @uml.associationEnd  
     */
    private OsylService osylService;

	/**
	 * @return
	 * @uml.property  name="osylService"
	 */
	public OsylService getOsylService() {
		return osylService;
	}

	/**
	 * @param osylService
	 * @uml.property  name="osylService"
	 */
	public void setOsylService(OsylService osylService) {
		this.osylService = osylService;
	}
	
	/**
	 * @uml.property  name="osylPublishService"
	 * @uml.associationEnd  
	 */
	private OsylPublishService osylPublishService;

	/**
	 * @return
	 * @uml.property  name="osylPublishService"
	 */
	public OsylPublishService getOsylPublishService() {
		return osylPublishService;
	}

	/**
	 * @param osylPublishService
	 * @uml.property  name="osylPublishService"
	 */
	public void setOsylPublishService(OsylPublishService osylPublishService) {
		this.osylPublishService = osylPublishService;
	}
	
}
