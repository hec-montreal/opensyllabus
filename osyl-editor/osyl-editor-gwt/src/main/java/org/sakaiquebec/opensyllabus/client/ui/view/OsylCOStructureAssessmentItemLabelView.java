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
import java.util.Iterator;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOStructureAssessmentItemEditor;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOStructureItemEditor;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureAssessmentItemLabelView extends OsylAbstractView {

    public OsylCOStructureAssessmentItemLabelView(COUnit model,
	    OsylController controller) {
	this(model, controller, false);
    }

    /**
     * @param model
     * @param controller
     * @param isInList
     */
    public OsylCOStructureAssessmentItemLabelView(COUnit model,
	    OsylController controller, boolean isInList) {
	super(model, controller);
	setEditor(new OsylCOStructureAssessmentItemEditor(this, isInList));
	initView();
    }

    public OsylCOStructureAssessmentItemLabelView(COUnit model,
	    OsylController controller, boolean isInList, String levelStyle) {
	super(model, controller);
	setEditor(new OsylCOStructureAssessmentItemEditor(this, isInList));
	((OsylCOStructureItemEditor) getEditor()).setViewerStyle(levelStyle);
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public COUnit getModel() {
	return (COUnit) super.getModel();
    }

    public String getTextFromModel() {
	return getModel().getLabel();
    }

    protected void updateModel() {
	updateMetaInfo();
	getModel().setLabel(getEditor().getText());
	updateAssignements(getModel());

    }

    private void updateAssignements(COElementAbstract model) {
	if (model.isCOContentResourceProxy()) {
	    COContentResourceProxy contentResourceProxy =
		    (COContentResourceProxy) model;
	    if (contentResourceProxy.getResource().getType().equals(
		    COContentResourceType.ASSIGNMENT)) {
		updateAssignement(contentResourceProxy);
	    }
	} else {
	    for (Iterator<COElementAbstract> childsIterator =
		    model.getChildrens().iterator(); childsIterator.hasNext();) {
		updateAssignements(childsIterator.next());
	    }
	}
    }

    public void updateAssignement(COContentResourceProxy contentResourceProxy) {
	String uri =
		contentResourceProxy.getResource().getProperty(
			COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI);
	String rawAssignmentId = uri.split("\\s*/a/\\s*")[1];
	rawAssignmentId = rawAssignmentId.split("\\s*/\\s*")[1];
	String assignmentId = rawAssignmentId.split("\\s*&panel=\\s*")[0];
	if (!getController().isInHostedMode()) {

	    // And at last, the RPC call
	    getController().createOrUpdateAssignment(contentResourceProxy,
		    assignmentId);
	}
    }

    private void updateMetaInfo() {
	setWeight(((OsylCOStructureAssessmentItemEditor) getEditor())
		.getWeight());
	setLocation(((OsylCOStructureAssessmentItemEditor) getEditor())
		.getLocation());
	setMode(((OsylCOStructureAssessmentItemEditor) getEditor()).getMode());
	// setResult(((OsylCOStructureEvaluationItemEditor) getEditor())
	// .getResult());
	// setScope(((OsylCOStructureEvaluationItemEditor)
	// getEditor()).getScope());
	setDateStart(((OsylCOStructureAssessmentItemEditor) getEditor())
		.getDate());
	setDateEnd(((OsylCOStructureAssessmentItemEditor) getEditor())
		.getDate());
	setSubmitionType(((OsylCOStructureAssessmentItemEditor) getEditor())
		.getSubmitionType());
	setAssessmentType((((OsylCOStructureAssessmentItemEditor) getEditor())
		.getType()));
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

    // public void setResult(String l) {
    // getModel().addProperty(COPropertiesType.RESULT, l);
    // }
    //
    // public String getResult() {
    // String reqLevel = null;
    // if (!"undefined"
    // .equals(getModel().getProperty(COPropertiesType.RESULT))
    // || null != getModel().getProperty(COPropertiesType.RESULT)) {
    // reqLevel = getModel().getProperty(COPropertiesType.RESULT);
    // }
    // return reqLevel;
    // }
    //
    // public void setScope(String l) {
    // getModel().addProperty(COPropertiesType.SCOPE, l);
    // }
    //
    // public String getScope() {
    // String reqLevel = null;
    // if (!"undefined".equals(getModel().getProperty(COPropertiesType.SCOPE))
    // || null != getModel().getProperty(COPropertiesType.SCOPE)) {
    // reqLevel = getModel().getProperty(COPropertiesType.SCOPE);
    // }
    // return reqLevel;
    // }

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

}
