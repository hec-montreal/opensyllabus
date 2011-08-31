package org.sakaiquebec.opensyllabus.portal.server;

import java.util.List;

import org.sakaiquebec.opensyllabus.portal.client.rpc.OsylPortalGwtService;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class OsylPortalGwtServiceImpl extends RemoteServiceServlet implements
	OsylPortalGwtService {

    public List<CODirectorySite> getCoursesForAcadCareer(String acadCareer) {
	return null;
    }

    public List<CODirectorySite> getCoursesForResponsible(String responsible) {
	return null;
    }

    public List<CODirectorySite> getCoursesForFields(String courseNumber,
	    String courseTitle, String instructor, String program,
	    String responsible, String trimester) {
	return null;
    }

    public String getDescription(String siteId) {
	return null;
    }

    public CODirectorySite getCODirectorySite(String courseNumber) {
	return null;
    }
    
    public String getAccessForSiteAndCurrentUser(String siteId) {
	return null;
    }
    
}
