package org.sakaiquebec.opensyllabus.server;

import java.util.Map;
import java.util.Vector;

import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtService;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service. (for hosted mode)
 */
@SuppressWarnings("serial")
public class OsylEditorGwtServiceImpl extends RemoteServiceServlet implements
	OsylEditorGwtService {

    public void applyPermissions(String resourceId, String permission) {
    }

    public String getCurrentUserRole() {
	return null;
    }

    public ResourcesLicencingInfo getResourceLicenceInfo() {
	return null;
    }

    public COConfigSerialized getSerializedConfig() throws Exception {
	return null;
    }

    public COSerialized getSerializedCourseOutline() throws Exception {
	return null;
    }

    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String accessType) throws Exception {
	return null;
    }

    public String getXslForGroup(String group) {
	return null;
    }

    public boolean hasBeenPublished() throws Exception {
	return false;
    }

    public void initTool() throws Exception {
    }

    public void ping() {
    }

    public Vector<Map<String, String>> publishCourseOutline() throws Exception,FusionException {
	return null;
    }

    public SakaiEntities getExistingEntities(String siteId) {
	return null;
    }

    public boolean updateSerializedCourseOutline(COSerialized co)
	    throws Exception {
	return false;
    }

    public boolean checkSitesRelation(String resourceURI) {
	return false;
    }

    public String transformXmlForGroup(String xml, String group) {
	return null;
    }

    public void releaseLock() {

    }

    public void createPrintableEditionVersion() throws Exception {

    }
    
    public void notifyOnPublish(String siteId, String subject, String body) {
    
    }
}
