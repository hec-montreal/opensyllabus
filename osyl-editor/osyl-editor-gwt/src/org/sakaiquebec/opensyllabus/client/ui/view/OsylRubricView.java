/*******************************************************************************
 * $Id: OsylRubricView.java 1414 2008-10-14 14:28:08Z remi.saias@hec.ca $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A wrapper for displaying a rubric and the resource proxies in this rubric.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: OsylRubricView.java 1414 2008-10-14 14:28:08Z remi.saias@hec.ca
 *          $
 */
public class OsylRubricView extends OsylViewableComposite {

    private Panel mainPanel;
    private Map<COModelInterface, OsylAbstractView> resProxViewMap;

    private String rubricStyleLevel;
    
    public void setRubricStyleLevel(String rubricStyleLevel) {
	this.rubricStyleLevel = rubricStyleLevel;
    }

    public String getRubricStyleLevel() {
	return rubricStyleLevel;
    }

//    public OsylRubricView(COContentRubric model, OsylController osylController) {
//	super(model, osylController);
//	initView();
//    }

    /**
     * Public constructor to display a rubric with the specified model.
     * 
     * @param model COContentRubric model
     * @param osylController
     */
    public OsylRubricView(COContentRubric model,
	    OsylController controller, String styleLevel) {
	super(model, controller);
	setRubricStyleLevel(styleLevel);
	initView();
   }

    private void initView() {
	resProxViewMap = new HashMap<COModelInterface, OsylAbstractView>();
	setMainPanel(new VerticalPanel());
	getMainPanel().setWidth("100%");

	// Rubric section display
	HorizontalPanel hPanel = new HorizontalPanel();
	Label l = new Label(getCoMessages().getMessage(getModel().getType()));
	// TODO adapt to the level...
	l.setStylePrimaryName("Osyl-UnitView-Title");
	l.addStyleName(getRubricStyleLevel());
	l.addStyleName("Osyl-RubricTitle");
	hPanel.add(l);
	getMainPanel().add(hPanel);

	initWidget(getMainPanel());
	refreshView();
    }

    public void refreshView() {
	checkVisibility();
    }

    /**
     * Add a ressource proxy and display it
     * 
     * @param resProx
     */
    public void addResProxView(COContentResourceProxy resProx) {

	OsylAbstractView oe = null;
	if(resProx.getResource() instanceof COContentResource){
	COContentResource resource = (COContentResource)resProx.getResource();
	if (resource.getType().equals(COContentResourceType.TEXT)) {
	    oe = new OsylResProxTextView(resProx, getController());
	} else if (resource.getType().equals(COContentResourceType.DOCUMENT)) {
	    oe = new OsylResProxDocumentView(resProx, getController());
	} else if (resource.getType().equals(COContentResourceType.URL)) {
	    oe = new OsylResProxLinkView(resProx, getController());
	} else if (resource.getType().equals(COContentResourceType.PERSON)) {
	    oe = new OsylResProxContactInfoView(resProx, getController());
	} else if (resource.getType().equals(COContentResourceType.ASSIGNMENT)) {
	    oe = new OsylResProxEvaluationView(resProx, getController());
	} else if (resource.getType().equals(
		COContentResourceType.BIBLIO_RESSOURCE)) {
	    oe = new OsylResProxCitationView(resProx, getController());
	}
	// else if (resource.getType().equals(
	// COContentResourceType.ASSIGNMENT)) {
	// oe = new OsylResProxAssignmentView(resProx, getController());
	// }
	else {
	    Window.alert("Internal error : addResProxView doesn't know how to "
		    + "handle resource of type " + resource.getType());
	    return;
	}

	addResProxView(oe);
	}
	else{
	    //TODO display COUnit as resource
	}

    }

    /**
     * Add a ressource proxy and display it
     * 
     * @param resProxView
     */
    public void addResProxView(OsylAbstractView resProxView) {
	resProxViewMap.put(resProxView.getModel(), resProxView);
	getMainPanel().add(resProxView);
	checkVisibility();
    }

    /**
     * Retrieves a ressource proxy view
     * 
     * @param contentResProx
     * @return {@link OsylAbstractView}
     */
    public OsylAbstractView findResProxView(
	    COContentResourceProxy contentResProx) {
	try {
	    return (OsylAbstractView) resProxViewMap.get(contentResProx);
	} catch (ClassCastException e) {
	    return null;
	}
    }

    /**
     * Removes a ressource proxy view
     * 
     * @param resProxView
     */
    public void removeResProxView(OsylAbstractView resProxView) {
	if (resProxView != null) {
	    getMainPanel().remove(resProxView);
	    resProxViewMap.remove(resProxView.getModel());
	}
	checkVisibility();
    }

    /**
     * Get the main panel
     */
    public Panel getMainPanel() {
	return mainPanel;
    }

    /**
     * Set the main panel
     * 
     * @param mainPanel
     */
    public void setMainPanel(Panel mainPanel) {
	this.mainPanel = mainPanel;
    }

    private void checkVisibility() {
	this.setVisible(false);

	// We make sure there is at least 1 view in the map
	if (resProxViewMap.size() > 0) {

	    // We check that we are in read-only mode
	    if (getController().isReadOnly()) {

		// We scroll the map and if we encounter a view that is not a
		// OsylAbstractResProx view, automatically the rubric is visible
		// if not, we scroll the map until we find a
		// OsylAbstractResProxView that is context hidden
		for (OsylAbstractView view : resProxViewMap.values()) {
		    if (!(view instanceof OsylAbstractResProxView)) {
			this.setVisible(true);
			break;
		    } else if (!((OsylAbstractResProxView) view)
			    .isContextHidden()) {
			this.setVisible(true);
			break;
		    }
		}
		// If we are in edition mode, we show the rubric if there is at
		// least
		// one view in the map no matter what the context is.
	    } else {
		this.setVisible(true);
	    }
	}
    }

}
