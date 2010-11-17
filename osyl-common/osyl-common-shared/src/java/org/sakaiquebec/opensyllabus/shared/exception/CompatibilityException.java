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
public class CompatibilityException extends Exception implements
	Serializable {

    private static final long serialVersionUID = 6162762727619313309L;

    public static final int STRUCTURE_INCOMPATIBILITY = 1;

    public static final int ELEMENT_ALREADY_ASSOCIATED = 2;

    public static final int SCHEMA_VERSION_INCOMPATIBILIYY = 3;

    private String arg1;

    private String arg2;

    private int type;

    public CompatibilityException() {
	super("Incompatibility");
    }

    public CompatibilityException(int type, String arg1, String arg2) {
	super("Incompatibility");
	this.setType(type);
	this.setArg1(arg1);
	this.setArg2(arg2);
    }

    public void setArg1(String arg1) {
	this.arg1 = arg1;
    }

    public String getArg1() {
	return arg1;
    }

    public void setArg2(String arg2) {
	this.arg2 = arg2;
    }

    public String getArg2() {
	return arg2;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getType() {
	return type;
    }

}
