package org.sakaiquebec.opensyllabus.admin.server;

import org.sakaiquebec.opensyllabus.admin.client.rpc.OsylAdminGwtService;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class OsylAdminGwtServiceImpl extends RemoteServiceServlet implements OsylAdminGwtService {

    public void createUsers(String fileDirectory){};
     
    public Set<String> getTemplateRoles() {return null;};
    public HashMap<String, List<String>> getTemplateFunctionsWithAllowedRoles(){return null;};
    
  
    public void updateTemplateFunction(String function, Set<String> allowedRoles){};
    
    public void updateTemplateFunctions(HashMap<String, Set<String>> functions){};
   
}
