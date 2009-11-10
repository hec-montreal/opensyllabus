package org.sakaiquebec.opensyllabus.shared.model.file;

import java.io.Serializable;

public class OsylFileItem extends OsylAbstractBrowserItem implements
	Serializable {

    private static final long serialVersionUID = 374077851404414736L;

    private String mimeType;

    private String description;

    private String copyrightChoice;

    public OsylFileItem() {
    }

    public OsylFileItem(String newFileName, String newFilePath,
	    boolean newIsFolder, String newLastModifTime, String newMimeType,
	    String newDescription, String newCopyrightChoice) {
	super(newFileName, newFilePath, newIsFolder, newLastModifTime);
	setMimeType(newMimeType);
	setDescription(newDescription);
	setCopyrightChoice(newCopyrightChoice);
    }

    public OsylFileItem(String filePath) {
	super.setFilePath(filePath);
    }

    public String getMimeType() {
	return mimeType;
    }

    public void setMimeType(String newMimeType) {
	this.mimeType = newMimeType;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCopyrightChoice() {
	return copyrightChoice;
    }

    public void setCopyrightChoice(String newCopyrightChoice) {
	this.copyrightChoice = newCopyrightChoice;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFolder() {
	return false;
    }

    @Override
    public String getItemTag() {
	return "UtilityRemoteFileBrowser_fileTag";
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof OsylFileItem) {
	    OsylFileItem ofi = (OsylFileItem) obj;
	    if (getFilePath().equals(ofi.getFilePath()))
		return true;
	    else
		return false;
	} else {
	    return false;
	}
    }

}