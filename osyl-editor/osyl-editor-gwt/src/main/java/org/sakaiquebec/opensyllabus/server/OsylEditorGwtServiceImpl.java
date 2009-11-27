package org.sakaiquebec.opensyllabus.server;

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
	// TODO Auto-generated method stub

    }

    public String createOrUpdateAssignment(String assignmentId, String title,
	    String instructions, int openYear, int openMonth, int openDay,
	    int openHour, int openMinute, int closeYear, int closeMonth,
	    int closeDay, int closeHour, int closeMinute,int dueYear, int dueMonth,
	    int dueDay, int dueHour, int dueMinute, int percentage) {
	// TODO Auto-generated method stub
	return null;
    }

    public String createOrUpdateCitation(String citationListId,
	    String citation, String author, String type, String isbnIssn,
	    String link) {
	// TODO Auto-generated method stub
	return null;
    }

    public String createTemporaryCitationList() {
	// TODO Auto-generated method stub
	return null;
    }

    public String getCurrentUserRole() {
	// TODO Auto-generated method stub
	return null;
    }

    public ResourcesLicencingInfo getResourceLicenceInfo() {
	// TODO Auto-generated method stub
	return null;
    }

    public COConfigSerialized getSerializedConfig() throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public COSerialized getSerializedCourseOutline(String id) throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public COSerialized getSerializedCourseOutline() throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String accessType) throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public String getXslForGroup(String group) {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean hasBeenPublished() throws Exception {
	// TODO Auto-generated method stub
	return false;
    }

    public void initTool() throws Exception {
	// TODO Auto-generated method stub

    }

    public void ping() {
	// TODO Auto-generated method stub

    }

    public void publishCourseOutline() throws Exception {
	// TODO Auto-generated method stub

    }

    public void removeAssignment(String assignmentId) {
	// TODO Auto-generated method stub

    }

    public void removeCitation(String citationId) {
	// TODO Auto-generated method stub

    }

    public String updateSerializedCourseOutline(COSerialized co)
	    throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean checkSitesRelation(String resourceURI) {
	// TODO Auto-generated method stub
	return false;
    }

    public String transformXmlForGroup(String xml, String group) {
	// TODO Auto-generated method stub
	return null;
    }

}
