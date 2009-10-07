package org.sakaiquebec.opensyllabus.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OsylRoundCornersPanel extends Composite {
    
    private VerticalPanel enclosingPanel;
    private SimplePanel bottomLeft;
    private SimplePanel bottomRight;
    private SimplePanel topLeft;
    private SimplePanel topRight;
    
    public OsylRoundCornersPanel(Composite internalWidget){
	    this(internalWidget, 
		    "Osyl-OsylRoundCornersPanel-EnclosingPanel",
		    "Osyl-OsylRoundCornersPanel-BottomLeft", 
		    "Osyl-OsylRoundCornersPanel-BottomRight", 
		    "Osyl-OsylRoundCornersPanel-TopLeft", 
		    "Osyl-OsylRoundCornersPanel-TopRight");
    }
    
    public OsylRoundCornersPanel(Composite internalWidget, 
	    		String enclosingPanelStyle,
	    		String bottomLeftStyle,
	    		String bottomRightStyle,
	    		String topLeftStyle,
	    		String topRightStyle
	    ){
	enclosingPanel = new VerticalPanel(); 
	enclosingPanel.setStyleName(enclosingPanelStyle); 
	bottomLeft = new SimplePanel(); 
	bottomLeft.add(new HTML(" "));
	bottomLeft.setStyleName(bottomLeftStyle); 
	bottomRight = new SimplePanel(); 
	bottomRight.add(new HTML(" "));
	bottomRight.setStyleName(bottomRightStyle); 
	topLeft = new SimplePanel(); 
	topLeft.add(new HTML(" "));
	topLeft.setStyleName(topLeftStyle); 	    
	topRight = new SimplePanel(); 
	topRight.add(new HTML(" "));
	topRight.setStyleName(topRightStyle); 
        // Corners panels and internal Widget are all 
         // children of the enclosingPanel
	enclosingPanel.add(bottomLeft);
	enclosingPanel.add(bottomRight); 
	enclosingPanel.add(topLeft); 
	enclosingPanel.add(topRight); 
	enclosingPanel.add(internalWidget); 
	initWidget(enclosingPanel);
    }

}
