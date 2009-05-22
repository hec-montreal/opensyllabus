package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**@see org.sakaiquebec.opensyllabus.common.dao.ResourceDaoImpl
 * */
public class ResourceDAOGetSerializedCourseOutlineBySiteIDTest extends AbstractResourceDAOTest {
	
	public void testGetNull() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();				
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(null, null);
			//FIXME: the behaviour in case of NULL is not specified by the interface documentation.
			//Lets assume that a null should produce an error.
			fail("getSerializedCourseOutlineBySiteId(): expected an exception because of null parameters.");
		}
		catch (Exception e) {
			//fine
		}
	}	
	public void testGetNullGroupName() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		course.setSecurity("sec");
		resourceDAO.createOrUpdateCourseOutline(course);
		
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(course.getSiteId(), null);
			//FIXME: the behaviour in case of NULL is not specified by the interface documentation.
			//Lets assume that a null should produce an error.
			fail("getSerializedCourseOutlineBySiteId(): expected an exception because of a null group parameter.");
		}
		catch (Exception e) {
			//fine
		}
	}	
	
	public void testGetNullCourseID() throws Exception {
		final String GROUP = "UnitTest";
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		course.setSecurity(GROUP);
		resourceDAO.createOrUpdateCourseOutline(course);
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(null, GROUP);
			//FIXME: the behaviour in case of NULL is not specified by the interface documentation.
			//Lets assume that a null should produce an error.
			fail("getSerializedCourseOutlineBySiteId(): expected an exception because of a null course parameter.");
		}
		catch (Exception e) {
			//fine
		}
	}	
	
	public void testGetExistingCourse() throws Exception {
		final String GROUP = "UnitTest";
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		course.setSecurity(GROUP);
		resourceDAO.createOrUpdateCourseOutline(course);
		
		COSerialized otherCourse = 
			resourceDAO.getSerializedCourseOutlineBySiteId(
					course.getSiteId(), 
					course.getSecurity());
		
		assertEquals(
				"Course retrieved using getSerializedCourseOutlineBySiteId() doesn't match the created one.",
				course, otherCourse);
	}
	
	public void testGetNonExistingCourse() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();
		//FIXME: the behaviour for a non-found object is not specified in the documentation.
		//Lets assume that a getter never returns null and fails on non-existence (contrary to a finder).
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(
					"" + System.currentTimeMillis(), 
					"" + System.currentTimeMillis());
			fail("Expected an Exception from 'getSerializedCourseOutlineBySiteId' since the course doesn't exist.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
	public void testGetExistingCourseWithNonExistingGroup() throws Exception {
		final String GROUP = "UnitTest";
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		course.setSecurity(GROUP);
		resourceDAO.createOrUpdateCourseOutline(course);
		//FIXME: the behaviour for a non-found object is not specified in the documentation.
		//Lets assume that a getter never returns null and fails on non-existence (contrary to a finder).
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(
					course.getSiteId(), 
					"" + System.currentTimeMillis());
			fail("Expected an Exception from 'getSerializedCourseOutlineBySiteId' since the course doesn't exist.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
	public void testGetNonExistingCourseWithExistingGroup() throws Exception {
		final String GROUP = "UnitTest";
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		course.setSecurity(GROUP);
		resourceDAO.createOrUpdateCourseOutline(course);
		//FIXME: the behaviour for a non-found object is not specified in the documentation.
		//Lets assume that a getter never returns null and fails on non-existence (contrary to a finder).
		try {
			resourceDAO.getSerializedCourseOutlineBySiteId(
					"" + System.currentTimeMillis(), 
					GROUP);
			fail("Expected an Exception from 'getSerializedCourseOutlineBySiteId' since the course doesn't exist.");
		}
		catch (Exception e) {
			//fine
		}
	}
}