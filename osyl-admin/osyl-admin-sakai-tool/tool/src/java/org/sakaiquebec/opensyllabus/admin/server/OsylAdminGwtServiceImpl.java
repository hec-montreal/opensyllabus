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


import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.admin.client.rpc.OsylAdminGwtService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the {@link OsylAdminGwtService}.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylAdminGwtServiceImpl extends RemoteServiceServlet implements
	OsylAdminGwtService {

    private static final long serialVersionUID = 1L;
    private OsylAdminBackingBean osylAdminServices;
    private ServletContext servletContext = null;
    private WebApplicationContext webAppContext = null;

    private static Log log = LogFactory.getLog(OsylAdminGwtServiceImpl.class);

    /** {@inheritDoc} */
    public void init() {
	log.warn("INIT OsylEditorGwtServiceImpl");

	servletContext = getServletContext();
	webAppContext =
		WebApplicationContextUtils
			.getWebApplicationContext(servletContext);

	if (webAppContext != null) {
	    osylAdminServices =
		    (OsylAdminBackingBean) webAppContext
			    .getBean("osylAdminMainBean");
	}

    }

    /**
     * Constructor.
     */
    public OsylAdminGwtServiceImpl() {}

    /** {@inheritDoc} */
    public void createUsers(String fileDirectory) {
	osylAdminServices.getOsylAdminService().createUsers(servletContext.getRealPath("/"));
    }


 }
