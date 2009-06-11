/**********************************************************************************
 * $Id: OsylTestRulesConfig.java 1866 2009-02-02 14:52:22Z laurent.danet@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.hostedMode;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

/**
 * This is the off-line version (i.e.: for development purposes) of default
 * configuration rules. In Sakai environment this file is there:
 * tool/src/webapp/osylcoconfigs/default/rules.xml.
 * 
 * @author <a href="mailto:laurent.danet@crim.ca">Laurent Danet</a>
 * @version $Id: OsylTestRulesConfig.java 1750 2008-12-01 22:03:01Z
 *          mathieu.cantin@hec.ca $
 */
public class OsylTestRulesConfigUdeM {

//    private static String FILEPATH="rules/rules.xml";
//    private static String FILEPATH="rules/rulesUdeMObjectifsSeances.xml";
//    private static String FILEPATH="rules/rulesUdeMObjectifsSeances3niveaux.xml";
    private static String FILEPATH="rules/rulesUdeMCompetencesSeances.xml";
//    private static String FILEPATH="rules/rulesUdeMObjectifsThemes.xml";
//    private static String FILEPATH="rules/rulesUdeMCompetencesComposantes.xml";
    
   
    static public void getXml() {
	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.GET, FILEPATH);

	try {
	    requestBuilder.sendRequest(null, new RequestCallback() {
		public void onError(Request request, Throwable exception) {
		    Window.alert("Error while reading "+FILEPATH+" :"+exception.toString());
		}

		public void onResponseReceived(Request request,
			Response response) {
		    OsylEditorEntryPoint.getInstance().initConfigFromRulesXml(
			    response.getText());
		}
	    });
	} catch (RequestException ex) {
	    Window.alert("Error while reading "+FILEPATH+" :"+ex.toString());
	}
    }
}