package org.sakaiquebec.opensyllabus.client.remoteservice.json.callback;

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylDirectory;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mathieu Colombet specialization of OsylCallBackAdaptator used to
 *         convert json returned string in an OsylDirectory object
 */
public abstract class OsylAbstractRemoteDirectoryContentCallBackAdaptator
	extends OsylCallBackAdaptator<List<OsylAbstractBrowserItem>> {

    /**
     * Constructor
     * 
     * @param asyncCallback AsyncCallback<List<OsylAbstractBrowserItem>>
     */
    public OsylAbstractRemoteDirectoryContentCallBackAdaptator(
	    AsyncCallback<List<OsylAbstractBrowserItem>> asyncCallback) {
	super(asyncCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<OsylAbstractBrowserItem> adaptResponse(Response response) {

	List<OsylAbstractBrowserItem> dirListing = null;
	OsylDirectory dir = getDirectoryContent(response.getText());
	if (dir != null) {
	    dirListing = dir.getFilesList();
	} else {
	    dirListing = new ArrayList<OsylAbstractBrowserItem>();
	}
	return dirListing;
    }

    /**
     * Parse a JSON String and construct corresponding RemoteDirectory
     * 
     * @param jsonString
     * @return The constructed remoteDirectory TODO
     */
    private OsylDirectory getDirectoryContent(String jsonString) {

	if (TRACE) {
	    Window.alert("JSON resp = " + jsonString);
	}
	JSONString name = null;
	JSONString path = null;

	JSONObject root = (JSONObject) JSONParser.parse(jsonString);
	JSONObject items = (JSONObject) root.get("items");
	List<OsylAbstractBrowserItem> dirItems =
		new ArrayList<OsylAbstractBrowserItem>();
	if (items != null) {
	    for (String key : items.keySet()) {
		boolean escaped = false;
		JSONObject jObject = (JSONObject) items.get(key);
		JSONString type = (JSONString) jObject.get("primaryNodeType");
		OsylAbstractBrowserItem osylRemoteDirItem = null;
		if (type != null && type.stringValue().equals("nt:folder")) {
		    path = (JSONString) jObject.get("path");
		    name =
			    (JSONString) ((JSONObject) jObject
				    .get("properties")).get("DAV:displayname");
		    String nameString =
			    (name == null) ? "" : name.stringValue();
		    List<OsylAbstractBrowserItem> list =
			    new ArrayList<OsylAbstractBrowserItem>();
		    osylRemoteDirItem =
			    new OsylDirectory(nameString, path.stringValue(),
				    "", list);
		} else {
		    OsylAbstractBrowserItem oBrowserItem =
			    getOsylAbstractBrowserItem(jObject);
		    if (oBrowserItem != null) {
			osylRemoteDirItem = oBrowserItem;
		    } else {
			escaped = true;
		    }
		}
		if (!escaped) {
		    dirItems.add(osylRemoteDirItem);
		}
	    }
	}

	OsylDirectory osylRemoteDirectory = null;
	if (!dirItems.isEmpty()) {
	    osylRemoteDirectory = new OsylDirectory("", "", "", dirItems);
	}

	if (TRACE) {
	    Window.alert("JSON resp parsed!");
	}
	return osylRemoteDirectory;
    }

    /**
     * Parse the json string and return the adequate OsylAbstractBrowserItem
     * 
     * @param jObject the JSONObject to be parsed.
     * @return the OSylFileItem
     */
    protected abstract OsylAbstractBrowserItem getOsylAbstractBrowserItem(
	    JSONObject jsObject);
}
