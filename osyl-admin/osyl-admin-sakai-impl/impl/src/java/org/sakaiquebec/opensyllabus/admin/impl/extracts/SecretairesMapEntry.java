package org.sakaiquebec.opensyllabus.admin.impl.extracts;

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
 *****************************************************************************/

/**
 * This entry repsents a secretary
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class SecretairesMapEntry implements java.io.Serializable {

    public static final long serialVersionUID = -5942666321016168578l;

    private String emplId;
    private String role;
    private String deptId;
    private String empl_status;
    private String jobCode;

    /**
     * Empty constructor.
     */
    public SecretairesMapEntry() {
    }

    /**
     * @return the emplId
     */
    public String getEmplId() {
	return emplId;
    }

    /**
     * @param emplId the emplId to set
     */
    public void setEmplId(String emplId) {
	this.emplId = emplId;
    }

    /**
     * @return the role
     */
    public String getRole() {
	return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
	this.role = role;
    }

    /**
     * @return the deptId
     */
    public String getDeptId() {
	return deptId;
    }

    /**
     * @param deptId the deptId to set
     */
    public void setDeptId(String deptId) {
	this.deptId = deptId;
    }

    /**
     * @return the empl_status
     */
    public String getEmpl_status() {
	return empl_status;
    }

    /**
     * @param empl_status the empl_status to set
     */
    public void setEmpl_status(String empl_status) {
	this.empl_status = empl_status;
    }

    /**
     * @return the jobCode
     */
    public String getJobCode() {
	return jobCode;
    }

    /**
     * @param jobCode the jobCode to set
     */
    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
	return serialVersionUID;
    }

}
