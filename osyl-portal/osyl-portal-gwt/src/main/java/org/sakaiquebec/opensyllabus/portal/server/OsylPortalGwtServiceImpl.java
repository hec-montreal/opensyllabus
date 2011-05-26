package org.sakaiquebec.opensyllabus.portal.server;

import java.util.List;

import org.sakaiquebec.opensyllabus.portal.client.rpc.OsylPortalGwtService;
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

    public List<String> getAllResponsibles() {
	return null;
    }

}
