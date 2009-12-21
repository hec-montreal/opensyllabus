package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;

/**
 * Represente les donnees provenant de l'extract detail_cours.dat<br/>
 * <br/>
 * Chaque <code>DetailCoursEntry</code> represente un cours.
 */
public class ServiceEnseignementMapEntry implements java.io.Serializable {

	// Pour assurer la compatibilite des instances serialisees meme
	// quand on change la classe...
	public static final long serialVersionUID = -5942666321016168578l;

	private String acadOrg;
	private String descFormal;
	private String deptId;

	/**
	 * Empty constructor.
	 */
	public ServiceEnseignementMapEntry() {
	}

	public String getAcadOrg() {
		return acadOrg;
	}


	public void setAcadOrg(String acadOrg) {
		this.acadOrg = acadOrg;
	}


	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}



	/**
	 * @return the descFormal
	 */
	public String getDescFormal() {
		return descFormal;
	}

	/**
	 * @param descFormal the descFormal to set
	 */
	public void setDescFormal(String descFormal) {
		this.descFormal = descFormal;
	}

	/**
	 * On redefinit <code>hashCode()</code> pour assurer que malgre la
	 * serialisation 2 instances soit reconnues egales lorsque c'est approprie
	 * (meme nom service et meme deptId). 
	 */
//	public int hashCode() {
//		try {
//			// On ajoute le chiffre 1 devant au cas ou le numero de session
//			// commence par un ou plusieurs zeros un jour...
//			return Integer.parseInt("1" + acadOrg + deptId);
//		} catch (NumberFormatException e) {
//			System.out.println("ServiceEnseignementMapEntry.hashCode: " + e);
//			return super.hashCode();
//		}
//	} // hashCode

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
				"ca.hec.peoplesoft.ServiceEnseignementMapEntry")) {
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
