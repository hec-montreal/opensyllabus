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

import java.util.Date;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOUnitAssessmentLabelEditor;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitAssessmentLabelView extends OsylCOUnitLabelView {

    public OsylCOUnitAssessmentLabelView(COUnit model,
	    OsylController controller, boolean isDeletable, String levelStyle) {
	this(model, controller, isDeletable, levelStyle, false);
    }

    public OsylCOUnitAssessmentLabelView(COUnit model,
	    OsylController controller, boolean isDeletable, String levelStyle,
	    boolean viewFirstElement) {
	super(model, controller, isDeletable, levelStyle, false,
		viewFirstElement);
	setEditor(new OsylCOUnitAssessmentLabelEditor(this, isDeletable));
	((OsylCOUnitAssessmentLabelEditor) getEditor())
		.setViewerStyle(levelStyle);
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected void updateModel() {
	super.updateModel();
	updateMetaInfo();
    }

    private void updateMetaInfo() {
	setWeight(((OsylCOUnitAssessmentLabelEditor) getEditor()).getWeight());
	setLocation(((OsylCOUnitAssessmentLabelEditor) getEditor())
		.getLocation());
	setMode(((OsylCOUnitAssessmentLabelEditor) getEditor()).getMode());
	setDateStart(((OsylCOUnitAssessmentLabelEditor) getEditor()).getDate());
	setDateEnd(((OsylCOUnitAssessmentLabelEditor) getEditor()).getDate());
	setSubmitionType(((OsylCOUnitAssessmentLabelEditor) getEditor())
		.getSubmitionType());
	setAssessmentType((((OsylCOUnitAssessmentLabelEditor) getEditor())
		.getType()));
	setModality((((OsylCOUnitAssessmentLabelEditor) getEditor())
		.getModality()));
	setModifiedDateToNow();
    }

    /**
     * =========================ADD METHODS=======================
     */

    public void setWeight(String l) {
	getModel().addProperty(COPropertiesType.WEIGHT, l);
    }

    public String getWeight() {
	String reqLevel = null;
	if (!"undefined"
		.equals(getModel().getProperty(COPropertiesType.WEIGHT))
		|| null != getModel().getProperty(COPropertiesType.WEIGHT)) {
	    reqLevel = getModel().getProperty(COPropertiesType.WEIGHT);
	}
	return reqLevel;
    }

    public void setDateStart(Date l) {
	if (l != null)
	    getModel().addProperty(COPropertiesType.DATE_START,
		    OsylDateUtils.getDateAsXmlString(l));
	else
	    getModel().removeProperty(COPropertiesType.DATE_START);

    }

    public void setDateEnd(Date l) {
	if (l != null)
	    getModel().addProperty(COPropertiesType.DATE_END,
		    OsylDateUtils.getDateAsXmlString(l));
	else
	    getModel().removeProperty(COPropertiesType.DATE_END);
    }

    public Date getDateEnd() {
	String dateString = getModel().getProperty(COPropertiesType.DATE_END);
	Date date = null;
	if (dateString != null && !dateString.trim().equals("")) {
	    date = OsylDateUtils.getDateFromXMLDate(dateString);
	}
	return date;
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

    public void setAssessmentType(String l) {
	getModel().addProperty(COPropertiesType.ASSESSMENT_TYPE, l);
    }

    public String getAssessmentType() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.ASSESSMENT_TYPE))
		|| null != getModel().getProperty(
			COPropertiesType.ASSESSMENT_TYPE)) {
	    reqLevel = getModel().getProperty(COPropertiesType.ASSESSMENT_TYPE);
	}
	return reqLevel;
    }

    public String getModality() {
	String modality = null;
	if (!"undefined".equals(getModel().getProperty(COPropertiesType.MODALITY))
		|| null != getModel().getProperty(COPropertiesType.MODALITY)) {
	    modality = getModel().getProperty(COPropertiesType.MODALITY);
	}
	return modality;
    }
    
    private void setModality(String l) {
	getModel().addProperty(COPropertiesType.MODALITY, l);
    }

}
