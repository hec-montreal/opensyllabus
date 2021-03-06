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
import java.util.Map;
import java.util.Vector;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.event.OsylModelUpdatedEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.PublishPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.SavePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.OsylMainView;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylPublishView;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COConfig;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;
import org.sakaiquebec.opensyllabus.shared.model.OsylSettings;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

import com.google.gwt.core.client.GWT;
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

    /**
     * Stand alone compilation mode : opensyllabus without sakai
     */
    public final static boolean STAND_ALONE_MODE = false;

    public static final boolean TRACE = false;
    
    // Singleton instance
    private static OsylController _instance;

    private OsylMainView mainView;

    private OsylViewContext osylViewContext;

    private COConfig osylConfig;

    private boolean inPreview = false;
    /**
     * Course outline and user interface message bundle
     */
    private OsylConfigMessages coMessages, uiMessages;

    private OsylPublishView osylPublishView = null;

    // Sakai ID of current site
    private String siteId;

    private COSerialized serializedCourseOutline;

    // Instance of AutoSaver to save current course outline automatically
    private AutoSaver autoSaver;

    // Our Model Controller
    private OsylModelController osylModelController;

    private boolean readOnly;

    private Date selectedDate;

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
		
	setSiteId(getSiteIdParameter());
	setReadOnly(getReadOnlyParameter());
	osylModelController.setReadOnly(isReadOnly());
	if (!isReadOnly()) {
	    autoSaver = new AutoSaver();
	    autoSaver.start();
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
     * This method is JSNI method to retrieve the value of osyl:ro meta tag in
     * the html head. &lt;meta name="osyl:ro" content="true" &gt; or &lt;meta
     * name="osyl:ro" content="false" &gt;
     * 
     * @return true if UI is in read-only mode, false if not.
     */
    public native String getSiteIdParameter() /*-{
						 metas = $doc.getElementsByTagName('meta');
						 ret = null;

						 for (i = 0; i < metas.length; i++) {

						 	if(metas[i].name == 'osyl:siteId') {
						 		ret = metas[i].content;
						 		break;
						 	}
						 }
						 return ret;
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
     * Request the loading of default CourseOutlineXML to the server.
     */
    public void getSerializedCourseOutline() {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = this;
	// We first create a call-back for this method call
	AsyncCallback<COSerialized> callback =
		new AsyncCallback<COSerialized>() {
		    // We define the behavior in case of success
		    public void onSuccess(COSerialized co) {
			try {
			    System.out
				    .println("RPC SUCCESS - getSerializedCourseOutline(...)");
			    if (co != null) {
				if (co.isIncompatibleWithHisParent()) {
				    final OsylAlertDialog alertBox =
					    new OsylAlertDialog(
						    false,
						    true,
						    uiMessages
							    .getMessage("Global.warning"),
						    uiMessages
							    .getMessage("Exception.incompatibleWithHisParent"));
				    alertBox.show();
				}
				if (co.isIncompatibleHierarchy()) {
				    final OsylAlertDialog alertBox =
					    new OsylAlertDialog(
						    false,
						    true,
						    uiMessages
							    .getMessage("Global.warning"),
						    uiMessages
							    .getMessage("Exception.incompatibleHierarchy"));
				    alertBox.show();
				}
				caller.getSerializedCourseOutlineCB(co);
			    } else {
				if (caller.isReadOnly()) {
				    caller.noPublishedCourseOutline();
				}
			    }
			} catch (Exception error) {
			    caller
				    .unableToInitSerializedCourseOutline("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			}
		    }

		    // And we define the behavior in case of failure
		    public void onFailure(Throwable error) {
			caller
				.unableToGetSerializedCourseOutline("RPC FAILURE - getSerializedCourseOutline(...) : "
					+ error.toString());
		    }
		};
	// Then we can call the method
	// TODO: uncomment and check this code for special published content:
	String publishedSecurityAccess = this.getPublishedSecurityAccessType();
	if (SecurityInterface.ACCESS_PUBLIC
		.equalsIgnoreCase(publishedSecurityAccess)
		|| SecurityInterface.ACCESS_ATTENDEE
			.equalsIgnoreCase(publishedSecurityAccess)
		|| SecurityInterface.ACCESS_COMMUNITY
			.equalsIgnoreCase(publishedSecurityAccess)) {
	    OsylRemoteServiceLocator.getEditorRemoteService()
		    .getSerializedPublishedCourseOutlineForAccessType(
			    publishedSecurityAccess, callback);
	} else {
	    OsylRemoteServiceLocator.getEditorRemoteService()
		    .getSerializedCourseOutline(getSiteId(),callback);
	}
	// OsylRemoteServiceLocator.getEditorRemoteService().getSerializedCourseOutline(callback);
    }

    /**
     * Call-back method for parsing the CourseOutlineXML downloaded from the
     * server.
     * 
     * @param COSerialized returned by the server
     */
    public void getSerializedCourseOutlineCB(COSerialized co){ 
	if (!co.isEditable()) {
	    readOnly = true;
	    autoSaver.stop();
	    OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, uiMessages
			    .getMessage("Global.warning"), uiMessages
			    .getMessage("courseOutline.lockedBy", co
				    .getLockedBy()));
	    alertBox.show();
	}
	// keep track of co messages
	setCoMessages(new OsylConfigMessages(co.getMessages()));
	
	OsylEditorEntryPoint.getInstance().initModel(co);
    }

    /**
     * Call-back method when we cannot download the CourseOutlineXML from the
     * server.
     * 
     * @param String errorMessage
     */
    public void unableToGetSerializedCourseOutline(String errorMessage) {
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
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Alert - RPC Error",
			uiMessages.getMessage("unableToInitCO", errorMessage));
	alertBox.show();
    }

    /**
     * Call back method when there is no published course outline for a group.
     * In his case, we display a message to the user.
     */
    public void noPublishedCourseOutline() {
	RootPanel rootPanel = OsylEditorEntryPoint.getInstance().getRootPanel();
	if (rootPanel == null)
	    rootPanel = RootPanel.get("osyl-editor-root");
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
    public void updateSerializedCourseOutline(final COSerialized pojo,
	    AsyncCallback<Boolean> callback) {
	OsylRemoteServiceLocator.getEditorRemoteService()
		.updateSerializedCourseOutline(pojo, callback);
    }

    /**
     * Call-back method for saving the CourseOutlineXML.
     * 
     * @param String the id of the saved XML content
     */
    public void updateSerializedCourseOutlineCB(Boolean reload) {

	if (reload) {
	    AsyncCallback<COSerialized> callback =
		    new AsyncCallback<COSerialized>() {
			// We define the behavior in case of success
			public void onSuccess(COSerialized co) {
			    try {
				OsylEditorEntryPoint.getInstance()
					.refreshModel(co);

			    } catch (Exception error) {
				handleRPCError("RPC FAILURE - updateSerializedCourseOutlineCB(...) : "
					+ error.toString());
			    }
			}

			// And we define the behavior in case of failure
			public void onFailure(Throwable error) {
			    handleRPCError("RPC FAILURE - updateSerializedCourseOutlineCB(...) : "
				    + error.toString());
			}
		    };
	    OsylRemoteServiceLocator.getEditorRemoteService()
		    .getSerializedCourseOutline(getSiteId(),callback);
	}

	// A POJO has been sent
	if (getExecMode().equals("test")) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(
			    false,
			    true,
			    "Alert - Save Confirmation",
			    "This is the call back from saveCourseOutlineXML. You have automatically "
				    + "updated the current course outline, unique ID "
				    + OsylEditorEntryPoint.getInstance()
					    .getSerializedCourseOutline()
					    .getCoId());
	    alertBox.show();
	} else {
	    OsylUnobtrusiveAlert alert =
		    new OsylUnobtrusiveAlert(uiMessages
			    .getMessage("save.savedOK"));
	    OsylEditorEntryPoint.showWidgetOnTop(alert);
	}
    }

    /**
     * Requests to the server that it publishes the CourseOutline whose ID is
     * specified.
     * 
     * @param String ID of the requested CourseOutlineXML
     */
    public void publishCourseOutline(
	    AsyncCallback<Vector<Map<String, String>>> callback) {
        String nonce = generateNonce();
	OsylRemoteServiceLocator.getEditorRemoteService().publishCourseOutline(
		nonce, callback);
    }

    private String generateNonce() {
        return System.currentTimeMillis() + siteId;
    }

    /**
     * Response of the server to tell that the course outline has been
     * published.
     * 
     * @param callback
     */
    public void hasBeenPublished(AsyncCallback<Boolean> callback) {
	OsylRemoteServiceLocator.getEditorRemoteService().hasBeenPublished(
		callback);
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
     * Checks if the current site has a relation (child - parent or ancestor)
     * with the site containing the resource. If it is the case, we allow the
     * site to access to the resource
     * 
     * @param resourceURI
     * @return
     */
    public void checkSitesRelation(String resourceURI) {
	try {
	    final OsylController caller = this;
	    AsyncCallback<Void> callback = new AsyncCallback<Void>() {
		public void onSuccess(Void relation) {
		    try {
			System.out
				.println("RPC SUCCESS - checkSitesRelation(...)");
			caller.checkSitesRelationCB(true);
		    } catch (Exception error) {
			caller
				.unableToInitServer("Error - Unable to checkSitesRelation(...) on RPC Success: "
					+ error.toString());
		    }
		}

		// And we define the behavior in case of failure
		public void onFailure(Throwable error) {
		    caller
			    .unableToInitServer("RPC FAILURE - checkSitesRelation(...): "
				    + error.toString()
				    + " Hint: Check GWT version");
		    caller.checkSitesRelationCB(false);
		}
	    };
	    // TODO: It is here that we allow or unable access to the resource
	    OsylRemoteServiceLocator.getEditorRemoteService()
		    .checkSitesRelation(resourceURI, callback);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void checkSitesRelationCB(boolean relation) {

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
	// The caller must be declared final to use it into an inner class
	final OsylController caller = this;
	// We first create a call-back for this method call
	AsyncCallback<COSerialized> callback =
		new AsyncCallback<COSerialized>() {
		    public void onSuccess(COSerialized co) {
			caller
				.getSerializedPublishedCourseOutlineForAccessTypeCB(co);
		    }

		    public void onFailure(Throwable error) {
			caller
				.handleRPCError("RPC FAILURE - getSerializedPublishedCourseOutlineForAccessType(...)");
		    }
		};
	// Then we can call the method
	OsylRemoteServiceLocator.getEditorRemoteService()
		.getSerializedPublishedCourseOutlineForAccessType(accessType,
			callback);
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
	// The caller must be declared final to use it into an inner class
	final OsylController caller = this;
	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    public void onSuccess(String role) {
		caller.getCurrentUserRoleCB(role);
	    }

	    public void onFailure(Throwable error) {
		caller
			.handleRPCError("RPC FAILURE - getCurrentUserRole(...) : "
				+ error.toString());
	    }
	};
	// Then we can call the method
	OsylRemoteServiceLocator.getEditorRemoteService().getCurrentUserRole(
		callback);
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
	// The caller must be declared final to use it into an inner class
	final OsylController caller = this;
	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	    public void onSuccess(Void serverResponse) {
		caller.applyPermissionsCB(true);
	    }

	    public void onFailure(Throwable error) {
		caller.applyPermissionsCB(false);
	    }
	};
	// Then we can call the method
	try {
	    OsylRemoteServiceLocator.getEditorRemoteService().applyPermissions(
		    resourceId, permission, callback);
	} catch (Exception e) {
	    e.printStackTrace();
	}
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

	// OsylRPCController.getInstance().getSerializedConfig(getInstance());
	try {
	    // The caller must be declared final to use it into an inner class
	    final OsylController caller = this;
	    // We first create a call-back for this method call
	    AsyncCallback<COConfigSerialized> callback =
		    new AsyncCallback<COConfigSerialized>() {
			// We define the behavior in case of success
			public void onSuccess(COConfigSerialized cfg) {
			    try {
				caller.getSerializedConfigCB(cfg);
			    } catch (Exception error) {
				caller
					.unableToInitServer("Error - Unable to getSerializedConfig(...) on RPC Success: "
						+ error.toString());
			    }
			}

			// And we define the behavior in case of failure
			public void onFailure(Throwable error) {
			    caller
				    .unableToInitServer("RPC FAILURE - getSerializedConfig(...): "
					    + error.toString()
					    + " Hint: Check GWT version");
			}
		    };
	    // Then we can call the method
	    OsylRemoteServiceLocator.getEditorRemoteService()
		    .getSerializedConfig(getSiteId(),callback);
	} catch (RuntimeException e) {
	    e.printStackTrace();
	    unableToInitServer(e.toString());
	}
    }

    public void getExistingEntities(String siteId,
	    AsyncCallback<SakaiEntities> callback) {

	OsylRemoteServiceLocator.getEditorRemoteService().getExistingEntities(
		siteId, callback);
    }

    public void setSelectedDate(Date selectedDate) {
	this.selectedDate = selectedDate;
	mainView.getWorkspaceTitleView().refreshView();
	mainView.getWorkspaceView().refreshView();
	mainView.getOsylTreeView().refreshView();
    }

    public Date getSelectedDate() {
	return selectedDate;
    }

    public boolean isSelectedDateBeforeDate(Date date) {
	if (getSelectedDate() != null && date != null) {
	    if (getSelectedDate().before(date))
		return true;
	}
	return false;
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
	saveCourseOutline(null);
    }

    public void saveCourseOutline(AsyncCallback<Void> callBack) {
	saveCourseOutline(callBack, false);
    }

    public void saveCourseOutline(final AsyncCallback<Void> callBack,
	    final boolean autoSave) {
	try {
	    // 1. Unless we are performing an auto-save, we instruct the
	    // ViewContext to close all editors in case an editor with modified
	    // content is still open.
	    if (!autoSave) {
		getViewContext().closeAllEditors();
	    }

	    // 2. Then we serialize the model
	    OsylEditorEntryPoint entryPoint =
		    OsylEditorEntryPoint.getInstance();
	    entryPoint.prepareModelForSave();

	    // 3. And we save it!
	    // The caller must be declared final to use it into an inner class
	    final OsylController caller = this;
	    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
		// We define the behavior in case of success
		public void onSuccess(Boolean reload) {
		    try {
			caller.updateSerializedCourseOutlineCB(reload);
			if (callBack != null){
			    callBack.onSuccess(null);
			}
			
		    } catch (Exception error) {
			caller
				.handleRPCError("Error - Unable to updateSerializedCourseOutline(...) on RPC Success: "
					+ error.toString());
		    }
		}

		// And we define the behavior in case of failure
		public void onFailure(Throwable error) {
		    caller
			    .handleRPCError("RPC FAILURE - updateSerializedCourseOutline(...) :"
				    + error.toString());
		}
	    };

	    updateSerializedCourseOutline(entryPoint
		    .getSerializedCourseOutline(), callback);

	    // 4. Set flag isDirty to false again
	    osylModelController.setModelDirty(false);
	} catch (Exception e) {
	    e.printStackTrace();
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, "Alert - Error",
			    uiMessages.getMessage("save.unableToSave", e
				    .toString()));
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
	osylPublishView = new OsylPublishView(this);
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
     * @return the general settings object.
     */
    public OsylSettings getSettings() {
	return getOsylConfig().getSettings();
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
	return !GWT.isScript();
    }

    /**
     * @return the documents folder name
     */
    public String getDocFolderName() {
	return "";
    }

    public String getDocRelativePath(String path) {
	String relativePath = path;
	String pattern = getSiteId() + "/";
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
    public String getPublishedModuleBaseURL() {
	String moduleBaseURL = GWT.getModuleBaseURL();
	moduleBaseURL = moduleBaseURL.substring(0, moduleBaseURL.length() - 1);
	moduleBaseURL =
		moduleBaseURL.substring(0, moduleBaseURL.lastIndexOf("/"));
	return moduleBaseURL;
    }

    public void displayMessage(String msg) {
	// Window.alert(msg);
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Message", msg);
	alertBox.show();
    }

    public void displayError(String msg) {
	// Window.alert(msg);
	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Error", msg);
	alertBox.show();
    }

 
    /**
     * Pings the server to keep the session alive.
     */
    public void pingServer() {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = this;

	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {

	    // We define the behavior in case of success
	    public void onSuccess(Void serverResponse) {
		try {
		    caller.pingServerCB();
		} catch (Exception e) {
		    caller.unableToPing(e);
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		caller.unableToPing(error);
	    }
	};
	// Then we can call the method
	OsylRemoteServiceLocator.getEditorRemoteService().ping(callback);
    }

    /**
     * Call-back method for pingServer. It is used to keep track of our
     * connection status.
     */
    public void pingServerCB() {
    }

    /**
     * Call-back method when we cannot ping the server.
     * 
     * @param String errorMessage
     */
    public void unableToPing(Throwable error) {
	String msg;

	int MAX_MSG = 120;

	String errMsg = error.toString();
	if (errMsg.length() > MAX_MSG) {
	    errMsg = errMsg.substring(0, MAX_MSG) + "...";
	}
	msg = uiMessages.getMessage("unableToPingServer", errMsg);

	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Error", msg);

	alertBox.show();
    }
    
    

    private class AutoSaver {
	private Timer t;

	// Run every 5 minutes if the model is dirty (it has been edited)
	private int delay = 5 * 60 * 1000;

	AutoSaver() {
	    t = new Timer() {
		public void run() {
		    if (isModelDirty()) {
			autoSave();
		    }
		}
	    };
	}

	private void start() {
	    t.scheduleRepeating(delay);
	}

	private void stop() {
	    t.cancel();
	}
    } // class AutoSaver

    /**
     * AutoSaves current course outline.
     */
    private void autoSave() {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = this;

	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {

	    // We define the behavior in case of success
	    public void onSuccess(Void serverResponse) {
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		caller.unableToAutoSave(error);
	    }
	};
	// Then we can call the method
	saveCourseOutline(callback, true);
    }

    /**
     * Call-back method when we cannot autoSave.
     * 
     * @param String errorMessage
     */
    public void unableToAutoSave(Throwable error) {
	String msg;
	int MAX_MSG = 120;
	String errMsg = error.toString();
	if (errMsg.length() > MAX_MSG) {
	    errMsg = errMsg.substring(0, MAX_MSG) + "...";
	}
	msg = uiMessages.getMessage("save.AutoSaveDidNotWork", errMsg);

	final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "Error", msg);
	alertBox.show();
    } // unableToAutoSave

    public native String xslTransform(String xml, String xsl)/*-{
							     var xml = $wnd.xmlParse(xml);
							     var xslt = $wnd.xmlParse(xsl);
							     var html = $wnd.xsltProcess(xml, xslt);
							     return html;
							     }-*/;

    public void getXslForGroup(String group, AsyncCallback<String> callBack) {

	OsylRemoteServiceLocator.getEditorRemoteService().getXslForGroup(group,
		callBack);

    }

    public void transformXmlForGroup(String xml, String group,
	    AsyncCallback<String> callback) {
	OsylRemoteServiceLocator.getEditorRemoteService().transformXmlForGroup(
		xml, group, callback);
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

    public void releaseLock() {
	AsyncCallback<Void> cb = new AsyncCallback<Void>() {
	    public void onFailure(Throwable caught) {
	    }

	    public void onSuccess(Void result) {
	    }
	};
	OsylRemoteServiceLocator.getEditorRemoteService().releaseLock(cb);
    }

    public void createPrintableEditionVersion(
	    AsyncCallback<Void> printEditionVersionCallback) {
	OsylRemoteServiceLocator.getEditorRemoteService()
		.createPrintableEditionVersion(printEditionVersionCallback);
    }

    public void notifyOnPublish(String siteId, String subject, String body,
	    AsyncCallback<Void> callback) {
	OsylRemoteServiceLocator.getEditorRemoteService().notifyOnPublish(
		siteId, subject, body, callback);
    }

    public void sendEvent(String citationLinkClickEvent, String resource) {
	AsyncCallback<Void> voidCallback = new AsyncCallback<Void>() {
	    public void onFailure(Throwable caught) {
	    }

	    public void onSuccess(Void result) {
	    }
	};
	OsylRemoteServiceLocator.getEditorRemoteService().sendEvent(citationLinkClickEvent,resource, voidCallback);
    }


}
