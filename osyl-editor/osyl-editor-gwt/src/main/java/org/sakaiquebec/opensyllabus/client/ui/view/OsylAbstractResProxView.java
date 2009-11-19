/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view;

import org.adamtacy.client.ui.NEffectPanel;
import org.adamtacy.client.ui.effects.impl.CollapseVertically;
import org.adamtacy.client.ui.effects.impl.Fade;
import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractResProxEditor;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;

/**
 * Abstract class providing display and edition capabilities for resources.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public abstract class OsylAbstractResProxView extends OsylAbstractView {

    private static final int SHRINK_BY_STEP_FACTOR = 8;
    private static final int INITIAL_SHRINK_DURATION = 30; // milliseconds
    private static final int ENDING_SHRINK_DURATION = 10; // milliseconds

    /**
     * Internal class to manage the shrinking of the view when it is deleted. At
     * the end the resource proxy is removed from the model.
     */
    private class ShrinkTimer extends Timer {

	private Panel resizingPanel;

	public ShrinkTimer(Panel panelToResize) {
	    super();
	    setResizingPanel(panelToResize);
	}

	@Override
	public void run() {
	    int height = getResizingPanel().getOffsetHeight();
	    if (height > 0) {
		// Shrink the height size of the view but with a minimum of 1
		height =
			height - Math.round(height / SHRINK_BY_STEP_FACTOR) - 1;
		if (height < 50) {
		    this.cancel();
		    ShrinkTimer fasterResizingEffect =
			    new ShrinkTimer(getResizingPanel());
		    fasterResizingEffect
			    .scheduleRepeating(ENDING_SHRINK_DURATION);
		}
		if (height < 0) {
		    height = 0;
		}
		getResizingPanel().setHeight(height + "px");
	    } else {
		this.cancel();
		getModel().remove();
	    }
	}

	/**
	 * @return the resizingPanel value.
	 */
	public Panel getResizingPanel() {
	    return this.resizingPanel;
	}

	/**
	 * @param resizingPanel the new value of resizingPanel.
	 */
	public void setResizingPanel(Panel resizingPanel) {
	    this.resizingPanel = resizingPanel;
	}

    }

    protected OsylAbstractResProxView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
    }

    /**
     * ==================== COMMON METHODS =====================
     */

    /**
     * Called when the child class requests its model to be deleted. This starts
     * a visual effects to ensure that the user understands what is happening.
     */
    public void updateModelOnDelete() {
	final NEffectPanel effectPanel = new NEffectPanel();

	effectPanel.setWidth("" + getEditor().getOffsetWidth() + "px");

	getButtonPanel().clear();

	effectPanel.add(getEditor());
	getMainPanel().add(effectPanel);

	// Add a Fade effect to the effect panel
	Fade fadeEffect = new Fade();
	effectPanel.addEffect(fadeEffect);

	// Add a Collapse effect to the effect panel
	CollapseVertically collapseEffect = new CollapseVertically();
	effectPanel.addEffect(collapseEffect);

	// Set the duration of the effects
	effectPanel.setEffectsLength(1);

	// Play the Effect
	effectPanel.playEffects();

	// And we wait for it to finish before shrinking the area!
	Timer t = new Timer() {
	    public void run() {

		cancel();
		// Resize the area
		ShrinkTimer resizingEffect = new ShrinkTimer(effectPanel);
		resizingEffect.scheduleRepeating(INITIAL_SHRINK_DURATION);
	    }
	};
	t.schedule(400);
    }

    protected void setProperty(String key, String val) {
	getModel().getResource().addProperty(key, val);
    }
    
    protected void setProperty(String key, String type, String val){
	getModel().getResource().addProperty(key, type, val);
    }

    protected String getProperty(String key) {
	return getModel().getResource().getProperty(key);
    }
    
    protected String getProperty(String key, String type) {
	return getModel().getResource().getProperty(key,type);
    }

    public void setContextImportant(boolean b) {
	String booleanValue = "" + b;
	getModel().addProperty(COPropertiesType.IMPORTANCE, booleanValue);
    }

    public boolean isContextImportant() {
	return "true".equals(getModel()
		.getProperty(COPropertiesType.IMPORTANCE));
    }

    public void setContextHidden(boolean b) {
	String booleanValue = "" + (!b);
	getModel().addProperty(COPropertiesType.VISIBILITY, booleanValue);
    }

    public boolean isContextHidden() {
	return "false".equals(getModel().getProperty(
		COPropertiesType.VISIBILITY));
    }

    public void setRequirementLevel(String l) {
	getModel().addProperty(COPropertiesType.REQUIREMENT_LEVEL, l);
    }

    public String getRequirementLevel() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.REQUIREMENT_LEVEL))
		|| null != getModel().getProperty(
			COPropertiesType.REQUIREMENT_LEVEL)) {
	    reqLevel =
		    getModel().getProperty(COPropertiesType.REQUIREMENT_LEVEL);
	}
	return reqLevel;
    }

    public void setDiffusionLevel(String l) {
	getModel().setAccess(l);
    }

    public String getDiffusionLevel() {
	return getModel().getAccess();
    }

    public String getRubricType() {
	return getModel().getRubricType();
    }

    public void setRubricType(String r) {
	getModel().setRubricType(r);
    }

    protected void updateMetaInfo() {
	//contextMetaInfo
	if(getEditor().isHasImportant())
	    setContextImportant(getEditor().isContextImportant());
	setContextHidden(getEditor().isContextHidden());
	setDiffusionLevel(getEditor().getDiffusionLevel());
	setRubricType(getEditor().getRubricType());
	getModel().addProperty(COPropertiesType.MODIFIED, OsylDateUtils.getNowDateAsXmlString());
	if (getEditor().isHasRequirement())
	    setRequirementLevel(getEditor().getRequirementLevel());
	//resourceMetaInfo
	if(getModel().getResource()!=null){
	     updateResourceMetaInfo();
	}
    }

    protected void moveTo(String targetUuid) {
	COUnitContent targetCoUnitContent =
		(COUnitContent) ((COContent) getController().getMainView()
			.getModel()).findCOElementAbstractWithId(targetUuid);
	getModel().getParent().removeChild(getModel());
	getModel().setParent(targetCoUnitContent);
	getModel().remove();
	targetCoUnitContent.addChild(getModel());
	// affichage du message
	OsylUnobtrusiveAlert alert =
		new OsylUnobtrusiveAlert(getUiMessage("element.moved"));
	OsylEditorEntryPoint.showWidgetOnTop(alert);

    }

    protected void moveIfNeeded() {
	if (getEditor().isMoveable()) {
	    String targetUuid = getEditor().getMoveToTarget();
	    if (!targetUuid.equals("")
		    && !targetUuid.equals(getModel().getParent().getId())) {
		moveTo(targetUuid);
	    }
	}
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public COContentResourceProxy getModel() {
	return (COContentResourceProxy) super.getModel();
    }

    /**
     * Returns the {@link OsylAbstractEditor} providing edition and display
     * capability.
     */
    public OsylAbstractResProxEditor getEditor() {
	return (OsylAbstractResProxEditor) super.getEditor();
    }

    public String getTextFromModelDesc() {
	return null;
    }
    
    public void updateResourceMetaInfo(){
	getModel().getResource().setAccess(getModel().getAccess());
    }

}
