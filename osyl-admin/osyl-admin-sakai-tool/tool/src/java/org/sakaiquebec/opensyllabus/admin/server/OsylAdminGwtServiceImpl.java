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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

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

    /** {@inheritDoc} */
	public HashMap<String, List<String>> getTemplateFunctionsWithAllowedRoles() {
		HashMap<String, List<String>> result = null;
		if (osylAdminServices != null) {
			result = osylAdminServices.getOsylRealmService().
				getFunctionsWithAllowedRoles();
		}
		return result;
	}

	/** {@inheritDoc} */
	public Set<String> getTemplateRoles() {
		Set<String> result = null;
		if (osylAdminServices != null) {
			result = osylAdminServices.getOsylRealmService().getRoles();
		}
		return result;
	}

	/** {@inheritDoc} */
	public void updateTemplateFunction(String function, Set<String> allowedRoles) {
		osylAdminServices.getOsylRealmService().updateFunction(function, allowedRoles);
	}
	
	/** {@inheritDoc} */
	public void updateTemplateFunctions(HashMap<String, Set<String>> functions) {
		for (Iterator<Entry<String, Set<String>>> iFunctions =
				functions.entrySet().iterator(); iFunctions.hasNext();) {
			Entry<String, Set<String>> entry = iFunctions.next();
			osylAdminServices.getOsylRealmService().
				updateFunction(entry.getKey(), entry.getValue());
		}
	}

 }
