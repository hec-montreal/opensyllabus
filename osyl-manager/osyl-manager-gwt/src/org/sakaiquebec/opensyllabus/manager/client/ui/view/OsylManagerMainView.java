/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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

package org.sakaiquebec.opensyllabus.manager.client.ui.view;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerMainView extends OsylManagerAbstractView implements
	OsylManagerEventHandler {

    private VerticalPanel mainPanel;

    public OsylManagerMainView(OsylManagerController controller) {
	super(controller);
	controller.addEventHandler(this);
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	initView();
    }
    
    private void initView(){
	Label title= new Label(getController().getMessages().optionsTitle());
	title.setStylePrimaryName("OsylManager-form-title");
	
	final RadioButton createRadioButton = new RadioButton("choix",getController().getMessages().createOption());
	final RadioButton exportRadioButton = new RadioButton("choix",getController().getMessages().exportOption());
	final RadioButton associateRadioButton = new RadioButton("choix",getController().getMessages().associateDissociate());
	
	Button valid = new Button();
	valid.setText(getController().getMessages().valid());
	valid.addClickListener(new ClickListener(){
	    public void onClick(Widget sender) {
		if(createRadioButton.isChecked()){
		    setView(new CreateSiteView(getController()));
		}
		else if(exportRadioButton.isChecked()){
		    setView(new ExportCOView(getController()));
		}
		else if(associateRadioButton.isChecked()){
		    setView(new AssociateView(getController()));
		}
	    }
	});
	
	mainPanel.add(title);
	mainPanel.add(createRadioButton);
	mainPanel.add(exportRadioButton);
	mainPanel.add(associateRadioButton);
	mainPanel.add(valid);
    }

    public void setView(OsylManagerAbstractView newView) {
	mainPanel.clear();
	mainPanel.add(newView);
    }

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    /**
     * Refreshes the <code>OsylManagerMainView</code> view.
     */
    public void refreshView() {
	switch (getController().getState()) {
	case OsylManagerController.STATE_CREATION_FORM:
	    setView(new CreateSiteView(getController()));
	    break;
	case OsylManagerController.STATE_UPLOAD_FORM:
	    setView(new ImportCOView(getController()));
	    break;
	case OsylManagerController.STATE_FINISH:
	    setView(new FinishView(getController()));
	    break;
	case OsylManagerController.STATE_FILE_DOWNLOAD:
	    break;
	default:
	    initView();
	    break;
	}
    }

    /**
     * {@inheritDoc}
     */
    public void onOsylManagerEvent() {
	refreshView();
    }

}
