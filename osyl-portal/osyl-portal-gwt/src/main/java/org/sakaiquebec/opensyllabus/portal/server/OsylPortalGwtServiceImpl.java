package org.sakaiquebec.opensyllabus.portal.server;

import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.portal.client.rpc.OsylPortalGwtService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class OsylPortalGwtServiceImpl extends RemoteServiceServlet implements
	OsylPortalGwtService {

    public Map<String, String> getCoursesForAcadCareer(String acadCareer) {
	return null;
    }

    public Map<String, String> getCoursesForResponsible(String responsible) {
	return null;
    }

    public List<String> getAllResponsibles() {
	return null;
    }

}
