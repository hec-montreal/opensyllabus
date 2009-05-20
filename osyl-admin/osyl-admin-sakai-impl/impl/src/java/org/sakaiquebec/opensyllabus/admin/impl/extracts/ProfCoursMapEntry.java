
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Represente les donnees provenant de l'extract prof_cours.dat<br/><br/>
 *
 * Chaque <code>ProfCoursEntry</code> correspond a un professeur et
 * permet d'acceder aux cours qu'il donne.
 *
 */
public class ProfCoursMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = -913935703184167796L;

    private Vector<DetailCoursMapEntry> cours;
    private Map<String,DetailCoursMapEntry> coursCoordonnes;
    private String matricule;

    /**
     * One arg Constructor.
     */
    ProfCoursMapEntry(String matricule) {
	this.matricule = matricule;
	cours = new Vector<DetailCoursMapEntry>();
	coursCoordonnes = new HashMap<String,DetailCoursMapEntry>();
    }

    public String getMatricule() {
	return matricule;
    }

    public String getEmailAddress() {
	return getMatricule() + "@hec.ca";
    }


    // ============= Gestion des cours enseignes =============
    //
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
     * Note: ne pas faire de modification a la liste des cours d'un prof sans
     * faire l'equivalent dans la liste des profs d'un cours!
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
     * Supprime tous les cours de ce prof, donnes ou coordonnes dans la session
     * specifiee.<br><br>
     *
     * Note: ne pas faire de modification a la liste des cours d'un prof sans
     * faire l'equivalent dans la liste des profs d'un cours!
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
	// TODO: ce code est rigoureusement identique a celui du dessus et a
	// celui utilise dans EtudiantCoursMapEntry... il faudrait le partager!
	coursIterator = getCoursCoordonnes();
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

    public boolean containsAnyGroupeCoursOf(DetailCoursMapEntry cours) {
	Iterator<DetailCoursMapEntry> iterator = getCours();
	while (iterator.hasNext()) {
	    if (cours.isSameCours((DetailCoursMapEntry) iterator.next())) {
		return true;
	    }
	}
	return false;
    } // containsAnyGroupeCoursOf

    // ==== Gestion des cours uniquement coordonnes (ie: non enseignes!) =====
    //
    public Iterator<DetailCoursMapEntry> getCoursCoordonnes() {
	return getCoursCoordonnesMap().values().iterator();
    }

    private Map<String,DetailCoursMapEntry> getCoursCoordonnesMap() {
	return coursCoordonnes;
    }

    Collection<DetailCoursMapEntry> getCoursCoordonnesCollection() {
	return coursCoordonnes.values();
    }

    public int getCoursCoordonnesCount() {
	return getCoursCoordonnesMap().size();
    }


    public void addCoursCoordonne(DetailCoursMapEntry cours) {
	getCoursCoordonnesMap().put(cours.getSession()
				    + cours.getNumeroRepertoire(), cours);
    } // addCoursCoordonne

    public boolean containsCoursCoordonne(DetailCoursMapEntry cours) {
	return getCoursCoordonnesMap().containsKey(
			     cours.getSession() + cours.getNumeroRepertoire());
    }


    public String toString() {
	return "Professeur " + getMatricule() + " coordonne: " + getCoursCoordonnesCollection().size() + " cours : " + getCoursCoordonnesMap().keySet();
    }

    /**
     * On redefinit <code>hashCode()</code> pour assurer que malgre la
     * serialisation 2 instances soit reconnues egales lorsque c'est approprie
     * (meme matricule).
     *
     */
    public int hashCode() {
	try {
	    // On ajoute le chiffre 1 devant car certains matricules commencent
	    // par un ou plusieurs zeros.
	    return Integer.parseInt("1" + getMatricule());
	} catch (NumberFormatException e) {
	    System.out.println("ProfCoursMapEntry.hashCode: " + e);
	    return super.hashCode();
	}
    } // hashCode

    /**
     * On redefinit <code>equals(Object)</code> pour assurer que malgre la
     * serialisation 2 instances soit reconnues egales lorsque c'est approprie.
     *
     */
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	} else if (! o.getClass().getName().equals(
					"ca.hec.peoplesoft.ProfCoursMapEntry")) {
	    return false;
	} else {
	    if (o.hashCode() == hashCode()) {
		return true;
	    } else {
		return false;
	    }
	}
    } // equals


}
