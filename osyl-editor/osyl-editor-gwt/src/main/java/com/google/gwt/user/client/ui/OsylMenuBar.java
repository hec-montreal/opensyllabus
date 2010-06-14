package com.google.gwt.user.client.ui;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class OsylMenuBar extends MenuBar {
	static MenuItem subMenuOpener;
	static String OPENER_STYLE_NAME = "gwt-MenuItem-OpenedSubMenu";
	static Boolean justClosed = false;
	public OsylMenuBar(boolean autoClose) {
		super(autoClose);
		this.addCloseHandler(new CloseHandler<PopupPanel>() {
		    public void onClose(CloseEvent<PopupPanel> event) {
		    	if (subMenuOpener != null) {
		    		// Prevent CloseEvent and ClickEvent simultaneously
		    		justClosed = true;
		    		Timer t = new Timer() {
						public void run() {justClosed = false;}
		    		};
		    		t.schedule(100);
		    		subMenuOpener.removeStyleName(OPENER_STYLE_NAME);
		    		if (OsylEditorEntryPoint.isInternetExplorer() &&
		    				hasStyle(subMenuOpener, "Osyl-MenuItem-View")) {
		    			DOM.setStyleAttribute(subMenuOpener.getElement(), 
		        				"backgroundPosition", "-49px 0");
		        	}
		    	}
		    }
		});
	}

	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
	    MenuItem item = myFindItem(DOM.eventGetTarget(event));
	    if (item == null) return; // MenuItems retained must contain SubMenu;
	    
	    switch (DOM.eventGetType(event)) {
	    case Event.ONCLICK:
	    	setRootMenuItem(item);
	    	break;
	    case KeyCodes.KEY_ENTER:
	    	setRootMenuItem(item);
	    	break;
	    case Event.ONMOUSEOUT:
	    	item.removeStyleName("gwt-MenuItem-selected");
	    	break;
	    	
	    case Event.ONMOUSEOVER:
	    	item.addStyleName("gwt-MenuItem-selected");
	    	break;
	    }
	    
	}
	private void setRootMenuItem(MenuItem item) {
    	if (item.getSubMenu() != null &&
    			!hasStyle(item, OPENER_STYLE_NAME) && 
    			((!justClosed && item == subMenuOpener) 
    			|| item != subMenuOpener)) {
    		subMenuOpener = item;
        	subMenuOpener.addStyleName(OPENER_STYLE_NAME);
        	// Hard coded value for the background position for IE <=7 ... sorry!
        	// TODO: Find a way to do it in stylesheet possible with one unique css class
        	if (OsylEditorEntryPoint.isInternetExplorer() &&
        		hasStyle(item, "Osyl-MenuItem-View")){
        			DOM.setStyleAttribute(subMenuOpener.getElement(), 
        				"backgroundPosition", "-49px -64px");
        	}
        }
	}
	
	private boolean hasStyle(MenuItem item, String style) {
		return item.getStyleName().indexOf(style) != -1;
	}
	
	private MenuItem myFindItem(Element hItem) {
		for (MenuItem item : getItems()) 
			if (DOM.isOrHasChild(item.getElement(), hItem))
				return item;
    
		return null;
	}
}
