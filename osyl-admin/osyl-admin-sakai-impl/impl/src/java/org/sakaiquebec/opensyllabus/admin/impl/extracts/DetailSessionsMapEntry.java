
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

    private String acadCareer;
    private String strm;
    private String descrFrancais;
    private String descAnglais; 
    private String descShortFrancais;
    private String descShortAnglais;
    private String beginDate;
    private String endDate;
    private String sessionCode;
    private String strmId;
    
    
    //        -> voir comment ca va etre utilise
 // TODO : translate in date/long ?
    /**
     * Empty constructor.
     */
    DetailSessionsMapEntry() {
	// empty constructor
    }

    public String getAcadCareer(){
	return acadCareer;
    }

    public String getStrm(){
	return strm;
    }

    public String getDescFrancais(){
	return descrFrancais;
    }

    public String getDescAnglais(){
	return descAnglais;
    }

    public String getDescShortFrancais(){
	return descShortFrancais;
    }

    public String getUniqueKey() {
	return getStrmId();
    }

    public void setAcadCareer(String acadCareer){
	this.acadCareer = acadCareer;
    }

    public void setDescFrancais(String descFrancais){
	this.descrFrancais = descFrancais;
    }

    public void setStrm(String strm){
	this.strm = strm;
    }

    public void setDescAnglais(String descAnglais){
	this.descAnglais = descAnglais;
    }

    public String getDescrFrancais() {
        return descrFrancais;
    }

    public void setDescrFrancais(String descrFrancais) {
        this.descrFrancais = descrFrancais;
    }

    public String getDescShortAnglais() {
        return descShortAnglais;
    }

    public void setDescShortAnglais(String descShortAnglais) {
        this.descShortAnglais = descShortAnglais;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getStrmId() {
        return strmId;
    }

    public void setStrmId(String strmId) {
        this.strmId = strmId;
    }

    public void setDescShortFrancais(String descShortFrancais){
	this.descShortFrancais = descShortFrancais;
    }

    public String toString() {
	return "Session " + getStrm() + " (" + getDescFrancais() + ", "
	    + getAcadCareer() + ")";
    }

}
