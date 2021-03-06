package org.sakaiquebec.opensyllabus.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.MenuBar;

public class OsylMenuBar extends MenuBar {

    public OsylMenuBar() {
	super(false);
	addDiv();
    }

    public OsylMenuBar(boolean vertical) {
	super(vertical);
	addDiv();
    }

    public OsylMenuBar(boolean vertical, Resources images) {
	super(vertical, images);
	addDiv();
    }

    public OsylMenuBar(Resources images) {
	super(images);
	addDiv();
    }
    
    public void addDiv() {
	Element menuBarDiv = this.getElement();
	Element table = DOM.getChild(menuBarDiv, 0);
	Element addedDiv = DOM.createDiv();
	menuBarDiv.insertBefore(addedDiv, table);
    }

}
