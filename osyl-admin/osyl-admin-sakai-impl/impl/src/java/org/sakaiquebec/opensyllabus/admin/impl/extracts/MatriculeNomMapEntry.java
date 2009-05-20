
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Represente les donnees provenant de l'extract matricule_nom.dat<br/><br/>
 *
 * Chaque <code>MatriculeNomEntry</code> represente un utilisateur,
 * etudiant ou professeur.
 *
 */
public class MatriculeNomMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = -2529805025342512579l;

    private String matricule;
    private String lastName;
    private String firstName;
    private String status = "";
    private Vector<DetailCoursMapEntry> stagiaireCours;

    /**
     * Empty constructor.
     */
    MatriculeNomMapEntry() {
    }

    public String getMatricule(){
	return matricule;
    }

    public String getEmailAddress() {
	return getMatricule() + "@hec.ca";
    }

    public String getLastName(){
	return lastName;
    }

    public String getFirstName(){
	return firstName;
    }

    public String getFullName(){
	return getFirstName() + " " + getLastName();
    }

    public String getStatus(){
	return status;
    }

    public void setMatricule(String matricule){
	this.matricule = matricule;
    }

    public void setFirstName(String firstname){
	this.firstName = firstname;
    }

    public void setLastName(String lastname){
	this.lastName = lastname;
    }

    /**
     * Met le statut du MatriculeNomMapEntry a Professeur si la chaine de
     * caracteres passee en parametre est "P" ou a etudiant si la chaine est
     * "E".<br>
     *
     * Note: les droits associes au statut de professeur incluent ceux des
     * etudiants, donc si setStatus est invoque plusieurs fois, quel que soit
     * l'ordre, avec "P" et "E", alors le statut conserve est "P".
     *
     */
    void setStatus(String status){
	if (isProfesseur()) {
	    return;
	} else {
	    this.status = status;
	}
    }

    public boolean isProfesseur() {
	return getStatus().equals("P");
    }

    public boolean isEtudiant() {
	return getStatus().equals("E");
    }

    public boolean isStagiaire() {
	try {
	    return ! getStagiaireCoursVector().isEmpty();
	} catch (NullPointerException e) {
	    return false;
	}
    }

    private Vector<DetailCoursMapEntry> getStagiaireCoursVector() {
	return stagiaireCours;
    }

    private void initStagiaireCours() {
	stagiaireCours= new Vector<DetailCoursMapEntry>();
    }

    private void resetStagiaireCours() {
	stagiaireCours= null;
    }

    private boolean isStagiaireCoursInitialized() {
	return stagiaireCours != null;
    }

    public Iterator<DetailCoursMapEntry> getStagiaireCours() {
	try {
	    return stagiaireCours.iterator();
	} catch (NullPointerException e) {
	    return new Vector<DetailCoursMapEntry>().iterator();
	}
    }

    public int getStagiaireCoursCount() {
	return getStagiaireCoursVector().size();
    }

    public boolean containsStagiaireCours(DetailCoursMapEntry cours) {
	try {
	    return getStagiaireCoursVector().contains(cours);
	} catch (NullPointerException e) {
	    return false;
	}
    }

    public void addStagiaireCours(DetailCoursMapEntry cours) {
	if (! isStagiaireCoursInitialized()) {
	    initStagiaireCours();
	}

	if (! containsStagiaireCours(cours)) {
	    getStagiaireCoursVector().add(cours);
	}
    }

    public void removeStagiaireCours(DetailCoursMapEntry cours) {
	try {
	    getStagiaireCoursVector().remove(cours);
	} catch (NullPointerException e) {
	    // stagiaireCours est null... tout est ok.
	    return;
	}

	// Si l'utilisateur n'est plus stagiaire (ie: il n'y a plus aucun cours
	// dans stagiaireCours) alors on efface le vector (petit gain d'espace)
	if (! isStagiaire()) {
	    resetStagiaireCours();
	}
    } // removeStagiaireCours

    public void removeAllStagiaireCours(DetailSessionsMapEntry session) {
	// Si on n'est pas stagiaire, il n'y a rien a faire.
	if (! isStagiaire()) {
	    return;
	}

	Iterator<DetailCoursMapEntry> coursIterator = getStagiaireCours();
	DetailCoursMapEntry cours;
	while (coursIterator.hasNext()) {
	    cours = (DetailCoursMapEntry) coursIterator.next();
	    if (cours.isInSession(session)) {
		// Attention il ne faut pas faire
		// getStagiaireCoursVector().remove(cours) car ca cree une
		// java.util.ConcurrentModificationException.
		coursIterator.remove();
	    }
	}

	// Si l'utilisateur n'est plus stagiaire (ie: il n'y a plus aucun cours
	// dans stagiaireCours) alors on efface le vector (petit gain d'espace)
	if (! isStagiaire()) {
	    resetStagiaireCours();
	}
    } // removeAllStagiaireCours(session)

    public String toString() {
	return getMatricule() + " " + getFullName() + " " + getStatus();
    }
}
