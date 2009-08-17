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
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureEvaluationItemLabelView extends OsylAbstractView {

    public OsylCOStructureEvaluationItemLabelView(COContentUnit model,
	    OsylController controller) {
	this(model, controller, false);
    }

    public OsylCOStructureEvaluationItemLabelView(COContentUnit model,
	    OsylController controller, boolean isInList) {
	super(model, controller);
	setEditor(new OsylCOStructureEvaluationItemEditor(this, isInList));
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
	updateAssignement();

    }

    private void updateAssignement() {
	for (Iterator<COContentResourceProxy> childsIterator =
		getModel().getChildrens().iterator(); childsIterator.hasNext();) {
	    COContentResourceProxy contentResourceProxy = childsIterator.next();
	    if (contentResourceProxy.getType().equals(
		    COContentResourceProxyType.ASSIGNMENT)) {

		String uri =
			contentResourceProxy.getProperty(COPropertiesType.URI);
		String rawAssignmentId = uri.split("\\s*/a/\\s*")[1];
		rawAssignmentId = rawAssignmentId.split("\\s*/\\s*")[1];
		String assignmentId =
			rawAssignmentId.split("\\s*&panel=\\s*")[0];

		int rating = -1;
		int openYear = 0;
		int openMonth = 0;
		int openDay = 0;
		int closeYear = 0;
		int closeMonth = 0;
		int closeDay = 0;
		String ratingString = getRating();
		if (null != ratingString || !"undefined".equals(ratingString)
			|| !"".equals(ratingString)) {
		    rating = Integer.parseInt(ratingString.substring(0, ratingString.lastIndexOf("%")));
		}

		String openDateString = getOpenDate();
		if (null != openDateString
			|| !"undefined".equals(openDateString)
			|| !"".equals(openDateString)) {
		    openYear =
			    Integer.parseInt(openDateString.substring(0, 4));
		    openMonth =
			    Integer.parseInt(openDateString.substring(5, 7));
		    openDay = Integer.parseInt(openDateString.substring(8, 10));

		}

		String closeDateString = getCloseDate();
		if (null != closeDateString
			|| !"undefined".equals(closeDateString)
			|| !"".equals(closeDateString)) {
		    closeYear =
			    Integer.parseInt(closeDateString.substring(0, 4));
		    closeMonth =
			    Integer.parseInt(closeDateString.substring(5, 7));
		    closeDay =
			    Integer.parseInt(closeDateString.substring(8, 10));
		}

		getController().createOrUpdateAssignment(contentResourceProxy,
			assignmentId, getModel().getLabel(), null, openYear,
			openMonth, openDay, 0, 0, closeYear, closeMonth,
			closeDay, 0, 0, rating);
	    }
	}
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
