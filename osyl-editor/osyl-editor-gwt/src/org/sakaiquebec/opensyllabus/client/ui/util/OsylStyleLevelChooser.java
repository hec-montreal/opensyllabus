package org.sakaiquebec.opensyllabus.client.ui.util;

import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

public class OsylStyleLevelChooser {

    /**
     * The <code>CourseOutlineContent</code> class type.
     */
    protected final static String CO_CONTENT_CLASS_TYPE = "CourseOutlineContent";

    /**
     * The <code>COStructureElement</code> class type.
     */
    protected final String CO_STRUCTURE_ELEMENT_CLASS_TYPE = "COStructureElement";

    /**
     * The <code>COContentUnit</code> class type.
     */
    protected final String CO_CONTENT_UNIT_CLASS_TYPE = "COContentUnit";

    public static boolean getHasANumber( COUnitContent model ) {
	COUnitContent unit = model;
	COElementAbstract eltAbs = unit.getParent();
	if (eltAbs.isCOStructureElement()) {
	    COStructureElement parent = (COStructureElement) unit.getParent();
	    String parentTypeString = parent.getType();
	    if ( (parent.getChildrens().size() > 1) &&
		    ( ! parentTypeString.endsWith("Header")))
	    {
		return true;
	    }
	}
	return false;   
    }
    
    //TODO Change this.
    public static boolean getHasANumber( COUnit model ) {
	return true;
    }

    public static String getLevelStyle( COUnitContent model ) {
	if (model.isCourseOutlineContent()) {
	    return "Osyl-Title1";
	}
	if (model.getParent().isCourseOutlineContent()) {
	    return "Osyl-Title2";
	}
	if ( model.getParent().isCOStructureElement() ) {
	    COStructureElement parent = (COStructureElement) model.getParent();
	    if ( parent.getParent().isCourseOutlineContent()){
		return "Osyl-Title3";
	    }
	}
	if ( model.getParent().isCOStructureElement() ) {
	    COStructureElement parent1 = (COStructureElement) model.getParent();
	    if (parent1.getParent().isCOStructureElement()) {
		COStructureElement parent2 = (COStructureElement) parent1.getParent();
		if ( parent2.getParent().isCourseOutlineContent()){
		    return "Osyl-Title4";
		}
	    }
	}
	return "Osyl-Title5";
    }


    public static String getLevelStyle(COStructureElement structureElement) {
	if (structureElement.isCourseOutlineContent()) {
	    return "Osyl-Title1";
	}
	if (structureElement.getParent().isCourseOutlineContent()) {
	    return "Osyl-Title2";
	}
	if ( structureElement.getParent().isCOStructureElement() ) {
	    COStructureElement parent = (COStructureElement) structureElement.getParent();
	    if ( parent.getParent().isCourseOutlineContent()){
		return "Osyl-Title3";
	    }
	}
	if ( structureElement.getParent().isCOStructureElement() ) {
	    COStructureElement parent1 = (COStructureElement) structureElement.getParent();
	    if (parent1.getParent().isCOStructureElement()) {
		COStructureElement parent2 = (COStructureElement) parent1.getParent();
		if ( parent2.getParent().isCourseOutlineContent()){
		    return "Osyl-Title4";
		}
	    }
	}
	return "Osyl-Title5";
    }

    public static String getSubLevelStyle(COUnitContent model) {
	if (model.isCourseOutlineContent()) {
	    return "Osyl-Title2";
	}
	if (model.getParent().isCourseOutlineContent()) {
	    return "Osyl-Title3";
	}
	if ( model.getParent().isCOStructureElement() ) {
	    COStructureElement parent = (COStructureElement) model.getParent();
	    if ( parent.getParent().isCourseOutlineContent()){
		return "Osyl-Title4";
	    }
	}
	if ( model.getParent().isCOStructureElement() ) {
	    COStructureElement parent1 = (COStructureElement) model.getParent();
	    if (parent1.getParent().isCOStructureElement()) {
		COStructureElement parent2 = (COStructureElement) parent1.getParent();
		if ( parent2.getParent().isCourseOutlineContent()){
		    return "Osyl-Title5";
		}
	    }
	}
	return "Osyl-Title5";
    }
    
    //TODO: We have to change this method.
    public static String getLevelStyle(COModelInterface coModelInterface){
	return "Osyl-Title3";
    }

}
