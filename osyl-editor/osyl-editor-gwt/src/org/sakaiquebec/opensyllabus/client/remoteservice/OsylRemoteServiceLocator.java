package org.sakaiquebec.opensyllabus.client.remoteservice;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.OsylCitationRemoteServiceHostedModeImpl;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.OsylDirectoryRemoteServiceHostedModeImpl;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.OsylEditorHostedModeImpl;
import org.sakaiquebec.opensyllabus.client.remoteservice.json.OsylCitationRemoteServiceJsonImpl;
import org.sakaiquebec.opensyllabus.client.remoteservice.json.OsylDirectoryRemoteServiceJsonImpl;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylCitationRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylDirectoryRemoteService;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylDirectoryRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtService;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * @author Mathieu Colombet
 * this class provide the adequate implementations of the 3 remote service interfaces
 */
public class OsylRemoteServiceLocator {



	private static OsylEditorGwtServiceAsync editorRemoteService;
	private static OsylDirectoryRemoteServiceAsync directoryRemoteService;
	private static OsylCitationRemoteServiceAsync citationRemoteService;


	/**
	 * @return OsylEditorGwtServiceAsync
	 */
	public static OsylEditorGwtServiceAsync getEditorRemoteService() {
		if (editorRemoteService == null) {
			editorRemoteService = createEditorRemoteService();
		}
		return editorRemoteService;
	}

	/**
	 * @return OsylDirectoryRemoteServiceAsync
	 */
	public static OsylDirectoryRemoteServiceAsync getDirectoryRemoteService() {
		if (directoryRemoteService == null) {
			directoryRemoteService = createDirectoryRemoteService();
		}
		return directoryRemoteService;
	}
	
	/**
	 * @return OsylCitationRemoteServiceAsync
	 */
	public static OsylCitationRemoteServiceAsync getCitationRemoteService() {
		if (citationRemoteService == null) {
			citationRemoteService = createCitationRemoteService();
		}
		return citationRemoteService;
	}

	/**
	 * work around: used to get the post url of the upload form
	 * @param relativePathFolder
	 * @return
	 */
	public static String getUploadFileUrl(String relativePathFolder) {
		if (directoryRemoteService == null) {
			getDirectoryRemoteService();
		}

		if (OsylController.getInstance().isInHostedMode()) {
			return null;
		} else if (directoryRemoteService instanceof OsylDirectoryRemoteServiceJsonImpl) {
			return ((OsylDirectoryRemoteServiceJsonImpl) directoryRemoteService)
			.getUploadFileUrl(relativePathFolder);
		} else {
			return getWebAppUrl() + "upload?dir=" + relativePathFolder;
		}
	}

	/**
	 * OsylEditorGwtServiceAsync factory
	 * @return OsylEditorGwtServiceAsync
	 */
	private static OsylEditorGwtServiceAsync createEditorRemoteService() {
		if (OsylController.getInstance().isInHostedMode()) {
			// GWT hosted mode
			return new OsylEditorHostedModeImpl();
		} else {
			// GWT RPC mode
			OsylEditorGwtServiceAsync serviceProxy = (OsylEditorGwtServiceAsync) GWT
			.create(OsylEditorGwtService.class);

			// This call is needed to initialize the target URL for the remote
			// service.
			// The specified path is defined in web.xml (for deployment) and
			// OsylEditorEntryPoint.gwt.xml (for testing).
			ServiceDefTarget pointService = (ServiceDefTarget) serviceProxy;

			pointService.setServiceEntryPoint(getWebAppUrl() + "OsylGwtService");

			return serviceProxy;
		}
	}

	/**
	 * OsylDirectoryRemoteServiceAsync factory
	 * @return OsylDirectoryRemoteServiceAsync
	 */
	private static OsylDirectoryRemoteServiceAsync createDirectoryRemoteService() {
		if (OsylController.getInstance().isInHostedMode()) {
			return new OsylDirectoryRemoteServiceHostedModeImpl();
		} else if (OsylController.STAND_ALONE_MODE) {
			// GWT RPC mode
			OsylDirectoryRemoteServiceAsync serviceProxy = (OsylDirectoryRemoteServiceAsync) GWT
			.create(OsylDirectoryRemoteService.class);

			// This call is needed to initialize the target URL for the remote
			// service.
			// The specified path is defined in web.xml (for deployment) and
			// OsylEditorEntryPoint.gwt.xml (for testing).
			ServiceDefTarget pointService = (ServiceDefTarget) serviceProxy;

			pointService.setServiceEntryPoint(getWebAppUrl() + "OsylDirectoryGwtService");

			return serviceProxy;
		} else {
			return new OsylDirectoryRemoteServiceJsonImpl();
		}
	}
	
	/**
	 * OsylDirectoryRemoteServiceAsync factory
	 * @return OsylCitationRemoteServiceAsync
	 */
	private static OsylCitationRemoteServiceAsync createCitationRemoteService() {
		if (OsylController.getInstance().isInHostedMode()) {
			return new OsylCitationRemoteServiceHostedModeImpl();
		} else if (OsylController.STAND_ALONE_MODE) {
			// GWT RPC mode : at this time not implemented
			// return OsylCitationRemoteServiceHostedModeImpl to avoid null pointer exception
			return new OsylCitationRemoteServiceHostedModeImpl();
		} else {
			return new OsylCitationRemoteServiceJsonImpl();
		}
	}

    private static final String RPC_QUALIFIED_NAME =
	    "org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/";

    private static String getWebAppUrl() {
	// The base url contains the qualified name. It's not compatible
	// with the tool servlet mapping
	String url = GWT.getModuleBaseURL();
	String cleanUrl =
		url.substring(0, url.length() - RPC_QUALIFIED_NAME.length());
	return cleanUrl;
    }


}
