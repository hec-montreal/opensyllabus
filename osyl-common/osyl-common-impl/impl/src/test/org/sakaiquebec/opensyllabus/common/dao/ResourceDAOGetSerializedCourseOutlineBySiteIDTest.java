package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**@see org.sakaiquebec.opensyllabus.common.dao.ResourceDaoImpl
 * */
public class ResourceDAOGetSerializedCourseOutlineBySiteIDTest extends AbstractResourceDAOTest {
	
	public void testGetNull() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();	
		
		COSerialized course = newCourseOutline("0");
		course.setAccess("sec");
		resourceDAO.createOrUpdateCourseOutline(course);
		
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(null);
			fail("getSerializedCourseOutlineBySiteId(): expected an exception because of null parameters.");
		}
		catch (Exception e) {
			//fine
		}
	}	
	
	public void testGetExistingCourse() throws Exception {
		final String GROUP = "UnitTest";
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		course.setAccess(GROUP);
		resourceDAO.createOrUpdateCourseOutline(course);
		
		COSerialized otherCourse = 
			resourceDAO.getSerializedCourseOutlineBySiteId(
					course.getSiteId());
		
		assertEquals(
				"Course retrieved using getSerializedCourseOutlineBySiteId() doesn't match the created one.",
				course, otherCourse);
	}
	
	public void testGetNonExistingCourse() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(
					"" + System.currentTimeMillis());
			fail("Expected an Exception from 'getSerializedCourseOutlineBySiteId' since the course doesn't exist.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
}