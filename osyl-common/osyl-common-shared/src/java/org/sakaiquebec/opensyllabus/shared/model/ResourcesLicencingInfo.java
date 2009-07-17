/**********************************************************************************
 * $Id: ResourcesLicencingInfo.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ResourcesLicencingInfo is a pojo about Resources Licensing information
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: ResourcesLicencingInfo.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 */
public class ResourcesLicencingInfo implements java.io.Serializable {

    private static final long serialVersionUID = 1005876208565176631L;

    private List<String> copyrightTypeList;
	private String defaultCopyright;
	
	/**
	 * @return the copyrightTypeList
	 */
	public List<String> getCopyrightTypeList() {
		return copyrightTypeList;
	}

	/**
	 * @param copyrightTypeList the copyrightTypeList to set
	 */
	public void setCopyrightTypeList(List<String> copyrightTypeList) {
		this.copyrightTypeList = copyrightTypeList;
	}

	/**
	 * @return the defaultCopyright
	 */
	public String getDefaultCopyright() {
		return defaultCopyright;
	}

	/**
	 * @param defaultCopyright the defaultCopyright to set
	 */
	public void setDefaultCopyright(String defaultCopyright) {
		this.defaultCopyright = defaultCopyright;
	}
	
	public ResourcesLicencingInfo(){
		copyrightTypeList = new ArrayList<String>();
		defaultCopyright = null;
	}

}
