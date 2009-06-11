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

package org.sakaiquebec.opensyllabus.client.hostedMode;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

/**
 * This class contains XML course outline content that can be used for tests
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 */
public class COTestContentUdeM {

    /**
     * Path to the xml files. The root is the folder public of GWT project.
     */
    private static String PATH = "xml_examples/";

    public static String DEFAULT_XML = "defaultXml.xml";
    
//    public static String UDEM_XML = "UdeMXmlObjectifsSeances.xml";
//    public static String UDEM_XML = "UdeMXmlObjectifsSeances3niveaux.xml";
    public static String UDEM_XML = "UdeMXmlCompetencesSeances.xml";
//    public static String UDEM_XML = "UdeMXmlObjectifsThemes.xml";
//    public static String UDEM_XML = "UdeMXmlCompetencesComposantes.xml";


    public static String XML_TEMPLATE = "templateXml.xml";

    public static void getTestXml() {
	// getXml(DEFAULT_XML);
	getXml(UDEM_XML);
    }

    public static void getXml(final String filename) {
	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.GET, COTestContentUdeM.PATH + filename);

	try {
	    requestBuilder.sendRequest(null, new RequestCallback() {
		public void onError(Request request, Throwable exception) {
		    Window.alert("Error while reading "+COTestContentUdeM.PATH + filename+" :"+exception.toString());
		}

		public void onResponseReceived(Request request,
			Response response) {
		    OsylEditorEntryPoint.getInstance().initModelFromXml(
			    response.getText());
		}
	    });
	} catch (RequestException ex) {
	    Window.alert("Error while reading "+COTestContentUdeM.PATH + filename+" :"+ex.toString());
	}
    }

}
