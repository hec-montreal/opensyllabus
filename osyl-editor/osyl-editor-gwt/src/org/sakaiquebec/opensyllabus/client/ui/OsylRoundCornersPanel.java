package org.sakaiquebec.opensyllabus.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class OsylRoundCornersPanel extends Composite {
    
    private SimplePanel enclosingPanel;
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
	enclosingPanel = new SimplePanel(); 
	enclosingPanel.setStyleName(enclosingPanelStyle); 
	bottomLeft = new SimplePanel(); 
	bottomLeft.setStyleName(bottomLeftStyle); 
	bottomRight = new SimplePanel(); 
	bottomRight.setStyleName(bottomRightStyle); 
	topLeft = new SimplePanel(); 
	topLeft.setStyleName(topLeftStyle); 	    
	topRight = new SimplePanel(); 
	topRight.setStyleName(topRightStyle); 
        // Nesting of panels like russian Matryoshka dolls ;-)
	enclosingPanel.add(bottomLeft);
	bottomLeft.add(bottomRight); 
	bottomRight.add(topLeft); 
	topLeft.add(topRight); 
	topRight.add(internalWidget); 
	initWidget(enclosingPanel);
    }

}
