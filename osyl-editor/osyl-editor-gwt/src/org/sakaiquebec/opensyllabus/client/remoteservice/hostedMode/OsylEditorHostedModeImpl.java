package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode;

import java.util.ArrayList;

import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylUdeMSwitch;

import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylTestCOMessages;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylTestCOMessagesUdeM;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylTestUIMessages;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylTestUIMessagesUdeM;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtServiceAsync;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

// OsylTestCOMessages
/**
 * @author mathieu colombet
 * This is the embedded implementation of OsylEditorGwtServiceAsync used in hosted mode only
 * It simulate the server behavior.
 */
public class OsylEditorHostedModeImpl implements OsylEditorGwtServiceAsync {

	private final static String SITE_ID="6b9188e5-b3ca-49dd-be7c-540ad9bd60c4";
	
	private static String DEFAULT_CONFIG_PATH = "rules/rules.xml";
	private static String  UDEM_CONFIG_PATH = "rules/rulesUdeM.xml";
//	  private static String UDEM_CONFIG_PATH = "rules/rulesUdeMCompetencesActivites.xml";
//	  private static String UDEM_CONFIG_PATH = "rules/rulesUdeMCompetencesComposantes.xml";
//    private static String UDEM_CONFIG_PATH = "rules/rulesUdeMCompetencesSeances.xml";
//    private static String UDEM_CONFIG_PATH = "rules/rulesUdeMObjectifsActivites.xml";
//    private static String UDEM_CONFIG_PATH = "rules/rulesUdeMObjectifsSeances.xml";
//    private static String UDEM_CONFIG_PATH = "rules/rulesUdeMObjectifsThemes.xml";

	private static String DEFAULT_MODEL_PATH = "xml_examples/defaultXml.xml";
	private static String UDEM_MODEL_PATH = "xml_examples/UdeMXml.xml";
//	  private static String UDEM_MODEL_PATH = "xml_examples/UdeMXmlCompetencesActivites.xml";
//	  private static String UDEM_MODEL_PATH = "xml_examples/UdeMXmlCompetencesComposantes.xml";
//    private static String UDEM_MODEL_PATH = "xml_examples/UdeMXmlCompetencesSeances.xml";
//    private static String UDEM_MODEL_PATH = "xml_examples/UdeMXmlObjectifsActivites.xml";
//    private static String UDEM_MODEL_PATH = "xml_examples/UdeMXmlObjectifsSeances.xml";
//    private static String UDEM_MODEL_PATH = "xml_examples/UdeMXmlObjectifsThemes.xml";

	
	public String getConfigPath() {
		// UdeM ?
		if ( OsylUdeMSwitch.isUdeM() ) {
			return UDEM_CONFIG_PATH;
		}
		else {
			return DEFAULT_CONFIG_PATH;			
		}
	}
	
	public String getModelPath() {
		// UdeM ?
		if ( OsylUdeMSwitch.isUdeM() ) {
			return UDEM_MODEL_PATH;
		}
		else {
			return DEFAULT_MODEL_PATH;
		}
	}
	
	public void applyPermissions(String resourceId, String permission,
			AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void createOrUpdateAssignment(String assignmentId, String title,
			String instructions, int openYear, int openMonth, int openDay,
			int openHour, int openMinute, int closeYear, int closeMonth,
			int closeDay, int closeHour, int closeMinute, int percentage,
			AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void createOrUpdateAssignment(String assignmentId, String title,
			AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void createOrUpdateCitation(String citationListId, String citation,
			String author, String type, String isbnIssn, String link,
			AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void createTemporaryCitationList(AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void getCurrentUserRole(AsyncCallback<String> callback) {
		callback.onSuccess(SecurityInterface.SECURITY_ROLE_PROJECT_MAINTAIN);
	}

	public void getResourceLicenceInfo(
			AsyncCallback<ResourcesLicencingInfo> callback) {

		ResourcesLicencingInfo ress = new ResourcesLicencingInfo();
		ress.setCopyrightTypeList(new ArrayList<String>());
		ress.getCopyrightTypeList().add("hosted mode : Material is in public domain.");
		ress.getCopyrightTypeList().add("hosted mode : I hold copyright.");
		ress.getCopyrightTypeList().add("hosted mode : Material is subject to fair une exception.");
		ress.getCopyrightTypeList().add("hosted mode : I have obtained permission to use this material.");
		ress.getCopyrightTypeList().add("hosted mode : Copyright status is not yet determined.");
		ress.getCopyrightTypeList().add("hosted mode : Use copyright below.");
		callback.onSuccess(ress);

	}

	public void getSerializedConfig(final AsyncCallback<COConfigSerialized> callback) {

		final COConfigSerialized configSer = new COConfigSerialized("config-test-id");
		
		if ( OsylUdeMSwitch.isUdeM() ) {
			configSer.setCoreBundle(OsylTestUIMessagesUdeM.getMap());
		}
		else {
			configSer.setCoreBundle(OsylTestUIMessages.getMap());	
		}

		RequestBuilder requestBuilder = null;

			requestBuilder = new RequestBuilder(RequestBuilder.GET, getConfigPath());

		try {
		    requestBuilder.sendRequest(null, new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			    Window.alert("Error while reading " + getConfigPath() + " :" + exception.toString());
			}

			public void onResponseReceived(Request request,Response response) {
				configSer.setRulesConfig(response.getText());
				callback.onSuccess(configSer);
			}
		    });
		} catch (RequestException ex) {
		    Window.alert("Error while reading " + getConfigPath() + " :" + ex.toString());
		}
	}

	public void getSerializedCourseOutline(String id,
			AsyncCallback<COSerialized> callback) {
		getSerializedCourseOutline(callback);

	}

	public void getSerializedCourseOutline(final AsyncCallback<COSerialized> callback) {

		final COSerialized modeledCo = new COSerialized();
		modeledCo.setSiteId(SITE_ID);
		
		if ( OsylUdeMSwitch.isUdeM() ) {
			modeledCo.setMessages(OsylTestCOMessagesUdeM.getMap());
		}
		else {
			modeledCo.setMessages(OsylTestCOMessages.getMap());	
		}
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, getModelPath());

		try {
		    requestBuilder.sendRequest(null, new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			    Window.alert("Error while reading " + getModelPath() + " :" + exception.toString());
			}

			public void onResponseReceived(Request request,
				Response response) {
				modeledCo.setSerializedContent(response.getText());
				callback.onSuccess(modeledCo);
			}
		    });
		} catch (RequestException ex) {
		    Window.alert("Error while reading " + getModelPath() + " :" + ex.toString());
		}

	}

	public void getSerializedPublishedCourseOutlineForAccessType(
			String accessType, AsyncCallback<COSerialized> callback) {
		getSerializedCourseOutline(callback);
	}

	public void getXslForGroup(String group, AsyncCallback<String> callback) {
		// TODO Auto-generated method stub

	}

	public void hasBeenPublished(AsyncCallback<Boolean> callback) {
		callback.onSuccess(true);
	}

	public void initTool(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void ping(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void publishCourseOutline(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void removeAssignment(String assignmentId,
			AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void removeCitation(String citationId, AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void updateSerializedCourseOutline(COSerialized co,
			AsyncCallback<String> callback) {
		callback.onSuccess("hostedId");
	}

}
