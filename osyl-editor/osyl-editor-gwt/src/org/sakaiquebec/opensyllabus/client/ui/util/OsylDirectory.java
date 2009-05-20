package org.sakaiquebec.opensyllabus.client.ui.util;

import java.io.Serializable;
import java.util.ArrayList;


public class OsylDirectory extends OsylAbstractBrowserItem implements Serializable {
    
    private static final long serialVersionUID=42L;
    
    private ArrayList<OsylAbstractBrowserItem> filesList;
    
    public OsylDirectory() {
	
    }
    
    public OsylDirectory(String newDirName,String newDirPath,String newLastModif,ArrayList<OsylAbstractBrowserItem> list) {
	super(newDirName,newDirPath,false,newLastModif);
	this.setFilesList(list);
    }

    /**
     * @return the directoryName value.
     */
    public String getDirectoryName() {
        return super.getFileName();
    }

    /**
     * @return the directoryPath value.
     */
    public void setDirectoryPath(String newPath) {
        super.setFilePath(newPath);
    }

    /**
     * @return the directoryPath value.
     */
    public String getDirectoryPath() {
        return super.getFilePath();
    }

    /**
     * @return the filesList value.
     */
    public ArrayList<OsylAbstractBrowserItem> getFilesList() {
        return this.filesList;
    }

    /**
     * @param list the new value of filesList.
     */
    public void setFilesList(ArrayList<OsylAbstractBrowserItem> list) {
        this.filesList = list;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFolder() {
	return true;
    }
    
    @Override
    public String getItemTag() {
	return "UtilityRemoteFileBrowser_directoryTag";
    }

}
