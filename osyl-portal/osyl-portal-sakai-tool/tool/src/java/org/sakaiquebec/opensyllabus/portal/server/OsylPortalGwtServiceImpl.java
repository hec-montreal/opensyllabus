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

package org.sakaiquebec.opensyllabus.portal.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.portal.client.rpc.OsylPortalGwtService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the {@link OsylAdminGwtService}.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylPortalGwtServiceImpl extends RemoteServiceServlet implements
	OsylPortalGwtService {

    private static final long serialVersionUID = 1L;
    private OsylPortalBackingBean osylPortalServices;
    private ServletContext servletContext = null;
    private WebApplicationContext webAppContext = null;

    private static Log log = LogFactory.getLog(OsylPortalGwtServiceImpl.class);

    /** {@inheritDoc} */
    public void init() {
	log.warn("INIT OsylPortalGwtServiceImpl");

	servletContext = getServletContext();
	webAppContext =
		WebApplicationContextUtils
			.getWebApplicationContext(servletContext);

	if (webAppContext != null) {
	    osylPortalServices =
		    (OsylPortalBackingBean) webAppContext
			    .getBean("osylPortalMainBean");
	}
    }

    @Override
    public Map<String, String> getCoursesForAcadCareer(String acadCareer) {
	return osylPortalServices.getOsylPortalService()
		.getCoursesForAcadCareer(acadCareer);

    }

    @Override
    public Map<String, String> getCoursesForResponsible(String responsible) {
	return osylPortalServices.getOsylPortalService()
		.getCoursesForResponsible(responsible);
    }

    @Override
    public List<String> getAllResponsibles() {
	return osylPortalServices.getOsylPortalService().getAllResponsibles();
    }

}