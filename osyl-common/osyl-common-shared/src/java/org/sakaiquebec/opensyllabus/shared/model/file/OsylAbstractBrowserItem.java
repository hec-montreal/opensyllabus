/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model.file;


import java.io.Serializable;

public abstract class OsylAbstractBrowserItem implements Serializable {

    private static final long serialVersionUID = 42L;

    private String fileName;
    
    private String filePath;

    private String LastModifTime;

    public OsylAbstractBrowserItem() {
    }

    public OsylAbstractBrowserItem(String newFileName, String newFilePath,
	    boolean newIsFolder, String newLastModifTime) {
	this.setFileName(newFileName);
	this.setFilePath(newFilePath);
	this.setLastModifTime(newLastModifTime);
    }

    /**
     * Returns the name of this file (without any path information).
     * 
     * @return the fileName value.
     */
    public String getFileName() {
	return this.fileName;
    }

    /**
     * @param fileName the new value of fileName.
     */
    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    /**
     * Returns the name of this file including complete path.
     * 
     * @return the filePath value.
     */
    public String getFilePath() {
	return this.filePath;
    }

    /**
     * @param filePath the new value of filePath.
     */
    public void setFilePath(String filePath) {
	this.filePath = filePath;
    }

    /**
     * @return the isFolder value.
     */
    public abstract boolean isFolder();

    /**
     * @return the lastModifTime value.
     */
    public String getLastModifTime() {
	return this.LastModifTime;
    }

    /**
     * @param lastModifTime the new value of lastModifTime.
     */
    public void setLastModifTime(String lastModifTime) {
	this.LastModifTime = lastModifTime;
    }
    
    public abstract String getItemTag();

}
