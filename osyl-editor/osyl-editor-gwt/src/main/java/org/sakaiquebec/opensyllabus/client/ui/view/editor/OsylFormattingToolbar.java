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
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
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

    /**
     * We use an inner EventListener class to avoid exposing event methods on
     * the RichTextToolbar itself.
     */
    private class EventListener implements ClickHandler, KeyUpHandler {

	/**
	 * @see ClickListener#onClick(Widget)
	 */
	public void onClick(ClickEvent event) {
	    Widget sender = (Widget) event.getSource();
	    if (sender == bold) {
		basic.toggleBold();
	    } else if (sender == italic) {
		basic.toggleItalic();
	    } else if (sender == underline) {
		basic.toggleUnderline();
	    } else if (sender == strikethrough) {
		extended.toggleStrikethrough();
	    } else if (sender == indent) {
		extended.rightIndent();
	    } else if (sender == outdent) {
		extended.leftIndent();
	    } else if (sender == justifyLeft) {
		basic.setJustification(RichTextArea.Justification.LEFT);
	    } else if (sender == justifyCenter) {
		basic.setJustification(RichTextArea.Justification.CENTER);
	    } else if (sender == justifyRight) {
		basic.setJustification(RichTextArea.Justification.RIGHT);
	    } else if (sender == createLink) {
		String url =
			Window.prompt(uiMessages.getMessage("enterLinkURL"),
				"http://");
		if (url != null) {
		    extended.createLink(url);
		}
	    } else if (sender == removeLink) {
		extended.removeLink();
	    } else if (sender == ol) {
		extended.insertOrderedList();
	    } else if (sender == ul) {
		extended.insertUnorderedList();
	    } else if (sender == removeFormat) {
		extended.removeFormat();
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

	/**
	 * @see KeyboardListener#onKeyUp(Widget, char, int)
	 */
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
    private RichTextArea.BasicFormatter basic;
    private RichTextArea.ExtendedFormatter extended;

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

	if (basic != null) {
	    topPanel.add(bold =
		    createToggleButton(osylImageBundle.rtt_bold(), uiMessages
			    .getMessage("rtt_bold")));
	    topPanel.add(italic =
		    createToggleButton(osylImageBundle.rtt_italic(), uiMessages
			    .getMessage("rtt_italic")));
	    topPanel.add(underline =
		    createToggleButton(osylImageBundle.rtt_underline(),
			    uiMessages.getMessage("rtt_underline")));
	    topPanel.add(justifyLeft =
		    createPushButton(osylImageBundle.rtt_justifyLeft(),
			    uiMessages.getMessage("rtt_justifyLeft")));
	    topPanel.add(justifyCenter =
		    createPushButton(osylImageBundle.rtt_justifyCenter(),
			    uiMessages.getMessage("rtt_justifyCenter")));
	    topPanel.add(justifyRight =
		    createPushButton(osylImageBundle.rtt_justifyRight(),
			    uiMessages.getMessage("rtt_justifyRight")));
	}

	if (extended != null) {
	    topPanel.add(strikethrough =
		    createToggleButton(osylImageBundle.rtt_strikeThrough(),
			    uiMessages.getMessage("rtt_strikeThrough")));
	    topPanel.add(indent =
		    createPushButton(osylImageBundle.rtt_indent(), uiMessages
			    .getMessage("rtt_indent")));
	    topPanel.add(outdent =
		    createPushButton(osylImageBundle.rtt_outdent(), uiMessages
			    .getMessage("rtt_outdent")));
	    topPanel.add(ol =
		    createPushButton(osylImageBundle.rtt_ol(), uiMessages
			    .getMessage("rtt_ol")));
	    topPanel.add(ul =
		    createPushButton(osylImageBundle.rtt_ul(), uiMessages
			    .getMessage("rtt_ul")));
	    topPanel.add(createLink =
		    createPushButton(osylImageBundle.rtt_createLink(),
			    uiMessages.getMessage("rtt_createLink")));
	    topPanel.add(removeLink =
		    createPushButton(osylImageBundle.rtt_removeLink(),
			    uiMessages.getMessage("rtt_removeLink")));
	    topPanel.add(removeFormat =
		    createPushButton(osylImageBundle.rtt_removeFormat(),
			    uiMessages.getMessage("rtt_removeFormat")));
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
	if (basic != null) {
	    bold.setDown(basic.isBold());
	    italic.setDown(basic.isItalic());
	    underline.setDown(basic.isUnderlined());
	}

	if (extended != null) {
	    strikethrough.setDown(extended.isStrikethrough());
	}
    }

    public RichTextArea getRichText() {
	return richText;
    }

    public void setRichText(RichTextArea richText) {
	this.richText = richText;
	if (richText != null) {
	    this.basic = richText.getBasicFormatter();
	    this.extended = richText.getExtendedFormatter();
	} else {
	    this.basic = null;
	    this.extended = null;
	}
    }

}
