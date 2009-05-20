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

package org.sakaiquebec.opensyllabus.client.controller;

import java.util.Date;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.event.PublishPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.SavePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.OsylMainView;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylPublishView;
import org.sakaiquebec.opensyllabus.shared.model.COConfig;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Controller centralizes functionalities and informations needed by the
 * different views of the OpenSyllabus editor.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylController implements SavePushButtonEventHandler,
	PublishPushButtonEventHandler {

    public static final boolean TRACE = false;
    /**
     * Publish folder name for documents in sakai
     */
    public static final String PUBLISH_FOLDER_NAME = "publish";

    /**
     * Work folder name for documents in sakai
     */
    public static final String WORK_FOLDER_NAME = "work";
    private static final int DEFAULT_ASSIGNMENT = -1;

    // Singleton instance
    private static OsylController _instance;

    private OsylMainView mainView;

    private OsylViewContext osylViewContext;

    private COConfig osylConfig;

    private boolean inPreview = false;

    private String citationListId;
    /**
     * Course outline and user interface message bundle
     */
    private OsylConfigMessages coMessages, uiMessages;

    private OsylPublishView osylPublishView = null;

    // Sakai ID of current site
    private String siteId;

    private COSerialized serializedCourseOutline;

    // Instance of pinger to keep the session alive.
    private Pinger pinger;

    // Our Model Controller
    private OsylModelController osylModelController;

    private boolean readOnly;

    /**
     * Returns a valid instance of Controller.
     * 
     * @return Controller instance
     */
    public static OsylController getInstance() {
	if (_instance == null) {
	    _instance = new OsylController();
	}
	return _instance;
    }

    /**
     * Private constructor to force the use of singleton.
     * 
     * @see getInstance
     */
    private OsylController() {
	osylViewContext = new OsylViewContext();
	setModelController(new OsylModelController());
	setReadOnly(getReadOnlyParameter());
	osylModelController.setReadOnly(isReadOnly());
	if (!isInHostedMode() && !isReadOnly()) {
	    pinger = new Pinger();
	    pinger.start();
	}
    }

    /**
     * Sets the Model Controller.
     * 
     * @param omc
     */
    private void setModelController(OsylModelController omc) {
	this.osylModelController = omc;
    }

    /**
     * Returns the {@link OsylModelController}.
     * 
     * @return
     */
    public OsylModelController getModelController() {
	return osylModelController;
    }

    /**
     * Shortcut for getModelController().isModelDirty().
     * 
     * @return
     */
    public boolean isModelDirty() {
	return osylModelController.isModelDirty();
    }

    /**
     * This method is JSNI method to retrieve the value of osyl:ro meta tag in
     * the html head. &lt;meta name="osyl:ro" content="true" &gt; or &lt;meta
     * name="osyl:ro" content="false" &gt;
     * 
     * @return true if UI is in read-only mode, false if not.
     */
    public native boolean getReadOnlyParameter() /*-{
            metas = $doc.getElementsByTagName('meta');
            ret = 'true';

            for (i = 0; i < metas.length; i++) {

                if(metas[i].name == 'osyl:ro') {
                    ret = metas[i].content;
                    break;
                }
            }
            if(ret == 'false'){
                return false;
            } else {
                return true;
            }
        }-*/;

    /**
     * This method is JSNI method to retrieve the value of osyl:sg meta tag in
     * the html head. &lt;meta name="osyl:sg" content="securityAttendee" &gt; or
     * &lt;meta name="osyl:ro" content="securityPublic" &gt;
     * 
     * @return null or security group if found
     */
    public native String getPublishedSecurityAccessType() /*-{
            metas = $doc.getElementsByTagName('meta');
            ret = null;

            for (i = 0; i < metas.length; i++) {

                if(metas[i].name == 'osyl:sg') {
                    ret = metas[i].content;
                    break;
                }
            }
            return ret;
        }-*/;

    /**
     * Returns the user agent stocked in the meta tag of index.jsp.
     * 
     * @return the user agent string.
     */
    public native String getUserAgent() /*-{
            metas = $doc.getElementsByTagName('meta');
            ret = '';

            for (i = 0; i < metas.length; i++) {

                if(metas[i].name == 'agent') {
                    ret = metas[i].content;
                    break;
                }
            }
            return ret;
        }-*/;

    /**
     * Tells if the specified model object is the same as current context
     * object.
     * 
     * @param COModelInterface modelObject
     * @return true if the specified object is the same as current context
     */
    public boolean viewContextEquals(COModelInterface modelObject) {
	return osylViewContext.getContextModel() == modelObject;
    }

    /**
     * Sets the type of object currently displayed in the editor, and the object
     * itself.
     * 
     * @param String contextType
     * @param String viewContextModel
     */
    public void setViewContext(COModelInterface viewContextModel) {
	osylViewContext.setContextModel(viewContextModel);
    }

    /**
     * Returns the object currently displayed in the editor.
     * 
     * @return Model object
     */
    public COModelInterface getViewContextModel() {
	return osylViewContext.getContextModel();
    }

    /**
     * Returns the view context object currently displayed in the editor.
     * 
     * @return ViewContext
     */
    public OsylViewContext getViewContext() {
	return osylViewContext;
    }

    /**
     * Sets the current site ID. This is called by the EntryPoint at
     * initialization.
     * 
     * @param site ID
     */
    public void setSiteId(String s) {
	this.siteId = s;
    }

    /**
     * Returns the current site ID. This allows the Controller to provide Views
     * or other classes with this information, when needed.
     * 
     * @param site ID
     */
    public String getSiteId() {
	return siteId;
    }

    /**
     * Returns the current execution mode: test or prod.
     * 
     * @return String
     */
    public static String getExecMode() {
	return OsylEditorEntryPoint.getExecMode();
    }

    /**
     * Returns current main view.
     * 
     * @return {@link EditorMainView}
     */
    public OsylMainView getMainView() {
	return mainView;
    }

    /**
     * Sets current main view.
     * 
     * @param {@link EditorMainView}
     */
    public void setMainView(OsylMainView mainView) {
	this.mainView = mainView;
    }

    // ==================== Begin RPCController block ====================
    //
    // IMPORTANT: because of the asynchronous nature of GWT, most method
    // calls to the server usually needs a call-back method to get the results.
    // For instance getSerializedCourseOutline(id) makes the request to the
    // server and
    // getSerializedCourseOutlineCB(COSerialized) is called by the asynchronous
    // call-back once the response has been received.
    // ===================================================================

    /**
     * Called when we cannot establish communication with the server. Typically
     * this only occurs during development. We initialize with test content and
     * configuration.
     * 
     * @param String error msg
     */
    void unableToInitServer(String err) {
	handleRPCError(uiMessages.getMessage("unableToInitServer", err));
	OsylEditorEntryPoint.getInstance().initOffline();
    }

    /**
     * handleRPCError is called by RPCController whenever an error occurs during
     * a server call. More specific behavior can be implemented by using
     * specific call-back methods.
     * 
     * @param msg
     */
    public void handleRPCError(String msg) {
	// Window.alert(msg);
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - RPC Error", msg);
	alertBox.show();
    }

    /**
     * Request the loading of a specific CourseOutlineXML to the server.
     * 
     * @param String ID of the requested CourseOutlineXML
     */
    public void getSerializedCourseOutline(String id) {
	OsylRPCController.getInstance().getSerializedCourseOutline(id,
		getInstance());
    }

    /**
     * Request the loading of default CourseOutlineXML to the server.
     */
    public void getSerializedCourseOutline() {
	OsylRPCController.getInstance().getSerializedCourseOutline(
		getInstance());
    }

    /**
     * Call-back method for parsing the CourseOutlineXML downloaded from the
     * server.
     * 
     * @param COSerialized returned by the server
     */
    public void getSerializedCourseOutlineCB(COSerialized co) {
	// keep track of co messages
	setCoMessages(new OsylConfigMessages(co.getMessages()));

	OsylEditorEntryPoint.getInstance().initModel(co);

	// We keep track of the current site ID.
	setSiteId(co.getSiteId());
    }

    /**
     * Call-back method when we cannot download the CourseOutlineXML from the
     * server.
     * 
     * @param String errorMessage
     */
    public void unableToGetSerializedCourseOutline(String errorMessage) {
	Window.alert(uiMessages.getMessage("unableToLoadCO", errorMessage));
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - RPC Error",
			uiMessages.getMessage("unableToLoadCO", errorMessage));
	alertBox.show();
    }

    /**
     * Call-back method when we cannot create or update an assigment on the
     * server.
     * 
     * @param String errorMessage
     */
    public void unableToCreateOrUpdateAssignment(String errorMessage) {
	// Window.alert(errorMessage);
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true,
			"Alert - Assignment Tool Error", errorMessage);
	alertBox.show();
    }

    /**
     * Call-back method when we cannot process the CourseOutlineXML that we have
     * received from the server. That mean we did receive something, but we
     * cannot parse it. We initialize the model with a default content to allow
     * development.
     * 
     * @param String errorMessage
     */
    public void unableToInitSerializedCourseOutline(String errorMessage) {
	Window.alert(uiMessages.getMessage("unableToInitCO", errorMessage));

	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - RPC Error",
			uiMessages.getMessage("unableToInitCO", errorMessage));
	alertBox.show();
	
	OsylEditorEntryPoint.getInstance().initModelFromTestContent();
    }

    /**
     * Call back method when there is no published course outline for a group.
     * In his case, we display a message to the user.
     */
    public void noPublishedCourseOutline() {
	RootPanel rootPanel = OsylEditorEntryPoint.getInstance().getRootPanel();
	if (rootPanel == null)
	    rootPanel = RootPanel.get();
	HTML html = new HTML();
	html.setHTML(uiMessages.getMessage("noPublishedVersion"));
	rootPanel.add(html);
    }

    /**
     * Call-back method when we cannot delete an assignment.
     * 
     * @param String errorMessage
     */
    public void unableToRemoveAssignment(String errorMessage) {
	// Window.alert(getUiMessages().getMessage("unableToRemoveAssignment",
	// errorMessage));
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true,
			"Alert - Assignment Tool Error", uiMessages.getMessage(
				"unableToRemoveAssignment", errorMessage));
	alertBox.show();
    }

    /**
     * Call-back method when we cannot delete a citation.
     * 
     * @param String errorMessage
     */
    public void unableToRemoveCitation(String errorMessage) {
	// Window.alert(getUiMessages().getMessage("unableToRemoveCitation",
	// errorMessage));
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - Citation Tool Error",
			uiMessages.getMessage("unableToRemoveCitation",
				errorMessage));
	alertBox.show();
    }

    /**
     * Call-back method when we cannot create a citation.
     * 
     * @param String errorMessage
     */
    public void unableToCreateOrUpdateCitation(String errorMessage) {
	// Window.alert(osylConfig.getI18nMessages().getMessage("unableToCreateOrUpdateCitation"));
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Citation Tool Error",
			uiMessages.getMessage("unableToCreateOrUpdateCitation"));
	alertBox.show();
    }

    /**
     * Call-back method when we cannot create a temporary citation.
     * 
     * @param String errorMessage
     */
    public void unableToCreateTemporaryCitationList(String errorMessage) {
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Citation Tool Error",
			uiMessages.getMessage("unableToCreateOrUpdateCitation"));
	alertBox.show();
    }

    /**
     * Requests to the server that it saves the CourseOutlineXML. IMPORTANT: the
     * XML must have been generated from the model before this call!
     * 
     * @param COSerialized the pojo of the course outline
     */
    public void updateSerializedCourseOutline(COSerialized pojo) {
	OsylRPCController.getInstance().updateSerializedCourseOutline(pojo,
		getInstance());
    }

    /**
     * Call-back method for saving the CourseOutlineXML.
     * 
     * @param String the id of the saved XML content
     */
    public void updateSerializedCourseOutlineCB(String id) {

	// A POJO has been sent
	if (getExecMode().equals("test")) {
	    Window
		    .alert("This is the call back from saveCourseOutlineXML. You have automatically "
			    + "updated the current course outline, unique ID "
			    + id);
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(
			    false, true,
			    "Alert - Save Confirmation",
			    "This is the call back from saveCourseOutlineXML. You have automatically "
				    + "updated the current course outline, unique ID "
				    + id);
		alertBox.show();
	} else {
	    OsylUnobtrusiveAlert alert =
		    new OsylUnobtrusiveAlert(uiMessages.getMessage("savedOK"));
	    OsylEditorEntryPoint.showWidgetOnTop(alert);
	}
    }

    /**
     * Requests to the server that it publishes the CourseOutline whose ID is
     * specified.
     * 
     * @param String ID of the requested CourseOutlineXML
     */
    public void publishCourseOutline(AsyncCallback<Void> callback) {
	OsylRPCController.getInstance().publishCourseOutline(getInstance(),
		callback);
    }

    /**
     * Response of the server to tell that the course outline has been
     * published.
     * 
     * @param callback
     */
    public void hasBeenPublished(AsyncCallback<Boolean> callback) {
	OsylRPCController.getInstance().hasBeenPublished(getInstance(),
		callback);
    }

    /**
     * Requests the server for the creation or update of an assignment.
     */
    public void createOrUpdateAssignment(COContentResourceProxy resProx,
	    String assignmentId, String title, String instructions,
	    int openYear, int openMonth, int openDay, int openHour,
	    int openMinute, int closeYear, int closeMonth, int closeDay,
	    int closeHour, int closeMinute, int rating) {
	if (isInHostedMode()) {
	    createOrUpdateAssignmentCB(resProx, "dummyAssignmentId");
	} else { // IMPORTANT : when the rating is -1 then it is a default
	    if (rating == DEFAULT_ASSIGNMENT) {
		Date today, nextWeek;
		// Date yyyy-mm-dd
		today = new Date();
		DateTimeFormat simple = DateTimeFormat.getFormat("yyyy-MM-dd");
		String todayString = simple.format(today);
		String[] dividedDate = new String[3];
		dividedDate = todayString.split("\\s*-\\s*");
		openYear = Integer.parseInt(dividedDate[0]);
		openMonth = Integer.parseInt(dividedDate[1]);
		openDay = Integer.parseInt(dividedDate[2]);
		nextWeek = new Date(today.getTime() + 1000 * 60 * 60 * 24 * 7);
		String nextWeekString = simple.format(nextWeek);
		dividedDate = nextWeekString.split("\\s*-\\s*");
		closeYear = Integer.parseInt(dividedDate[0]);
		closeMonth = Integer.parseInt(dividedDate[1]);
		closeDay = Integer.parseInt(dividedDate[2]);
		rating = 0;
	    }
	    OsylRPCController.getInstance().createOrUpdateAssignment(resProx,
		    assignmentId, title, instructions, openYear, openMonth,
		    openDay, openHour, openMinute, closeYear, closeMonth,
		    closeDay, closeHour, closeMinute, rating, getInstance());
	}
    }

    /**
     * Requests the server for the creation or update of an assignment.
     */
    public void createOrUpdateAssignment(COContentResourceProxy resProx,
	    String assignmentId, String title) {
	if (isInHostedMode()) {
	    createOrUpdateAssignmentCB(resProx, "dummyAssignmentId");
	} else {
	    OsylRPCController.getInstance().createOrUpdateAssignment(resProx,
		    assignmentId, title, getInstance());
	}
    }

    /**
     * CallBack for server request for the creation or update of an assignment.
     */
    public void createOrUpdateAssignmentCB(COContentResourceProxy resProx,
	    String assignmentId) {
	if (assignmentId.equals("failed")) {
	    unableToCreateOrUpdateAssignment("failed");
	}

	try {
	    if (!COContentResourceProxyType.ASSIGNMENT
		    .equals(resProx.getType())) {
		throw new IllegalArgumentException(
			"createOrUpdateAssignmentCB:"
				+ " Wrong type of resource!");
	    }
	    String url = GWT.getModuleBaseURL();
	    String serverId = url.split("\\s*/portal/tool/\\s*")[0];
	    assignmentId = serverId + assignmentId;
	    if (TRACE) {
		Window.alert("ModuleBaseURL = " + url);
		Window.alert("serverId = " + serverId);
		Window.alert("AssignmentID = " + assignmentId);
		Window.alert("NewAssignmentID = " + assignmentId);
	    }
	    resProx.addProperty(COPropertiesType.URI, assignmentId);
	} catch (Exception e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, 
			    "Alert - Assignment Tool Error",
			    "ERROR - Controller createOrUpdateAssignmentCB "
				    + "ResourceProxyType ="
				    + (resProx == null ? "null" : resProx
					    .getType()) + ", err=" + e);
		alertBox.show();
	}
    }

    /**
     * Call-back method for publishing the CourseOutlineXML.
     * 
     * @param boolean either the process was successful or not
     */
    public void publishCourseOutlineCB(boolean b) {
	if (b) {
	    displayMessage(uiMessages.getMessage("publishOK"));
	} else {
	    displayError(uiMessages.getMessage("publishError"));
	}

	if (getExecMode().equals("test")) {
	    // Window.alert("Have I published your course outline?: " + b);
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Confirmation",
			    "Have I published your course outline?: " + b);
		alertBox.show();
	}
    }

    /**
     * Requests the URL where we can access the CourseOutline whose ID is
     * specified. It must have been published previously.
     * 
     * @param String id
     * @return String URL
     */
    public void getSerializedPublishedCourseOutlineForAccessType(
	    String accessType) {
	OsylRPCController.getInstance()
		.getSerializedPublishedCourseOutlineForAccessType(accessType,
			getInstance());
    }

    /**
     * Call-back method for getting the published CourseOutline URL.
     * 
     * @param String URL
     */
    public void getSerializedPublishedCourseOutlineForAccessTypeCB(
	    COSerialized coSerialized) {
	// TODO: implement logic

	if (getExecMode().equals("test")) {
	    // Window.alert("Here is the published content url you asked for");
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Confirmation",
			    "Here is the published content url you asked for");
		alertBox.show();
	}
    }

    /**
     * Requests the user role for current user.
     */
    public void getCurrentUserRole() {
	OsylRPCController.getInstance().getCurrentUserRole(getInstance());
    }

    /**
     * Call-back method for getting the current user role.
     * 
     * @param String current user role
     */
    public void getCurrentUserRoleCB(String role) {
	if (getExecMode().equals("test")) {
	    // Window.alert("I just received your role: " + role);
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Confirmation",
			    "I just received your role: " + role);
		alertBox.show();
	}
    }

    /**
     * Applies the specified permission for the specified resource.
     * 
     * @param String resourceId
     * @param String permission
     */
    public void applyPermissions(String resourceId, String permission) {
	OsylRPCController.getInstance().applyPermissions(resourceId,
		permission, getInstance());
    }

    /**
     * Call-back method for applying permissions.
     * 
     * @param boolean either successful or not
     */
    public void applyPermissionsCB(boolean ok) {
	if (getExecMode().equals("test")) {
	    // Window.alert("Have I just applied permission ? " + ok);
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Confirmation",
			    "Have I just applied permission ? " + ok);
		alertBox.show();
	}
    }

    /**
     * Delete a citation from the course outline citation list
     */
    public void removeCitation(String citationId) {
	OsylRPCController.getInstance().removeCitation(citationId,
		getInstance());
    }

    /**
     * Call-back method for assignment suppression.
     */
    public void removeCitationCB() {
	;
    }

    /**
     * Add or updates a citation in the course outline citation list
     */
    public void createOrUpdateCitation(COContentResourceProxy resProx,
	    String citationListId, String citation, String author, String type,
	    String isbnIssn, String link) {

	if (isInHostedMode())
	    createOrUpdateCitationCB(resProx, "dummyCitationListId");
	else
	    OsylRPCController.getInstance().createOrUpdateCitation(resProx,
		    citationListId, citation, author, type, isbnIssn, link,
		    getInstance());
    }

    /**
     * Call back for citation update or creation
     */
    public void createOrUpdateCitationCB(COContentResourceProxy resProx,
	    String citationListId) {
	if (citationListId.equals("failed")) {
	    unableToCreateTemporaryCitationList("failed");
	}

	try {
	    if (!COContentResourceProxyType.CITATION.equals(resProx.getType())) {
		throw new IllegalArgumentException("createOrUpdateCitationCB:"
			+ " Wrong type of resource!");
	    }

	    resProx.getResource().addProperty(COPropertiesType.CITATIONLISTID,
		    citationListId);

	} catch (Exception e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true,
			    "Alert - Citation Tool Error",
			    "ERROR - Controller createOrUpdateCitationCB "
				    + "ResourceProxyType ="
				    + (resProx == null ? "null" : resProx
					    .getType()) + ", err=" + e);
		alertBox.show();
	}
    }

    /**
     * Creates a temporary citation in the course outline
     */
    public void createTemporaryCitationList(COContentResourceProxy resProx) {
	OsylRPCController.getInstance().createTemporaryCitationList(resProx,
		getInstance());
    }

    /**
     * CallBack for server request for the creation or update of an assignment.
     */
    public void createTemporaryCitationListCB(COContentResourceProxy resProx,
	    String citationListId) {
	if (citationListId.equals("failed")) {
	    unableToCreateTemporaryCitationList("failed");
	}

	try {
	    if (!COContentResourceProxyType.CITATION.equals(resProx.getType())) {
		throw new IllegalArgumentException("createOrUpdateCitationCB:"
			+ " Wrong type of resource!");
	    }

	    resProx
		    .addProperty(COPropertiesType.CITATIONLISTID,
			    citationListId);
	} catch (Exception e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true,
			    "Alert - Citation Tool Error",
			    "ERROR - Controller createOrUpdateCitationCB "
				    + "ResourceProxyType ="
				    + (resProx == null ? "null" : resProx
					    .getType()) + ", err=" + e);
		alertBox.show();
	}
    }

    /**
     * Call for assignment suppression.
     */
    public void removeAssignment(String assignmentId) {

	OsylRPCController.getInstance().removeAssignment(assignmentId,
		getInstance());
    }

    /**
     * Call-back method for assignment suppression.
     */
    public void removeAssignmentCB() {
	OsylUnobtrusiveAlert alert =
		new OsylUnobtrusiveAlert(uiMessages
			.getMessage("assignmentRemoved"));
	OsylEditorEntryPoint.showWidgetOnTop(alert);
    }

    /**
     * Loads the default configuration and course outline for current site.
     */
    public void loadData() {
	// We only request the config here. The course outline is
	// loaded in the call-back (getSerializedConfigCB).
	getSerializedConfig();
    }

    /**
     * Requests the default configuration to the server.
     */
    public void getSerializedConfig() {

	try {
	    OsylRPCController.getInstance().getSerializedConfig(getInstance());
	} catch (RuntimeException e) {
	    e.printStackTrace();
	    unableToInitServer(e.toString());
	}
    }

    /**
     * Call-back method for receiving the configuration downloaded from the
     * server. It is invoked only if the RPC call was successful. In this case
     * we load the course outline.
     * 
     * @param COConfigSerialized returned by the server
     */
    public void getSerializedConfigCB(COConfigSerialized cfg) {
	try {
	    setOsylConfig(new COConfig(cfg));
	    getSerializedCourseOutline();
	} catch (RuntimeException e) {
	    e.printStackTrace();
	    unableToInitServer(e.toString());
	    // Window.alert("getSerializedConfigCB: " + e);
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Error",
			    "getSerializedConfigCB: " + e);
		alertBox.show();
	}
    }

    // ===================================================================
    // ===================== End RPCController block =====================

    // =============== Begin listener implementation block ===============
    //

    /**
     * Called when the save button is clicked!
     */
    public void onSavePushButton(SavePushButtonEvent event) {
	if (isReadOnly()) {
	    return;
	}
	try {
	    // 1. We instruct the ViewContext to close all editors in case an
	    // editor with modified content is still open.
	    getViewContext().closeAllEditors();

	    // 2. Then we serialize the model
	    OsylEditorEntryPoint entryPoint =
		    OsylEditorEntryPoint.getInstance();
	    entryPoint.prepareModelForSave();

	    // 3. And we save it!
	    updateSerializedCourseOutline(entryPoint
		    .getSerializedCourseOutline());

	    // 4. Set flag isDirty to false again
	    osylModelController.setModelDirty(false);
	} catch (Exception e) {
	    e.printStackTrace();
	    // Window.alert(getUiMessages().getMessage("unableToSave",
	    // e.toString()));
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Error",
			    uiMessages.getMessage("unableToSave", e.toString()));
		alertBox.show();
	}
    }

    /**
     * Called when the publish button is clicked!
     */
    public void onPublishPushButton(PublishPushButtonEvent event) {
	if (isReadOnly()) {
	    return;
	}
	if (osylPublishView == null) {
	    osylPublishView = new OsylPublishView(this);
	}
	osylPublishView.show();

    }

    //
    // ================ End listener implementation block ================

    /**
     * @return the osylConfig value.
     */
    public COConfig getOsylConfig() {
	return osylConfig;
    }

    /**
     * @param osylConfig the new value of osylConfig.
     */
    public void setOsylConfig(COConfig osylConfig) {
	this.osylConfig = osylConfig;
	setUiMessages(osylConfig.getI18nMessages());
    }

    /**
     * @return the coMessages value.
     */
    public OsylConfigMessages getCoMessages() {
	return coMessages;
    }

    /**
     * Returns the Course Outline related message corresponding to the specified
     * key
     * 
     * @return String CO Message .
     */
    public String getCoMessage(String key) {
	return getCoMessages().getMessage(key);
    }

    /**
     * @param coMessages the new value of coMessages.
     */
    public void setCoMessages(OsylConfigMessages coMessages) {
	this.coMessages = coMessages;
    }

    /**
     * @return the uiMessages value.
     */
    public OsylConfigMessages getUiMessages() {
	return uiMessages;
    }

    /**
     * Returns the UI message corresponding to the specified key
     * 
     * @return String UI Message .
     */
    public String getUiMessage(String key) {
	return getUiMessages().getMessage(key);
    }

    /**
     * @param uiMessages the new value of uiMessages.
     */
    public void setUiMessages(OsylConfigMessages uiMessages) {
	this.uiMessages = uiMessages;
    }

    /**
     * Checks if the application is running in hosted mode or with the backend.
     * 
     * @return true if in hosted mode, false if not.
     */
    public boolean isInHostedMode() {
	String url = GWT.getHostPageBaseURL();

	if (url.startsWith("http://localhost:8888/")
		&& (url
			.indexOf("org.sakaiquebec.opensyllabus.OsylEditorEntryPoint") != -1))
	    return true;
	else
	    return false;
    }

    /**
     * @return the documents folder name
     */
    public String getDocFolderName() {
	String folder = WORK_FOLDER_NAME;
	if ((null != getCOSerialized()) && getCOSerialized().isPublished()) {
	    folder = PUBLISH_FOLDER_NAME;
	}
	return folder;
    }

    public String getDocRelativePath(String path) {
	String relativePath = path;
	String pattern = getSiteId() + "/" + getDocFolderName() + "/";
	int startIndex = relativePath.indexOf(pattern);
	relativePath = relativePath.substring(startIndex + pattern.length());
	return relativePath;
    }

    /**
     * @param serializedCourseOutline
     */
    public void setCOSerialized(COSerialized serializedCourseOutline) {
	this.serializedCourseOutline = serializedCourseOutline;
    }

    public COSerialized getCOSerialized() {
	return (serializedCourseOutline);
    }

    /**
     * Retrieves the GWT module URL and only retain the beginning until the last
     * occurrence of "/".
     * 
     * @return the adjusted module URL
     */
    public String getAdjustedModuleBaseURL() {
	String moduleBaseURL = GWT.getModuleBaseURL();
	moduleBaseURL = moduleBaseURL.substring(0, moduleBaseURL.length() - 1);
	moduleBaseURL =
		moduleBaseURL.substring(0, moduleBaseURL.lastIndexOf("/"));
	return moduleBaseURL;
    }

    private void displayMessage(String msg) {
	// Window.alert(msg);
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - Message", msg);
	alertBox.show();
    }

    private void displayError(String msg) {
	// Window.alert(msg);
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - Error", msg);
	alertBox.show();
    }

    private class Pinger {
	private int errors;
	private long lastErrortime;
	private Timer t;

	// Run every 2 minutes
	private int delay = 2 * 60 * 1000;

	Pinger() {
	    t = new Timer() {
		public void run() {
		    // Window.alert("Pinger alive");
		    pingServer();
		    // Window.alert("Pinger done!");
		}
	    };
	}

	private void start() {
	    t.scheduleRepeating(delay);
	}

	private void stop() {
	    t.cancel();
	}

	private void incrementErrorCount() {
	    lastErrortime = System.currentTimeMillis();
	    errors++;
	}

	private void setServerOK() {
	    long delta = System.currentTimeMillis() - lastErrortime;
	    // If we were able to ping the server 5 times with no trouble, we
	    // dismiss any errors we had...
	    if (delta < 5 * delay) {
		errors = 0;
		lastErrortime = 0;
	    }
	}

	private int getRecentErrorCount() {
	    return errors;
	}
    } // class Pinger

    /**
     * Pings the server to keep the session alive.
     */
    public void pingServer() {
	OsylRPCController.getInstance().pingServer(getInstance());
    }

    /**
     * Call-back method for pingServer. It is used to keep track of our
     * connection status.
     */
    public void pingServerCB() {
	pinger.setServerOK();
	// Window.alert("Ping callback!");
    }

    /**
     * Call-back method when we cannot ping the server.
     * 
     * @param String errorMessage
     */
    public void unableToPing(Throwable error) {
	pinger.incrementErrorCount();
	String msg;
	if (pinger.getRecentErrorCount() >= 3) {
	    msg = uiMessages.getMessage("unableToPingServerSorry");
	    pinger.stop();
	} else {
	    int MAX_MSG = 120;
	    String errMsg = error.toString();
	    if (errMsg.length() > MAX_MSG) {
		errMsg = errMsg.substring(0, MAX_MSG) + "...";
	    }
	    msg = uiMessages.getMessage("unableToPingServer", errMsg);
	}
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Error", msg);
	alertBox.show();
    }

    public native String xslTransform(String xml, String xsl)/*-{
           var xml = $wnd.xmlParse(xml);
           var xslt = $wnd.xmlParse(xsl);
           var html = $wnd.xsltProcess(xml, xslt);
           return html;
        }-*/;

    public void getXslForGroup(String group, AsyncCallback<String> callBack) {

	OsylRPCController.getInstance().getXslForGroup(group, callBack);

    }

    public boolean isReadOnly() {
	return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
	this.readOnly = readOnly;
    }

    public boolean isInPreview() {
	return inPreview;
    }

    public void setInPreview(boolean inPreview) {
	this.inPreview = inPreview;
    }

    public String getTemporaryCitationList() {
	return citationListId;
    }

}
