
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Permet d'acceder aux donnees provenant de l'extract service_enseignement.dat, chaque
 * ligne etant un service d'enseignement represente par une
 * <code>ServiceEnseignementMapEntry</code>.<br/><br/>
 *
 */
public class ServiceEnseignementMap extends HashMap<String,ServiceEnseignementMapEntry> {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = 5386630822650707643l;

    public void put(ServiceEnseignementMapEntry entry) {
	put(entry.getAcadOrg(), entry);
    }

    public ServiceEnseignementMapEntry get(String key) {
	return (ServiceEnseignementMapEntry) super.get(key);
    }

    public void remove(ServiceEnseignementMapEntry entry) {
	remove(entry.getAcadOrg());
    }

 
    /**
     * Retourne les differents service d'enseignement de l'institution.</b>.
     *
     */
    public Iterator<ServiceEnseignementMapEntry> getAllServices() {
	
	return values().iterator();
    } 

}
