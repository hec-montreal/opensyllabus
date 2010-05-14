package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represente les donnees provenant de l'extract detail_cours.dat<br/>
 * <br/>
 * Chaque <code>DetailCoursEntry</code> represente un cours.
 */
public class DetailCoursMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = -5942666321016168578l;

    private String strm;
    private String sessionCode;
    private String acadOrg;
    private String courseId;
    private String catalogNbr;
    private String classSection;
    private String courseTitleLong;
    private String strmId;
    private String lang;
    private String acadCareer;
    private ProfCoursMapEntry coordonnateur;
    private Vector<ProfCoursMapEntry> professeurs;
    private Vector<String> etudiants;
    private Vector<MatriculeNomMapEntry> stagiaires;
    private Vector<ExamenMapEntry> examens;
    

    private static Log log = LogFactory.getLog(DetailCoursMapEntry.class);

    /**
     * Empty constructor.
     */
    public DetailCoursMapEntry() {
	professeurs = new Vector<ProfCoursMapEntry>();
	etudiants = new Vector<String>();
	stagiaires = new Vector<MatriculeNomMapEntry>();
	examens = new Vector<ExamenMapEntry>();
    }

    public String getStrm() {
	return strm;
    }

    public String getSessionCode() {
	return sessionCode;
    }

    public String getAcadOrg() {
	return acadOrg;
    }


    public String getAcadCareer() {
	return acadCareer;
    }
    
    /**
     * Retourne vrai si le cours est au MBA.<br>
     * <br>
     * Ceci equivaut a tester que <code>getProgramme()</code> retourne "MBA".
     * 
     * @return <code>true</code> si le cours est donne au MBA,
     *         <code>false</code> sinon.
     */
    public boolean isInMBA() {
	return "MBA".equals(getAcadOrg());
    }

    public String getCourseId() {
	return courseId;
    }

    public String getCatalogNbr() {
	return catalogNbr;
    }

    /**
     * Retourne vrai si le <code>DetailCoursMapEntry</code> specifie est du meme
     * cours (ie: les 2 ont le meme numero de repertoire).<br>
     * <br>
     */
    public boolean isSameCours(DetailCoursMapEntry cours) {
	return cours.getCatalogNbr().equals(getCatalogNbr());
    }

    public String getClassSection() {
	return classSection;
    }

    public ProfCoursMapEntry getCoordonnateur() {
	return coordonnateur;
    }

    public void setCoordonnateur(ProfCoursMapEntry coordonnateur) {
	this.coordonnateur = coordonnateur;
    }

    public String getUniqueKey() {
	return getUniqueKey(getCatalogNbr(), getStrmId(), getClassSection());
    }

    public void setStrm(String strm) {
	this.strm = strm;
    }

    public boolean isInSession(DetailSessionsMapEntry strm) {
	return strm.getStrm().equals(this.strm);
    }

    public boolean isInSession(String s) {
	return s.equals(this.strm);
    }

    public void setAcadOrg(String acadOrg) {
	this.acadOrg = acadOrg;
    }

    public void setAcaCareer(String acadCareer) {
	this.acadCareer = acadCareer;
    }

    public void setSessionCode(String sessionCode) {
	this.sessionCode = sessionCode;
    }

    public void setCourseId(String courseId) {
	this.courseId = courseId;
    }

    public void setCatalogNbr(String catalogNbr) {
	this.catalogNbr = catalogNbr;
    }

    public void setSection(String classSection) {
	this.classSection = classSection;
    }

    /*
     * public void setTitreFr(String titreFr){ this.titreFr = titreFr; } public
     * String getTitreFr(){ return titreFr; } public void setTitreEn(String
     * titreEn){ this.titreEn = titreEn; } public String getTitreEn(){ return
     * titreEn; } public void setTitreSp(String titreSp){ this.titreSp =
     * titreSp; } public String getTitreSp(){ return titreSp; } Retourne le
     * courseTitleLong dans la langue du cours, s'il est d�fini, null sinon.
     * public String getTitre() { return getTitre(getLangue()); } Retourne le
     * courseTitleLong dans la langue sp�cifi�e, s'il est d�fini, null
     * sinon. public String getTitre(int l) { if (l == Constants.SPANISH) {
     * return getTitreSp(); } else if(l == Constants.ENGLISH) { return
     * getTitreEn(); } else if(l == Constants.FRENCH) { return getTitreFr(); }
     * else { return null; } } Retourne le courseTitleLong, si possible dans la
     * langue du cours, sinon dans la langue sp�cifi�e. public String
     * getTitreFallback(int defaultLang) { String courseTitleLong = getTitre();
     * if (courseTitleLong == null) { return getTitre(defaultLang); } else {
     * return courseTitleLong; } }
     */

    public void setCourseTitleLong(String courseTitle) {
	this.courseTitleLong = courseTitle;
    }

    public String getCourseTitleLong() {
	return courseTitleLong;
    }

    public String getStrmId() {
	return strmId;
    }

    public void setStrmId(String strmId) {
	this.strmId = strmId;
    }

    public void setLangue(String langStr) {
	if (langStr == null || langStr.equals("")
		|| langStr.matches("FRAN(�|.+)AIS")) {
	    this.lang = Constants.FRENCH;
	} else if (langStr.equals("ANGLAIS")) {
	    this.lang = Constants.ENGLISH;
	} else if (langStr.equals("ESPAGNOL")) {
	    this.lang = Constants.SPANISH;
	} else {
	    log.info("DetailCoursMapEntry [" + getCourseId()
		    + "] setLangue: langue inconnue [" + langStr + "]");
	    this.lang = Constants.FRENCH;
	}
    }


    public String getLangue() {
	return lang;
    }

    // ------ Gestion des professeurs ------
    //

    /**
     * Supprime tous les profs de ce cours.<br>
     * <br>
     * Note: ne pas faire de modification a la liste des profs d'un cours sans
     * faire l'equivalent dans la liste des cours d'un prof!
     */
    public void removeAllProfs() {
	getProfesseursVector().removeAllElements();
    }

    /**
     * Ajoute un prof a ce cours.<br>
     * <br>
     * Note: ne pas faire de modification a la liste des profs d'un cours sans
     * faire l'equivalent dans la liste des cours d'un prof!
     */
    public void addProfesseur(ProfCoursMapEntry prof) {
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
    public void addStagiaire(MatriculeNomMapEntry stagiaire) {
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
     * Supprime tous les etudiants de ce cours.<br>
     * <br>
     * Note: ne pas faire de modification a la liste des etudiants d'un cours
     * sans faire l'equivalent dans la liste des cours d'un etudiant!
     */
    public void removeAllEtudiants() {
	getEtudiantsVector().removeAllElements();
    }

    /**
     * Ajoute un etudiant a ce cours.<br>
     * <br>
     * Note: ne pas faire de modification a la liste des etudiants d'un cours
     * sans faire l'equivalent dans la liste des cours d'un etudiant!
     */
    public void addEtudiant(EtudiantCoursMapEntry etudiant) {
	getEtudiantsVector().add(etudiant.getMatricule());
    }

    /**
     * Retourne un iterateur des <b>matricules</b> de tous les etudiants de ce
     * cours.<br>
     * <br>
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
     * Supprime tous les examens de ce cours.<br>
     * <br>
     * Note: ne pas faire de modification a la liste des examens d'un cours sans
     * faire l'equivalent dans la liste des cours d'un examen!
     */
    public void removeAllExamens() {
	getExamensVector().removeAllElements();
    }

    /**
     * Ajoute un examen a ce cours.<br>
     * <br>
     */
    public void addExamen(ExamenMapEntry examen) {
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
	return "Course "
		+ getCatalogNbr()
		+ "_"
		+ getClassSection()
		+ " profs: "
		+ getProfesseursVector()
		+ " trim: "
		+ getStrm()
		+ (null == getCoordonnateur() ? "" : " coord: "
			+ getCoordonnateur());
    }

    public static String getUniqueKey(String catalogNbr, String strmId,
	    String section) {
	return strmId + catalogNbr + section;
    }

    /**
     * On redefinit <code>hashCode()</code> pour assurer que malgre la
     * serialisation 2 instances soit reconnues egales lorsque c'est approprie
     * (meme numero HEL et meme session). TODO: maintenant que le mode de
     * serialisation est change, cette methode ne devrait plus etre necessaire.
     * Voir si on peut l'enlever. Quoique je me demande si ce n'est pas
     * necessaire de la conserver pour s'assurer que les methodes
     * EtudiantCoursMapEntry.containsCours(...), par exemple, fonctionne comme
     * voulu...
     */
    public int hashCode() {
	try {
	    // On ajoute le chiffre 1 devant au cas ou le numero de session
	    // commence par un ou plusieurs zeros un jour...
	    return Integer.parseInt("1" + strm + courseId);
	} catch (NumberFormatException e) {
	    log.info("DetailCoursMapEntry.hashCode: " + e);
	    return super.hashCode();
	}
    } // hashCode

    /**
     * On redefinit <code>equals(Object)</code> pour assurer que malgre la
     * serialisation 2 instances soit reconnues egales lorsque c'est approprie.
     * TODO: maintenant que le mode de serialisation est change, cette methode
     * ne devrait plus etre necessaire. Voir si on peut l'enlever.
     */
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	} else if (!o.getClass().getName().equals(
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
