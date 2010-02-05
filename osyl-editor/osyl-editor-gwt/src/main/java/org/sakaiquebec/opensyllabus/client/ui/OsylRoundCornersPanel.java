package org.sakaiquebec.opensyllabus.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class OsylRoundCornersPanel extends Composite {

    private DecoratorPanel enclosingPanel;

    // private VerticalPanel enclosingPanel;
    // private SimplePanel bottomLeft;
    // private SimplePanel bottomRight;
    // private SimplePanel topLeft;
    // private SimplePanel topRight;

    public OsylRoundCornersPanel(Composite internalWidget) {
	enclosingPanel = new DecoratorPanel();
	enclosingPanel.setWidget(internalWidget);
	initWidget(enclosingPanel);
    }

    public OsylRoundCornersPanel(SimplePanel internalWidget) {
	enclosingPanel = new DecoratorPanel();
	enclosingPanel.setWidget(internalWidget);
	initWidget(enclosingPanel);
    }

    public OsylRoundCornersPanel(Composite internalWidget, String P1,
	    String P2, String P3, String P4, String P5) {
	this(internalWidget);
    }

    public OsylRoundCornersPanel(SimplePanel enclosingPanel, String string,
	    String string2, String string3, String string4, String string5) {
	this(enclosingPanel);
    }

    // public OsylRoundCornersPanel(Composite internalWidget){
    // this(internalWidget,
    // "Osyl-OsylRoundCornersPanel-EnclosingPanel",
    // "Osyl-OsylRoundCornersPanel-BottomLeft",
    // "Osyl-OsylRoundCornersPanel-BottomRight",
    // "Osyl-OsylRoundCornersPanel-TopLeft",
    // "Osyl-OsylRoundCornersPanel-TopRight");
    // }

    // public OsylRoundCornersPanel(Composite internalWidget,
    // String enclosingPanelStyle,
    // String bottomLeftStyle,
    // String bottomRightStyle,
    // String topLeftStyle,
    // String topRightStyle
    // ){
    // enclosingPanel = new VerticalPanel();
    // enclosingPanel.setStyleName(enclosingPanelStyle);
    // bottomLeft = new SimplePanel();
    // bottomLeft.add(new HTML(" "));
    // bottomLeft.setStyleName(bottomLeftStyle);
    // bottomRight = new SimplePanel();
    // bottomRight.add(new HTML(" "));
    // bottomRight.setStyleName(bottomRightStyle);
    // topLeft = new SimplePanel();
    // topLeft.add(new HTML(" "));
    // topLeft.setStyleName(topLeftStyle);
    // topRight = new SimplePanel();
    // topRight.add(new HTML(" "));
    // topRight.setStyleName(topRightStyle);
    // // Corners panels and internal Widget are all
    // // children of the enclosingPanel
    // enclosingPanel.add(bottomLeft);
    // enclosingPanel.add(bottomRight);
    // enclosingPanel.add(topLeft);
    // enclosingPanel.add(topRight);
    // enclosingPanel.add(internalWidget);
    // initWidget(enclosingPanel);
    // }

}
