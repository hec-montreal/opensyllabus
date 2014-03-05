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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.OsylMainView;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewable;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COModeled;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
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
 * @author <a href="mailto:gilles-philippe.leblanc@umontreal.ca">Gilles-Philippe
 *         Leblanc</a>
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
    private RootPanel rootPanel;
    private OsylMainView editorMainView;
    private static OsylEditorEntryPoint singleton;
    private OsylViewable editorEntryPointView;

    public static String execmode = "prod";

    private static final int MIN_TOOL_HEIGHT = 200;
    public static final int MIN_TOOL_WIDTH = 640;

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
		event.setMessage(osylController.getUiMessage("save.reallyQuit"));
	    }
	}); // addWindowClosingHandler

	Window.addCloseHandler(new CloseHandler<Window>() {

	    public void onClose(CloseEvent<Window> event) {
		if (!osylController.isReadOnly()) {
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

    public void refreshModel(COSerialized co) {
	this.serializedCO = co;

	// And now we initialize the model from the XML content
	this.modeledCo = new COModeled();
	this.modeledCo.setContent(co.getContent());
	this.modeledCo.XML2Model();
	setModel(this.modeledCo.getModeledContent());
	editorMainView =
		new OsylMainView(getModel(), OsylController.getInstance());
	editorMainView.initView();
	this.setView(editorMainView);
	refreshView();
    }

    public Map<String, Map<String, String>> getResourceContextVisibilityMap() {
	return this.modeledCo.getResourceContextVisibilityMap();
    }

    public void setResourceContextVisibilityMap(
	    Map<String, Map<String, String>> m) {
	this.modeledCo.setResourceContextVisibilityMap(m);
    }

    public Map<String, Map<String, String>> getResourceContextTypeMap() {
	return this.modeledCo.getResourceContextTypeMap();
    }

    public void setResourceContextTypeMap(Map<String, Map<String, String>> m) {
	this.modeledCo.setResourceContextTypeMap(m);
    }

    public Map<String, Map<String, String>> getDocumentContextLicenceMap() {
	return this.modeledCo.getDocumentContextLicenceMap();
    }

    public void setDocumentContextLicenceMap(Map<String, Map<String, String>> m) {
	this.modeledCo.setDocumentContextLicenceMap(m);
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
	editorMainView = new OsylMainView(getModel(), osylController);
	editorMainView.setAccess(osylController.getCOSerialized().getAccess());
	editorMainView.initView();

	this.setView(editorMainView);
	rootPanel.add((Widget) this.getView());

	setBrowserEvents(this);
	Timer t = new Timer() {
	    public void run() {
		setToolSize();
		setSakaiEnvironment();
	    }
	};
	setSakaiScrollBar(isInSakai() ? "hidden" : "auto");
	t.scheduleRepeating(500);
	if (OsylController.getInstance().isInHostedMode()) {
	    DOM.setStyleAttribute(getRootPanel().getElement(), "fontSize",
		    "12px");
	}
	// Uncomment this display debug messages specified with setDebugMsg().
	// startDebugMsg();
    }

    public void refreshView() {
	rootPanel.clear();
	rootPanel.add((Widget) this.getView());
	setToolSize();
    }

    /**
     * Set the size of the tool. We call this when the Window's Browser is
     * resized.
     */
    public void setToolSize() {
	int h = Math.max(
			Math.round(getDesiredToolHeight()),
			Math.max(editorMainView.getOsylTreeView().getTree()
					.getOffsetHeight(), editorMainView.getWorkspaceView()
					.getCurrentView().getOffsetHeight()) + 144);
	int sc = 0;
	if (isInSakai()) {
	    setSakaiIFrameHeight(h);
	}
	((OsylViewableComposite) getView()).setHeight((h - 16) + "px");
	if (!isInternetExplorer() && toolWidthMinReached())
	    sc = 18;
	getRootPanel().setHeight((h - sc) + "px");

	if (OsylController.getInstance().isInPreview()) {
	    OsylController.getInstance().getMainView().resize();
	} else {
	    editorMainView.resize(true);
	}
	setToolWidth();
    }

    /**
     * overrides the default initial Sakai environnement UI for correct display
     * of the tool.
     */
    private static void setSakaiEnvironment() {
	if (isInSakai()) {
	    Element col1 = getElementById("col1");
	    Element portlet =
		    getElementsByClass("portletMainWrap", col1, "DIV")[0];
	    DOM.setStyleAttribute(col1, "paddingRight", "0");
	    DOM.setStyleAttribute(portlet, "width", "100%");
	    setSakaiScrollBar("auto");
	}
    }

    /**
     * set the display type for the scrollbars of the iframe containing the
     * tool.
     *
     * @params the css type of display for the scrollbars.
     */
    private static void setSakaiScrollBar(String value) {
	DOM.setStyleAttribute(getSakaiToolIframe(), "overflow", value);
    };

    /**
     * Returns the Sakai current iframe containing the tool. If the tool isn't
     * in Sakai environment (stand alone), return current window.
     *
     * @return the Sakai iframe element.
     */
    public static native Element getSakaiToolIframe() /*-{
						      var elm = $wnd.parent.document.getElementById($wnd.name);
						      return (elm != null ? elm : $wnd.document.body);
						      }-*/;

    /**
     * Returns the Sakai footer DOM's element. If the tool isn't in Sakai
     * environment (stand alone), return nothing.
     *
     * @return the Sakai footer element.
     */
    public static native Element getSakaiFooter() /*-{
						  return $wnd.parent.document.getElementById("footer");
						  }-*/;

    /**
     * Returns the Sakai footer DOM's element. If the tool isn't in Sakai
     * environment (stand alone), return nothing.
     *
     * @return the Sakai footer element.
     */
    public static native Element getSakaiLeftMenu() /*-{
						    return $wnd.parent.document.getElementById("toolMenuWrap");
						    }-*/;

    /**
     * Returns, in pixels, the total height of the Sakai left menu section. If
     * the tool isn't in Sakai environment (stand alone), return 0.
     *
     * @return number of pixels for the height of the Sakai left menu.
     */
    public static int getSakaiLeftMenuHeight() {
	Element elm = getSakaiLeftMenu();
	return (elm != null ? elm.getOffsetHeight() + elm.getAbsoluteTop() : 0);
    }

    /**
     * Returns, in pixels, the total height of the Sakai footer section. If the
     * tool isn't in Sakai environment (stand alone), return 0.
     *
     * @return number of pixels for the height of the Sakai footer.
     */
    public static int getSakaiFooterHeight() {
	Element elm = getSakaiFooter();
	return (elm != null ? elm.getOffsetHeight() : 0);
    }

    /**
     * Returns, in pixels, the desired total height of the tool section based on
     * viewport height less the header and the footer of Sakai. The height is
     * also based on the presence of scrollbar du to left Sakai menu height and
     * the scrollY of the Window. If the tool isn't in Sakai environment (stand
     * alone), return the viewport height.
     *
     * @return number of pixels for the desired height for the tool.
     */
    private static int getDesiredToolHeight() {
	int viewportHeight = getViewportHeight();
	int diff = 0;
	Element sakaiLeftMenu = getSakaiLeftMenu();
	if (sakaiLeftMenu != null) {
	    int totalHeight = getTotalHeight();
	    int footerHeight = getSakaiFooterHeight();
	    int scrollY = getGlobalScrollY();
	    int sakaiLeftMenuMaxHeight =
		    sakaiLeftMenu.getOffsetHeight()
			    + sakaiLeftMenu.getAbsoluteTop();
	    if (viewportHeight > sakaiLeftMenuMaxHeight) {
		if (viewportHeight < (sakaiLeftMenuMaxHeight + footerHeight)) {
		    int bottom = totalHeight - scrollY - viewportHeight;
		    if (bottom < footerHeight) {
			diff -= bottom + scrollY;
		    }
		    /*
		     * Sometimes a space is inserted between the Sakai footer
		     * and the left menu this fix remove it.
		     */
		    int fix =
			    Math.max(totalHeight
				    - (sakaiLeftMenuMaxHeight + footerHeight),
				    0);
		    if (fix > 0) {
			diff += fix;
		    }

		}
		diff += footerHeight;
	    }
	    if (sakaiLeftMenuMaxHeight >= (scrollY + viewportHeight)) {
		diff -= scrollY;
	    } else {
		if (viewportHeight < sakaiLeftMenuMaxHeight) {
		    diff -= (sakaiLeftMenuMaxHeight - viewportHeight);
		}
	    }
	}
	return Math.max(viewportHeight - getToolAbsoluteTop() - diff,
		MIN_TOOL_HEIGHT);
    }

    /**
     * Returns the current viewport height. This is the height of current
     * visible area.
     *
     * @return int viewport height in pixel
     */
    public static native int getViewportHeight() /*-{
						 var o = $wnd.top;
						 myHeight = 0;
						 if( typeof( o.innerHeight ) == 'number' ) {
						 //Non-IE
						 myHeight = o.innerHeight;
						 } else if( o.document.documentElement && o.document.documentElement.clientHeight) {
						 //IE 6+ in 'standards compliant mode'
						 myHeight = o.document.documentElement.clientHeight;
						 } else if( o.document.body && o.document.body.clientHeight) {
						 //IE 4 compatible
						 myHeight = o.document.body.clientHeight;
						 }
						 return myHeight;
						 }-*/;

    /**
     * Returns the current total page's height. This is the total height of all
     * objects in the page, including Sakai.
     *
     * @return int total page height in pixel
     */
    public static native int getTotalHeight() /*-{
					      var o = $wnd.top;
					      // FIREFOX
					      var height = o.document.documentElement.scrollHeight;
					      // IE7+ & OPERA
					      if(o.document.documentElement.clientHeight > height ) {
					      height  = o.document.documentElement.clientHeight;
					      }
					      // SAFARI
					      if(o.document.body.scrollHeight > height) {
					      height = o.document.body.scrollHeight;
					      }
					      return height;
					      }-*/;

    /**
     * Returns the current viewport width. This is the width of current visible
     * area.
     *
     * @return int viewport width in pixel
     */
    public static native int getViewportWidth() /*-{
						var o = $wnd.top;
						var myWidth = 0
						if( typeof( o.innerWidth ) == 'number' ) {
						//Non-IE
						myWidth = o.innerWidth;
						} else if( o.document.documentElement && o.document.documentElement.clientWidth) {
						//IE 6+ in 'standards compliant mode'
						myWidth = o.document.documentElement.clientWidth;
						} else if( o.document.body && o.document.body.clientWidth) {
						//IE 4 compatible
						myWidth = o.document.body.clientWidth;
						}
						return myWidth;
						}-*/;

    /**
     * set Events for the browser, resize and scroll. As Osyl is in use in a
     * iframe, GWT don't provide correct implementation for listening the
     * Browser events, only iframe Events.
     */
    private static native void setBrowserEvents(OsylEditorEntryPoint o) /*-{
									function addEvent(obj, type, fn, par) {
									if(obj.addEventListener){
									obj.addEventListener(type, function(event){
									return fn.call(obj, event, par);
									}, false );
									}else if(obj.attachEvent){
									obj.attachEvent("on"+type, function(e){
									if (!e) var e = $wnd.event;
									return fn.call(obj, e, par);
									});
									}
									}
									function resize() {
									o.@org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint::setToolSize()();
									}
									addEvent($wnd.top,'resize',resize);
									addEvent($wnd.top,'scroll',resize);
									var n = $wnd.parent.document.getElementById($wnd.name);
									if ($wnd != $wnd.parent && n != null) n.style.marginBottom = "0";
									}-*/;

    private static void setSakaiIFrameHeight(int h) {
	DOM.setStyleAttribute(getSakaiToolIframe(), "height", h + "px");
    }

    /**
     * If Osyl is currently loaded in Sakai environment or is is stand alone.
     *
     * @return Boolean state if is in Sakai.
     */

    private static native boolean isInSakai() /*-{
					      return $wnd != $wnd.parent;
					      }-*/;

    /**
     * If the current Browser is Internet Explorer.
     *
     * @return Boolean state if is in IE.
     */
    public static native boolean isInternetExplorer() /*-{
						      return($doc.all ? true: false);
						      }-*/;

    /**
     * Returns the current vertical scroll position in browser (ie: for the
     * whole Sakai page).
     *
     * @return int number of pixels from top
     */
    private static native int getGlobalScrollY() /*-{
						 if ($doc.all) {
						 // We are In MSIE.
						 return top.document.documentElement.scrollTop;
						 } else {
						 // In Firefox
						 return top.pageYOffset;
						 }
						 }-*/;

    /**
     * Returns the current horizontal scroll position in browser (ie: for the
     * whole Sakai page).
     *
     * @return int number of pixel from left
     */
    public static native int getGlobalScrollX() /*-{
						if ($doc.all) {
						// We are In MSIE.
						return $wnd.top.document.documentElement.scrollLeft;
						} else {
						// In Firefox
						return $wnd.top.pageXOffset;
						}
						}-*/;

    /**
     * Returns, in pixels, the space above the tool (The Sakai navigation
     * header). If the tool isn't in Sakai environment (stand alone), return 0.
     *
     * @return number of pixels above the tool
     */
    public static int getToolAbsoluteTop() {
	return getSakaiToolIframe().getAbsoluteTop();
    }

    /**
     * Returns, in pixel, the space to the left of the tool (The Sakai
     * navigation left column). If the tool isn't in Sakai environment (stand
     * alone), return 0.
     *
     * @return number of pixels before the tool
     */
    public static int getToolAbsoluteLeft() {
	return getSakaiToolIframe().getAbsoluteLeft();
    }

    /**
     * Returns the current vertical scroll position in OSYL.
     *
     * @return int number of pixel from top
     */
    public static int getYPosition() {
	if (!isInSakai()) {
	    return getInstance().getRootPanel().getAbsoluteTop();
	} else {
	    int global = getGlobalScrollY();
	    int toolOffset = getToolAbsoluteTop();
	    return Math.max(0, (global - toolOffset));
	}
    }

    /**
     * Returns the current horizontal scroll position.
     *
     * @return int number of pixel from left
     */
    public static int getXPosition() {
	int global = getGlobalScrollX();
	int toolOffset = getToolAbsoluteLeft();
	return Math.max(0, (global - toolOffset));
    }

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
	    return "global x/y=" + getGlobalScrollX() + "/"
		    + getGlobalScrollY() + " Osyl x/y=" + getXPosition() + "/"
		    + getYPosition() + " VP w/h=" + getViewportWidth() + "/"
		    + getViewportHeight() + " Client w/h="
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
	setTopMostPosition(p,
		(Window.getClientWidth() - p.getOffsetWidth()) / 2);
    }

    private static void setTopMostPosition(PopupPanel p, int x) {
	p.setPopupPosition(x, getTopMostPosition());
    }

    /**
     * getTopMostPosition returns the top most position. Since Osyl have the
     * title, there is no need to place this value below the tool menu. So we
     * simply put it with the course title.
     */
    private static int getTopMostPosition() {
	return Window.getScrollTop() + 2;
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
	width =
		Math.max(Window.getScrollLeft(),
			(Window.getClientWidth() - width) / 2);
	height =
		Math.max(Window.getScrollTop(),
			(Window.getClientHeight() - height) / 2);
	widget.setPopupPosition(width, height);
    }

    /**
     * Makes the main window (the one containing Sakai) scroll to the specified
     * position.
     *
     * @param y pixels from the top
     */
    public static native void scrollToYPosition(int y) /*-{
						       $wnd.top.scrollTo(0,y);
						       }-*/;

    /**
     * Returns the RootPanel's width.
     *
     * @return int width in pixel
     */
    public int getToolWidth() {
	return getRootPanel().getOffsetWidth();
    }

    public void setToolWidth() {
	String width = toolWidthMinReached() ? MIN_TOOL_WIDTH + "px" : "auto";
	DOM.setStyleAttribute(getSakaiToolIframe(), "overflowX",
		toolWidthMinReached() ? "auto" : "hidden");
	getRootPanel().setWidth(width);
    }

    public Boolean toolWidthMinReached() {
	return Window.getClientWidth() < MIN_TOOL_WIDTH;
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
	getModel().addProperty(COPropertiesType.MODIFIED,
		OsylDateUtils.getCurrentDateAsXmlString());
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

    public COModelInterface getCoModelInterfaceWithId(String id) {
	return getCoModelInterfaceWithId(getModel(), id);
    }

    @SuppressWarnings("unchecked")
    private COModelInterface getCoModelInterfaceWithId(COElementAbstract coe,
	    String id) {
	COModelInterface comi = null;
	List<COElementAbstract> childrenList = coe.getChildrens();
	boolean find = false;
	Iterator<COElementAbstract> iter = childrenList.iterator();
	while (iter.hasNext()) {
	    comi = (COModelInterface) iter.next();
	    if (id.equals(comi.getId())) {
		find = true;
		break;
	    } else {
		if (comi instanceof COElementAbstract) {
		    COElementAbstract coea = (COElementAbstract) comi;
		    comi = getCoModelInterfaceWithId(coea, id);
		    if (comi != null) {
			find = true;
			break;
		    }
		}
	    }
	}
	if (!find)
	    return null;
	return comi;
    }

    // TODO: Move function used by the browser in the file BrowserUtil.java
    public static int parsePixels(String value) {
	int pos = value.indexOf("px");
	int posComma = value.indexOf(".");

	if (posComma != -1){
		value = value.substring(0, posComma);
		}
	else if (pos != -1){
		value = value.substring(0, pos);
		}

	return Integer.parseInt(value);
    }

    private static Element[] createElements(int len) {
	return new Element[len];
    }

    private static void setElement(Element[] container, int pos, Element value) {
	container[pos] = value;
    }

    /**
     * Find the DOM element with the specified Id. Created the search in the
     * Sakai environnement.
     *
     * @params String elm: DOM Id to search.
     * @return Element with the specific Id.
     */
    public static native Element getElementById(String elm) /*-{
							    return $wnd.top.document.getElementById(elm);
							    }-*/;

    /**
     * set the display type for the scrollbars of the iframe containing the
     * tool.
     *
     * @params String searchClass: DOM class to search.
     * @params Element node: root node where begin the search (optionnal).
     * @params String tag: specific html tag to search (optionnal).
     * @return Array Element[] of each elements found by the query.
     */
    public static native Element[] getElementsByClass(String searchClass,
	    Element node, String tag) /*-{
				      var classElements = [];
				      if ( node == null ) node = $doc;
				      if ( tag == null ) tag = '*';
				      var els = node.getElementsByTagName(tag);
				      var elsLen = els.length;
				      var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
				      for (i = 0, j = 0; i < elsLen; i++) {
				      if ( pattern.test(els[i].className) ) {
				      classElements[j] = els[i];
				      j++;
				      }
				      }
				      var newElements =
				      @org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint::createElements(I)(classElements.length);
				      for (i = 0;i < classElements.length; i++) {
				      @org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint::setElement([Lcom/google/gwt/user/client/Element;ILcom/google/gwt/user/client/Element;)(newElements, i, classElements[i]);
				      }
				      return newElements;
				      }-*/;

    public void changePropertyInMap(Map<String, String> map, String value) {
	if (map != null) {
	    for (Entry<String, String> entry : map.entrySet()) {
		entry.setValue(value);
	    }
	}
    }

    public void changePropertyForResourceProxy(Map<String, String> map,
	    String propertyName, String propertyValue) {
	if (map != null) {
	    for (Entry<String, String> entry : map.entrySet()) {
		COModelInterface comi =
			OsylEditorEntryPoint.getInstance()
				.getCoModelInterfaceWithId(entry.getKey());
		COContentResourceProxy coContentResourceProxy =
			(COContentResourceProxy) comi;
		coContentResourceProxy.addProperty(propertyName, propertyValue);
	    }
	}
    }

    public void changePropertyForResource(Map<String, String> map,
	    String propertyName, String propertyValue) {
	if (map != null) {
	    for (Entry<String, String> entry : map.entrySet()) {
		COModelInterface comi =
			OsylEditorEntryPoint.getInstance()
				.getCoModelInterfaceWithId(entry.getKey());
		COContentResourceProxy coContentResourceProxy =
			(COContentResourceProxy) comi;
		coContentResourceProxy.getResource().addProperty(propertyName,
			propertyValue);
	    }
	}
    }

    public void changePropertyForResource(Map<String, String> map,
	    String propertyName, String propertyType, String propertyValue) {
	if (map != null) {
	    for (Entry<String, String> entry : map.entrySet()) {
		COModelInterface comi =
			OsylEditorEntryPoint.getInstance()
				.getCoModelInterfaceWithId(entry.getKey());
		COContentResourceProxy coContentResourceProxy =
			(COContentResourceProxy) comi;
		coContentResourceProxy.getResource().addProperty(propertyName,
			propertyType, propertyValue);
	    }
	}
    }

    public void addAttributeToProperty(Map<String, String> map,
	    String propertyName, String propertyType, String attributeName,
	    String value) {
	if (map != null) {
	    for (Entry<String, String> entry : map.entrySet()) {
		COModelInterface comi =
			OsylEditorEntryPoint.getInstance()
				.getCoModelInterfaceWithId(entry.getKey());
		COContentResourceProxy coContentResourceProxy =
			(COContentResourceProxy) comi;
		coContentResourceProxy.getResource()
			.getCOProperty(propertyName, propertyType)
			.addAttribute(attributeName, value);
	    }
	}
    }

    public void removePropertyForResource(Map<String, String> map,
	    String propertyName, String propertyType) {
	if (map != null) {
	    for (Entry<String, String> entry : map.entrySet()) {
		COModelInterface comi =
			OsylEditorEntryPoint.getInstance()
				.getCoModelInterfaceWithId(entry.getKey());
		COContentResourceProxy coContentResourceProxy =
			(COContentResourceProxy) comi;
		coContentResourceProxy.getResource().removeProperty(
			propertyName, propertyType);
	    }
	}
    }

}
