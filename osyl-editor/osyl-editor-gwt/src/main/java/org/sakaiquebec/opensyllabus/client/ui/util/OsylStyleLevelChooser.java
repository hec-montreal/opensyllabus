package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

public class OsylStyleLevelChooser {

    private static final String OSYL_TITLE = "Osyl-Title";

    public static boolean getHasANumber(COUnit model) {
	COUnit unit = model;
	COElementAbstract eltAbs = unit.getParent();
	if (eltAbs.isCOStructureElement()) {
	    COStructureElement parent = (COStructureElement) unit.getParent();
	    String parentTypeString = parent.getType();
	    if ((parent.getChildrens().size() > 1)
		    && (!parentTypeString.endsWith("Header"))) {
		return true;
	    }
	}
	return false;
    }

    public static String getLevelStyle(COElementAbstract model) {
	OsylController controller = OsylController.getInstance();
	int i = 1;
	while (!model.isCourseOutlineContent()) {
	    model = model.getParent();
	    if (model.isCOStructureElement() || model.isCOUnit() || model.isCOStructureElement()) {
		List<COModelInterface> subModels =
			controller.getOsylConfig().getOsylConfigRuler()
				.getAllowedSubModels(model);
		if (subModels.isEmpty() && model.getChildrens().size() < 2)
		    i--;
	    }
	    i++;
	}
	if (i > 5)
	    i = 5;
	return OSYL_TITLE + i;
    }
}
