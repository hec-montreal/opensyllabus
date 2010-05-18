/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.client;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.OsylMainView;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewable;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COModeled;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The "Main Class" (EntryPoint in GWT) of the OpenSyllabus Editor.
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class OsylEditorEntryPoint implements EntryPoint {

    // Trace & debugging variables
    // final private static boolean TEST_ASSIGNMENT = false;
    private static String debugMsg;

    // Model variables
    private COContent editorEntryPointModel;
    private COModeled modeledCo;
    private COSerialized serializedCO;

    // View variables
    private RootPanel rootPanel;;
    static private OsylEditorEntryPoint singleton;
    private OsylViewable editorEntryPointView;
    static private int yPosition;
    static private int previousHeight;

    public static String execmode = "prod";

    private static final int MIN_SIZE = 800;

    // Default Constructor. We ensure that our singleton instance is the same as
    // the one initialized by the GWT framework!
    public OsylEditorEntryPoint() {
	singleton = this;
    }

    public static OsylEditorEntryPoint getInstance() {
	return singleton;
    }

    // static private final Images images = (Images) GWT.create(Images.class);

    public RootPanel getRootPanel() {
	return rootPanel;
    }

    /**
     * This is the entry point method, i.e.: the first method called when the
     * client application is loaded. This is automatic and provided by the GWT
     * framework.
     */
    public void onModuleLoad() {
	final OsylController osylController = OsylController.getInstance();

	// we initialize the communication
	// and load the course outline from the server. If some error
	// occurs, the callback will take charge of loading some default
	// content for development purposes (i.e: it will call
	// initOffline()).
	osylController.loadData();
	
	Window.addWindowClosingHandler(new Window.ClosingHandler() {
	    public void onWindowClosing(Window.ClosingEvent event) {

		// We instruct the ViewContext to close all editors in case
		// an editor with modified content is still open.
		osylController.getViewContext().closeAllEditors();

		// If we don't need to save the model it is ok to leave now
		if (!osylController.isModelDirty()) {
		    return;
		}
		event
			.setMessage(osylController
				.getUiMessage("save.reallyQuit"));
	    }
	}); // addWindowClosingHandler
	
	Window.addCloseHandler(new CloseHandler<Window>() {
	    
	    public void onClose(CloseEvent<Window> event) {
		if(!osylController.isReadOnly()){
		    osylController.releaseLock();
		}
	    }
	});

    } // onModuleLoad

    public void initModel(COSerialized co) {
	// This is absolutely required to get the ID:
	this.serializedCO = co;

	// And now we initialize the model from the XML content
	this.modeledCo = new COModeled();
	this.modeledCo.setContent(co.getContent());
	this.modeledCo.XML2Model();
	setModel(this.modeledCo.getModeledContent());

	// We can now initialize the view!
	initView();
    }

    /**
     * Initializes the view, its model and controller.
     */
    private void initView() {
	// We initialize the controller
	OsylController osylController = OsylController.getInstance();
	// We get hold of our slot in the html/jsp page. We now use the body
	// itself (that is we do not specify an element ID):
	rootPanel = RootPanel.get();
	osylController.setCOSerialized(getSerializedCourseOutline());
	// We create our main view
	OsylMainView editorMainView =
		new OsylMainView(getModel(), osylController);
	editorMainView.initView();
	// And we instruct it about its size. We use the slot's width but
	// the height cannot be computed as we are in Sakai's iFrame.
	// We subtract 16px to the slot width for a perfect alignment
	// (optimized for FireFox).
	int width = Math.max(rootPanel.getOffsetWidth() - 16, 500);
	editorMainView.setWidth(width + "px");
	this.setView(editorMainView);
	rootPanel.add((Widget) this.getView());
	// We start with an arbitrary 800px high area. It will be updated after.
	setToolHeight(MIN_SIZE);
	// Uncomment this display debug messages specified with setDebugMsg().
	// startDebugMsg();
    }

    public void refreshView() {
	rootPanel.clear();
	rootPanel.add((Widget) this.getView());
	previousHeight = 0;
	int width = Math.max(rootPanel.getOffsetWidth() - 16, 500);
	((OsylViewableComposite) this.getView()).setWidth(width + "px");
    }

    /**
     * We call this whenever a view is displayed to avoid scroll-bars.
     * 
     * @param h the new tool height
     */
    public void setToolHeight(int h) {
	try {
	    if (previousHeight >= h && previousHeight<1.1*h) {
		// This prevents costly and unneeded resizes
		//we do not resize if actual size is within 10% greater than required size
		//else we resize 
		return;
	    }
	    h = Math.max(h,MIN_SIZE);
	    previousHeight = h;

	    // We keep track of current y-position to scroll to it after the
	    // resize. This makes the interface kind of flash... not very nice
	    // but it's better than getting back to the top.
	    yPosition = getYPosition();
	    getRootPanel().setHeight(h + "px");
	    ((OsylViewableComposite) getView()).setHeight(h + "px");
	    if (OsylController.getInstance().isInHostedMode()) {
		return;
	    }
	    // We resize the Sakai iFrame
	    String frameId = getSakaiFrameId();
	    if (!resizeSakaiIFrame(frameId)) {
		// sometimes the ID provided in current URL is invalid!
		// All "-" (dash character) needs to be replaced by "x"!!!
		if (!resizeSakaiIFrame(frameId.replaceAll("-", "x"))) {
		    // Either we have a version of setMainFrameHeightNow which
		    // doesn't return a boolean or we could not resize our main
		    // frame for real. Too bad.
		}
	    }
	    scrollToYPosition(yPosition);
	} catch (Exception e) {
	    com.google.gwt.user.client.Window.alert("Error setToolHeight " + h
		    + " : " + e);
	}
    }

    /**
     * Calls a Sakai JavaScript method (located in headscripts.js) to resize the
     * iFrame containing the OpenSyllabus application. Originally
     * setMainFrameHeightNow does not return a boolean. It still works but is
     * called twice in this case (see setToolHeight).
     */
    private static native boolean resizeSakaiIFrame(String id) /*-{
							       return parent.setMainFrameHeightNow(id);
							       }-*/;

    /**
     * Returns the current vertical scroll position in browser (ie: for the
     * whole Sakai page).
     * 
     * @return int number of pixels from top
     */
    public static native int getGlobalYPosition() /*-{
						  if (document.all) {
						  // We are In MSIE.
						  return top.document.documentElement.scrollTop;
						  } else {
						  // In Firefox
						  return top.pageYOffset;
						  } 
						  }-*/;

    /**
     * Returns, in pixels, the space above the tool (The Sakai navigation
     * header).
     * 
     * @return number of pixels above the tool
     */
    private static int getToolYOffset() {
	// TODO: find a way for this to work
	// Doesn't work!
	// return getInstance().getRootPanel().getAbsoluteTop()

	// Instead we have hard coded the following values
	if (isInternetExplorer()) {
	    return 177; // 177 ok for MSIE 6/7 with default Sakai 2.5 Skin
	} else {
	    return 163; // 163 ok for FF3 with default Sakai 2.5 Skin
	}
    }

    private static native boolean isInternetExplorer() /*-{
						       if (document.all) {
						       return true;
						       } else {
						       return false;
						       }
						       }-*/;

    /**
     * Returns the current vertical scroll position in OSYL.
     * 
     * @return int number of pixel from top
     */
    public static int getYPosition() {
	if (OsylController.getInstance().isInHostedMode()) {
	    return getInstance().getRootPanel().getAbsoluteTop();
	} else {
	    int global = getGlobalYPosition();
	    int toolOffset = getToolYOffset();
	    return Math.max(0, (global - toolOffset));
	}
    }

    /**
     * Returns the current horizontal scroll position in browser (ie: for the
     * whole Sakai page).
     * 
     * @return int number of pixel from left
     */
    public static native int getGlobalXPosition() /*-{
						  if (document.all) {
						  // We are In MSIE.
						  return top.document.documentElement.scrollLeft;
						  } else {
						  // In Firefox
						  return top.pageXOffset;
						  } 
						  }-*/;

    /**
     * Returns, in pixel, the space to the left of the tool (The Sakai
     * navigation left column).
     * 
     * @return
     */
    private static int getToolXOffset() {
	// TODO: find a way for this to work
	// Doesn't work!
	// return getInstance().getRootPanel().getAbsoluteLeft();
	return 120;
    }

    /**
     * Returns the current horizontal scroll position.
     * 
     * @return int number of pixel from left
     */
    public static int getXPosition() {
	int global = getGlobalXPosition();
	int toolOffset = getToolXOffset();
	return Math.max(0, (global - toolOffset));
    }

    /**
     * Returns the current page height. This is not the window viewPort (visible
     * area) height but the height of complete page including Sakai
     * navigation...
     * 
     * @return int height in pixel
     */
    public static native int getPageHeight() /*-{
					     // This is a Sakai method (located in headscripts.js)
					     return parent.browserSafeDocHeight();
					     }-*/;

    /**
     * Returns the current viewport height. This is the height of current
     * visible area.
     * 
     * @return int viewport height in pixel
     */
    public static int getViewportHeight() {
	if (OsylController.getInstance().isInHostedMode()) {
	    return Window.getClientHeight();
	} else {
	    return getViewportHeightNative();
	}
    }

    /**
     * Returns the current viewport height. This is the height of current
     * visible area.
     * 
     * @return int viewport height in pixel
     */
    private static native int getViewportHeightNative() /*-{

							// This code is adapted from Sakai's browserSafeDocHeight
							if (top.innerHeight) {
							// all except Explorer
							return top.innerHeight;
							} else if (top.document.documentElement
							&& top.document.documentElement.clientHeight) {
							// Explorer 7 and MSIE embedded in FF
							return top.document.documentElement.clientHeight;
							} else if (document.body) {
							// other Explorers (including GWT HostedMode)
							alert("Unsupported browser");
							// return document.body.clientHeight;
							return 400;
							}
							
							}-*/;

    /**
     * Returns the current viewport width. This is the width of current visible
     * area.
     * 
     * @return int viewport width in pixel
     */
    public static int getViewportWidth() {
	// Oddly enough, this works in hosted mode as well as in Sakai! (FF &
	// IE)
	// if (OsylController.getInstance().isInHostedMode()) {
	return Window.getClientWidth();
	// } else {
	// return getViewportWidthNative();
	// }
    }

    /**
     * Returns the current viewport width. This is the width of current visible
     * area.
     * 
     * @return int viewport width in pixel
     */
    private static native int getViewportWidthNative() /*-{

	// This code is adapted from Sakai's browserSafeDocHeight
	if (top.innerWidth) {
		// all except Explorer
		return top.innerWidth;
	} else if (top.document.documentElement
		&& top.document.documentElement.clientWidth) {
		// Explorer 7 and MSIE embedded in FF
		return top.document.documentElement.clientWidth;
	} else if (document.body) {
		// other Explorers (including GWT HostedMode)
		alert("Unsupported browser");
	// return document.body.clientWidth;
		return 600;
	}				       
    }-*/;

    // Shows the y-position in a sticky label
    private void startDebugMsg() {
	final OsylUnobtrusiveAlert a = new OsylUnobtrusiveAlert(getDebugMsg());
	a.show();
	setTopMostPosition(a, 0);
	Timer timer = new Timer() {
	    public void run() {
		a.setMsg("GWT " + GWT.getVersion() + " " + getDebugMsg());
		a.show();
		setTopMostPosition(a, 0);
	    }
	};
	timer.scheduleRepeating(30);

    }

    public static void setDebugMsg(String msg) {
	debugMsg = msg;
    }

    private static String getDebugMsg() {
	if (debugMsg == null) {
	    return "global x/y=" + getGlobalXPosition() + "/"
		    + getGlobalYPosition() + " Osyl x/y=" + getXPosition()
		    + "/" + getYPosition() + " VP w/h=" + getViewportWidth()
		    + "/" + getViewportHeight() + " Client w/h="
		    + Window.getClientWidth() + "/" + Window.getClientHeight();
	} else {
	    return debugMsg;
	}
    }

    /**
     * Shows the specified PopupPanel widget as close as possible to the top of
     * interface (but always below the toolBar). After the specified default
     * delay (3 seconds) it will be hidden.
     * 
     * @param p widget
     */
    public static void showWidgetOnTop(PopupPanel panel) {
	showWidgetOnTop(panel, 3000);
    }

    /**
     * Shows the specified PopupPanel widget as close as possible to the top of
     * interface (but always below the toolBar). After the specified amount of
     * time (in ms) it is hidden.
     * 
     * @param p widget
     * @param time
     */
    public static void showWidgetOnTop(PopupPanel panel, int time) {
	final PopupPanel p = panel;
	final int maxTime = time;
	p.show();
	setTopMostPosition(p);
	final long start = System.currentTimeMillis();
	Timer timer = new Timer() {
	    public void run() {
		setTopMostPosition(p);
		if (System.currentTimeMillis() - start >= maxTime) {
		    p.hide();
		    cancel();
		}
	    }
	};
	timer.scheduleRepeating(30);
    }

    private static void setTopMostPosition(PopupPanel p) {
	setTopMostPosition(p, -100 + getViewportWidth() / 2);
    }

    private static void setTopMostPosition(PopupPanel p, int x) {
	p.setPopupPosition(x, Math.max(getTopMostPosition(), getYPosition()));
    }

    /**
     * getTopMostPosition returns the toolBar height including every possible
     * margin. We return different hardCoded values according to the current
     * context. There is surely a better way to do this.
     */
    private static int getTopMostPosition() {
	if (OsylController.getInstance().isInHostedMode()) {
	    return 62;
	} else {
	    // We return 26px (24 for the toolBar + 2 for its borders)
	    return 47; // Is OK for MSIE 6/7 and Firefox
	}
    }

    /**
     * Centers the specified {@link PopupPanel} on the current view. This method
     * takes into account the current position in OSYLEditor, as well as the
     * Sakai navigation header to ensure the centering of the specified pop-up.
     * Warning: the widget must already be visible otherwise its dimensions may
     * not be available! You should ensure that widget.show() is called before.
     * 
     * @param widget the pop-up to center
     */
    public static void centerObject(PopupPanel widget) {
	int width = widget.getOffsetWidth();
	int height = widget.getOffsetHeight();
	widget.setPopupPosition(getXPosition() + (getViewportWidth() - width)
		/ 2, getYPosition() + (getViewportHeight() - height) / 2);
    }

    /**
     * Makes the main window (the one containing Sakai) scroll to the specified
     * position.
     * 
     * @param y pixels from the top
     */
    public static native void scrollToYPosition(int y) /*-{
						       top.scrollTo(0,y);
						       }-*/;

    /**
     * Returns the RootPanel's width.
     * 
     * @return int width in pixel
     */
    public int getToolWidth() {
	return getRootPanel().getOffsetWidth();
    }

    /**
     * Returns the Sakai iFrame ID based on current URL. WARNING: sometimes the
     * ID provided in URL is invalid: all "-" (dash character) needs to be
     * replaced by "x"!
     * 
     * @return current iFrame ID
     */
    public static String getSakaiFrameId() {
	String url = GWT.getModuleBaseURL();
	String tool = "/tool/";
	int toolPos = url.indexOf(tool) + tool.length();
	int nextSlash = url.indexOf('/', toolPos);
	return "Main" + url.substring(toolPos, nextSlash);
    }

    public OsylViewable getView() {
	return editorEntryPointView;
    }

    public void setView(OsylViewable newView) {
	editorEntryPointView = newView;
    }

    public COContent getModel() {
	return editorEntryPointModel;
    }

    public void setModel(COContent editorEntryPointModel) {
	this.editorEntryPointModel = editorEntryPointModel;
    }

    /**
     * Returns the the Course Outline with XML representation. If the model has
     * been modified, the XML representation needs to be updated by calling
     * prepareModelForSave().
     * 
     * @return {@link COSerialized} Course Outline including XML representation
     */
    public COSerialized getSerializedCourseOutline() {
	return serializedCO;
    }

    public COSerialized getUpdatedSerializedCourseOutline() {
	COModeled comodeled = new COModeled();
	comodeled.setSchemaVersion(this.modeledCo.getSchemaVersion());
	comodeled.setModeledContent(getModel());
	comodeled.model2XML(true);
	COSerialized cos = new COSerialized();
	cos.setContent(comodeled.getContent());
	return cos;
    }

    /**
     * Called by the Controller to update the XML representation of current
     * model. This is needed before sending the Course Outline to the server.
     */
    public void prepareModelForSave() {
	getModel().addProperty(COPropertiesType.MODIFIED, OsylDateUtils
		    .getNowDateAsXmlString());
	modeledCo.setModeledContent(getModel());
	modeledCo.model2XML(false);
	if (serializedCO == null) {
	    // This happens if we are offline (ie: we did not receive a
	    // COSerialized, therefore we don't have an ID! It won't save
	    // because we are offline, but it won't crash (NPE) here!
	    serializedCO = new COSerialized();
	}
	serializedCO.setContent(modeledCo.getContent());
    }

    public static String getExecMode() {
	return execmode;
    }

}
