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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.controller;

import org.sakaiquebec.opensyllabus.client.rpc.OsylEditorGwtService;
import org.sakaiquebec.opensyllabus.client.rpc.OsylEditorGwtServiceAsync;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * RPCController takes charge of the RPC communications with the server side of
 * the application.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class OsylRPCController {

    // Singleton instance
    private static OsylRPCController _instance = null;

    private final OsylEditorGwtServiceAsync serviceProxy;
    private ServiceDefTarget pointService;

    private static String QUALIFIED_NAME =
	    "org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/";

    /**
     * Private constructor to force the use of getInstance()
     */
    private OsylRPCController() {
	serviceProxy =
		(OsylEditorGwtServiceAsync) GWT
			.create(OsylEditorGwtService.class);

	// This call is needed to initialize the target URL for the remote
	// service.
	// The specified path is defined in web.xml (for deployment) and
	// OsylEditorEntryPoint.gwt.xml (for testing).
	pointService = (ServiceDefTarget) serviceProxy;

	// The base url contains the qualified name. It's not compatible with
	// the tool servlet mapping
	String url = GWT.getModuleBaseURL();
	String cleanUrl =
		url.substring(0, url.length() - QUALIFIED_NAME.length());
	pointService.setServiceEntryPoint(cleanUrl + "OsylGwtService");
    }

    /**
     * Returns an instance of RPCController after initialization, if required.
     * 
     * @return RPCController valid instance
     */
    public static OsylRPCController getInstance() {
	// If the instance was not previously created we do it now
	if (null == _instance) {
	    _instance = new OsylRPCController();
	}
	// And we return it.
	return _instance;
    }

    /**
     * Loads the CourseOutlineXML whose ID is specified from the server. Once it
     * has been received, it asks to the controller to load it by calling
     * getCourseOutlineXMLCB(CourseOutlineXML).
     * 
     * @param String CourseOutline id
     * @param OsylController the caller
     */
    public void getSerializedCourseOutline(OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<COSerialized> callback =
		new AsyncCallback<COSerialized>() {
		    // We define the behavior in case of success
		    public void onSuccess(COSerialized co) {
			try {
			    System.out
				    .println("RPC SUCCESS - getSerializedCourseOutline(...)");
			    if (co != null)
				caller.getSerializedCourseOutlineCB(co);
			    else {
				if (caller.isReadOnly()) {
				    caller.noPublishedCourseOutline();
				}
			    }
			} catch (Exception error) {
			    System.out
				    .println("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			    Window
				    .alert("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			    caller
				    .unableToInitSerializedCourseOutline("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			}
		    }

		    // And we define the behavior in case of failure
		    public void onFailure(Throwable error) {
			System.out
				.println("RPC FAILURE - getSerializedCourseOutline(...) : "
					+ error.toString());
			Window
				.alert("RPC FAILURE - getSerializedCourseOutline(...) : "
					+ error.toString());
			caller
				.unableToGetSerializedCourseOutline("RPC FAILURE - getSerializedCourseOutline(...) : "
					+ error.toString());
		    }
		};
	// Then we can call the method
	// TODO: uncomment and check this code for special published content:
	String publishedSecurityAccess =
		osylController.getPublishedSecurityAccessType();
	if (SecurityInterface.SECURITY_ACCESS_PUBLIC
		.equalsIgnoreCase(publishedSecurityAccess)
		|| SecurityInterface.SECURITY_ACCESS_ATTENDEE
			.equalsIgnoreCase(publishedSecurityAccess)) {
	    serviceProxy.getSerializedPublishedCourseOutlineForAccessType(
		    publishedSecurityAccess, callback);
	} else {
	    serviceProxy.getSerializedCourseOutline(callback);
	}
	// serviceProxy.getSerializedCourseOutline(callback);
    } // getCourseOutlineXML

    /**
     * Loads the CourseOutlineXML whose ID is specified from the server. Once it
     * has been received, it asks to the controller to load it by calling
     * getCourseOutlineXMLCB(CourseOutlineXML).
     * 
     * @param String CourseOutline id
     * @param OsylController the caller
     */
    public void getSerializedCourseOutline(String id,
	    OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<COSerialized> callback =
		new AsyncCallback<COSerialized>() {
		    // We define the behavior in case of success
		    public void onSuccess(COSerialized co) {
			try {
			    System.out
				    .println("RPC SUCCESS getSerializedCourseOutline(...)");
			    caller.getSerializedCourseOutlineCB(co);
			} catch (Exception error) {
			    System.out
				    .println("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			    Window
				    .alert("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			    caller
				    .handleRPCError("Error - Unable to getSerializedCourseOutline(...) on RPC Success: "
					    + error.toString());
			}
		    }

		    // And we define the behavior in case of failure
		    public void onFailure(Throwable error) {
			System.out
				.println("RPC FAILURE getSerializedCourseOutline(...) : "
					+ error.toString());
			Window
				.alert("RPC FAILURE - getSerializedCourseOutline(...) : "
					+ error.toString());
			caller
				.handleRPCError("RPC FAILURE - getSerializedCourseOutline(...): "
					+ error.toString());
		    }
		};
	// Then we can call the method
	serviceProxy.getSerializedCourseOutline(id, callback);
    } // getCourseOutlineXML

    /**
     * Saves the CourseOutlineXML in the server.
     * 
     * @param COSerialized the pojo to save
     * @param OsylController the caller
     */
    public void updateSerializedCourseOutline(COSerialized pojo,
	    OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    // We define the behavior in case of success
	    public void onSuccess(String id) {
		try {
		    System.out
			    .println("RPC SUCCESS - updateSerializedCourseOutline(...)");
		    caller.updateSerializedCourseOutlineCB(id);
		} catch (Exception error) {
		    System.out
			    .println("Error - Unable to updateSerializedCourseOutline(...) on RPC Success: "
				    + error.toString());
		    Window
			    .alert("Error - Unable to updateSerializedCourseOutline(...) on RPC Success: "
				    + error.toString());
		    caller
			    .handleRPCError("Error - Unable to updateSerializedCourseOutline(...) on RPC Success: "
				    + error.toString());
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		System.out
			.println("RPC FAILURE - updateSerializedCourseOutline(...) : "
				+ error.toString());
		Window
			.alert("RPC FAILURE - updateSerializedCourseOutline(...) : "
				+ error.toString());
		caller
			.handleRPCError("RPC FAILURE - updateSerializedCourseOutline(...) :"
				+ error.toString());
	    }
	};
	// Then we can call the method
	serviceProxy.updateSerializedCourseOutline(pojo, callback);
    } // getCourseOutlineXML

    /**
     * Requests to the server that it publishes the CourseOutline whose ID is
     * specified. Once it's received the server response, the caller is informed
     * through the call-back method publishCourseOutlineCB(boolean).
     * 
     * @param String CourseOutline id
     * @param OsylController the caller
     */
    public void publishCourseOutline(OsylController osylController,
	    AsyncCallback<Void> callback) {
	serviceProxy.publishCourseOutline(callback);
    } // publishCourseOutline

    /**
     * Check if the CO of the current context as been published
     * 
     * @return true if the CO has been published at least one time;
     */
    public void hasBeenPublished(OsylController osylController,
	    AsyncCallback<Boolean> callback) {
	serviceProxy.hasBeenPublished(callback);
    }

    /**
     * Requests the URL where we can access the CourseOutline whose ID is
     * specified. It must have been published previously. Once it's received the
     * server response, the caller is informed through the call-back method
     * getPublishedCourseOutlineURLCB(String).
     * 
     * @param String CourseOutline id
     * @param OsylController the caller
     */
    public void getSerializedPublishedCourseOutlineForAccessType(
	    String accessType, OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<COSerialized> callback =
		new AsyncCallback<COSerialized>() {
		    public void onSuccess(COSerialized co) {
			System.out
				.println("RPC SUCCESS - getSerializedPublishedCourseOutlineForAccessType(...)");
			caller
				.getSerializedPublishedCourseOutlineForAccessTypeCB(co);
		    }

		    public void onFailure(Throwable error) {
			System.out
				.println("RPC FAILURE - getSerializedPublishedCourseOutlineForAccessType(...) : "
					+ error.toString());
			Window
				.alert("RPC FAILURE - getSerializedPublishedCourseOutlineForAccessType(...) : "
					+ error.toString());
			caller
				.handleRPCError("RPC FAILURE - getSerializedPublishedCourseOutlineForAccessType(...)");
		    }
		};
	// Then we can call the method
	serviceProxy.getSerializedPublishedCourseOutlineForAccessType(
		accessType, callback);
    }

    /**
     * Requests the user role for current user. Once it's received the server
     * response, the caller is informed through the call-back method
     * getCurrentUserRoleCB(String).
     * 
     * @param OsylController the caller
     */
    public void getCurrentUserRole(OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    public void onSuccess(String role) {
		System.out.println("RPC SUCCESS - getCurrentUserRole(...)");
		caller.getCurrentUserRoleCB(role);
	    }

	    public void onFailure(Throwable error) {
		System.out.println("RPC FAILURE - getCurrentUserRole(...) : "
			+ error.toString());
		Window.alert("RPC FAILURE - getCurrentUserRole(...) : "
			+ error.toString());
		caller
			.handleRPCError("RPC FAILURE - getCurrentUserRole(...) : "
				+ error.toString());
	    }
	};
	// Then we can call the method
	serviceProxy.getCurrentUserRole(callback);
    } // getCurrentUserRole

    /**
     * Requests to the server that it publishes the CourseOutline whose ID is
     * specified. Once it's received the server response, the caller is informed
     * through the call-back method applyPermissionsCB(boolean).
     * 
     * @param String CourseOutline id
     * @param OsylController the caller
     */
    public void applyPermissions(String resourceId, String permission,
	    OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	    public void onSuccess(Void serverResponse) {
		System.out.println("RPC SUCCESS applyPermissions(...)");
		caller.applyPermissionsCB(true);
	    }

	    public void onFailure(Throwable error) {
		System.out.println("RPC FAILURE - applyPermissions(...) : "
			+ error.toString());
		Window.alert("RPC FAILURE - applyPermissions(...) : "
			+ error.toString());
		caller.applyPermissionsCB(false);
	    }
	};
	// Then we can call the method
	try {
	    serviceProxy.applyPermissions(resourceId, permission, callback);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    } // applyPermissions

    /**
     * Loads the default {@link COConfigSerialized} from the server. Once it has
     * been received, it asks to the controller to load it by calling
     * {@link OsylController#getSerializedConfigCB(COConfigSerialized)}.
     * 
     * @param OsylController the caller
     */
    public void getSerializedConfig(OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<COConfigSerialized> callback =
		new AsyncCallback<COConfigSerialized>() {
		    // We define the behavior in case of success
		    public void onSuccess(COConfigSerialized cfg) {
			try {
			    System.out
				    .println("RPC SUCCESS - getSerializedConfig(...)");
			    caller.getSerializedConfigCB(cfg);
			} catch (Exception error) {
			    System.out
				    .println("Error - Unable to getSerializedConfig(...) on RPC Success: "
					    + error.toString());
			    Window
				    .alert("Error - Unable to getSerializedConfig(...) on RPC Success: "
					    + error.toString());
			    caller
				    .unableToInitServer("Error - Unable to getSerializedConfig(...) on RPC Success: "
					    + error.toString());
			}
		    }

		    // And we define the behavior in case of failure
		    public void onFailure(Throwable error) {
			System.out
				.println("RPC FAILURE - getSerializedConfig(...): "
					+ error.toString()
					+ " Hint: Check GWT version");
			Window
				.alert("RPC FAILURE - getSerializedConfig(...): "
					+ error.toString()
					+ " Hint: Check GWT version");
			caller
				.unableToInitServer("RPC FAILURE - getSerializedConfig(...): "
					+ error.toString()
					+ " Hint: Check GWT version");
		    }
		};
	// Then we can call the method
	serviceProxy.getSerializedConfig(callback);
    } // getSerializedConfig

    /**
     * Creates or updates an assignment.
     * 
     * @param OsylController the caller
     */
    public void createOrUpdateAssignment(COContentResourceProxy resProx,
	    String assignmentId, String title, OsylController osylController) {
	createOrUpdateAssignment(resProx, assignmentId, title, null, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, osylController);
    } // createOrUpdateAssignment

    /**
     * Creates or updates an assignment.
     * 
     * @param OsylController the caller
     */
    public void createOrUpdateAssignment(COContentResourceProxy resProx,
	    String assignmentId, String title, String instructions,
	    int openYear, int openMonth, int openDay, int openHour,
	    int openMinute, int closeYear, int closeMonth, int closeDay,
	    int closeHour, int closeMinute, int percentage,
	    OsylController osylController) {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;

	final COContentResourceProxy rProx = resProx;

	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {

	    // We define the behavior in case of success
	    public void onSuccess(String assignmentId) {
		try {
		    System.out
			    .println("RPC SUCCESS - createOrUpdateAssignment(...)");
		    caller.createOrUpdateAssignmentCB(rProx, assignmentId);
		} catch (Exception error) {
		    System.out
			    .println("Error - Unable to createOrUpdateAssignment(...) on RPC Success: "
				    + error.toString());
		    Window
			    .alert("Error - Unable to createOrUpdateAssignment(...) on RPC Success: "
				    + error.toString());
		    caller
			    .unableToCreateOrUpdateAssignment("Error - Unable to createOrUpdateAssignment(...) on RPC Success: "
				    + error.toString());
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		System.out
			.println("RPC FAILURE - createOrUpdateAssignment(...): "
				+ error.toString());
		Window.alert("RPC FAILURE - createOrUpdateAssignment(...): "
			+ error.toString());
		caller
			.unableToCreateOrUpdateAssignment("RPC FAILURE - createOrUpdateAssignment(...): "
				+ error.toString());
	    }
	};

	// Then we can call the method
	if (-1 == openYear) {
	    // We are in the case where we just update the title.
	    serviceProxy
		    .createOrUpdateAssignment(assignmentId, title, callback);
	} else {
	    serviceProxy.createOrUpdateAssignment(assignmentId, title,
		    instructions, openYear, openMonth, openDay, openHour,
		    openMinute, closeYear, closeMonth, closeDay, closeHour,
		    closeMinute, percentage, callback);
	}
    } // createOrUpdateAssignment

    /**
     * Creates or updates a citation.
     * 
     * @param OsylController the caller
     */
    public void createOrUpdateCitation(COContentResourceProxy resProx,
	    String citationListId, String citation, String author, String type,
	    String isbnIssn, String link, OsylController osylController) {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;

	final COContentResourceProxy rProx = resProx;

	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {

	    // We define the behavior in case of success
	    public void onSuccess(String citationListId) {
		try {
		    
		    System.out
			    .println("RPC SUCCESS - createOrUpdateCitation(...)");
		    caller.createOrUpdateCitationCB(rProx, citationListId);
		} catch (Exception error) {
		    System.out
			    .println("Error - Unable to createOrUpdateCitation(...) on RPC Success: "
				    + error.toString());
		    Window
			    .alert("Error - Unable to createOrUpdateCitation(...) on RPC Success: "
				    + error.toString());
		    caller
			    .unableToCreateOrUpdateCitation("Error - Unable to createOrUpdateCitation(...) on RPC Success: "
				    + error.toString());
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		System.out
			.println("RPC FAILURE - createOrUpdateCitation(...): "
				+ error.toString());
		Window.alert("RPC FAILURE - createOrUpdateCitation(...): "
			+ error.toString());
		caller
			.unableToCreateOrUpdateAssignment("RPC FAILURE - createOrUpdateCitation(...): "
				+ error.toString());
	    }
	};
	// Then we can call the method
	serviceProxy.createOrUpdateCitation(citationListId, citation, author,
		type, isbnIssn, link, callback);
    } // createOrUpdateCitation

    /**
     * Creates a temporary citation.
     * 
     * @param OsylController the caller
     */
    public void createTemporaryCitationList(COContentResourceProxy resProx,
	    OsylController osylController) {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;

	final COContentResourceProxy rProx = resProx;

	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {

	    // We define the behavior in case of success
	    public void onSuccess(String citationId) {
		try {
		    System.out
			    .println("RPC SUCCESS - createTemporaryCitationList(...)");
		    caller.createTemporaryCitationListCB(rProx, citationId);
		} catch (Exception error) {
		    System.out
			    .println("Error - Unable to createTemporaryCitationList(...) on RPC Success: "
				    + error.toString());
		    Window
			    .alert("Error - Unable to createTemporaryCitationList(...) on RPC Success: "
				    + error.toString());
		    caller
			    .unableToCreateOrUpdateCitation("Error - Unable to createTemporaryCitationList(...) on RPC Success: "
				    + error.toString());
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		System.out
			.println("RPC FAILURE - createTemporaryCitationList(...): "
				+ error.toString());
		Window.alert("RPC FAILURE - createTemporaryCitationList(...): "
			+ error.toString());
		caller
			.unableToCreateTemporaryCitationList("RPC FAILURE - createTemporaryCitationList(...): "
				+ error.toString());
	    }
	};
	// Then we can call the method
	serviceProxy.createTemporaryCitationList(callback);
    } // createTemporaryCitationList

    /**
     * Removes an assignment.
     * 
     * @param OsylController the caller
     */
    public void removeAssignment(String assignmentId,
	    OsylController osylController) {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;

	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {

	    // We define the behavior in case of success
	    public void onSuccess(Void serverResponse) {
		try {
		    System.out.println("RPC SUCCESS - removeAssignment(...)");
		    caller.removeAssignmentCB();
		} catch (Exception error) {
		    System.out
			    .println("Error - Unable to removeAssignment(...) on RPC Success: "
				    + error.toString());
		    Window
			    .alert("Error - Unable to removeAssignment(...) on RPC Success: "
				    + error.toString());
		    caller
			    .unableToRemoveAssignment("Error - Unable to removeAssignment(...) on RPC Success: "
				    + error.toString());
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		System.out.println("RPC FAILURE - removeAssignment(...): "
			+ error.toString());
		Window.alert("RPC FAILURE - removeAssignment(...): "
			+ error.toString());
		caller
			.unableToRemoveAssignment("RPC FAILURE - removeAssignment(...): "
				+ error.toString());
	    }
	};
	// Then we can call the method
	serviceProxy.removeAssignment(assignmentId, callback);
    } // removeAssignment

    /**
     * Removes an citation.
     * 
     * @param OsylController the caller
     */
    public void removeCitation(String citationId, OsylController osylController) {
	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;
	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {

	    // We define the behavior in case of success
	    public void onSuccess(Void serverResponse) {
		try {
		    System.out.println("RPC SUCCESS - removeCitation(...)");
		    caller.removeCitationCB();
		} catch (Exception error) {
		    System.out
			    .println("Error - Unable to removeCitation(...) on RPC Success: "
				    + error.toString());
		    Window
			    .alert("Error - Unable to removeCitation(...) on RPC Success: "
				    + error.toString());
		    caller
			    .unableToRemoveCitation("Error - Unable to removeCitation(...) on RPC Success: "
				    + error.toString());
		}
	    }

	    // And we define the behavior in case of failure
	    public void onFailure(Throwable error) {
		System.out.println("RPC FAILURE - removeCitation(...): "
			+ error.toString());
		Window.alert("RPC FAILURE - removeCitation(...): "
			+ error.toString());
		caller
			.unableToRemoveAssignment("RPC FAILURE - removeCitation(...): "
				+ error.toString());
	    }
	};
	// Then we can call the method
	serviceProxy.removeCitation(citationId, callback);
    } // removeAssignment

    /**
     * Pings the server.
     */
    public void pingServer(OsylController osylController) {

	// The caller must be declared final to use it into an inner class
	final OsylController caller = osylController;

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
	serviceProxy.ping(callback);
    } // removeAssignment

    public void getXslForGroup(String group, AsyncCallback<String> callBack) {
	serviceProxy.getXslForGroup(group, callBack);
    }

    public void getResourceLicenceInfo(
	    AsyncCallback<ResourcesLicencingInfo> callBack) {
	serviceProxy.getResourceLicenceInfo(callBack);
    }
}
