
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Represente les donnees provenant de l'extract examens.dat<br/><br/>
 *
 * Chaque <code>ExamenEntry</code> represente un examen-repartition.
 * C'est-�-dire qu'un examen qui est s�par� en 2 salles pour le m�me
 * groupe-cours aura 2 ExamenEntry diff�rents. Le champ repartition indique les
 * noms des �tudiants concern�s. Les donn�es sur le cours ne sont pas contenues
 * ici car l'ExamenEntry est ajout� directement au DetailCoursMapEntry.
 *
 */
public class ExamenMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = 2591692265806260051l;

    private static Log log = LogFactory.getLog(ExamenMapEntry.class);

	private DetailCoursMapEntry cours;
    private long beginTime, endTime;
    private String salle;
    private String edifice;
    private String repartition;
    private String type;

    /**
     * Constructeur.
	 *
	 * Exemple de param�tres re�us:
	 * 2006-06-01, 18:30, 20:30, Xerox Canada, �difice C�te-Sainte-Catherine, HALJ � Z, INTR
	 *
     */
    public ExamenMapEntry(DetailCoursMapEntry cours, String date, String hrDebut,
						  String hrFin, String salle, String edifice,
						  String repartition, String type) {

		setCours(cours);
		setDate(date, hrDebut, hrFin);

		this.salle = salle;
		this.edifice = edifice;
		this.repartition = repartition;
		this.type = type;
    } // Constructeur

	private void setDate(String date, String hrDebut, String hrFin) {
		try {
			int year  = Integer.parseInt(date.substring(0,4));
			int month = Integer.parseInt(date.substring(5,7)) - 1;
			int day   = Integer.parseInt(date.substring(8,10));

			int hr    = Integer.parseInt(hrDebut.substring(0,2));
			int min   = Integer.parseInt(hrDebut.substring(3,5));
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day, hr, min, 0);
			this.beginTime = calendar.getTimeInMillis();

			hr  = Integer.parseInt(hrFin.substring(0,2));
			min = Integer.parseInt(hrFin.substring(3,5));
			calendar.set(year, month, day, hr, min, 0);
			this.endTime = calendar.getTimeInMillis();
		} catch (Exception e) {
			log.info("Impossible d'initialiser la date: " + date + " " + hrDebut + "-" + hrFin);
		}

	} // setDate

	private void setCours(DetailCoursMapEntry cours) {
		this.cours = cours;
	}


	public DetailCoursMapEntry getCours() {
		return this.cours;
	}

	public long getBeginTimeMillis() {
		return this.beginTime;
	}

	public long getEndTimeMillis() {
		return this.endTime;
	}

	public void setBeginTimeMillis(long beginTime) {
		this.beginTime = beginTime;
	}

	public void setEndTimeMillis(long endTime) {
		this.endTime = endTime;
	}

	public String getRepartition() {
		return this.repartition;
	}

	public String getSalle() {
		return this.salle;
	}

	public String getEdifice() {
		return this.edifice;
	}

	public String getType() {
		if ("FIN".equals(type)) {
			return "examenFinal";
		} else if ("INTR".equals(type)) {
			return "examenIntra";
		} else if ("TEST".equals(type)) {
			return "examenTest";
		} else {
			log.info("Examen de type inconnu: " + type + " (cours "
							   + getCours().getUniqueKey() + ")");
			return "examenInconnu";
		}
	} // getType


    public String toString() {
		return "Examen de " + new Date(beginTime) + " a " + new Date(endTime)
			+ " dans la salle " + salle + ", " + edifice + " pour " + repartition
			+ " (" + type + ")";
    }
}
