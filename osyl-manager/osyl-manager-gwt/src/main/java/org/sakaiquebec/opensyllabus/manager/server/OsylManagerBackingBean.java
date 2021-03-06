/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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

package org.sakaiquebec.opensyllabus.manager.server;

import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylManagerBackingBean {

    private OsylManagerService osylManagerService;

    public OsylManagerService getOsylManagerService() {
	return osylManagerService;
    }

    public void setOsylManagerService(OsylManagerService osylManagerService) {
	this.osylManagerService = osylManagerService;
    }

    private OsylSiteService osylSiteService;

    public OsylSiteService getOsylSiteService() {
	return osylSiteService;
    }

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    private OsylConfigService osylConfigService;

    public OsylConfigService getOsylConfigService() {
	return osylConfigService;
    }

    public void setOsylConfigService(OsylConfigService osConfigService) {
	this.osylConfigService = osConfigService;
    }
    
    private OsylPublishService osylPublishService;
    
    public void setOsylPublishService(OsylPublishService osylPublishService) {
	this.osylPublishService = osylPublishService;
    }
    
    public OsylPublishService getOsylPublishService(){
	return this.osylPublishService;
    }
    
    private OsylSecurityService osylSecurityService;
    
    public void setOsylSecurityService(OsylSecurityService osylSecurityService) {
	this.osylSecurityService = osylSecurityService;
    }

    public OsylSecurityService getOsylSecurityService() {
	return osylSecurityService;
    }
    

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {

    }

}
