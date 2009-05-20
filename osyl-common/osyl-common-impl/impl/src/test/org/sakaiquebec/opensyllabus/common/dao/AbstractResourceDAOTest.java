package org.sakaiquebec.opensyllabus.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

import edu.emory.mathcs.backport.java.util.Collections;

/**@see org.sakaiquebec.opensyllabus.common.dao.ResourceDaoImpl
 * */
public class AbstractResourceDAOTest extends AbstractDAOTest {
	
	private ResourceDao resourceDAO;//What we're testing
	
	private COConfigDao configDAO;//We do NOT test this.
	private COConfigSerialized osylConfig;
	
	@Override
	protected void onSetUp() throws Exception {	
		super.onSetUp();
		osylConfig = new COConfigSerialized();
		
		//FIXME: not setting an ID on this bean gives:
		//org.springframework.orm.hibernate3.HibernateSystemException:
		//ids for this class must be manually assigned before calling save(): 
		//Shouldn't IDs be auto-generated??
		osylConfig.setConfigId("0");
		
		osylConfig.setCascadingStyleSheetURI("http://acme/stylesheet.css");
		osylConfig.setConfigRef(null);
		osylConfig.setRulesConfig("");
		configDAO.createConfig(osylConfig);		
	}
	
	public void setConfigDAO(COConfigDao configDAO) {
		this.configDAO = configDAO;
	}
	
	public void setResourceDAO(ResourceDao resourceDAO) {
		this.resourceDAO = resourceDAO;
	}
	
	
	public final ResourceDao getResourceDAO() {
		return resourceDAO;
	}

	protected final COConfigSerialized getConfig() {
		return this.osylConfig;
	}
	
	protected final void assertExists(COSerialized course) {
		//FIXME: there's no course finder in ResourceDAO?!
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		for (COSerialized c: courses) {
			if (c.getCoId().equals(course.getCoId())) {
				return;
			}
		}
		fail("CourseOutlineDTO with ID '" + course.getCoId() + "' not found.");
	}
	
	protected final void assertNotExists(COSerialized course) {
		//FIXME: there's no course finder in ResourceDAO?!
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		for (COSerialized c: courses) {
			if (c.getCoId().equals(course.getCoId())) {
				fail("CourseOutlineDTO with ID '" + course.getCoId() + "' was found.");;
			}
		}		
	}
	
	protected final COSerialized newCourseOutline(String id) {
		return newCourseOutline(id, osylConfig);
	}

	protected static final COSerialized newCourseOutline(String id, COConfigSerialized config) {
		COSerialized course = new COSerialized();
		course.setSerializedContent("Course Content " + id);
		course.setDescription("Description " + id);
		course.setLang("en");
		course.setPublished(false);
		course.setShortDescription("ShortDescription " + id);
		course.setTitle("Title " + id);
		course.setType("T");
		
		//FIXME: not setting an ID on this bean gives:
		//org.springframework.orm.hibernate3.HibernateSystemException:
		//ids for this class must be manually assigned before calling save(): 
		course.setCoId(id);
		course.setSiteId(id);		
		course.setOsylConfig(config);
		
		course.setParents(Collections.emptyList());
		course.setChildren(Collections.emptyList());
		
		Map<String, String> messages = new HashMap<String, String>();
		messages.put("A", "A" + System.currentTimeMillis());
		messages.put("B", "B" + System.currentTimeMillis());
		messages.put("C", "C" + System.currentTimeMillis());
		course.setMessages(messages);
		
		return course;
	}
	
	protected static final void assertEquals(COSerialized course, COSerialized otherCourse) {
		assertEquals(
				"Course.getCoId",
				course.getCoId(), otherCourse.getCoId());
		assertEquals(
				"Course.getContent",
				course.getContent(), otherCourse.getContent());
		assertEquals(
				"Course.getDescription",
				course.getDescription(), otherCourse.getDescription());
		assertEquals(
				"Course.getLang",
				course.getLang(), otherCourse.getLang());
		assertEquals(
				"Course.getSection",
				course.getSection(), otherCourse.getSection());
		assertEquals(
				"Course.getSecurity",
				course.getSecurity(), otherCourse.getSecurity());
		assertEquals(
				"Course.getSerializedContent",
				course.getSerializedContent(), otherCourse.getSerializedContent());
		assertEquals(
				"Course.getShortDescription",
				course.getShortDescription(), otherCourse.getShortDescription());
		assertEquals(
				"Course.getSiteId",
				course.getSiteId(), otherCourse.getSiteId());
		assertEquals(
				"Course.getTitle",
				course.getTitle(), otherCourse.getTitle());
		assertEquals(
				"Course.getType",
				course.getType(), otherCourse.getType());
		
		List<String> kids = course.getChildren();
		assertNotNull(
				"Unexpected null children collection.", kids);
		
		List<String> otherKids = otherCourse.getChildren();
		assertNotNull(
				"Unexpected null children collection.", otherKids);
		assertEquals(
				"Unequal number of children.", kids.size(), otherKids.size());
		for (int i = 0; i < kids.size(); i++) {
			assertEquals(
					"Children at index '" + i + "' aren't equal.", 
					kids.get(i), otherKids.get(i));
		}
		
		List<String> parents = course.getParents();
		assertNotNull(
				"Unexpected null parents collection.", parents);
		
		List<String> otherParents = otherCourse.getParents();
		assertNotNull(
				"Unexpected null parents collection.", otherParents);
		assertEquals(
				"Unequal number of parents.", parents.size(), otherParents.size());
		for (int i = 0; i < parents.size(); i++) {
			assertEquals(
					"Parents at index '" + i + "' aren't equal.", 
					parents.get(i), otherParents.get(i));
		}
		
		Map<String, String> messages = course.getMessages();
		assertNotNull(
				"Unexpected null messages map.", messages);
		
		Map<String, String> otherMessages = otherCourse.getMessages();
		assertNotNull(
				"Unexpected null messages map.", otherMessages);
		
		assertEquals(
				"Unequal number of messages.", messages.size(), otherMessages.size());
		
		assertConfigEquals(
				course.getOsylConfig(), 
				otherCourse.getOsylConfig());
	}
	
	private static final void assertConfigEquals(COConfigSerialized config, COConfigSerialized otherConfig) {
		assertEquals(
				"CascadingStyleSheetURIs don't match.",
				config.getCascadingStyleSheetURI(),
				otherConfig.getCascadingStyleSheetURI());
		
		assertEquals(
				"configIDs don't match.",
				config.getConfigId(),
				otherConfig.getConfigId());
		
		assertEquals(
				"configRefs don't match.",
				config.getConfigRef(),
				otherConfig.getConfigRef());
		
		assertEquals(
				"rulesConfigs don't match.",
				config.getRulesConfig(),
				otherConfig.getRulesConfig());
		
		assertEquals(
				"I18nMessages don't match.",
				config.getI18nMessages(),
				otherConfig.getI18nMessages());
	}
}
