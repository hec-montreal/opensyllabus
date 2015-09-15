
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Permet d'acceder aux donnees provenant de l'extract prof_cours3.dat,
 * chaque professeur (potentiellement sur plusieurs lignes) etant une
 * <code>ProfCoursMapEntry</code>.<br/><br/>
 *
 */
public class ProfCoursMap extends HashMap<String,ProfCoursMapEntry> {

//    // Pour assurer la compatibilite des instances serialisees meme
//    // quand on change la classe...
//    public static final long serialVersionUID = -505521782889381739L;
//
//    public ProfCoursMapEntry get(String matricule) {
//	return (ProfCoursMapEntry) super.get(matricule);
//    }
//
//    public void put(ProfCoursMapEntry prof) {
//	put(prof.getEmplId(),prof);
//    }
//
//    public boolean contains(MatriculeNomMapEntry matricule) {
//	return containsKey(matricule.getMatricule());
//    }
}
