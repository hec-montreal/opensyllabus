package org.sakaiquebec.opensyllabus.client.remoteservice.json.callback;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylFileItem;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mathieu Colombet
 * implementation of OsylAbstractRemoteDirectoryContentCallBackAdaptator used to convert json returned string in a "Directory" OsylDirectory object
 */
public class OsylFileRemoteDirectoryContentCallBackAdaptator extends
		OsylAbstractRemoteDirectoryContentCallBackAdaptator {

	public OsylFileRemoteDirectoryContentCallBackAdaptator(
			AsyncCallback<List<OsylAbstractBrowserItem>> asyncCallback) {
		super(asyncCallback);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OsylAbstractBrowserItem getOsylAbstractBrowserItem(
			JSONObject jObject) {
	    try{
		JSONString referenceRoot = (JSONString) ((JSONObject) jObject
				.get("properties")).get("sakai:reference-root");
		if (referenceRoot != null
				&& referenceRoot.stringValue().equals("/citation")) {
			return null;
		} else {

			JSONString path = (JSONString) jObject.get("path");
			JSONString name = (JSONString) ((JSONObject) jObject
					.get("properties")).get("DAV:displayname");
			String mimeTypeString = "";
			String lastmodifiedString = "";
			String descriptionString = "";
			String copyrightchoiceString = "";

			if (jObject.get("mimeType") != null) {
				JSONString mimeType = (JSONString) jObject.get("mimeType");
				mimeTypeString = mimeType.stringValue();
			}
			if (((JSONObject) jObject.get("properties"))
					.get("DAV:getlastmodified") != null) {
				JSONString lastmodified = (JSONString) ((JSONObject) jObject
						.get("properties")).get("DAV:getlastmodified");
				lastmodifiedString = OsylDateUtils.getXmlDateStringFromSakaiDateString(lastmodified.stringValue());
			}
			if (((JSONObject) jObject.get("properties"))
					.get("CHEF:description") != null) {
				JSONString description = (JSONString) ((JSONObject) jObject
						.get("properties")).get("CHEF:description");
				descriptionString = description.stringValue();
			}
			if (((JSONObject) jObject.get("properties"))
					.get("CHEF:copyrightchoice") != null) {
				JSONString copyrightchoice = (JSONString) ((JSONObject) jObject
						.get("properties")).get("CHEF:copyrightchoice");
				copyrightchoiceString = copyrightchoice.stringValue();
			}
			return new OsylFileItem(name.stringValue(), path.stringValue(),
					false, lastmodifiedString, mimeTypeString,
					descriptionString, copyrightchoiceString);
		}
	    }catch (Exception e) {
		return null;
	    }
	}
	
	

}
