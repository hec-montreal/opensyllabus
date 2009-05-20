
package org.sakaiquebec.opensyllabus.admin.impl.extracts;



/**
 * Represente les donnees provenant de l'extract detail_sessions.dat<br/><br/>
 *
 * Chaque <code>DetailSessionsEntry</code> represente une session.
 *
 */
public class DetailSessionsMapEntry implements java.io.Serializable {

    // Pour assurer la compatibilite des instances serialisees meme
    // quand on change la classe...
    public static final long serialVersionUID = 2768315680298436303l;

    private String numero;
    private String longForm;
    private String shortForm;
    private String startDate; // TODO : translate in date/long ?
    private String endDate;   //        -> voir comment ca va etre utilise

    /**
     * Empty constructor.
     */
    DetailSessionsMapEntry() {
	// empty constructor
    }

    public String getNumero(){
	return numero;
    }

    public String getLongForm(){
	return longForm;
    }

    public String getShortForm(){
	return shortForm;
    }

    public String getStartDate(){
	return startDate;
    }

    public String getEndDate(){
	return endDate;
    }

    public String getUniqueKey() {
	return getNumero();
    }

    public void setNumero(String numero){
	this.numero = numero;
    }

    public void setShortForm(String shortForm){
	this.shortForm = shortForm;
    }

    public void setLongForm(String longForm){
	this.longForm = longForm;
    }

    public void setStartDate(String startDate){
	this.startDate = startDate;
    }

    public void setEndDate(String endDate){
	this.endDate = endDate;
    }

    public String toString() {
	return "Session " + getLongForm() + " (" + getShortForm() + ", "
	    + getNumero() + ")";
    }

}
