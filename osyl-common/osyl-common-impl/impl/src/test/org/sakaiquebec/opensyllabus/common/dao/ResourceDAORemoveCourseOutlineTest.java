package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**@see org.sakaiquebec.opensyllabus.common.dao.ResourceDaoImpl
 * */
public class ResourceDAORemoveCourseOutlineTest extends AbstractResourceDAOTest {
	
	public void testRemoveNullCourseOutline() throws Exception {
		try {
			getResourceDAO().removeCourseOutline(null);
			//FIXME: removeCourseOutline() probably returns false...but it's not pretty...
			fail("removeCourseOutline(): expected an exception because of a null course parameter.");
		}
		catch (Exception e) {
			//fine
		}
	}
	
	public void testRemoveNonExistingCourseOutline() throws Exception {
		assertFalse(
				"removeCourseOutline(): expected a FALSE value because of a non existing course parameter.",
				getResourceDAO().removeCourseOutline("" + System.currentTimeMillis()));
	}
	
	public void testRemoveCourseOutline() throws Exception {
		final String ID = "0";
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline(ID);
		resourceDAO.createOrUpdateCourseOutline(course);	
		assertExists(course);
		
		boolean result = resourceDAO.removeCourseOutline(ID);
		assertEquals(
				"removeCourseOutline() should return TRUE with an existing course.",
				true, result);
		assertNotExists(course);
		
		//Just making sure by testing non existence using another mean
		try {
			resourceDAO.getCourseOutlineInfo(ID);
			fail("Expected an Exception raised as course should not be found.");
		}
		catch (Exception e) {
			//fine
		}
	}
		
}
