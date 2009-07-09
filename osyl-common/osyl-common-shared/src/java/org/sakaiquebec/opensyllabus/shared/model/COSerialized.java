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

package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the main POJO container sent to the client. It contains an XML
 * serialized version of the course outline, as well as other meta-info and
 * attributes. Once a client need to manipulate the model, it should declare a
 * <code>COModeled</code>, effectively extending COSerialized.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:tom.landry@crim.ca>Tom Landry</a>
 * @version $Id: $
 */
public class COSerialized implements IsSerializable, Serializable,
	COModelInterface {

    private static final long serialVersionUID = 2L;

    /**
     * The value tells the course outline is published or not
     */
    private boolean published;

    /**
     * The unique id of the course outline
     */
    private String coId;

    /**
     * The language of the course outline
     */
    private String lang;

    /**
     * The type of the course outline
     */
    private String type;

    /**
     * The general security level. Might change.
     */
    private String security;

    /**
     * The site to which this outline relates.
     */
    private String siteId;

    /**
     * The group to which this outline relates.
     */
    private String group;

    /**
     * The actual XML data of the course outline
     */
    private String serializedCO;

    /**
     * The unique id of the configuration to use in edition mode
     */
    private COConfigSerialized osylConfig;

    /**
     * A short description for this course. Comes from the site service.
     */
    private String shortDescription;

    /**
     * A complete description for this course. Comes from the site service.
     */
    private String description;

    /**
     * A title for this course. Comes from the site service.
     */
    private String title;

    /**
     * A map for the titles of rubrics
     */
    private Map<String, String> messages;

    /**
     * is editable or not
     */
    private boolean editable;

    /**
     * Default constructor
     */
    public COSerialized() {
    }

    /**
     * A constructor with an id
     */
    public COSerialized(String idCo) {
	this.setCoId(idCo);
    }

    /**
     * Copy constructor. Since all fields are immutable, this works. See
     * http://www.javapractices.com/topic/TopicAction.do?Id=12
     */
    public COSerialized(COSerialized courseOutlineXML) {
	this(courseOutlineXML.getCoId(), courseOutlineXML.getLang(),
		courseOutlineXML.getType(), courseOutlineXML.getSecurity(),
		courseOutlineXML.getSiteId(), courseOutlineXML.getSection(),
		courseOutlineXML.getOsylConfig(),
		courseOutlineXML.getContent(), courseOutlineXML
			.getShortDescription(), courseOutlineXML
			.getDescription(), courseOutlineXML.getTitle(),
		courseOutlineXML.isPublished());
    }

    /**
     * A complete constructor
     * 
     * @param published TODO
     */
    public COSerialized(String coId, String lang, String type, String security,
	    String siteId, String sectionId, COConfigSerialized osylConfigId,
	    String content, String shortDescription, String description,
	    String title, boolean published) {
	this.setCoId(coId);
	this.setLang(lang);
	this.setType(type);
	this.setSecurity(security);
	this.setSiteId(siteId);
	this.setSection(sectionId);
	this.setOsylConfig(osylConfigId);
	this.setContent(content);
	this.setShortDescription(shortDescription);
	this.setDescription(description);
	this.setTitle(title);
	this.setPublished(published);

    }

    /**
     * Get the short description for this course.
     * 
     * @return a String of the short description
     */
    public String getShortDescription() {
	return shortDescription;
    }

    /**
     * Set the short description for this course.
     * 
     * @param a String of the short description
     */
    public void setShortDescription(String shortDescription) {
	this.shortDescription = shortDescription;
    }

    /**
     * Get the complete description for this course.
     * 
     * @return a String of the complete description
     */
    public String getDescription() {
	return description;
    }

    /**
     * Set the complete description for this course.
     * 
     * @param a String of the complete description
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * Get the title of the current course outline.
     * 
     * @return a String of course outline title
     */
    public String getTitle() {
	return title;
    }

    /**
     * Set the title of the current course outline.
     * 
     * @param a String of course outline title
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * Get the unique Id of the current course outline.
     * 
     * @return a String of the course outline id
     */
    public String getCoId() {
	return this.coId;
    }

    /**
     * Set the unique Id of the current course outline.
     * 
     * @param a String of the course outline id
     */
    public void setCoId(String coId) {
	this.coId = coId;
    }

    /**
     * Get the locale of the current course outline.
     * 
     * @return a String of course outline locale
     */
    public String getLang() {
	return this.lang;
    }

    /**
     * Set the locale of the current course outline.
     * 
     * @param a String of course outline locale
     */
    public void setLang(String lang) {
	this.lang = lang;
    }

    /**
     * Get the type of the current course outline.
     * 
     * @return a String of the course outline type
     */
    public String getType() {
	return this.type;
    }

    /**
     * Set the type of the current course outline.
     * 
     * @param a String of the course outline type
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * Get the security level of the current course outline.
     * 
     * @return an Integer of the course outline security level
     */
    public String getSecurity() {
	return this.security;
    }

    /**
     * Get the site id in which resides of the current course outline.
     * 
     * @return a String of course outline site
     */
    public String getSiteId() {
	return this.siteId;
    }

    /**
     * Set the site id in which resides of the current course outline.
     * 
     * @param a String of course outline site id
     */
    public void setSiteId(String siteId) {
	this.siteId = siteId;
    }

    /**
     * Get the site section of the current course outline.
     * 
     * @return a String of course outline section, or empty if not applicable
     */
    public String getSection() {
	return this.group;
    }

    /**
     * Get the site section of the current course outline.
     * 
     * @param a String of course outline section
     */
    public void setSection(String sectionId) {
	this.group = sectionId;
    }

    /**
     * Get the configuration id to use to edit this course outline.
     * 
     * @return a String of the course outline config id
     */
    public COConfigSerialized getOsylConfig() {
	return this.osylConfig;
    }

    /**
     * Set the configuration id to use to edit this course outline.
     * 
     * @param a String of the course outline config id
     */
    public void setOsylConfig(COConfigSerialized osylConfig) {
	this.osylConfig = osylConfig;
    }

    /**
     * Get the actual XML content of the current course outline.
     * 
     * @return a String that describes all the course outline itself
     */
    public String getContent() {
	return this.serializedCO;
    }

    /**
     * Set the actual XML content of the current course outline.
     * 
     * @param a String that describes all the course outline itself
     */
    public void setContent(String content) {
	this.serializedCO = content;
    }

    /**
     * Returns the map containing the rubric titles
     * 
     * @return the messages value.
     */
    public Map<String, String> getMessages() {
	return messages;
    }

    /**
     * Sets the maps containing the rubric title
     * 
     * @param messages the new value of messages.
     */
    public void setMessages(Map<String, String> messages) {
	this.messages = messages;
    }

    /**
     * Says if the course outline is published or not
     * 
     * @return the published
     */
    public boolean isPublished() {
	return published;
    }

    /**
     * Sets the course outline to a published state
     * 
     * @param published the published to set
     */
    public void setPublished(boolean published) {
	this.published = published;
    }

    /**
     * Set the security level of the current course outline.
     * 
     * @param security the security to set
     */
    public void setSecurity(String security) {
	this.security = security;
    }

    public boolean isEditable() {
	return editable;
    }

    public void setEditable(boolean edit) {
	this.editable = edit;
    }

    public boolean equals(Object obj) {
	COSerialized cos = (COSerialized) obj;

	return (published == cos.isPublished() && coId.equals(cos.getCoId())
		&& lang.equals(cos.getLang()) && type.equals(cos.getType())
		&& security.equals(cos.getSecurity())
		&& siteId.equals(cos.getSiteId())
		&& group.equals(cos.getSection())
		&& serializedCO.equals(cos.getContent())
		&& osylConfig.equals(cos.getOsylConfig())
		&& shortDescription.equals(cos.getShortDescription())
		&& description.equals(cos.getDescription())
		&& title.equals(cos.getTitle())
		&& messages.equals(cos.getMessages()) && editable == cos
		.isEditable());
    }

}
