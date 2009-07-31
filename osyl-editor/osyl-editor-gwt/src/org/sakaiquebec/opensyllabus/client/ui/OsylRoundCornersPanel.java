package org.sakaiquebec.opensyllabus.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class OsylRoundCornersPanel extends Composite {
    
    private SimplePanel bottomLeft;
    private SimplePanel bottomRight;
    private SimplePanel topLeft;
    private SimplePanel topRight;

    public OsylRoundCornersPanel(Composite internalWidget){
	    bottomLeft = new SimplePanel(); 
	    bottomLeft.setStyleName("Osyl-OsylRoundCornersPanel-BottomLeft"); 
	    bottomRight = new SimplePanel(); 
	    bottomRight.setStyleName("Osyl-OsylRoundCornersPanel-BottomRight"); 
	    topLeft = new SimplePanel(); 
	    topLeft.setStyleName("Osyl-OsylRoundCornersPanel-TopLeft"); 	    
	    topRight = new SimplePanel(); 
	    topRight.setStyleName("Osyl-OsylRoundCornersPanel-TopRight"); 
	    
	    bottomLeft.add(bottomRight); 
	    bottomRight.add(topLeft); 
	    topLeft.add(topRight); 
	    topRight.add(internalWidget); 
	    initWidget(bottomLeft);
    }
}
