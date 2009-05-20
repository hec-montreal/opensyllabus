
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Represente les donnees provenant de l'extract detail_cours.dat<br/><br/>
 *
 * Chaque <code>DetailCoursEntry</code> represente un cours.
 *
 */
public class DetailCoursMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = -5942666321016168578l;

    private String session;
    private String periode;
    private String programme;
    private String numeroHEL;
    private String numeroRepertoire;
    private String groupe;
    private String titre;
    private String titreAlt;
    private int    lang;
    private ProfCoursMapEntry coordonnateur;
    private Vector<ProfCoursMapEntry> professeurs;
    private Vector<String> etudiants;
    private Vector<MatriculeNomMapEntry> stagiaires;
    private Vector<ExamenMapEntry> examens;

    /**
     * Empty constructor.
     */
    public DetailCoursMapEntry() {
	professeurs = new Vector<ProfCoursMapEntry>();
	etudiants = new Vector<String>();
	stagiaires = new Vector<MatriculeNomMapEntry>();
	examens = new Vector<ExamenMapEntry>();
    }

    public String getSession(){
	return session;
    }

    public String getPeriode(){
	return periode;
    }

    public String getProgramme(){
	return programme;
    }

    /**
     * Retourne vrai si le cours est au MBA.<br><br>
     *
     * Ceci equivaut a tester que <code>getProgramme()</code> retourne "MBA".
     *
     * @return <code>true</code> si le cours est donne au MBA,
     *         <code>false</code> sinon.
     */
    public boolean isInMBA(){
	return "MBA".equals(getProgramme());
    }

    public String getNumeroHEL(){
	return numeroHEL;
    }

    public String getNumeroRepertoire(){
	return numeroRepertoire;
    }

    /**
     * Retourne vrai si le <code>DetailCoursMapEntry</code> specifie est du meme
     * cours (ie: les 2 ont le meme numero de repertoire).<br><br>
     *
     */
    public boolean isSameCours(DetailCoursMapEntry cours) {
	return cours.getNumeroRepertoire().equals(getNumeroRepertoire());
    }

    /**
     * @deprecated Utiliser getSection()
     */
    public String getGroupe(){
	return getSection();
    }


    public String getSection(){
	return groupe;
    }

    public ProfCoursMapEntry getCoordonnateur(){
	return coordonnateur;
    }

    public void setCoordonnateur(ProfCoursMapEntry coordonnateur){
	this.coordonnateur = coordonnateur;
    }

    public String getUniqueKey() {
	return getUniqueKey(getNumeroHEL(), getSession(), getPeriode());
    }

    public void setSession(String session){
	this.session = session;
    }

    public boolean isInSession(DetailSessionsMapEntry session) {
	return session.getNumero().equals(this.session);
    }

    public boolean isInSession(String s) {
	return s.equals(this.session);
    }

    public void setProgramme(String programme){
	this.programme = programme;
    }

    public void setPeriode(String periode){
	this.periode = periode;
    }

    public void setNumeroHEL(String numero){
	this.numeroHEL = numero;
    }

    public void setNumeroRepertoire(String numero){
	this.numeroRepertoire = numero;
    }


    /**
     * @deprecated Utiliser setSection()
     */
    public void setGroupe(String groupe){
	setSection(groupe);
    }

    public void setSection(String section){
	this.groupe = section;
    }


	/*

    public void setTitreFr(String titreFr){
		this.titreFr = titreFr;
    }

    public String getTitreFr(){
		return titreFr;
    }

    public void setTitreEn(String titreEn){
		this.titreEn = titreEn;
    }

    public String getTitreEn(){
		return titreEn;
    }

    public void setTitreSp(String titreSp){
		this.titreSp = titreSp;
    }

    public String getTitreSp(){
		return titreSp;
    }

	**
	 * Retourne le titre dans la langue du cours, s'il est d�fini, null sinon.
	 *
	public String getTitre() {
		return getTitre(getLangue());
	}

	**
	 * Retourne le titre dans la langue sp�cifi�e, s'il est d�fini, null sinon.
	 *
	public String getTitre(int l) {
		if (l == Constants.SPANISH) {
			return getTitreSp();
		} else if(l == Constants.ENGLISH) {
			return getTitreEn();
		} else if(l == Constants.FRENCH) {
			return getTitreFr();
		} else {
			return null;
		}
	}

	**
	 * Retourne le titre, si possible dans la langue du cours, sinon dans la
	 * langue sp�cifi�e.
	 *
	public String getTitreFallback(int defaultLang) {
		String titre = getTitre();
		if (titre == null) {
			return getTitre(defaultLang);
		} else {
			return titre;
		}
	}
	*/

    public void setTitre(String titre){
		this.titre = titre;
//		System.out.println("DetailCoursMapEntry setTitre: " + titre);
    }

    public String getTitre(){
		return titre;
    }

    public void setTitreAlt(String titreAlt){
		this.titreAlt = titreAlt;
//		System.out.println("DetailCoursMapEntry setTitreAlt: " + titreAlt);
    }

    public String getTitreAlt(){
		return titreAlt;
    }



    public void setLangue(String langStr){
		if (langStr == null || langStr.equals("") || langStr.matches("FRAN(�|.+)AIS")) {
			this.lang = Constants.FRENCH;
		} else if (langStr.equals("ANGLAIS")) {
			this.lang = Constants.ENGLISH;
		} else if (langStr.equals("ESPAGNOL")) {
			this.lang = Constants.SPANISH;
		} else {
			System.out.println("DetailCoursMapEntry [" + getNumeroHEL()
						+ "] setLangue: langue inconnue [" + langStr + "]");
			this.lang = Constants.FRENCH;
		}
    }

    public void setLangue(int lang){
		this.lang = lang;
    }

    public int getLangue(){
		return lang;
    }


    // ------ Gestion des professeurs ------
    //

    /**
     * Supprime tous les profs de ce cours.<br><br>
     *
     * Note: ne pas faire de modification a la liste des profs d'un cours
     * sans faire l'equivalent dans la liste des cours d'un prof!
     */
    public void removeAllProfs() {
	getProfesseursVector().removeAllElements();
    }

    /**
     * Ajoute un prof a ce cours.<br><br>
     *
     * Note: ne pas faire de modification a la liste des profs d'un cours
     * sans faire l'equivalent dans la liste des cours d'un prof!
     */
    public void addProfesseur(ProfCoursMapEntry prof){
	if (prof == null) {
	    throw new NullPointerException();
	} else {
	    getProfesseursVector().add(prof);
	}
    }

    private Vector<ProfCoursMapEntry> getProfesseursVector() {
	return professeurs;
    }

    public Iterator<ProfCoursMapEntry> getProfesseurs() {
	return getProfesseursVector().iterator();
    }

    public ProfCoursMapEntry getProfesseur(int i) {
	return (ProfCoursMapEntry) getProfesseursVector().get(i);
    }

    public boolean containsProfesseur(ProfCoursMapEntry prof) {
	return getProfesseursVector().contains(prof);
    }

    // ------ Gestion des stagiaires ------
    //
    public void addStagiaire(MatriculeNomMapEntry stagiaire){
	if (stagiaire == null) {
	    throw new NullPointerException();
	} else {
	    getStagiairesVector().add(stagiaire);
	}
    }

    private Vector<MatriculeNomMapEntry> getStagiairesVector() {
	return stagiaires;
    }

    public Iterator<MatriculeNomMapEntry> getStagiaires() {
	return getStagiairesVector().iterator();
    }

    public int getStagiaireCount() {
	return getStagiairesVector().size();
    }

    public MatriculeNomMapEntry getStagiaire(int i) {
	return (MatriculeNomMapEntry) getStagiairesVector().get(i);
    }

    public boolean containsStagiaire(MatriculeNomMapEntry stagiaire) {
	return getStagiairesVector().contains(stagiaire);
    }

    public void removeStagiaire(MatriculeNomMapEntry stagiaire) {
	getStagiairesVector().remove(stagiaire);
    }

    public void removeAllStagiaires() {
	getStagiairesVector().removeAllElements();
    }

    // ------ Gestion des etudiants ------
    //

    /**
     * Supprime tous les etudiants de ce cours.<br><br>
     *
     * Note: ne pas faire de modification a la liste des etudiants d'un cours
     * sans faire l'equivalent dans la liste des cours d'un etudiant!
     */
    public void removeAllEtudiants() {
	getEtudiantsVector().removeAllElements();
    }

    /**
     * Ajoute un etudiant a ce cours.<br><br>
     *
     * Note: ne pas faire de modification a la liste des etudiants d'un cours
     * sans faire l'equivalent dans la liste des cours d'un etudiant!
     */
    public void addEtudiant(EtudiantCoursMapEntry etudiant){
	getEtudiantsVector().add(etudiant.getMatricule());
    }

    /**
     * Retourne un iterateur des <b>matricules</b> de tous les etudiants de ce
     * cours.<br><br>
     */
    public Iterator<String> getEtudiants() {
	return getEtudiantsVector().iterator();
    }

    public int getEtudiantCount() {
	return getEtudiantsVector().size();
    }

    private Vector<String> getEtudiantsVector() {
	return etudiants;
    }

    public boolean containsEtudiant(EtudiantCoursMapEntry etudiant) {
	return getEtudiantsVector().contains(etudiant.getMatricule());
    }

    // ------ Gestion des examens ------
    //

    /**
     * Supprime tous les examens de ce cours.<br><br>
     *
     * Note: ne pas faire de modification a la liste des examens d'un cours
     * sans faire l'equivalent dans la liste des cours d'un examen!
     */
    public void removeAllExamens() {
		getExamensVector().removeAllElements();
    }

    /**
     * Ajoute un examen a ce cours.<br><br>
     *
     */
    public void addExamen(ExamenMapEntry examen){
		if (examen == null) {
			throw new NullPointerException();
		} else {
			getExamensVector().add(examen);
		}
    }

    private Vector<ExamenMapEntry> getExamensVector() {
		return examens;
    }

    public Iterator<ExamenMapEntry> getExamens() {
		return getExamensVector().iterator();
    }

    public ExamenMapEntry getExamen(int i) {
		return (ExamenMapEntry) getExamensVector().get(i);
    }

    public boolean containsExamen(ExamenMapEntry examen) {
		return getExamensVector().contains(examen);
    }


    public String toString() {
	return "Cours " + getNumeroRepertoire() + "_" + getSection()
	    + " donn&eacute; par " + getProfesseursVector()
	    + " au " + getProgramme()
	    + ("MBA".equals(getProgramme()) ? " (p&eacute;riode " + getPeriode() + ")" : "")
	    + " pendant la session " + getSession()
	    + (null == getCoordonnateur() ? "" : " coordonn&eacute; par " + getCoordonnateur());
    }

    public static String getUniqueKey(String numeroHEL, String session,
				      String periode) {
	return session + numeroHEL + periode;
    }

    /**
     * On redefinit <code>hashCode()</code> pour assurer que malgre la
     * serialisation 2 instances soit reconnues egales lorsque c'est approprie
     * (meme numero HEL et meme session).
     *
     * TODO: maintenant que le mode de serialisation est change, cette methode
     * ne devrait plus etre necessaire. Voir si on peut l'enlever.
     *
     * Quoique je me demande si ce n'est pas necessaire de la conserver pour
     * s'assurer que les methodes EtudiantCoursMapEntry.containsCours(...), par
     * exemple, fonctionne comme voulu...
     *
     */
    public int hashCode() {
	try {
	    // On ajoute le chiffre 1 devant au cas ou le numero de session
	    // commence par un ou plusieurs zeros un jour...
	    return Integer.parseInt("1" + session + numeroHEL);
	} catch (NumberFormatException e) {
	    System.out.println("DetailCoursMapEntry.hashCode: " + e);
	    return super.hashCode();
	}
    } // hashCode

    /**
     * On redefinit <code>equals(Object)</code> pour assurer que malgre la
     * serialisation 2 instances soit reconnues egales lorsque c'est approprie.
     *
     * TODO: maintenant que le mode de serialisation est change, cette methode
     * ne devrait plus etre necessaire. Voir si on peut l'enlever.
     *
     */
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	} else if (! o.getClass().getName().equals(
					"ca.hec.peoplesoft.DetailCoursMapEntry")) {
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
