package org.sakaiquebec.opensyllabus.server;

import java.util.Date;
import java.util.Map;

import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtService;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service. (for hosted mode)
 */
@SuppressWarnings("serial")
public class OsylEditorGwtServiceImpl extends RemoteServiceServlet implements
	OsylEditorGwtService {

    public void applyPermissions(String resourceId, String permission) {
    }

    public String createOrUpdateAssignment(String assignmentId, String title,
	    String instructions, Date openDate, Date closeDate, Date dueDate) {
	return null;
    }

    public String createOrUpdateCitation(String citationListId,
	    String citation, String author, String type, String isbnIssn,
	    String link) {
	return null;
    }

    public String createTemporaryCitationList() {
	return null;
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

    public COSerialized getSerializedCourseOutline(String id) throws Exception {
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

    public Map<String, String> publishCourseOutline() throws Exception {
	return null;
    }

    public void removeAssignment(String assignmentId) {
    }

    public void removeCitation(String citationId) {
    }

    public String updateSerializedCourseOutline(COSerialized co)
	    throws Exception {
	return null;
    }

    public boolean checkSitesRelation(String resourceURI) {
	return false;
    }

    public String transformXmlForGroup(String xml, String group) {
	return null;
    }
}
