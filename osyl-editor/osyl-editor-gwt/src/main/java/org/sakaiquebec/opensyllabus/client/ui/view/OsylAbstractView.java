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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylEditableMouseOverListener;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class for edition and display of Course Outline Element.
 *
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public abstract class OsylAbstractView extends OsylViewableComposite {

    /**
     * The panel where will be added widgets either for displaying or editing
     * the model object.
     */
    // private HorizontalPanel mainPanel;
    private AbsolutePanel mainPanel;

    /**
     * The panel where buttons will be added to delete this item or enter and
     * leave edition.
     */
    private HorizontalPanel buttonPanel;

    /**
     * The editor for this view. Handles both the edition and display modes.
     */
    private OsylAbstractEditor editor;

    private VerticalPanel upAndDownPanel;

    protected OsylEditableMouseOverListener popUpMouseOverListener;

    /**
     * Constructor specifying the model to be displayed and edited by this
     * OsylAbstractView. The current {@link OsylController} must be provided.
     *
     * @param model
     * @param osylController
     */
    protected OsylAbstractView(COModelInterface model,
	    OsylController osylController) {
	this(model, osylController, true);
    }

    protected OsylAbstractView(COModelInterface model,
	    OsylController osylController, boolean editable) {
	super(model, osylController);
	setMainPanel(new AbsolutePanel());
	setButtonPanel(new HorizontalPanel());
	setUpAndDownPanel(new VerticalPanel());

	if (model.isEditable() && editable) {
	    popUpMouseOverListener = new OsylEditableMouseOverListener(this);
	}
    }

    /**
     * ==================== PRIVATE METHODS =====================
     */

    public VerticalPanel getUpAndDownPanel() {
	return upAndDownPanel;
    }

    public void setUpAndDownPanel(VerticalPanel upAndDownPanel) {
	this.upAndDownPanel = upAndDownPanel;
	this.upAndDownPanel
		.setStyleName("Osyl-MouseOverPopup-ArrowButtonPanel");
    }

    /**
     * @param HorizontalPanel the Panel to set
     */
    private void setMainPanel(AbsolutePanel panel) {
	this.mainPanel = panel;
	// Control move to CSS
	// this.mainPanel.setWidth("93%");
	// this.mainPanel.setStylePrimaryName("Osyl-UnitView-ResPanel");
    }

    /**
     * Provides subclasses and their listeners with the button panel.
     */
    private void setButtonPanel(HorizontalPanel panel) {
	this.buttonPanel = panel;
	// this.buttonPanel.setStylePrimaryName("Osyl-ButtonPanel");
	this.buttonPanel.setStyleName("Osyl-MouseOverPopup-ButtonPanel");
    }

    /**
     * ==================== OVERRIDEN METHODS =====================
     */

    /**
     * Returns the model object being display or edited by this
     * OsylAbstractView.
     *
     * @return {@link COModelInterface}
     */
    public COModelInterface getModel() {
	return super.getModel();
    }

    /**
     * ==================== COMMON METHODS =====================
     */

    /**
     * Initializes the widgets. This method must absolutely be invoked before
     * anything. The best is to call it as the constructor's last statement.
     */
    protected void initView() {
	/// The view extends Composite therefore it is absolutely necessary to
	// call initWidget on our main widget
	super.initWidget(getMainPanel());

	// We add the editor and the button panel
	if (!("false".equals(getModel()
		.getProperty(COPropertiesType.VISIBILITY)) && getController()
		.isReadOnly())) {
	    getMainPanel().add(getEditor());
	    getEditor().setWidth("100%");
	    setStylePrimaryName("Osyl-UnitView-ResPanel");
	    if (!getController().isReadOnly()) {
		getMainPanel().add(getButtonPanel());
		getMainPanel().add(getUpAndDownPanel());

		// We just want the ButtonPanel to be shown on MouseOver events.
		getButtonPanel().setVisible(false);
		getUpAndDownPanel().setVisible(false);
		// If we don't position the button panel at 0,0 then firefox
		// does
		// not
		// compute correctly the mainPanel height at first and this
		// causes
		// the
		// buttonPanel to move after its apparition.
		getMainPanel().setWidgetPosition(getButtonPanel(), 0, 0);
		getMainPanel().setWidgetPosition(getUpAndDownPanel(), 0, 0);
	    }
	    // And we refresh the view for the first time
	    refreshView();
	}
    } // initView

    /**
     * Returns the main panel which is the top container for this
     * OsylAbstractView.
     *
     * @return {@link HorizontalPanel}
     */
    public AbsolutePanel getMainPanel() {
	return mainPanel;
    }

    /**
     * Provides subclasses and their listeners with the button panel.
     */
    public HorizontalPanel getButtonPanel() {
	return buttonPanel;
    }

    /**
     * Called when entering edition.
     */
    public void enterEdit() {
	getMainPanel().addStyleDependentName("Hover");
	getButtonPanel().setVisible(false);
	getEditor().enterEdit();
    }

    /**
     * Called when leaving edition and Save button was clicked.
     *
     * @param boolean if modification should be saved.
     */
    public void closeAndSaveEdit(boolean save) {
	if (save) {
	    if (getEditor().prepareForSave()) {
		updateModel();
		moveIfNeeded();
	    } else {
		return;
	    }
	}
	getEditor().closeEditor();
    }

    protected void moveIfNeeded() {
	if (getEditor().isMoveable()) {
	    // TODO
	}
    }

    /**
     * Called when leaving edition (and either entering view or leaving the
     * current context).
     */
    public void leaveEdit() {
	getMainPanel().removeStyleDependentName("Hover");
	getButtonPanel().setVisible(false);
	getUpAndDownPanel().setVisible(false);
	getEditor().enterView();
    }

    /**
     * Returns the {@link OsylAbstractEditor} providing edition and display
     * capability.
     */
    public OsylAbstractEditor getEditor() {
	return editor;
    }

    /**
     * Sets the {@link OsylAbstractEditor} providing edition and display
     * capability.
     *
     * @param editor
     */
    public void setEditor(OsylAbstractEditor editor) {
	this.editor = editor;
    }

    /**
     * Refreshes the current view.
     */
    public void refreshView() {
	if (getModel() != null) {
	    editor.refreshView();
	}
    }

    /**
     * Returns the HTML code for a link whose URI and text are specified. Can be
     * used to have an {@link HTML} component clickable. The target of this link
     * is _blank (ie: new window).
     *
     * @param uri
     * @param text
     * @return HTML link
     */
    protected String generateHTMLLink(String uri, String text) {
	return "<a href=\"" + uri + "\" target=\"_blank\">" + text + "</a>";
    }

    /**
     * Returns the document name which is the part after the last slash if it is
     * a local resource, or the full URI if it looks as an external URI.
     *
     * @param uri
     * @return uri for presentation
     */
    protected String extractResourceName(String uri) {
	if (uri.matches("^(https?|ftp|mailto)://.+")) {
	    // If it's an external link we return it as is
	    return uri;
	} else {
	    return uri.substring(uri.lastIndexOf("/") + 1).trim();
	}
    }

    /**
     * Returns the document name which is the part after the last slash if it is
     * a local resource, or the full URI if it looks as an external URI.
     *
     * @param uri
     * @return uri for presentation
     */
    protected String extractResourcePath(String uri) {
	if (uri.matches("^(https?|ftp|mailto)://.+")) {
	    // If it's an external link we return it as is
	    return uri;
	} else {
	    return uri.substring(0, uri.lastIndexOf("/")).trim();
	}
    }

    /**
     * @see Widget#setElement(Element)
     */
    protected void setElement(Element elem) {
	super.setElement(elem);
	sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
    }

    /**
     * @see Widget#onBrowserEvent(Event)
     */
    public void onBrowserEvent(Event event) {
	if (popUpMouseOverListener != null && !getEditor().isInEditionMode()) {
	    switch (DOM.eventGetType(event)) {
	    case Event.ONMOUSEOVER:
		popUpMouseOverListener.onMouseOver(null);
		break;
	    case Event.ONMOUSEOUT:
		popUpMouseOverListener.onMouseOut(null);
		break;
	    }
	}
    }

    /**
     * ==================== ABSTRACT METHODS =====================
     */

    /**
     * Implemented by subclasses to provide model update capability.
     */
    protected abstract void updateModel();

    /**
     * Returns the text content of current resource as stored in its model.
     * Caveat: This only applies to views responsible for a single text field as
     * the {@link OsylResProxTextView}, {@link OsylResProxDocumentView} and
     * {@link OsylCOUnitStructureLabelView}. Return value for other views is
     * either undefined (may throw an exception) or implicit (may be the first
     * field of the view). Refer to the specific view documentation.
     *
     * @see OsylAbstractEditor#getText()
     */
    public abstract String getTextFromModel();

}
