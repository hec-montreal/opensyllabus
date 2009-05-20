
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Represente les donnees provenant de l'extract etudiant_cours.dat<br/><br/>
 *
 * Chaque <code>EtudiantCoursEntry</code> correspond a un etudiant et
 * permet d'acceder aux cours qu'il suit.
 *
 */
public class EtudiantCoursMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = 8092564845500650222L;

    private String matricule;
    private Vector<DetailCoursMapEntry> cours;

    /**
     * Empty constructor.
     */
    EtudiantCoursMapEntry(String matricule) {
	this.matricule = matricule;
	cours = new Vector<DetailCoursMapEntry>();
	// empty constructor
    }

    public String getMatricule(){
	return matricule;
    }

    public Iterator<DetailCoursMapEntry> getCours() {
	return getCoursVector().iterator();
    }

    Vector<DetailCoursMapEntry> getCoursVector() {
	return cours;
    }

    public DetailCoursMapEntry getCours(int i) {
	return (DetailCoursMapEntry) getCoursVector().get(i);
    }

    public int getCoursCount() {
	return getCoursVector().size();
    }

    /**
     * Note: ne pas faire de modification a la liste des cours d'un etudiant
     * sans faire l'equivalent dans la liste des etudiants d'un cours!
     */
    public void addCours(DetailCoursMapEntry cours) {
	if (! containsCours(cours)) {
	    getCoursVector().add(cours);
	}
    }

    public boolean containsCours(DetailCoursMapEntry cours) {
	return getCoursVector().contains(cours);
    }

    /**
     * Supprime tous les cours de cet etudiant, donnes dans la session
     * specifiee.<br><br>
     *
     * Note: ne pas faire de modification a la liste des cours d'un etudiant
     * sans faire l'equivalent dans la liste des etudiants d'un cours!
     */
    public void removeAllCours(DetailSessionsMapEntry session) {
	Iterator<DetailCoursMapEntry> coursIterator = getCours();
	DetailCoursMapEntry cours;
	while (coursIterator.hasNext()) {
	    cours = (DetailCoursMapEntry) coursIterator.next();
	    if (cours.isInSession(session)) {
		// Attention il ne faut pas faire getCoursVector().remove(cours)
		// car ca cree une java.util.ConcurrentModificationException.
		coursIterator.remove();
	    }
	}
    } // removeAllCours

//    private void removeCours(DetailCoursMapEntry cours) {
//	getCoursVector().remove(cours);
//    }

}
