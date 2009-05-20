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

package org.sakaiquebec.opensyllabus.admin.server;

import org.sakaiquebec.opensyllabus.admin.api.*;
import org.sakaiquebec.opensyllabus.common.api.OsylRealmService;

/**
 * A bean that acts as a concentrator for OSYL admin services, to be used in JSP
 * pages and in the servlets.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylAdminBackingBean {
    
    private OsylAdminService osylAdminService;
    
    /**
     * Gets the {@link OsylAdminService}.
     * 
     * @return
     *         the {@link OsylAdminService}
     */
    public OsylAdminService getOsylAdminService() {
        return osylAdminService;
    }
    
    /**
     * Sets the {@link OsylAdminService}.
     * 
     * @param osylAdminService
     */
    public void setOsylAdminService(OsylAdminService osylAdminService) {
        this.osylAdminService = osylAdminService;
    }
    
    private OsylRealmService osylRealmService;
    
    /**
     * Gets the {@link OsylRealmService}.
     * 
     * @return
     *         the {@link OsylRealmService}
     */
    public OsylRealmService getOsylRealmService() {
        return osylRealmService;
    }
    
    /**
     * Sets the {@link OsylRealmService}.
     * 
     * @param osylRealmService
     */
    public void setOsylRealmService(OsylRealmService osylRealmService) {
        this.osylRealmService = osylRealmService;
    }
    
    /**
     * Init method called at initialization of the bean.
     */
    public void init(){
	
    }
}

