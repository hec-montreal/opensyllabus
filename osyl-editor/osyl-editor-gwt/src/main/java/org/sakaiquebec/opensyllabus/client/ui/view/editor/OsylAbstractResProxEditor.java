/******************************************************************************
 * $Id$
 ******************************************************************************
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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylPushButton;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylDeleteClickListener;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractResProxView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class defining common methods for Resource Proxy editors.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public abstract class OsylAbstractResProxEditor extends OsylAbstractEditor {

    // TODO: document
    private CheckBox importantCheckBox;
    private CheckBox hideCheckBox;
    private CheckBox modifyRubricDesc;
    private ListBox diffusionListBox;
    private ListBox rubricListBox;
    private ListBox requirementListBox;
    private TextBox userDefLabel;
    private boolean hasRequirement;
    private boolean rubricMoveable;
    private boolean hasImportant;
    private boolean moveableInRubric;
    private boolean hasHide;
    

    /**
     * Constructor.
     * 
     * @param view
     */
    public OsylAbstractResProxEditor(OsylAbstractView view) {
	super(view);
	hasRequirement = true;
	rubricMoveable = true;
	hasImportant = true;
	moveableInRubric = true;
	hasHide = true;
    }

    protected PushButton createPushButtonDelete() {
	Image img = getOsylImageBundle().delete().createImage();
	String title = getUiMessage("delete");
	ClickHandler listener =
		new OsylDeleteClickListener((OsylAbstractResProxView) getView());
	return createPushButton(img, title, listener);
    }

    protected ImageAndTextButton createButtonDelete() {
	AbstractImagePrototype imgDeleteButton = getOsylImageBundle().delete();
	String title = getUiMessage("delete");
	ClickHandler listener =
		new OsylDeleteClickListener((OsylAbstractResProxView) getView());
	return createButton(imgDeleteButton, title, listener);
    }

    protected OsylPushButton createButtonUp() {
	OsylPushButton upButton;
	if (getView().getModel().hasPredecessor()) {
	    upButton =
		    new OsylPushButton(getOsylImageBundle().up_full()
			    .createImage(), getOsylImageBundle().up_full()
			    .createImage(), getOsylImageBundle().up_full()
			    .createImage());
	    upButton.setTitle(getUiMessage("UpButton.title"));
	    upButton.setEnabledButton();
	    upButton.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    getView().leaveEdit();
		    getView().getModel().moveUp();
		}

	    });
	} else {
	    upButton =
		    new OsylPushButton(getOsylImageBundle().up_empty()
			    .createImage(), getOsylImageBundle().up_empty()
			    .createImage(), getOsylImageBundle().up_empty()
			    .createImage());
	    upButton.setDisabledButton();
	}
	upButton.setVisible(true);
	return upButton;
    }

    protected OsylPushButton createButtonDown() {
	OsylPushButton downButton;
	if (getView().getModel().hasSuccessor()) {
	    downButton =
		    new OsylPushButton(getOsylImageBundle().down_full()
			    .createImage(), getOsylImageBundle().down_full()
			    .createImage(), getOsylImageBundle().down_full()
			    .createImage());
	    downButton.setTitle(getUiMessage("DownButton.title"));
	    downButton.setEnabledButton();
	    downButton.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    getView().leaveEdit();
		    getView().getModel().moveDown();
		}

	    });
	} else {
	    downButton =
		    new OsylPushButton(getOsylImageBundle().down_empty()
			    .createImage(), getOsylImageBundle().down_empty()
			    .createImage(), getOsylImageBundle().down_empty()
			    .createImage());
	    downButton.setDisabledButton();
	}
	return downButton;
    }

    public boolean isRubricMoveable() {
	return rubricMoveable;
    }

    public void setRubricMoveable(boolean rubricMoveable) {
	this.rubricMoveable = rubricMoveable;
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    /**
     * {@inheritDoc}
     */
    protected OsylAbstractResProxView getView() {
	return (OsylAbstractResProxView) super.getView();
    }

    /**
     * {@inheritDoc}
     */
    protected void addViewerStdButtons() {
	super.addViewerStdButtons();
	ImageAndTextButton pbDelete = createButtonDelete();
	getView().getButtonPanel().add(pbDelete);

	refreshUpAndDownPanel();
    }

    @Override
    public Widget[] getOptionWidgets() {
	ArrayList<Widget> widgetList = new ArrayList<Widget>();
	if (isRubricMoveable()) {
	    VerticalPanel rubricPanel = new VerticalPanel();
	    rubricPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	    // configGroups.add(rubricPanel);
	    rubricPanel.add(new Label(
		    getUiMessage("EditorPopUp.options.rubric")));
	    rubricListBox = generateRubricList();
	    rubricListBox.setWidth("150px");
	    rubricPanel.add(rubricListBox);
	    rubricListBox
		    .setTitle(getUiMessage("EditorPopUp.options.rubric.choose.title"));
	    rubricListBox.addChangeHandler(new ChangeHandler() {

		public void onChange(ChangeEvent event) {
		    refreshTargetCoAbsractElementListBox(targetsListBox);
		    if(rubricListBox.getSelectedIndex() > 0){
		    	modifyRubricDesc.setEnabled(true);
		    	modifyRubricDesc.setValue(false);
		    	userDefLabel.setText("");
		    	userDefLabel.setEnabled(false);	    	
			    fillRubricUserDefLabel();
		    }else{
		    	modifyRubricDesc.setEnabled(false);
		    	modifyRubricDesc.setValue(false);
		    	userDefLabel.setText("");
		    	userDefLabel.setEnabled(false);	    	
		    }
		}
	    });
	    modifyRubricDesc = new CheckBox(getUiMessage("EditorPopUp.options.rubric.check"));
	    boolean userDefLabelSet = false;
	    if(getView().getModel().getRubric().getUserDefLabel() != null && 
	    		getView().getModel().getRubric().getUserDefLabel().length() > 0){
	    	userDefLabelSet = true;
	    }
    	modifyRubricDesc.setEnabled(rubricListBox.getSelectedIndex() > 0);
    	modifyRubricDesc.setValue(userDefLabelSet);
	    modifyRubricDesc.addClickHandler(new ClickHandler(){
	    	public void onClick(ClickEvent clickEvent){
	    		boolean checked = ((CheckBox) clickEvent.getSource()).getValue();
	    		if(checked){
	    			userDefLabel.setEnabled(true);
	    			userDefLabel.setFocus(true);
	    		}else{
	    			userDefLabel.setEnabled(false);
	    			userDefLabel.setText("");	    			
	    		}
	    		
	    	}
	    });
	    //TODO set checkbox to true if a value is found in new attribute userdeftype
	    //modifyRubricDesc.setValue(getView().isContextHidden());
	    userDefLabel = new TextBox();
	    userDefLabel.setEnabled(userDefLabelSet);
	    if(userDefLabelSet){
	    	userDefLabel.setText(getView().getModel().getRubric().getUserDefLabel());
	    }
        rubricPanel.add(modifyRubricDesc);
        rubricPanel.add(userDefLabel);
	    if(!(getView().getSettings().isRubricDescEditable())){
	    	modifyRubricDesc.setVisible(false);
	    	userDefLabel.setVisible(false);
	    }
	    widgetList.add(rubricPanel);
	}
	// Diffusion Level ListBox
	VerticalPanel diffusionPanel = new VerticalPanel();
	diffusionPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	// configGroups.add(diffusionPanel);
	diffusionPanel.add(new Label(getUiMessage("MetaInfo.audience")));
	diffusionListBox = new ListBox();
	diffusionListBox.setWidth("100px");
	diffusionListBox.setTitle(getUiMessage("MetaInfo.audience.title"));
	diffusionListBox.addItem(getUiMessage("MetaInfo.audience.attendee"),
		SecurityInterface.ACCESS_ATTENDEE);
	diffusionListBox.addItem(getUiMessage("MetaInfo.audience.public"),
		SecurityInterface.ACCESS_PUBLIC);
	diffusionPanel.add(diffusionListBox);
	String level = getView().getDiffusionLevel();
	int levelIndex = 1;
	if (SecurityInterface.ACCESS_ATTENDEE.equals(level)) {
	    levelIndex = 0;
	} else if (SecurityInterface.ACCESS_PUBLIC.equals(level)) {
	    levelIndex = 1;
	}
	diffusionListBox.setSelectedIndex(levelIndex);
	widgetList.add(diffusionPanel);

	// Other options
	boolean hasOption = false;
	HorizontalPanel options = new HorizontalPanel();
	if (hasHide) {
	    // Hide CheckBox
	    hideCheckBox = new CheckBox(getUiMessage("MetaInfo.hidden"));
	    hideCheckBox.setValue(getView().isContextHidden());
	    hideCheckBox.setTitle(getUiMessage("MetaInfo.hidden.title"));
	    options.add(hideCheckBox);
	    hasOption = true;
	}

	// "Important" CheckBox
	if (hasImportant) {
	    importantCheckBox =
		    new CheckBox(getView().getCoMessage("MetaInfo.important"));
	    importantCheckBox.setValue(getView().isContextImportant());
	    importantCheckBox
		    .setTitle(getUiMessage("MetaInfo.important.title"));
	    options.add(importantCheckBox);
	    hasOption = true;
	}
	// Other options, specified by each subclass
	Widget[] optionWidgets = getAdditionalOptionWidgets();
	if (null != optionWidgets) {
	    hasOption = true;
	    for (int i = 0; i < optionWidgets.length; i++) {
		options.add(optionWidgets[i]);
	    }
	}
	VerticalPanel optionPanel = null;
	if (hasOption) {
	    optionPanel = new VerticalPanel();
	    optionPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	    // configGroups.add(optionPanel);
	    optionPanel.add(new Label(getUiMessage("MetaInfo.title") + ": "));
	    optionPanel.add(options);
	    widgetList.add(optionPanel);
	}

	if (hasRequirement) {
	    // Requirement level ListBox
	    VerticalPanel requirementPanel = new VerticalPanel();
	    requirementPanel
		    .setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	    requirementPanel.add(new Label(getView().getUiMessage(
		    "MetaInfo.requirement")));
	    requirementListBox = new ListBox();
	    requirementListBox.setWidth("100px");
	    requirementListBox
		    .setTitle(getUiMessage("MetaInfo.requirement.title"));
	    requirementListBox.addItem("",
		    COPropertiesType.REQ_LEVEL_UNDEFINED);
	    requirementListBox.addItem(getView().
		    getCoMessage("MetaInfo.requirement.mandatory"),
		    COPropertiesType.REQ_LEVEL_MANDATORY);
	    requirementListBox.addItem(getView().
		    getCoMessage("MetaInfo.requirement.recommended"),
		    COPropertiesType.REQ_LEVEL_RECOMMENDED);
	    requirementListBox.addItem(getView().
		    getCoMessage("MetaInfo.requirement.complementary"),
		    COPropertiesType.REQ_LEVEL_COMPLEMENTARY);
	    requirementPanel.add(requirementListBox);

	    String reqLevel = getView().getRequirementLevel();
	    int reqLevelIndex = 0;
	    if (COPropertiesType.REQ_LEVEL_MANDATORY.equals(reqLevel)) {
		reqLevelIndex = 1;
	    } else if (COPropertiesType.REQ_LEVEL_RECOMMENDED.equals(reqLevel)) {
		reqLevelIndex = 2;
	    } else if (COPropertiesType.REQ_LEVEL_COMPLEMENTARY
		    .equals(reqLevel)) {
		reqLevelIndex = 3;
	    } else {
		reqLevelIndex = 0;
	    }
	    requirementListBox.setSelectedIndex(reqLevelIndex);
	    widgetList.add(requirementPanel);
	} else {
	    if (optionPanel != null)
		optionPanel
			.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	}
	return (Widget[]) widgetList.toArray(new Widget[widgetList.size()]);
    }

    public Widget getBrowserWidget() {
	return null;
    }

    public Widget getInformationWidget() {
	return new HTML(OsylAbstractEditor.MANDATORY_FIELD_INDICATOR
		+ getUiMessage("Global.fields.mandatory"));
    }

    /**
     * ==================== ABSTRACT METHODS =====================
     */

    protected abstract Widget[] getAdditionalOptionWidgets();

    /**
     * ====================== ADDED METHODS =======================
     */

    /**
     * Used to refresh up and down arrows
     */
    public void refreshUpAndDownPanel() {
	getView().getUpAndDownPanel().clear();
	if (isMoveableInRubric()) {
	    OsylPushButton upButton = createButtonUp();
	    OsylPushButton downButton = createButtonDown();
	    getView().getUpAndDownPanel().add(upButton);
	    getView().getUpAndDownPanel().add(downButton);
	}
    }

    /**
     * Returns whether the checkBox "important" is checked.
     * 
     * @return boolean
     */
    public boolean isContextImportant() {
	return importantCheckBox.getValue();
    }

    /**
     * Returns whether the checkBox "hidden" is checked.
     * 
     * @return boolean
     */
    public boolean isContextHidden() {
	return hideCheckBox.getValue();
    }

    protected String getUiMessage(String key) {
	return getView().getUiMessage(key);
    }

    /**
     * Returns the selected requirement level (Mandatory, Recommended or
     * Complementary)
     * 
     * @return String
     */
    public String getRequirementLevel() {
	return requirementListBox.getValue(requirementListBox
		.getSelectedIndex());
    }

    protected Image getCurrentRequirementLevelIcon() {
	Image img = null;
	String reqLevel = getView().getRequirementLevel();
	if (COPropertiesType.REQ_LEVEL_MANDATORY.equals(reqLevel)) {
	    img = getOsylImageBundle().iconeObl().createImage();
	} else if (COPropertiesType.REQ_LEVEL_RECOMMENDED.equals(reqLevel)) {
	    img = getOsylImageBundle().iconeRec().createImage();
	} else if (COPropertiesType.REQ_LEVEL_COMPLEMENTARY.equals(reqLevel)) {
	    img = getOsylImageBundle().iconeCompl().createImage();
	}
	if (img != null)
	    img.setTitle(getView().getCoMessage("MetaInfo.requirement." + reqLevel));
	return img;
    }

    protected Image getImportantIcon() {
	Image img = null;
	if (getView().isContextImportant()) {
	    img = getOsylImageBundle().iconeImportant().createImage();
	    img.setTitle(getView().getCoMessage("MetaInfo.important"));
	}
	return img;
    }

    protected String getLocalizedRequirementLevel() {
	String reqLev = getView().getRequirementLevel();
	if (reqLev != null && reqLev != COPropertiesType.REQ_LEVEL_UNDEFINED) {
	    return getView().getCoMessage(
		    "MetaInfo.requirement." + getView().getRequirementLevel());
	} else
	    return "";
    }

    /**
     * Returns the selected diffusion level. This is either
     * {@link SecurityInterface#SECURITY_GROUP_PUBLIC},
     * {@link SecurityInterface#SECURITY_GROUP_ATTENDEE} or
     * {@link SecurityInterface#SECURITY_GROUP_ONSITE}.
     * 
     * @return String
     */
    public String getDiffusionLevel() {
	return diffusionListBox.getValue(diffusionListBox.getSelectedIndex());
    }

    /**
     * Returns the selected rubric.
     * 
     * @return String
     */
    public String getRubricType() {
	return rubricListBox.getValue(rubricListBox.getSelectedIndex());
    }

    /**
     * Returns the user defined rubric name.
     * 
     * @return String
     */
    public String getRubricUserDefLabel() {
    	return userDefLabel!=null?userDefLabel.getText():null;
    }
    
    private ListBox generateRubricList() {
	ListBox lb = new ListBox();
	lb.setName("listBoxFormElement");

	COContentResourceProxy resProx =
		(COContentResourceProxy) getView().getModel();

	List<COModelInterface> subModels =
		getView().getController().getOsylConfig().getOsylConfigRuler()
			.getAllowedSubModels(resProx.getParent());

	Vector<String> rubricTypes = new Vector<String>();

	for (int i = 0; i < subModels.size(); i++) {
	    Object subModel = subModels.get(i);

	    if (subModel instanceof COContentRubric) {
		COContentRubric coContentRubric = (COContentRubric) subModel;
		if (!coContentRubric.getType().equals(
			COContentRubric.RUBRIC_TYPE_NEWS))
		    rubricTypes.addElement(coContentRubric.getType());
	    }
	}
	String[] choices = new String[rubricTypes.size()];

	for (int i = 0; i < rubricTypes.size(); i++) {
	    choices[i] = rubricTypes.elementAt(i).toString();
	}

	String undefinedRubricType = choices[0];
	lb.addItem(getUiMessage("selectRubric"), undefinedRubricType);
	for (int i = 1; i < choices.length; i++) {
	    String rubricType = choices[i];
	    lb.addItem(getView().getCoMessages().getShortMessage(rubricType),
		    rubricType);
	    if (rubricType.equalsIgnoreCase(resProx.getRubricType())) {
		lb.setItemSelected(i, true);
	    }
	}
	
	return lb;
    }

    protected void generateTargetCoAbstractElementListBox(ListBox lb) {
	lb.clear();
	lb.addItem("");
	fillListBoxWithAllowedCoUnits((COContent) getView().getController()
		.getMainView().getModel(), lb);

    }

    /**
     * Create a list of potential coUnit targets considering the model type.
     * 
     * @param model
     * @param lb
     */
    @SuppressWarnings("unchecked")
    private void fillListBoxWithAllowedCoUnits(COElementAbstract model,
	    ListBox lb) {
	if (model.isCOUnit() && model.getChildrens().size() == 1) {
	    COUnitStructure coUnitStructure =
		    (COUnitStructure) model.getChildrens().get(0);
	    // TODO modify this method when multiple coUnitContent could be
	    // process
	    COUnitContent targetModel =
		    (COUnitContent) coUnitStructure.getChildrens().get(0);
	    if (isAnAllowedTargetModel(targetModel)) {
		String label = model.getLabel();
		if (label.length() > 35)
		    label = label.substring(0, 35) + "...";
		lb.addItem(label, targetModel.getId());
	    }
	} else if (model.isCOUnitStructure()) {
	    // TODO modify this method when multiple coUnitContent could be
	    // process
	    COUnitContent targetModel =
		    (COUnitContent) model.getChildrens().get(0);
	    if (isAnAllowedTargetModel(targetModel)) {
		String label = model.getLabel();
		if (label.length() > 35)
		    label = label.substring(0, 35) + "...";
		lb.addItem(label, targetModel.getId());
	    }
	} else {
	    for (Iterator<COElementAbstract> iter =
		    model.getChildrens().iterator(); iter.hasNext();) {
		COElementAbstract coElement = iter.next();
		fillListBoxWithAllowedCoUnits(coElement, lb);
	    }
	}
    }

    private boolean isAnAllowedTargetModel(COElementAbstract targetModel) {
	boolean rubricAllowed = false;
	boolean resourceTypeAllowed = false;
	List<COModelInterface> subModels =
		getView().getController().getOsylConfig().getOsylConfigRuler()
			.getAllowedSubModels(targetModel);
	for (Iterator<COModelInterface> iter = subModels.iterator(); iter
		.hasNext();) {
	    COModelInterface coi = iter.next();
	    if (coi instanceof COContentRubric) {
		if (coi.getType().equals(getRubricType()))
		    rubricAllowed = true;
	    }
	    if (coi instanceof COContentResource) {
		if (coi.getType().equals(
			getView().getModel().getResource().getType())) {
		    resourceTypeAllowed = true;
		}

	    }
	}
	return (rubricAllowed && resourceTypeAllowed);
    }

    public String getMoveToTarget() {
	return targetsListBox.getValue(targetsListBox.getSelectedIndex());
    }

    /**
     * Returns a Widget displaying the meta-information for current resProx. The
     * requirement level is blank by default and is not displayed if not set.
     * 
     * @return Widget
     */
    protected Widget getMetaInfoLabel() {
	Label metaInfoLabel = new Label();
	String diffusionLevel =
		getUiMessage("MetaInfo.audience."
			+ getView().getDiffusionLevel());

	String reqLevelUiMsg = "";
	String requirementLevel = "";
	String requirementLevelLabel = "";

	String metaInfoLabelStr =
		getUiMessage("MetaInfo.audience") + ": " + diffusionLevel;

	if (hasHide) {
	    String hidden =
		    (getView().isContextHidden() ? getUiMessage("Global.yes")
			    : getUiMessage("Global.no"));
	    metaInfoLabelStr +=
		    " | " + getUiMessage("MetaInfo.hidden") + ": " + hidden;
	}

	if (hasImportant) {
	    String important =
		    (getView().isContextImportant() ? getUiMessage("Global.yes")
			    : getUiMessage("Global.no"));
	    metaInfoLabelStr +=
		    " | " + getView().getCoMessage("MetaInfo.important") + ": "
			    + important;
	}

	if (hasRequirement) {
	    String reqLevelfromView = getView().getRequirementLevel();
	    if (reqLevelfromView != null && !"".equals(reqLevelfromView)
		    && !"undefined".equals(reqLevelfromView)) {
		reqLevelUiMsg =
			"MetaInfo.requirement."
				+ getView().getRequirementLevel();
		requirementLevel = getView().getCoMessage(reqLevelUiMsg);
		requirementLevelLabel =
			getUiMessage("MetaInfo.requirement") + ": ";

		metaInfoLabelStr +=
			" | " + requirementLevelLabel + requirementLevel;

	    }
	}
	metaInfoLabel.setText(metaInfoLabelStr);

	metaInfoLabel.setStylePrimaryName("Osyl-ResProxView-MetaInfo");

	return metaInfoLabel;
    }

    private void fillRubricUserDefLabel() {
    	
    if(rubricListBox.getSelectedIndex() == 0)
    	return;
    	
    String listBoxRubricType = rubricListBox.getValue(rubricListBox.getSelectedIndex());

    COContentResourceProxy resProx =
   		(COContentResourceProxy) getView().getModel();

    List<COContentResourceProxy> subModels = resProx.getParent().getChildrens();
    Iterator<COContentResourceProxy> iter = subModels.listIterator();
    boolean rubricFound = false;
    while(iter.hasNext() && !rubricFound){
    	COContentResourceProxy iterResProx = iter.next();
		if(listBoxRubricType.equals(iterResProx.getRubricType())){
			rubricFound = true;
			if( iterResProx.getRubricUserDefLabel() != null &&
			    iterResProx.getRubricUserDefLabel().length() > 0){
					modifyRubricDesc.setValue(true);
					userDefLabel.setEnabled(true);
					userDefLabel.setText(iterResProx.getRubricUserDefLabel());
			}

		}
    }
	}

    public boolean isMoveable() {
	return true;
    }

    public boolean isHasRequirement() {
	return hasRequirement;
    }

    public void setHasRequirement(boolean hasRequirement) {
	this.hasRequirement = hasRequirement;
    }

    public boolean isHasImportant() {
	return hasImportant;
    }

    public void setHasImportant(boolean hasImportant) {
	this.hasImportant = hasImportant;
    }

    public boolean isMoveableInRubric() {
	return moveableInRubric;
    }

    public void setMoveableInRubric(boolean moveableInRubric) {
	this.moveableInRubric = moveableInRubric;
    }

    public boolean isHasHide() {
	return hasHide;
    }

    public void setHasHide(boolean hasHide) {
	this.hasHide = hasHide;
    }
}
