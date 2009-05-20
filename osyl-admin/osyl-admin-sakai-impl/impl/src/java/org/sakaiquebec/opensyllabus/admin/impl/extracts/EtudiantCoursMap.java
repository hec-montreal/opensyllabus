
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Permet d'acceder aux donnees provenant de l'extract etudiant_cours.dat,
 * chaque etudiant (potentiellement sur plusieurs lignes) etant une
 * <code>EtudiantCoursMapEntry</code>.<br/><br/>
 *
 */
public class EtudiantCoursMap extends HashMap<String,EtudiantCoursMapEntry> {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = 6442577651300871765L;

    public EtudiantCoursMapEntry get(String matricule) {
	return (EtudiantCoursMapEntry) super.get(matricule);
    }

    /**
     * Retourne un <code>Iterator</code> permettant de parcourir tous les
     * etudiants.
     *
     * @return <code>Iterator</code> pour tous les etudiants
     */
    public Iterator<EtudiantCoursMapEntry> getEtudiants() {
	return values().iterator();
    }

    public void put(EtudiantCoursMapEntry etudiant) {
	put(etudiant.getMatricule(), etudiant);
    }

    public boolean contains(MatriculeNomMapEntry matricule) {
	return containsKey(matricule.getMatricule());
    }

}
