
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Permet d'acceder aux donnees provenant de l'extract detail_sessions.dat,
 * chaque ligne etant une <code>DetailSessionsMapEntry</code>.<br/><br/>
 *
 */
public class DetailSessionsMap extends HashMap<String,DetailSessionsMapEntry> {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    static final long serialVersionUID = 1489168447867453239L;

    public DetailSessionsMapEntry get(String numero) {
	return (DetailSessionsMapEntry) super.get(numero);
    }

    public DetailSessionsMapEntry get(int numero) {
	return (DetailSessionsMapEntry) super.get("" + numero);
    }

    public void put(DetailSessionsMapEntry entry) {
	super.put(entry.getUniqueKey(), entry);
    }

    public void remove(DetailSessionsMapEntry entry) {
	remove(entry.getUniqueKey());
    }

    /**
     * Retourne la session ayant le numero de session le plus eleve.
     *
     */
    public DetailSessionsMapEntry getLatestSession() {
	int sessionNo = 0;
	DetailSessionsMapEntry session;
	Iterator<DetailSessionsMapEntry> values = values().iterator();

	while (values.hasNext()) {
	    session = (DetailSessionsMapEntry) values.next();
	    try {
		sessionNo = Math.max(Integer.parseInt(session.getAcadCareer()),
				     sessionNo);
	    } catch (NumberFormatException e) {
		System.out.println("DetailSessionsMapEntry.getLatestSession "
				   + "NumberFormatException: " + session);
	    }
	}

	// Avertissement: encapsulation peu rigoureuse: on profite du fait que
	// le numero de session qui sert a determiner la derniere session est
	// aussi la cle...
	return get("" + sessionNo);

    } // getLatestSession
}
