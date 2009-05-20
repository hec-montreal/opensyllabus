/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresItemListingAcquiredEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.UploadFileEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler.ItemListingAcquiredEvent;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class defining common methods for resources browsers
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylAbstractBrowserComposite extends Composite implements
	UploadFileEventHandler, FiresItemListingAcquiredEvents {

    protected static final boolean TRACE = false;

    // Number of elements shown in the file listing
    private static final int LISTING_ITEM_NUMBER = 6;

    private OsylDirectory currentDirectory = null;

    private String baseDirectoryPath;

    private String initialDirPath;

    private String browsedSiteId;

    private TextBox currentDirectoryTextBox;

    private DoubleClickListBox fileListing;

    private OsylAbstractBrowserItem selectedAbstractBrowserItem;

    private String fileItemPathToSelect;

    private TextBox currentSelectionTextBox;

    private RequestCallback remoteDirListingRespHandler =
	    new RequestCallback() {

		private static final int STATUS_CODE_SUCCESS = 200;

		/**
		 * {@inheritDoc}
		 */
		public void onError(Request request, Throwable exception) {
		    removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(false, true, getController()
				    .getUiMessage("Global.error"),
				    getController().getUiMessage(
					    "fileUpload.unableReadRemoteDir"));
		    alertBox.show();
		}

		/** {@inheritDoc} */
		public void onResponseReceived(Request request,
			Response response) {
		    // Window.alert(response.getStatusCode()+" "+response.getText());
		    if (response.getStatusCode() == STATUS_CODE_SUCCESS) {
			try {
			    getFileListing().clear();
			    ArrayList<OsylAbstractBrowserItem> dirListing =
				    (ArrayList<OsylAbstractBrowserItem>) getDirectoryContent(
					    response.getText()).getFilesList();
			    getCurrentDirectory().setFilesList(dirListing);
			    sortDirContent(dirListing);

			    for (int i = 0; i < dirListing.size(); i++) {
				OsylAbstractBrowserItem fileItem =
					dirListing.get(i);
				getFileListing().addItem(
					formatListingLine(fileItem));

				// if there is a file to auto select (after
				// uploading for example)
				if (fileItem.getFilePath().equals(
					getFileItemPathToSelect())) {
				    setFileItemPathToSelect(fileItemPathToSelect);
				}
			    }

			    // The FileListingAcquiredEvent handlers are then
			    // notified
			    notifyFileListingAcquiredEventHandlers();
			} finally {
			    getFileListing().removeStyleName(
				    "Osyl-RemoteFileBrowser-WaitingState");
			    removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
			}
		    } else {
			final OsylAlertDialog alertBox =
				new OsylAlertDialog(
					false,
					true, 
					getController().getUiMessage(
						"Global.error"),
					getController()
						.getUiMessage(
							"fileUpload.unableReadRemoteDir"));
			alertBox.show();
		    }
		}
	    };

    private String fileExtensionFilter;

    private ArrayList<RFBAddFolderEventHandler> RFBAddFolderEventHandlerList;

    private ArrayList<RFBItemSelectionEventHandler> RFBFileSelectionEventHandlerList;

    private ArrayList<ItemListingAcquiredEventHandler> fileListingAcquiredEventHandlerList;

    // Image Bundle
    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    /**
     * Constructors for OsylRemoteFileBrowser with default directory and no
     * filter
     */

    public OsylAbstractBrowserComposite() {
	super();
	this.setFileExtensionFilter(null);
	initView();
    }

    public OsylAbstractBrowserComposite(String newDirPath, String newFilter) {
	super();
	setInitialDirPath(newDirPath);
	this.setFileExtensionFilter(newFilter);
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylAbstractBrowserComposite(String newResDirName,
	    String newResDirPath, String newFilter, String fileItemNameToSelect) {
	if(TRACE)
	    Window.alert("OsylAbstractBrowserComposite("+newResDirName+","+
		    newResDirPath+","+newFilter+","+fileItemNameToSelect+")");
	setBrowsedSiteId(extractRessourceSiteId(newResDirName));
	String path = extractRessourceFolder(newResDirName);
	setBaseDirectoryPath(newResDirPath);
	setFileExtensionFilter(newFilter);
	setCurrentDirectory(new OsylDirectory(path, path, newFilter,
		new ArrayList<OsylAbstractBrowserItem>()));
	setInitialDirPath(getCurrentDirectory().getDirectoryPath());
	setFileExtensionFilter(newFilter);
	setFileItemPathToSelect(fileItemNameToSelect);
	initView();
    }

    /**
     * Initialize and renders the view of the <code>OsylRemoteFileBrowser</code>
     * .
     */

    public void initView() {

	// Browser Main Panel
	VerticalPanel mainContentPanel = new VerticalPanel();

	// First row sub-panel
	HorizontalPanel firstRowPanel = new HorizontalPanel();
	mainContentPanel.add(firstRowPanel);
	firstRowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

	// 1st row, 1st widget: Folder name label
	Label folderPanelLabel =
		new Label(getController().getUiMessage(
			"UtilityRemoteFileBrowser_FolderPanelLabel"));
	folderPanelLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	folderPanelLabel.setWidth("130px");
	firstRowPanel.add(folderPanelLabel);

	// 1st row, 2nd widget: Folder TextBox
	TextBox folderName = new TextBox();
	if (currentDirectory.getDirectoryName() != null)
	    folderName.setText(currentDirectory.getDirectoryName());
	setCurrentDirectoryTextBox(folderName);
	getCurrentDirectoryTextBox().setWidth("315px");
	// To disallow direct edition of the file path, set to true:
	// and comment-out the 4 lines below
	getCurrentDirectoryTextBox().setReadOnly(false);
	getCurrentDirectoryTextBox().addKeyboardListener(
		new CurrentFolderEnterKeyListener());
	getCurrentDirectoryTextBox().addFocusListener(
		new CurrentFolderFocusChangeListener());
	firstRowPanel.add(getCurrentDirectoryTextBox());

	// 1st row, 3rd widget, button panel:
	HorizontalPanel buttonPanel = new HorizontalPanel();
	firstRowPanel.add(buttonPanel);

	// One Level Up Button
	PushButton upButton =
		createTopButton(getOsylImageBundle().action_OneLevelUp(),
			getController()
				.getUiMessage("Browser.upButton.tooltip"));
	upButton.addClickListener(new UpButtonClickListener());
	buttonPanel.add(upButton);

	// Folder Creation button
	PushButton folderAddButton =
		createTopButton(getOsylImageBundle().folderAdd(),
			getController().getUiMessage(
				"Browser.addFolderButton.tooltip"));
	folderAddButton.addClickListener(new FolderAddButtonClickListener());
	buttonPanel.add(folderAddButton);

	// Add file button
	PushButton addFileButton = createAddPushButton();
	buttonPanel.add(addFileButton);

	// 2nd row: File Listing
	setFileListing(new DoubleClickListBox());
	getFileListing().setWidth("99%");
	getFileListing().setVisibleItemCount(LISTING_ITEM_NUMBER);
	getFileListing().addStyleName("Osyl-RemoteFileBrowser-FileListing");

	// Put the Listing in place
	// Trick to avoid selection of the first item at initialization
	getFileListing().setSelectedIndex(-1);
	getFileListing().addClickListener(new SpecialDoubleClickListener());

	mainContentPanel.add(getFileListing());

	// 3rd row: File field (first a label then a TextBox)
	HorizontalPanel fileSelectionSubPanel = new HorizontalPanel();
	mainContentPanel.add(fileSelectionSubPanel);

	// The Label
	Label currentSelectionLabel = new Label(getCurrentSelectionLabel());
	currentSelectionLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	currentSelectionLabel.setWidth("130px");
	fileSelectionSubPanel.add(currentSelectionLabel);

	// And the TextBox
	setCurrentSelectionTextBox(new TextBox());
	getCurrentSelectionTextBox().setText("");
	getCurrentSelectionTextBox().setReadOnly(true);
	getCurrentSelectionTextBox().setWidth("315px");
	fileSelectionSubPanel.add(getCurrentSelectionTextBox());

	// All composites must call initWidget() in their constructors.
	initWidget(mainContentPanel);

	// Give the overall composite a style name
	this.setStylePrimaryName("Osyl-RemoteFileBrowser");

	// Fill the listing
	getRemoteDirectoryListing(getCurrentDirectory().getDirectoryPath());
	// getCurrentDirectoryTextBox().setText(
	// getCurrentDirectory().getDirectoryPath());
    }

    public String getFileItemPathToSelect() {
	return fileItemPathToSelect;
    }

    public void setFileItemPathToSelect(String fileItemPathToSelect) {
	this.fileItemPathToSelect = fileItemPathToSelect;
    }

    protected PushButton createTopButton(AbstractImagePrototype img,
	    String tooltip) {
	// TODO: Bug with ImageBundle, we have to use AbstractImagePrototype
	PushButton button = new PushButton(img.createImage());
	button.setTitle(tooltip);
	button.setStylePrimaryName("Osyl-FileBrowserTopButton");
	button.setSize("24px", "24px");
	return button;
    }

    /**
     * Parse a JSON String and construct corresponding RemoteDirectory
     * 
     * @param jsonString
     * @return The constructed remoteDirectory
     */
    public OsylDirectory getDirectoryContent(String jsonString) {
	if (TRACE)
	    Window.alert("JSON resp = " + jsonString);
	JSONString name = null;
	JSONString path = null;

	JSONObject root = (JSONObject) JSONParser.parse(jsonString);
	JSONObject items = (JSONObject) root.get("items");
	Set<String> keys = items.keySet();
	ArrayList<OsylAbstractBrowserItem> dirItems =
		new ArrayList<OsylAbstractBrowserItem>();
	for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
	    String key = (String) iter.next();
	    boolean escaped = false;
	    JSONObject jObject = (JSONObject) items.get(key);
	    JSONString type = (JSONString) jObject.get("primaryNodeType");
	    OsylAbstractBrowserItem osylRemoteDirItem = null;
	    if (type != null && type.stringValue().equals("nt:folder")) {
		path = (JSONString) jObject.get("path");
		name =
			(JSONString) ((JSONObject) jObject.get("properties"))
				.get("DAV:displayname");
		if (name.stringValue().equals("publish")) {
		    // TODO change this to a non hard coded value
		    escaped = true;
		} else {
		    ArrayList<OsylAbstractBrowserItem> list =
			    new ArrayList<OsylAbstractBrowserItem>();
		    osylRemoteDirItem =
			    new OsylDirectory(name.stringValue(), path
				    .stringValue(), "", list);
		}
	    } else {
		OsylAbstractBrowserItem oBrowserItem =
			getOsylAbstractBrowserItem(jObject);
		if (oBrowserItem != null) {
		    osylRemoteDirItem = oBrowserItem;
		} else {
		    escaped = true;
		}
	    }
	    if (!escaped)
		dirItems.add(osylRemoteDirItem);
	}
	OsylDirectory osylRemoteDirectory = null;
	if (!dirItems.isEmpty()) {
	    osylRemoteDirectory = new OsylDirectory("", "", "", dirItems);
	}
	return osylRemoteDirectory;
    }

    /**
     * Sorts the specified list according to the type and name of each element.
     * Folders come before files and name is case insensitive.
     */
    private void sortDirContent(ArrayList<OsylAbstractBrowserItem> dirListing) {
	java.util.Collections.sort(dirListing,
		new Comparator<OsylAbstractBrowserItem>() {
		    public int compare(OsylAbstractBrowserItem file1,
			    OsylAbstractBrowserItem file2) {

			if (file1.isFolder() == file2.isFolder()) {
			    return file1.getFileName().toLowerCase().compareTo(
				    file2.getFileName().toLowerCase());
			} else {
			    if (file1.isFolder()) {
				return -1;
			    } else {
				return 1;
			    }
			}
		    }
		});
    }

    /**
     * Retrieves the remote directory listing via sdata
     * 
     * @param directoryPath the path where to look to get the file listing.
     */
    public void getRemoteDirectoryListing(String directoryPath) {
	getFileListing().addStyleName("Osyl-RemoteFileBrowser-WaitingState");
	if (TRACE)
	    Window.alert("DIR = " + getBaseDirectoryPath() + getBrowsedSiteId()
		    + "/" + directoryPath);
	OsylJSONRemoteDirectory.getRemoteDirectoryContent(
		getBaseDirectoryPath() + getBrowsedSiteId() + "/"
			+ directoryPath, remoteDirListingRespHandler);
    }

    public String formatListingLine(OsylAbstractBrowserItem abstractBrowserItem) {
	String formattedLine = " ";
	String fileName = abstractBrowserItem.getFileName();
	formattedLine +=
		getController().getUiMessage(abstractBrowserItem.getItemTag());
	formattedLine += "   " + fileName;
	return formattedLine;
    }

    /**
     * This class represents the box that contains the file listing and where
     * files can be selected by a single click and where folders are accessed by
     * a double click or by pressing Enter key.
     */
    public class DoubleClickListBox extends ListBox {

	private static final int DBCLICK_DURATION = 200;

	private Timer clickTimer = null;

	public DoubleClickListBox() {
	    // Create a normal ListBox
	    super();
	    // Sink the double click
	    this.sinkEvents(Event.ONDBLCLICK);
	}

	public void onBrowserEvent(Event event) {
	    // Handle events as a normal ListBox
	    super.onBrowserEvent(event);
	    // Look at the type of event
	    int type = DOM.eventGetType(event);
	    switch (type) {
	    // If it is a double click event, handle it
	    case Event.ONDBLCLICK: {
		// Code to execute for a double click event
		if (clickTimer != null) {
		    if (TRACE)
			Window.alert("double clicked");
		    this.onDoubleClick();
		    clickTimer.cancel();
		    clickTimer = null;
		}
		break;
	    }
		// If it is a click event, handle it
	    case Event.ONCLICK: {
		// Create a new timer that calls Window.alert()
		if (clickTimer == null) {
		    clickTimer = new Timer() {
			public void run() {
			    if (TRACE)
				Window.alert("simple click");
			    onSimpleClick();
			}
		    };
		}
		// Schedule the timer to run once in 200 ms
		clickTimer.schedule(DBCLICK_DURATION);
		break;
	    }
	    default:
		break;
	    }
	}

	public void onSimpleClick() {
	    int indexSelectedFile = getFileListing().getSelectedIndex();
	    if (indexSelectedFile >= 0) {
		getFileListing().setItemSelected(indexSelectedFile, true);
		getFileListing().addStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
		String newFilePath =
			getCurrentDirectory().getDirectoryPath()
				+ "/"
				+ getCurrentDirectory().getFilesList().get(
					indexSelectedFile).getFileName();
		newFilePath = uriSlashCorrection(newFilePath);
		OsylAbstractBrowserItem selectedFile =
			getCurrentDirectory().getFilesList().get(
				indexSelectedFile);

		setSelectedAbstractBrowserItem(selectedFile);
		setFileItemPathToSelect(selectedFile.getFilePath());
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");

		// Notify all the event handlers and
		// call the onFileSelectionEvent on each one
		RFBItemSelectionEventHandler.RFBItemSelectionEvent event =
			new RFBItemSelectionEventHandler.RFBItemSelectionEvent(
				getBaseDirectoryPath()
					+ getCurrentDirectoryTextBox()
						.getText() + "/"
					+ selectedFile.getFileName());
		if (getRFBFileSelectionEventHandlerList() != null) {
		    Iterator<RFBItemSelectionEventHandler> iter =
			    getRFBFileSelectionEventHandlerList().iterator();
		    while (iter.hasNext()) {
			RFBItemSelectionEventHandler handler =
				(RFBItemSelectionEventHandler) iter.next();
			handler.onItemSelectionEvent(event);
		    }
		}
	    }
	}

	public void onDoubleClick() {
	    int index = getFileListing().getSelectedIndex();
	    if (index >= 0) {
		getFileListing().addStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
		String newFilePath =
			getCurrentDirectory().getDirectoryPath()
				+ "/"
				+ getCurrentDirectory().getFilesList().get(
					index).getFileName();
		newFilePath = uriSlashCorrection(newFilePath);
		OsylAbstractBrowserItem selectedFile =
			getCurrentDirectory().getFilesList().get(index);
		setSelectedAbstractBrowserItem(selectedFile);

		if (selectedFile.isFolder()) {
		    getCurrentDirectory().setDirectoryPath(newFilePath);
		    setCurrentDirectory((OsylDirectory) getCurrentDirectory()
			    .getFilesList().get(index));
		    getFileListing().clear();
		    getRemoteDirectoryListing(newFilePath);
		    getFileListing().removeStyleName(
			    "Osyl-RemoteFileBrowser-WaitingState");
		    getCurrentDirectoryTextBox().setText(newFilePath);
		    getCurrentSelectionTextBox().setText("");
		    getCurrentDirectory().setDirectoryPath(newFilePath);
		    // Trick to avoid selection of the first item at
		    // initialization
		    getFileListing().setSelectedIndex(-1);
		} else {
		    setFileItemPathToSelect(selectedFile.getFilePath());
		    onFileDoubleClicking();
		}
		RFBItemSelectionEventHandler.RFBItemSelectionEvent event =
			new RFBItemSelectionEventHandler.RFBItemSelectionEvent(
				getBaseDirectoryPath()
					+ getCurrentDirectoryTextBox()
						.getText() + "/"
					+ selectedFile.getFileName());
		if (getRFBFileSelectionEventHandlerList() != null) {
		    Iterator<RFBItemSelectionEventHandler> iter =
			    getRFBFileSelectionEventHandlerList().iterator();
		    while (iter.hasNext()) {
			RFBItemSelectionEventHandler handler =
				(RFBItemSelectionEventHandler) iter.next();
			handler.onItemSelectionEvent(event);
		    }
		}
		// }
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
	    }
	}
    }

    private final class CurrentFolderFocusChangeListener implements
	    FocusListener {

	public void onLostFocus(Widget sender) {
	    // Reset the current path
	    getCurrentDirectoryTextBox().setText(
		    getCurrentDirectory().getDirectoryPath());
	}

	public void onFocus(Widget sender) {
	}
    }

    private final class CurrentFolderEnterKeyListener extends
	    KeyboardListenerAdapter {
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
	    if (((keyCode == (char) KEY_ENTER))) {
		// Fill the listing
		String newFilePath = getCurrentDirectoryTextBox().getText();
		try {
		    getRemoteDirectoryListing(newFilePath);
		    getCurrentDirectoryTextBox().setText(
			    getCurrentDirectory().getDirectoryPath());
		    getCurrentSelectionTextBox().setText("");
		    int nbrElements =
			    getCurrentDirectory().getFilesList().size();
		    for (int i = 0; i < nbrElements; i++) {
			getFileListing().addItem(
				formatListingLine(getCurrentDirectory()
					.getFilesList().get(i)));
		    }
		    nbrElements = getCurrentDirectory().getFilesList().size();
		    for (int i = 0; i < nbrElements; i++) {
			if (newFilePath.endsWith(getCurrentDirectory()
				.getFilesList().get(i).getFileName())) {
			    getFileListing().setItemSelected(i, true);
			}
		    }
		} catch (Exception e) {
		    // TODO: handle exception
		}
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
		return;
	    }
	}
    }

    class SpecialDoubleClickListener implements ClickListener {
	// just get the click event and dispatch it
	public void onClick(Widget sender) {
	    sender.sinkEvents(Event.ONDBLCLICK);
	}
    }

    private final class UpButtonClickListener implements ClickListener {
	public void onClick(Widget sender) {
	    if (currentDirectory == null) {
		return;
	    }
	    String newFilePath = getCurrentDirectory().getDirectoryPath();
	    if (newFilePath.contains("/")) {
		newFilePath =
			newFilePath.substring(0, newFilePath.lastIndexOf("/"));
	    }
	    getCurrentDirectory().setDirectoryPath(newFilePath);
	    // Fill the listing
	    getFileListing().clear();
	    getRemoteDirectoryListing(newFilePath);
	    getCurrentDirectoryTextBox().setText(newFilePath);
	    getCurrentSelectionTextBox().setText("");
	    getFileListing().removeStyleName(
		    "Osyl-RemoteFileBrowser-WaitingState");
	}
    }

    private final class FolderAddButtonClickListener implements ClickListener {
	public void onClick(Widget sender) {
	    // Notify all the event handlers and
	    // call the onClickAddFolderButton on each one
	    RFBAddFolderEventHandler.RFBAddFolderEvent event =
		    new RFBAddFolderEventHandler.RFBAddFolderEvent(
			    getCurrentDirectoryTextBox().getText());
	    if (getRFBAddFolderEventHandlerList() != null) {
		Iterator<RFBAddFolderEventHandler> iter =
			getRFBAddFolderEventHandlerList().iterator();
		while (iter.hasNext()) {
		    RFBAddFolderEventHandler handler =
			    (RFBAddFolderEventHandler) iter.next();
		    handler.onClickAddFolderButton(event);
		}
	    }
	}
    }

    public OsylController getController() {
	return OsylController.getInstance();
    }

    /**
     * Utilitary method to correct a url.
     * 
     * @param initialURL The url to correct.
     * @return
     */
    public static String uriSlashCorrection(String initialURL) {
	String correctURL = initialURL;
	// Removes antislash
	correctURL = initialURL.replace("\\", "/");
	// Remove double slash
	if (correctURL.contains("://")) {
	    correctURL = correctURL.replace("://", "\\");
	    correctURL = correctURL.replace("//", "/");
	    correctURL = correctURL.replace("\\", "://");
	} else {
	    correctURL = correctURL.replace("//", "/");
	}
	return correctURL;
    }

    // GETTERS AND SETTERS
    public RequestCallback getRemoteDirListingRespHandler() {
	return remoteDirListingRespHandler;
    }

    public OsylDirectory getCurrentDirectory() {
	return currentDirectory;
    }

    public void setCurrentDirectory(OsylDirectory currentDirectory) {
	this.currentDirectory = currentDirectory;
    }

    public TextBox getCurrentDirectoryTextBox() {
	return currentDirectoryTextBox;
    }

    public void setCurrentDirectoryTextBox(TextBox currentDirectoryTextBox) {
	this.currentDirectoryTextBox = currentDirectoryTextBox;
    }

    public DoubleClickListBox getFileListing() {
	return fileListing;
    }

    public void setFileListing(DoubleClickListBox fileListing) {
	this.fileListing = fileListing;
    }

    public TextBox getCurrentSelectionTextBox() {
	return currentSelectionTextBox;
    }

    public void setCurrentSelectionTextBox(TextBox currentSelectionTextBox) {
	this.currentSelectionTextBox = currentSelectionTextBox;
    }

    public String getInitialDirPath() {
	return initialDirPath;
    }

    /**
     * @return the baseDirectoryPath value.
     */
    public String getBaseDirectoryPath() {
	return this.baseDirectoryPath;
    }

    /**
     * @param baseDirectoryPath the new value of baseDirectoryPath.
     */
    public void setBaseDirectoryPath(String newBaseDirectoryPath) {
	this.baseDirectoryPath = newBaseDirectoryPath;
    }

    /**
     * @return the Previous Directory Path value.
     */
    public String getInitialDirectoryPath() {
	return this.initialDirPath;
    }

    /**
     * @param newDirPath the new value of Previous Directory Path.
     */
    public void setInitialDirPath(String newDirPath) {
	this.initialDirPath = newDirPath;
    }

    /**
     * @return the selectedAbstractFileItem value.
     */
    public OsylAbstractBrowserItem getSelectedAbstractBrowserItem() {
	return this.selectedAbstractBrowserItem;
    }

    /**
     * @param selectedAbstractFileItem the new value of
     *            selectedAbstractFileItem.
     */
    public void setSelectedAbstractBrowserItem(
	    OsylAbstractBrowserItem selectedAbstractFileItem) {
	this.selectedAbstractBrowserItem = selectedAbstractFileItem;
    }

    /**
     * @return the File Extension filter value.
     */
    public String getFileExtensionFilter() {
	return fileExtensionFilter;
    }

    /**
     * @param fileExtensionFilter the new value of filter
     */
    public void setFileExtensionFilter(String newFilter) {
	this.fileExtensionFilter = newFilter;
    }

    public OsylImageBundleInterface getOsylImageBundle() {
	return osylImageBundle;
    }

    public String getBrowsedSiteId() {
	return browsedSiteId;
    }

    public void setBrowsedSiteId(String currentSiteId) {
	this.browsedSiteId = currentSiteId;
    }

    /**
     * @return the rFBFileSelectionEventHandlerList value.
     */
    public ArrayList<RFBAddFolderEventHandler> getRFBAddFolderEventHandlerList() {
	return this.RFBAddFolderEventHandlerList;
    }

    /**
     * @param arrayList the new value of rFBFileSelectionEventHandlerList.
     */
    public void setRFBAddFolderEventHandlerList(
	    ArrayList<RFBAddFolderEventHandler> arrayList) {
	this.RFBAddFolderEventHandlerList = arrayList;
    }

    /**
     * @return the rFBFileSelectionEventHandlerList value.
     */
    public ArrayList<RFBItemSelectionEventHandler> getRFBFileSelectionEventHandlerList() {
	return this.RFBFileSelectionEventHandlerList;
    }

    /**
     * @param arrayList the new value of rFBFileSelectionEventHandlerList.
     */
    public void setRFBFileSelectionEventHandlerList(
	    ArrayList<RFBItemSelectionEventHandler> arrayList) {
	this.RFBFileSelectionEventHandlerList = arrayList;
    }

    public ArrayList<ItemListingAcquiredEventHandler> getFileListingAcquiredEventHandlerList() {
	return fileListingAcquiredEventHandlerList;
    }

    public void setFileListingAcquiredEventHandlerList(
	    ArrayList<ItemListingAcquiredEventHandler> fileListingAcquiredEventHandlerList) {
	this.fileListingAcquiredEventHandlerList =
		fileListingAcquiredEventHandlerList;
    }

    //
    /** {@inheritDoc} */
    public void addEventHandler(RFBAddFolderEventHandler handler) {
	if (handler == null) {
	    return;
	}
	if (getRFBAddFolderEventHandlerList() == null) {
	    setRFBAddFolderEventHandlerList(new ArrayList<RFBAddFolderEventHandler>());
	}
	getRFBAddFolderEventHandlerList().add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(RFBAddFolderEventHandler handler) {
	if (getRFBAddFolderEventHandlerList() != null) {
	    getRFBAddFolderEventHandlerList().remove(handler);
	}
    }

    /** {@inheritDoc} */
    public void addEventHandler(RFBItemSelectionEventHandler handler) {
	if (handler == null) {
	    return;
	}
	if (getRFBFileSelectionEventHandlerList() == null) {
	    setRFBFileSelectionEventHandlerList(new ArrayList<RFBItemSelectionEventHandler>());
	}
	getRFBFileSelectionEventHandlerList().add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(RFBItemSelectionEventHandler handler) {
	if (getRFBFileSelectionEventHandlerList() != null) {
	    getRFBFileSelectionEventHandlerList().remove(handler);
	}
    }

    public void addEventHandler(ItemListingAcquiredEventHandler handler) {
	if (handler != null) {
	    if (getFileListingAcquiredEventHandlerList() == null) {
		setFileListingAcquiredEventHandlerList(new ArrayList<ItemListingAcquiredEventHandler>());
	    }
	    getFileListingAcquiredEventHandlerList().add(handler);
	}
    }

    public void removeEventHandler(ItemListingAcquiredEventHandler handler) {
	if (getFileListingAcquiredEventHandlerList() != null) {
	    getFileListingAcquiredEventHandlerList().remove(handler);
	}
    }

    public void notifyFileListingAcquiredEventHandlers() {

	if (getFileListingAcquiredEventHandlerList() != null) {
	    ItemListingAcquiredEvent event = new ItemListingAcquiredEvent(null);
	    for (int i = 0; i < getFileListingAcquiredEventHandlerList().size(); i++) {
		getFileListingAcquiredEventHandlerList().get(i)
			.onItemListingAcquired(event);
	    }
	}
    }

    /** to refresh the listing */
    public void onUploadFile(UploadFileEvent event) {
	setFileItemPathToSelect(event.getSource().toString());
	getRemoteDirectoryListing(getCurrentDirectory().getDirectoryPath());
    }

    /**
     * Refreshes the file browser according to the newly selected file.
     */
    public void refreshBrowser() {

	boolean fileItemFound = false;
	String filePath = getFileItemPathToSelect();

	if (filePath != null) {

	    for (int i = 0; i < getCurrentDirectory().getFilesList().size(); i++) {
		OsylAbstractBrowserItem fileItem =
			getCurrentDirectory().getFilesList().get(i);

		if (fileItem.getFilePath().equals(filePath)) {
		    fileItemFound = true;
		    setSelectedAbstractBrowserItem(fileItem);
		    getFileListing().setSelectedIndex(i);
		    getFileListing().setFocus(true);
		    getCurrentDirectoryTextBox().setText(
			    getCurrentDirectory().getDirectoryPath());
		    if (fileItem.isFolder()) {
			getCurrentSelectionTextBox().setText("");
		    } else {
			getCurrentSelectionTextBox().setText(fileItem.getFileName());
		    }
		}
	    }
	}

	if (!fileItemFound) {
	    setSelectedAbstractBrowserItem(null);
	    getFileListing().setSelectedIndex(-1);
	}
    }

    /**
     * Returns the document path which is the part before the last slash
     * 
     * @param uri
     * @return uri for presentation
     */
    protected String extractRessourceFolder(String uri) {
	String resourcesPath = "/group/" + getBrowsedSiteId() + "/";
	return uri.substring(uri.indexOf(resourcesPath)
		+ resourcesPath.length(), uri.length());
    }

    /**
     * Returns the site id provided in the document path
     * 
     * @param uri
     * @return uri for presentation
     */
    protected String extractRessourceSiteId(String uri) {
	String resourcesPath = "/group/";
	String uu =
		uri.substring(uri.indexOf(resourcesPath)
			+ resourcesPath.length());
	return uu.substring(0, uu.indexOf("/"));
    }

    // ABSTRACT METHOD

    protected abstract PushButton createAddPushButton();

    protected abstract String getCurrentSelectionLabel();

    protected abstract void onFileDoubleClicking();

    /**
     * @param jObject the JSONObject to be parsed.
     * @return the OSylFileItem
     */
    protected abstract OsylAbstractBrowserItem getOsylAbstractBrowserItem(
	    JSONObject jsObject);

}
