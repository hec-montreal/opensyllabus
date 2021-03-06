/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.shared.exception;

import java.io.Serializable;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class FusionException extends Exception implements Serializable {

    private static final long serialVersionUID = -8218897299880366249L;

    private boolean hierarchyFusionException = false;

    private String conflictedCoId1;

    private String conflictedCoId2;

    public FusionException() {
	super("FusionException");
    }

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     * 
     * @param message the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public FusionException(String message) {
	super(message);
    }

    public void setCoIds(String co1, String co2) {
	this.conflictedCoId1 = co1;
	this.conflictedCoId2 = co2;
    }

    public String getConflictedCoId1() {
	return conflictedCoId1;
    }

    public void setConflictedCoId1(String conflictedCoId1) {
	this.conflictedCoId1 = conflictedCoId1;
    }

    public String getConflictedCoId2() {
	return conflictedCoId2;
    }

    public void setConflictedCoId2(String conflictedCoId2) {
	this.conflictedCoId2 = conflictedCoId2;
    }

    public void setHierarchyFusionException(boolean hierarchyFusionException) {
	this.hierarchyFusionException = hierarchyFusionException;
    }

    public boolean isHierarchyFusionException() {
	return hierarchyFusionException;
    }

}
