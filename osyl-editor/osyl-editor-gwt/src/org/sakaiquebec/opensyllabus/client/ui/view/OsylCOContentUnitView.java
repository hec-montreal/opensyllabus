/*******************************************************************************
 * $Id: $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewable;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylRichTextEditor;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnitType;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The view dedicated to displaying a {@link COContentUnit}.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylCOContentUnitView extends OsylViewableComposite implements
	UpdateCOContentResourceProxyEventHandler,
	UpdateCOContentUnitEventHandler {

    // View variables
    private VerticalPanel mainPanel;
    private Label unitTitleLabel;
    private Label unitSemanticTag;
    
    final static boolean TRACE = false;

    private Map<String, OsylRubricView> rubricViewsMap;
    private Map<COContentResourceProxy, OsylRubricView> resProxMap;

    public OsylCOContentUnitView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	initView();
    }

    private void initView() {
	setMainPanel(new VerticalPanel());
	initWidget(getMainPanel());
	refreshView();
    }

    private void initRubricViews(String styleLevel) {
	rubricViewsMap = new HashMap<String, OsylRubricView>();

	List<COModelInterface> subModels =
	    getController().getOsylConfig().getOsylConfigRuler()
	    .getAllowedSubModels(getModel());

	for (int i = 0; i < subModels.size(); i++) {
	    Object subModel = subModels.get(i);

	    if (subModel instanceof COContentRubric) {
		COContentRubric coContentRubric = (COContentRubric) subModel;
		OsylRubricView rubricView =
		    new OsylRubricView(coContentRubric, getController(), styleLevel);
		getMainPanel().add(rubricView);
		rubricViewsMap.put(coContentRubric.getType(), rubricView);
	    }
	}
    }

    /**
     * View display using Model
     */
    public void refreshView() {
	getMainPanel().setStylePrimaryName("Osyl-UnitView-MainPanel");
	getMainPanel().setWidth("98%");

	if (TRACE) Window.alert("*** Model = " + getCoMessage(getModel().getType()) + 
		" ***\n*** Label = " + getModel().getLabel() + " ***");

	// If we are editing a lecture or theme we allow to edit the title
	// otherwise we don't (presentation, contact info, etc.)
	if (COContentUnitType.LECTURE.equals(getModel().getType())
		|| COContentUnitType.THEME.equals(getModel().getType())
	) {
	    // do not allow to delete the title and therefore the lecture
	    // within the COContentUnit (only at COStructure)
	    OsylCOStructureItemLabelView lbv =
		new OsylCOStructureItemLabelView(getModel(),
			getController(), false, OsylStyleLevelChooser.getLevelStyle(getModel()));
	    getMainPanel().add(lbv);
	}
	else if (COContentUnitType.EVALUATION.equals(getModel().getType())) {
	    // do not allow to delete the title and therefore the evaluation
	    // within the COContentUnit (only at COStructure)
	    OsylCOStructureItemLabelView lbv =
		new OsylCOStructureItemLabelView(getModel(),
			getController(), false, OsylStyleLevelChooser.getLevelStyle(getModel()));

	    getMainPanel().add(lbv);
	} 
	else {
	    if (isCOUnitTitleEditable()) {
		OsylCOStructureItemLabelView lbv =
		    new OsylCOStructureItemLabelView(getModel(),
			    getController(), false, OsylStyleLevelChooser.getLevelStyle(getModel()));
		getMainPanel().add(lbv);
	    }
	    else {
		Label lbv = new Label(getCoMessage(getModel().getType()));
		lbv.setStylePrimaryName("Osyl-UnitView-Title");
		lbv.addStyleName(OsylStyleLevelChooser.getLevelStyle(getModel()));  
		getMainPanel().add(lbv);
	    }
	}

	// Semantic Tag linked to COUnit 
	if ( OsylStyleLevelChooser.getHasANumber((COContentUnit) getModel()) ) {
	    String positionString = getUnitPosition();
	    if ( positionString.length() < 2 ) {
		positionString = "0" + positionString; 
	    }
	    setUnitSemanticTag(new Label(getCoMessage(getModel().getType())  
		    + " "
		    + positionString));
	} 
	else {
	    setUnitSemanticTag(new Label(getCoMessage(getModel().getType())));	
	}

	getUnitSemanticTag().setStylePrimaryName("Osyl-UnitView-SemanticTag");

	// If the COUnitTitle is not editable, it is equal to its semantic tag
	// and there is no need to add the tag since it is already there
	if ( isCOUnitTitleEditable()) {
	    getMainPanel().add(getUnitSemanticTag());
	}

	initRubricViews(OsylStyleLevelChooser.getSubLevelStyle(getModel()));
	if (getModel() != null) {
	    // We cast our content unit
	    COContentUnit coContentUnit = (COContentUnit) getModel();
	    // We iterate through the resources proxies to create a rubric map
	    // and the corresponding views
	    resProxMap = new HashMap<COContentResourceProxy, OsylRubricView>();
	    Iterator<COContentResourceProxy> iter =
		coContentUnit.getChildrens().iterator();
	    while (iter.hasNext()) {
		COContentResourceProxy resProx =
		    (COContentResourceProxy) iter.next();
		// And we create the rubric views
		if (resProx != null)
		    addResProxToRubricView(resProx);
	    }
	}
    }

    /**
     * @return Boolean if the COUnit title is editable or not
     */
    	public boolean isCOUnitTitleEditable() {
    	    String proposedXMLTitle = getModel().getLabel();
    	    String modelCoUnitSemTag = getCoMessage(getModel().getType());
    	     // If the proposed label in the XML is null or 
    	     // empty then the label is not editable
    	     // Also if the proposed label is identical to the model 
    	     // semantic Tag, so the label is invariable and not editable
   	    if (  ( proposedXMLTitle.length() <= 0) || 
    		  ( proposedXMLTitle.equals(modelCoUnitSemTag) )  
    	        ){
    		return false;
    	    }
    	    return true;    
	}

    /**
     * @return String the content unit position (for instance "2" if it is the
     *         second child of its parent)
     */
    public String getUnitPosition() {
	// some content unit don't have a position in the parent structure
	// element because they don't have a structure element parent :
	// For example: presentation
	String position = "";
	COContentUnit unit = (COContentUnit) getModel();
	COElementAbstract eltAbs = unit.getParent();
	if (eltAbs.isCOStructureElement()) {
	    COStructureElement parent = (COStructureElement) unit.getParent();
	    position = parent.getChildPosition(unit);
	}
	return position;
    }

    /**
     * Get the main panel
     */
    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    /**
     * Set the main panel
     * 
     * @param mainPanel
     */
    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    /**
     * Get the main title label
     */
    public Label getUnitTitleLabel() {
	return unitTitleLabel;
    }

    /**
     * Set the main title label
     * 
     * @param newMainTitleLabel
     */
    public void setUnitTitleLabel(Label newUnitTitleLabel) {
	this.unitTitleLabel = newUnitTitleLabel;
    }

    public Label getUnitSemanticTag() {
        return unitSemanticTag;
    }

    public void setUnitSemanticTag(Label newUnitSemanticTag) {
        this.unitSemanticTag = newUnitSemanticTag;
    }

    /**
     * @see OsylViewable#setModel(COModelInterface)
     */
    public void setModel(COModelInterface model) {
	super.setModel(model);
	getModel().addEventHandler(this);
    }

    public COContentUnit getModel() {
	return (COContentUnit) super.getModel();
    }

    public void onUpdateModel(UpdateCOContentResourceProxyEvent event) {
	COContentResourceProxy resProx =
	    (COContentResourceProxy) event.getSource();
	OsylRubricView rubricView = (OsylRubricView) resProxMap.get(resProx);
	// retrieve the resproxview
	OsylAbstractView resProxView = rubricView.findResProxView(resProx);

	// Here we are using specific event ids to avoid refresh of the whole
	// view.
	if (event.isRubricUpdateEvent()) {

	    OsylRubricView newDestRubricView =
		(OsylRubricView) rubricViewsMap
		.get(resProx.getRubricType());
	    // remove old resprox view from the rubricView
	    rubricView.removeResProxView(resProxView);
	    // move it to the new dest rubricview
	    newDestRubricView.addResProxView(resProxView);
	    // update a reference of the move:
	    resProxMap.put(resProx, newDestRubricView);
	    refreshResProxUpAndDownArrowsInSameRubric(newDestRubricView);
	} else if (event.isDeleteEvent()) {
	    // remove the reference
	    resProxMap.remove(resProx);
	    // remove old resprox view from the rubricView
	    rubricView.removeResProxView(resProxView);
	} else if (event.isMoveInRubricEvent()) {
	    refreshRubric(rubricView);
	}
	refreshResProxUpAndDownArrowsInSameRubric(rubricView);
    }

    /**
     * update up and down arrows
     * 
     * @param rubricView
     */
    private void refreshResProxUpAndDownArrowsInSameRubric(
	    OsylRubricView rubricView) {
	for (Iterator<COContentResourceProxy> respIt =
	    resProxMap.keySet().iterator(); respIt.hasNext();) {
	    COContentResourceProxy coCRProxy = respIt.next();
	    OsylAbstractView rPView = rubricView.findResProxView(coCRProxy);
	    if (rPView != null) {
		((OsylAbstractResProxView) rPView).getEditor()
		.refreshUpAndDownPanel();
	    }
	}
    }

    /**
     * Update the rubric content. Make the resProx to be add in order of the
     * model
     * 
     * @param rubricView
     */
    private void refreshRubric(OsylRubricView rubricView) {
	COContentUnit coContentUnit = (COContentUnit) getModel();
	Iterator<COContentResourceProxy> iter =
	    coContentUnit.getChildrens().iterator();
	while (iter.hasNext()) {
	    COContentResourceProxy resProx =
		(COContentResourceProxy) iter.next();
	    // And we create the rubric views
	    if (resProx != null
		    && resProx.getRubricType().equals(
			    rubricView.getModel().getType())) {
		OsylAbstractView resProxView =
		    rubricView.findResProxView(resProx);
		rubricView.removeResProxView(resProxView);
		rubricView.addResProxView(resProxView);
	    }
	}
    }

    public void onUpdateModel(UpdateCOContentUnitEvent event) {
	if (event.isAddRessProxEvent()) {
	    // the last resProxy is the added one
	    List<COContentResourceProxy> proxies =
		((COContentUnit) getModel()).getChildrens();
	    COContentResourceProxy resProx =
		(COContentResourceProxy) proxies.get(proxies.size() - 1);
	    addResProxToRubricView(resProx);
	}
    }

    private void addResProxToRubricView(COContentResourceProxy resProx) {
	OsylRubricView newDestRubricView =
	    (OsylRubricView) rubricViewsMap.get(resProx.getRubricType());
	if (newDestRubricView != null)
	    newDestRubricView.addResProxView(resProx);
	resProx.addEventHandler(this);
	// inverted Map for convenience
	resProxMap.put(resProx, newDestRubricView);
    }
}
