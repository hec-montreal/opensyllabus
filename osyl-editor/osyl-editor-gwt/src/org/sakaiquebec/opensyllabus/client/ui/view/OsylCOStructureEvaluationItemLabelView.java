/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOStructureEvaluationItemEditor;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureEvaluationItemLabelView extends OsylAbstractView {

    public OsylCOStructureEvaluationItemLabelView(COContentUnit model,
	    OsylController controller) {
	super(model, controller);
	setEditor(new OsylCOStructureEvaluationItemEditor(this));
	initView();
    }
    
    public OsylCOStructureEvaluationItemLabelView(COContentUnit model,
	    OsylController controller, boolean isNotDeletable) {
	super(model, controller);
	setEditor(new OsylCOStructureEvaluationItemEditor(this));
	((OsylCOStructureEvaluationItemEditor) getEditor()).setIsNotDeletable(isNotDeletable);
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public COContentUnit getModel() {
	return (COContentUnit) super.getModel();
    }

    public String getTextFromModel() {
	return getModel().getLabel();
    }

    protected void updateModel() {
	updateMetaInfo();
	getModel().setLabel(getEditor().getText());
    }

    private void updateMetaInfo() {
	setRating(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getWeight());
	setLocation(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getLocation());
	setMode(((OsylCOStructureEvaluationItemEditor) getEditor()).getMode());
	setResult(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getResult());
	setScope(((OsylCOStructureEvaluationItemEditor) getEditor()).getScope());
	setOpenDate(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getOpenDate());
	setCloseDate(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getCloseDate());
	setSubmitionType(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getSubmitionType());
	setType((((OsylCOStructureEvaluationItemEditor) getEditor()).getType()));
    }

    /**
     * =========================ADD METHODS=======================
     */

    public void setRating(String l) {
	getModel().addProperty(COPropertiesType.RATING, l);
    }

    public String getRating() {
	String reqLevel = null;
	if (!"undefined"
		.equals(getModel().getProperty(COPropertiesType.RATING))
		|| null != getModel().getProperty(COPropertiesType.RATING)) {
	    reqLevel = getModel().getProperty(COPropertiesType.RATING);
	}
	return reqLevel;
    }

    public void setOpenDate(String l) {
	getModel().addProperty(COPropertiesType.OPENDATE, l);
    }

    public String getOpenDate() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.OPENDATE))
		|| null != getModel().getProperty(COPropertiesType.OPENDATE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.OPENDATE);
	}
	return reqLevel;
    }

    public void setCloseDate(String l) {
	getModel().addProperty(COPropertiesType.CLOSEDATE, l);
    }

    public String getCloseDate() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.CLOSEDATE))
		|| null != getModel().getProperty(COPropertiesType.CLOSEDATE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.CLOSEDATE);
	}
	return reqLevel;
    }

    public void setLocation(String l) {
	getModel().addProperty(COPropertiesType.LOCATION, l);
    }

    public String getLocation() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.LOCATION))
		|| null != getModel().getProperty(COPropertiesType.LOCATION)) {
	    reqLevel = getModel().getProperty(COPropertiesType.LOCATION);
	}
	return reqLevel;
    }

    public void setMode(String l) {
	getModel().addProperty(COPropertiesType.MODE, l);
    }

    public String getMode() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(COPropertiesType.MODE))
		|| null != getModel().getProperty(COPropertiesType.MODE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.MODE);
	}
	return reqLevel;
    }

    public void setResult(String l) {
	getModel().addProperty(COPropertiesType.RESULT, l);
    }

    public String getResult() {
	String reqLevel = null;
	if (!"undefined"
		.equals(getModel().getProperty(COPropertiesType.RESULT))
		|| null != getModel().getProperty(COPropertiesType.RESULT)) {
	    reqLevel = getModel().getProperty(COPropertiesType.RESULT);
	}
	return reqLevel;
    }

    public void setScope(String l) {
	getModel().addProperty(COPropertiesType.SCOPE, l);
    }

    public String getScope() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(COPropertiesType.SCOPE))
		|| null != getModel().getProperty(COPropertiesType.SCOPE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.SCOPE);
	}
	return reqLevel;
    }

    public void setSubmitionType(String l) {
	getModel().addProperty(COPropertiesType.SUBMITION_TYPE, l);
    }

    public String getSubmitionType() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.SUBMITION_TYPE))
		|| null != getModel().getProperty(
			COPropertiesType.SUBMITION_TYPE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.SUBMITION_TYPE);
	}
	return reqLevel;
    }

    public void setType(String l) {
	getModel().addProperty(COPropertiesType.EVALUATION_TYPE, l);
    }

    public String getType() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.EVALUATION_TYPE))
		|| null != getModel().getProperty(
			COPropertiesType.EVALUATION_TYPE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.EVALUATION_TYPE);
	}
	return reqLevel;
    }

}
