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

import java.util.Iterator;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOStructureEvaluationItemEditor;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOStructureItemEditor;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureEvaluationItemLabelView extends OsylAbstractView {

    public OsylCOStructureEvaluationItemLabelView(COUnit model,
	    OsylController controller) {
	this(model, controller, false);
    }

    /**
     * @param model
     * @param controller
     * @param isInList
     */
    public OsylCOStructureEvaluationItemLabelView(COUnit model,
	    OsylController controller, boolean isInList) {
	super(model, controller);
	setEditor(new OsylCOStructureEvaluationItemEditor(this, isInList));
	initView();
    }

    public OsylCOStructureEvaluationItemLabelView(COUnit model,
	    OsylController controller, boolean isInList, String levelStyle) {
	super(model, controller);
	setEditor(new OsylCOStructureEvaluationItemEditor(this, isInList));
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
	updateAssignements();

    }

    private void updateAssignements() {
	for (Iterator<COElementAbstract> coUnitChildsIterator =
		getModel().getChildrens().iterator(); coUnitChildsIterator
		.hasNext();) {
	    COElementAbstract coUnitChild = coUnitChildsIterator.next();
	    if (coUnitChild.isCOUnitContent()) {
		COUnitContent coUnitContent = (COUnitContent) coUnitChild;
		for (Iterator<COContentResourceProxy> childsIterator =
			coUnitContent.getChildrens().iterator(); childsIterator
			.hasNext();) {
		    COContentResourceProxy contentResourceProxy =
			    childsIterator.next();
		    if (contentResourceProxy.getResource().getType().equals(
			    COContentResourceType.ASSIGNMENT)) {
			updateAssignement(contentResourceProxy);
		    }
		}
	    }
	    if (coUnitChild.isCOUnitStructure()) {
		// TODO when counitstructure is ready
	    }
	}

    }

    private void updateAssignement(COContentResourceProxy contentResourceProxy) {
	boolean error = false;
	String uri =
		contentResourceProxy.getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI);
	String rawAssignmentId = uri.split("\\s*/a/\\s*")[1];
	rawAssignmentId = rawAssignmentId.split("\\s*/\\s*")[1];
	String assignmentId = rawAssignmentId.split("\\s*&panel=\\s*")[0];

	int rating = -1;
	int openYear = 0;
	int openMonth = 0;
	int openDay = 0;
	int closeYear = 0;
	int closeMonth = 0;
	int closeDay = 0;
	String ratingString = getWeight();
	if (null != ratingString && !"undefined".equals(ratingString)
		&& !"".equals(ratingString)) {
	    rating =
		    Integer.parseInt(ratingString.substring(0, ratingString
			    .lastIndexOf("%")));
	} else {
	    error = true;
	}

	String openDateString = getDateStart();
	if (null != openDateString && !"undefined".equals(openDateString)
		&& !"".equals(openDateString)) {
	    openYear = Integer.parseInt(openDateString.substring(0, 4));
	    openMonth = Integer.parseInt(openDateString.substring(5, 7));
	    openDay = Integer.parseInt(openDateString.substring(8, 10));

	} else {
	    error = true;
	}

	String closeDateString = getDateEnd();
	if (null != closeDateString && !"undefined".equals(closeDateString)
		&& !"".equals(closeDateString)) {
	    closeYear = Integer.parseInt(closeDateString.substring(0, 4));
	    closeMonth = Integer.parseInt(closeDateString.substring(5, 7));
	    closeDay = Integer.parseInt(closeDateString.substring(8, 10));
	} else {
	    error = true;
	}
	if (!error) {
	    getController().createOrUpdateAssignment(contentResourceProxy,
		    assignmentId, getModel().getLabel(), null, openYear,
		    openMonth, openDay, 0, 0, closeYear, closeMonth, closeDay,
		    0, 0, rating);
	}
    }

    private void updateMetaInfo() {
	setWeight(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getWeight());
	setLocation(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getLocation());
	setMode(((OsylCOStructureEvaluationItemEditor) getEditor()).getMode());
//	setResult(((OsylCOStructureEvaluationItemEditor) getEditor())
//		.getResult());
//	setScope(((OsylCOStructureEvaluationItemEditor) getEditor()).getScope());
	setDateStart(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getOpenDate());
	setDateEnd(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getCloseDate());
	setSubmitionType(((OsylCOStructureEvaluationItemEditor) getEditor())
		.getSubmitionType());
	setAssessmentType((((OsylCOStructureEvaluationItemEditor) getEditor())
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

    public void setDateStart(String l) {
	getModel().addProperty(COPropertiesType.DATE_START, l);
    }

    public String getDateStart() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.DATE_START))
		|| null != getModel().getProperty(COPropertiesType.DATE_START)) {
	    reqLevel = getModel().getProperty(COPropertiesType.DATE_START);
	}
	return reqLevel;
    }

    public void setDateEnd(String l) {
	getModel().addProperty(COPropertiesType.DATE_END, l);
    }

    public String getDateEnd() {
	String reqLevel = null;
	if (!"undefined".equals(getModel().getProperty(
		COPropertiesType.DATE_END))
		|| null != getModel().getProperty(COPropertiesType.DATE_END)) {
	    reqLevel = getModel().getProperty(COPropertiesType.DATE_END);
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

//    public void setResult(String l) {
//	getModel().addProperty(COPropertiesType.RESULT, l);
//    }
//
//    public String getResult() {
//	String reqLevel = null;
//	if (!"undefined"
//		.equals(getModel().getProperty(COPropertiesType.RESULT))
//		|| null != getModel().getProperty(COPropertiesType.RESULT)) {
//	    reqLevel = getModel().getProperty(COPropertiesType.RESULT);
//	}
//	return reqLevel;
//    }
//
//    public void setScope(String l) {
//	getModel().addProperty(COPropertiesType.SCOPE, l);
//    }
//
//    public String getScope() {
//	String reqLevel = null;
//	if (!"undefined".equals(getModel().getProperty(COPropertiesType.SCOPE))
//		|| null != getModel().getProperty(COPropertiesType.SCOPE)) {
//	    reqLevel = getModel().getProperty(COPropertiesType.SCOPE);
//	}
//	return reqLevel;
//    }

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
