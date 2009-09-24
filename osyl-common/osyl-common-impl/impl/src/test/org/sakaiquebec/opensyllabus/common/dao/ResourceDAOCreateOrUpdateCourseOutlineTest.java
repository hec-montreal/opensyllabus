package org.sakaiquebec.opensyllabus.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**@see org.sakaiquebec.opensyllabus.common.dao.ResourceDaoImpl
 * */
public class ResourceDAOCreateOrUpdateCourseOutlineTest extends AbstractResourceDAOTest {
	
	public void testCreateOrUpdateNullCourseOutline() {
		try {
			getResourceDAO().createOrUpdateCourseOutline(null);
			fail("createOrUpdateCourseOutline(): expected an exception because of a null course parameter.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
	public void testCreateCourseOutline() throws Exception {	
		ResourceDao resourceDAO = getResourceDAO();		
		COSerialized course = newCourseOutline("0");
		
		resourceDAO.createOrUpdateCourseOutline(course);
		
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		assertEquals(
				"Expected 1 course found (with ID=0).",
				1,
				courses.size());		
		
		assertEquals(
				course, courses.get(0));		
	}
	
	public void testUpdateCourseOutline() throws Exception {	
		ResourceDao resourceDAO = getResourceDAO();		
		COSerialized course = newCourseOutline("0");
		
		resourceDAO.createOrUpdateCourseOutline(course);
		
		//Test update
		course.setTitle("NewTitle");
		course.setAccess("NewSecurity");
		
		Map<String, String> messages = new HashMap<String, String>();
		messages.put("newA", "newA");
		messages.put("newB", "newB");
		course.setMessages(messages);
		
		resourceDAO.createOrUpdateCourseOutline(course);
		
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		assertEquals(				
				course,
				courses.get(0));		
	}
	
	public void testCreateCourseOutlineWithSameSiteIdAndSameSecurity() throws Exception {	
		ResourceDao resourceDAO = getResourceDAO();
		String siteId="siteId";
		String access="security";
		
		COSerialized course = newCourseOutline("0");
		course.setSiteId(siteId);
		course.setAccess(access);
		
		resourceDAO.createOrUpdateCourseOutline(course);
		
		
		course = newCourseOutline("1");
		course.setSiteId(siteId);
		course.setAccess(access);
		
		try{
		    resourceDAO.createOrUpdateCourseOutline(course);
		    fail("create same course with indentical siteId and group should fail");
		}catch (Exception e) {
		    //fine
		}
			
	}
}