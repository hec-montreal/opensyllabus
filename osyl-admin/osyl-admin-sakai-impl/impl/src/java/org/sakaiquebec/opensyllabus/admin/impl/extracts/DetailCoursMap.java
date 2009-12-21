
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;


/**
 * Permet d'acceder aux donnees provenant de l'extract detail_cours.dat, chaque
 * ligne etant un cours represente par une
 * <code>DetailCoursMapEntry</code>.<br/><br/>
 *
 */
public class DetailCoursMap extends HashMap<String,DetailCoursMapEntry> {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = 5386630822650707643l;

    public void put(DetailCoursMapEntry entry) {
	put(entry.getUniqueKey(), entry);
    }

    public DetailCoursMapEntry get(String catalogNbr, String strmId,
				   String section) {
	return get(DetailCoursMapEntry.getUniqueKey(
                                           catalogNbr, strmId, section));
    }

    public DetailCoursMapEntry get(String key) {
	return (DetailCoursMapEntry) super.get(key);
    }

    public void remove(DetailCoursMapEntry entry) {
	remove(entry.getUniqueKey());
    }

    /**
     * Retourne les differents cours qui sont des groupe-cours du cours
     * specifie, <b>et coordonnes par le meme coordonnateur</b>.
     *
     */
    public Iterator<DetailCoursMapEntry> getAllGroupeCours(DetailCoursMapEntry cours) {
	return getAllGroupeCours(cours.getCatalogNbr(),
				 cours.getCoordonnateur());
    }

    /**
     * Retourne les differents cours qui sont des groupe-cours du cours dont le
     * numero de repertoire est specifie, <b>et coordonnes par le professeur
     * specifie</b>, independamment de la session.
     *
     */
    // TODO: verifier que le fait que ce soit independant de la session est ok
    // (remarque du 2005-08-05)
    public Iterator<DetailCoursMapEntry> getAllGroupeCours(String catalogNbr,
				      ProfCoursMapEntry coordonnateur) {
	Vector<DetailCoursMapEntry> v = new Vector<DetailCoursMapEntry>();

	// Si le coordonnateur est nul, on retourne un Iterator vide
	if (null == coordonnateur) {
	    return v.iterator();
	}

	Iterator<DetailCoursMapEntry> values = values().iterator();
	while (values.hasNext()) {
	    DetailCoursMapEntry cours = (DetailCoursMapEntry) values.next();
	    if (catalogNbr.equals(cours.getCatalogNbr())
		    && coordonnateur == cours.getCoordonnateur()) {
		v.add(cours);
	    }
	}

	return v.iterator();
    } // getAllGroupeCours

    /**
     * Retourne les differents groupe-cours du cours dont le numero de
     * repertoire est specifie donnes dans la session specifiee,
     * <b>independamment du ou des coordonnateurs</b>.
     *
     */
    public Iterator<DetailCoursMapEntry> getAllGroupeCours(String numeroRepertoire,
				      DetailSessionsMapEntry session) {
	Vector<DetailCoursMapEntry> v = new Vector<DetailCoursMapEntry>();

	Iterator<DetailCoursMapEntry> values = values().iterator();
	while (values.hasNext()) {
	    DetailCoursMapEntry cours = (DetailCoursMapEntry) values.next();
	    if (numeroRepertoire.equals(cours.getCatalogNbr())
		&& cours.isInSession(session)) {
		v.add(cours);
	    }
	}

	return v.iterator();
    } // getAllGroupeCours


    /**
     * Retourne les differents groupe-cours du cours dont le numero de
     * repertoire est specifie donnes dans la session specifiee,
     * <b>independamment du ou des coordonnateurs</b>.
     *
     */
    public Iterator<DetailCoursMapEntry> getAllGroupeCours(String numeroRepertoire, String session) {
	Vector<DetailCoursMapEntry> v = new Vector<DetailCoursMapEntry>();

	Iterator<DetailCoursMapEntry> values = values().iterator();
	while (values.hasNext()) {
	    DetailCoursMapEntry cours = (DetailCoursMapEntry) values.next();
	    if (numeroRepertoire.equals(cours.getCatalogNbr())
		&& cours.isInSession(session)) {
		v.add(cours);
	    }
	}

	return v.iterator();
    } // getAllGroupeCours

    /**
     * Retourne les differents cours enseignes a la session specifiee.</b>.
     *
     */
    public Iterator<DetailCoursMapEntry> getAllCours(DetailSessionsMapEntry session) {
	Vector<DetailCoursMapEntry> v = new Vector<DetailCoursMapEntry>();

	Iterator<DetailCoursMapEntry> values = values().iterator();
	while (values.hasNext()) {
	    DetailCoursMapEntry cours = (DetailCoursMapEntry) values.next();
	    if (cours.isInSession(session)) {
		v.add(cours);
	    }
	}

	return v.iterator();
    } // getAllGroupeCours

}
