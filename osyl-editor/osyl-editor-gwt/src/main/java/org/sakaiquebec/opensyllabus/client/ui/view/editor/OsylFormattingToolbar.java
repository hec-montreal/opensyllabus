/*******************************************************************************
 * $Id: OsylAbstractConfigView.java 636 2008-05-27 15:41:58Z remi.saias@hec.ca $
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

package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;
import org.sakaiquebec.opensyllabus.shared.util.LinkValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * A WYSIWYG editor toolbar providing access to standard formatting functions:
 * bold, italic, etc.
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylFormattingToolbar extends Composite {

    
    private static final String BR = "MARKUP_BR";
    private static final String P1 = "MARKUP_P_BEGIN";
    private static final String P2 = "MARKUP_P_END";
    
    /**
     * We use an inner EventListener class to avoid exposing event methods on
     * the RichTextToolbar itself.
     */
    private class EventListener implements ClickHandler, KeyUpHandler {

	public void onClick(ClickEvent event) {
	    Widget sender = (Widget) event.getSource();
	    if (sender == bold) {
		formatter.toggleBold();
	    } else if (sender == italic) {
		formatter.toggleItalic();
	    } else if (sender == underline) {
		formatter.toggleUnderline();
	    } else if (sender == strikethrough) {
		formatter.toggleStrikethrough();
	    } else if (sender == indent) {
		formatter.rightIndent();
	    } else if (sender == outdent) {
		formatter.leftIndent();
	    } else if (sender == justifyLeft) {
		formatter.setJustification(RichTextArea.Justification.LEFT);
	    } else if (sender == justifyCenter) {
		formatter.setJustification(RichTextArea.Justification.CENTER);
	    } else if (sender == justifyRight) {
		formatter.setJustification(RichTextArea.Justification.RIGHT);
	    } else if (sender == createLink) {
		String url =
			Window.prompt(uiMessages.getMessage("enterLinkURL"),
				"http://");
		boolean valid = true;
		String messages = "";
		if (url != null) {
		    if (!LinkValidator.isValidLink(url)) {
			valid = false;
			messages =
				uiMessages.getMessage("LinkEditor.unvalidLink");
		    }
		    if (!valid) {
			OsylAlertDialog osylAlertDialog =
				new OsylAlertDialog(uiMessages
					.getMessage("Global.error"), messages);
			osylAlertDialog.center();
			osylAlertDialog.show();
		    } else {
			createHTMLLink(LinkValidator.parseLink(url));
		    }
		}
	    } else if (sender == removeLink) {
		formatter.removeLink();
	    } else if (sender == ol) {
		formatter.insertOrderedList();
	    } else if (sender == ul) {
		formatter.insertUnorderedList();
	    } else if (sender == removeFormat) {
		String t = getSelectedHTML(richText.getElement());
		t=t.replaceAll("<!--[\\s\\S]+?-->", "");
		t=t.replaceAll("&lt;!--[\\s\\S]+?--&gt;", "");
		t=t.replaceAll("<style[\\s\\S]+?</style>", "");
		t=t.replaceAll("<[pP]>|<[pP][\\s\\S]+?>", P1);
		t=t.replaceAll("</[pP]>", P2);
		t=t.replaceAll("<br[\\s\\S]+?/?>|<BR[\\s\\S]+?/?>", BR);
		t=t.replaceAll("<[\\s\\S]+?>", "");
		t=t.replaceAll(BR, "<br>");
		t=t.replaceAll(P1,"<p>");
		t=t.replaceAll(P2,"</p>");
		clearSelection(richText.getElement(), t);
		formatter.insertHTML(t);
		//formatter.removeFormat();
		
	    } else if (sender == richText) {
		// We use the RichTextArea's onKeyUp event to update the toolbar
		// status.
		// This will catch any cases where the user moves the cursor
		// using the
		// keyboard, or uses one of the browser's built-in keyboard
		// shortcuts.
		updateStatus();
	    }
	}

	public void onKeyUp(KeyUpEvent event) {
	    Widget sender = (Widget) event.getSource();
	    if (sender == richText) {
		// We use the RichTextArea's onKeyUp event to update the toolbar
		// status.
		// This will catch any cases where the user moves the cursor
		// using the
		// keyboard, or uses one of the browser's built-in keyboard
		// shortcuts.
		updateStatus();
	    }
	}
    }

    /**
     * Image Bundle
     */
    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    /**
     * User interface message Bundle
     */
    private OsylConfigMessages uiMessages;
    /**
     * My listener
     */
    private EventListener listener = new EventListener();

    private RichTextArea richText;
    private RichTextArea.Formatter formatter;

    private HorizontalPanel topPanel;
    private ToggleButton bold;
    private ToggleButton italic;
    private ToggleButton underline;
    private ToggleButton strikethrough;
    private PushButton indent;
    private PushButton outdent;
    private PushButton justifyLeft;
    private PushButton justifyCenter;
    private PushButton justifyRight;
    private PushButton ol;
    private PushButton ul;
    private PushButton createLink;
    private PushButton removeLink;
    private PushButton removeFormat;

    /**
     * Creates a toolBar that will work for a not-yet-specified
     * {@link RichTextArea}.
     */
    public OsylFormattingToolbar() {
	this(null);
    }

    /**
     * Creates a toolBar that works for the specified {@link RichTextArea}.
     * 
     * @param richText the {@link RichTextArea} to be formatted
     */
    public OsylFormattingToolbar(RichTextArea richText) {
	setRichText(richText);
	this.topPanel = new HorizontalPanel();
	uiMessages = OsylController.getInstance().getUiMessages();

	topPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	topPanel.setStylePrimaryName("Osyl-EditorToolbar-TopPanel");

	if (formatter != null) {
	    topPanel.add(bold =
		    createToggleButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_bold()), uiMessages
			    .getMessage("FormattingToolbar.bold")));
	    topPanel.add(italic =
		    createToggleButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_italic()), uiMessages
			    .getMessage("FormattingToolbar.italic")));
	    topPanel.add(underline =
		    createToggleButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_underline()),
			    uiMessages.getMessage("FormattingToolbar.underline")));
	    topPanel.add(justifyLeft =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_justifyLeft()),
			    uiMessages.getMessage("FormattingToolbar.justifyLeft")));
	    topPanel.add(justifyCenter =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_justifyCenter()),
			    uiMessages.getMessage("FormattingToolbar.justifyCenter")));
	    topPanel.add(justifyRight =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_justifyRight()),
			    uiMessages.getMessage("FormattingToolbar.justifyRight")));
	    topPanel.add(strikethrough =
		    createToggleButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_strikeThrough()),
			    uiMessages.getMessage("FormattingToolbar.strikeThrough")));
	    topPanel.add(indent =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_indent()), uiMessages
			    .getMessage("FormattingToolbar.indent")));
	    topPanel.add(outdent =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_outdent()), uiMessages
			    .getMessage("FormattingToolbar.outdent")));
	    topPanel.add(ol =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_ol()), uiMessages
			    .getMessage("FormattingToolbar.ol")));
	    topPanel.add(ul =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_ul()), uiMessages
			    .getMessage("FormattingToolbar.ul")));
	    topPanel.add(createLink =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_createLink()),
			    uiMessages.getMessage("FormattingToolbar.createLink")));
	    topPanel.add(removeLink =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_removeLink()),
			    uiMessages.getMessage("FormattingToolbar.removeLink")));
	    topPanel.add(removeFormat =
		    createPushButton(AbstractImagePrototype.create(
			    osylImageBundle.rtt_removeFormat()),
			    uiMessages.getMessage("FormattingToolbar.removeFormat")));
	}

	initWidget(topPanel);
    }

    /**
     * Create a push button
     * 
     * @param img Button image
     * @param tip Button text
     * @return The push button
     */
    private PushButton createPushButton(AbstractImagePrototype img, String tip) {
	PushButton pb = new PushButton(img.createImage());
	pb.addClickHandler(listener);
	pb.setTitle(tip);
	return pb;
    }

    /**
     * Create a toggle Button
     * 
     * @param img Button image
     * @param tip Button text
     * @return The toggle button
     */
    private ToggleButton createToggleButton(AbstractImagePrototype img,
	    String tip) {
	ToggleButton tb = new ToggleButton(img.createImage());
	tb.addClickHandler(listener);
	tb.setTitle(tip);
	return tb;
    }

    /**
     * Updates the status of all the stateful buttons.
     */
    private void updateStatus() {
	if (formatter != null) {
	    bold.setDown(formatter.isBold());
	    italic.setDown(formatter.isItalic());
	    underline.setDown(formatter.isUnderlined());
	    strikethrough.setDown(formatter.isStrikethrough());
	}
    }

    public RichTextArea getRichText() {
	return richText;
    }

    public void setRichText(RichTextArea richText) {
	this.richText = richText;
	if (richText != null) {
	    this.formatter = richText.getFormatter();
	} else {
	    this.formatter = null;
	}
    }

    /**
     * Native JavaScript that returns the selected text and position of the
     * start
     **/
    private static native JsArrayString getSelection(Element elem) /*-{
    var txt = "";
       	var pos = 0;
       	var range;
       	var parentElement;
       	var container;

    if (elem.contentWindow.getSelection) {
    	txt = elem.contentWindow.getSelection();
          		pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
           } else if (elem.contentWindow.document.getSelection) {
          		txt = elem.contentWindow.document.getSelection();
    	pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
       	} else if (elem.contentWindow.document.selection) {
       		range = elem.contentWindow.document.selection.createRange();
          		txt = range.text;
          		parentElement = range.parentElement();
          		container = range.duplicate();
          		container.moveToElementText(parentElement);
          		container.setEndPoint('EndToEnd', range);
          		pos = container.text.length - range.text.length;
           }
       	return [""+txt,""+pos];
       }-*/;
    
    private static native String getSelectedHTML(Element elem) /*-{
     	var rng=null,html="";                  
       if (elem.contentWindow.document.selection && elem.contentWindow.document.selection.createRange)      
       {         
            rng=elem.contentWindow.document.selection.createRange();      
            if( rng.htmlText )       
            {       
               html=rng.htmlText;       
            }       
            else if(rng.length >= 1)       
            {       
               html=rng.item(0).outerHTML;       
            }      
       }      
       else if (elem.contentWindow.getSelection)      
       {      
             rng=elem.contentWindow.getSelection();      
             if (rng.rangeCount > 0 && window.XMLSerializer)      
             {      
                   rng=rng.getRangeAt(0);      
                   html=new XMLSerializer().serializeToString(rng.cloneContents()); 
                   //html=new XMLSerializer().serializeToString(rng.extractContents());       
             }      
       }      
       return html;      
    }-*/;

    
    private static native void clearSelection(Element elem, String html) /*-{                  
       if (elem.contentWindow.document.selection)      
       {      
             //ie
             elem.contentWindow.document.selection.clear();      
       }      
       else if (elem.contentWindow.getSelection)      
       {      
       	     //other browser
             var rng=elem.contentWindow.getSelection();      
             if (rng.rangeCount > 0)      
             {      
                   rng=rng.getRangeAt(0);      
                   rng.deleteContents();         
             }      
       }           
   }-*/;
    
    private void createHTMLLink(String url) {
	url = LinkValidator.parseLink(url);
	formatter.insertHTML("<a target=\"_blank\" href=\"" + url + "\">"
		+ getSelection(richText.getElement()).get(0) + "</a>");
    }
}
