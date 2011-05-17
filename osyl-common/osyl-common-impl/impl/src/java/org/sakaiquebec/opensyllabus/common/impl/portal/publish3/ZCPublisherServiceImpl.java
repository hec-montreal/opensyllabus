//FILE HEC ONLY SAKAI-2723

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.common.impl.portal.publish3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiquebec.opensyllabus.common.api.portal.publish3.ZCPublisherService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class ZCPublisherServiceImpl implements ZCPublisherService {

    private static Log log = LogFactory.getLog(ZCPublisherServiceImpl.class);

    private String parameter = null;

    String urlConn = null;

    public void init() {
	log.info("INIT from ZCPublisherImpl");
    }

    public void publier(String koId, String langue, String nivSec) {

	if (koId != null && koId.length() > 0 && langue != null
		&& langue.length() > 0)
	    parameter = "?file=" + koId + "&lang=" + langue + "&c=2&nivSecu=" + nivSec;

	if (parameter != null)
	    urlConn =
		    ServerConfigurationService.getServerUrl() + URL_CONN_PUBLIER
			    + parameter;

	if (urlConn != null) {

	    try {
		URL url = new URL(urlConn);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter request =
			new OutputStreamWriter(conn.getOutputStream());

		request.write(parameter);
		request.flush();

		BufferedReader br =
			new BufferedReader(new InputStreamReader(conn
				.getInputStream()));

		String line;

		while ((line = br.readLine()) != null) {
		    log.trace(line);
		}

		request.close();
		br.close();

	    } catch (MalformedURLException e) {
		log.error(e.getMessage());
	    } catch (IOException e) {
		log.error(e.getMessage());
	    }

	}
    }


    public void depublier(String koId, String langue){
	if (koId != null && koId.length() > 0 && langue != null
		&& langue.length() > 0)
	    parameter = "?file=" + koId + "&lang=" + langue;

	if (parameter != null)
	    urlConn =
		    ServerConfigurationService.getServerUrl() + URL_CONN_DEPUBLIER
			    + parameter;

	if (urlConn != null) {

	    try {
		URL url = new URL(urlConn);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter request =
			new OutputStreamWriter(conn.getOutputStream());

		request.write(parameter);
		request.flush();

		BufferedReader br =
			new BufferedReader(new InputStreamReader(conn
				.getInputStream()));

		String line;

		while ((line = br.readLine()) != null) {
		    log.trace(line);
		}

		request.close();
		br.close();

	    } catch (MalformedURLException e) {
		log.error(e.getMessage());
	    } catch (IOException e) {
		log.error(e.getMessage());
	    }

	}
    }

}
