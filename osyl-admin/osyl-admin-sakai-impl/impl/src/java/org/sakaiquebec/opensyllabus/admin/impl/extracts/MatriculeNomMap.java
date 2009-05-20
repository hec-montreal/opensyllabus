
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Permet d'acceder aux donnees provenant de l'extract matricule_nom.dat,
 * chaque ligne etant une <code>MatriculeNomEntry</code>.<br/><br/>
 *
 */
public class MatriculeNomMap extends HashMap<String,MatriculeNomMapEntry> {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = -258155462459805983L;

    public MatriculeNomMapEntry get(String matricule) {
	return (MatriculeNomMapEntry) super.get(matricule);
    }

    public void put(MatriculeNomMapEntry value) {
	put(value.getMatricule(), value);
    }

}
