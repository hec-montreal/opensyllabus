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
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The view dedicated to displaying a {@link COUnitContent}.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylCOContentUnitView extends OsylViewableComposite implements
	UpdateCOContentResourceProxyEventHandler,
	UpdateCOUnitContentEventHandler {

    private String propertyType =
	    getController().getOsylConfig().getOsylConfigRuler()
		    .getPropertyType();
    // View variables
    private VerticalPanel mainPanel;
    private Label mainTitleLabel;
    final static boolean TRACE = false;
    private boolean showLabel;

    private Map<String, OsylRubricView> rubricViewsMap;
    private Map<COContentResourceProxy, OsylRubricView> resProxMap;

    public OsylCOContentUnitView(COModelInterface model,
	    OsylController osylController) {
	this(model, osylController, false);
    }

    public OsylCOContentUnitView(COModelInterface model,
	    OsylController osylController, boolean showLabel) {
	super(model, osylController);
	this.showLabel = showLabel;
	initView();
    }

    private void initView() {
	setMainPanel(new VerticalPanel());
	initWidget(getMainPanel());
	refreshView();
    }

    private void initRubricViews() {
	rubricViewsMap = new HashMap<String, OsylRubricView>();

	List<COModelInterface> subModels =
		getController().getOsylConfig().getOsylConfigRuler()
			.getAllowedSubModels(getModel());

	for (int i = 0; i < subModels.size(); i++) {
	    Object subModel = subModels.get(i);

	    if (subModel instanceof COContentRubric) {
		COContentRubric coContentRubric = (COContentRubric) subModel;
		coContentRubric
			.setUserDefLabel(getUserDefLabelForRubricView(coContentRubric));
		OsylRubricView rubricView =
			new OsylRubricView(coContentRubric, getController(),
				OsylStyleLevelChooser.getLevelStyle(getModel()));
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

	final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
	getMainPanel().add(horizontalPanel_1);
	if (showLabel) {
	    setMainTitleLabel(new Label(getCoMessage(getModel().getType())));
	    // }
	    getMainTitleLabel().setStylePrimaryName("Osyl-UnitView-Title");
	    getMainTitleLabel().addStyleName(
		    OsylStyleLevelChooser.getLevelStyle(getModel()));
	    horizontalPanel_1.add(getMainTitleLabel());
	}

	initRubricViews();
	if (getModel() != null) {
	    // We cast our content unit
	    COUnitContent coContentUnit = (COUnitContent) getModel();
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
     * @return String the content unit position (for instance "2" if it is the
     *         second child of its parent)
     */
    public String getUnitPosition() {
	// some content unit don't have a position in the parent structure
	// element because they don't have a structure element parent :
	// For example: presentation
	String position = "";
	COUnitContent unit = (COUnitContent) getModel();
	COElementAbstract eltAbs = unit.getParent();
	if (eltAbs.isCOStructureElement()) {
	    position = unit.getPosition();
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
    public Label getMainTitleLabel() {
	return mainTitleLabel;
    }

    /**
     * Set the main title label
     * 
     * @param newMainTitleLabel
     */
    public void setMainTitleLabel(Label newMainTitleLabel) {
	this.mainTitleLabel = newMainTitleLabel;
    }

    /**
     * @see OsylViewable#setModel(COModelInterface)
     */
    public void setModel(COModelInterface model) {
	super.setModel(model);
	getModel().addEventHandler(this);
    }

    public COUnitContent getModel() {
	return (COUnitContent) super.getModel();
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
		    (OsylRubricView) rubricViewsMap.get(resProx
			    .getRubricType(propertyType));
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
	} else if (event.isRubricLabelUpdateEvent()) {
	    String userDefLabel = resProx.getRubricUserDefLabel(propertyType);
	    ((COContentRubric) rubricView.getModel())
		    .setUserDefLabel(userDefLabel);
	    rubricView.refreshView();
	    refreshResProxiesRubricLabel(rubricView, userDefLabel);
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
	COUnitContent coContentUnit = (COUnitContent) getModel();
	Iterator<COContentResourceProxy> iter =
		coContentUnit.getChildrens().iterator();
	while (iter.hasNext()) {
	    COContentResourceProxy resProx =
		    (COContentResourceProxy) iter.next();
	    // And we create the rubric views
	    if (resProx != null
		    && resProx.getRubricType(propertyType).equals(
			    rubricView.getModel().getType())) {
		OsylAbstractView resProxView =
			rubricView.findResProxView(resProx);
		rubricView.removeResProxView(resProxView);
		rubricView.addResProxView(resProxView);
	    }
	}
    }

    /**
     * Update the Resource Proxy models with latest rubric label.
     * 
     * @param rubricView
     */
    private void refreshResProxiesRubricLabel(OsylRubricView rubricView,
	    String userDefLabel) {
	COUnitContent coContentUnit = (COUnitContent) getModel();
	Iterator<COContentResourceProxy> iter =
		coContentUnit.getChildrens().iterator();
	while (iter.hasNext()) {
	    COContentResourceProxy resProx =
		    (COContentResourceProxy) iter.next();
	    // And we create the rubric views
	    if (resProx != null
		    && resProx.getRubricType(propertyType).equals(
			    rubricView.getModel().getType())) {
		resProx.getRubric(propertyType).setUserDefLabel(userDefLabel);
	    }
	}
    }

    /**
     * return the userDefLabel of a rubric found in a resource proxy model.
     * 
     * @return userDefLabel
     */
    private String getUserDefLabelForRubricView(COContentRubric coContentRubric) {
	String userDefLabel = null;
	COUnitContent coContentUnit = (COUnitContent) getModel();
	Iterator<COContentResourceProxy> iter =
		coContentUnit.getChildrens().iterator();
	while (iter.hasNext()) {
	    COContentResourceProxy resProx =
		    (COContentResourceProxy) iter.next();
	    // And we create the rubric views
	    if (resProx != null
		    && resProx.getRubricType(propertyType).equals(
			    coContentRubric.getType())) {
		userDefLabel = resProx.getRubricUserDefLabel(propertyType);
	    }
	}
	return userDefLabel;
    }

    public void onUpdateModel(UpdateCOUnitContentEvent event) {
	if (event.isAddRessProxEvent()) {
	    List<COContentResourceProxy> proxies =
		    ((COUnitContent) getModel()).getChildrens();
	    COContentResourceProxy resProx =
		    (COContentResourceProxy) proxies.get(proxies.size() - 1);
	    addResProxToRubricView(resProx);
	    if (resProx.getRubricType(propertyType).equals(
		    COContentRubric.RUBRIC_TYPE_NEWS)) {
		resProx.moveToTheTopOfTheRubric();
	    }
	    // OsylEditorEntryPoint.getInstance().setToolHeight(getOffsetHeight()
	    // + 100);
	}
    }

    private void addResProxToRubricView(COContentResourceProxy resProx) {
	OsylRubricView newDestRubricView =
		(OsylRubricView) rubricViewsMap.get(resProx
			.getRubricType(propertyType));
	if (newDestRubricView != null)
	    newDestRubricView.addResProxView(resProx);
	resProx.addEventHandler(this);
	// inverted Map for convenience
	resProxMap.put(resProx, newDestRubricView);
    }
}
