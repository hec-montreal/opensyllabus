package org.sakaiquebec.opensyllabus.common.dao;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**@see org.sakaiquebec.opensyllabus.common.dao.ResourceDaoImpl
 * */
public class ResourceDAOGetCourseOutlinesTest extends AbstractResourceDAOTest {
	
	public void testGetEmptyCourseOutlines() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();
		
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		assertNotNull(
				"getCourseOutlines() should have returned an empty list, not a NULL value.",
				courses);
		assertEquals(
				"There should not be any course outline found.",
				0,
				courses.size());
	}
	
	public void testGetOneCourseOutlines() throws Exception {
		ResourceDao resourceDAO = getResourceDAO();
		
		COSerialized course = newCourseOutline("0");
		resourceDAO.createOrUpdateCourseOutline(course);
		
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		assertEquals(
				"Expected 1 course found (with ID=0).",
				1,
				courses.size());
		assertEquals(
				"Incorrect ID from course found using getCourseOutlines()",
				course,
				courses.get(0));
	}
	

	public void testGetManyCourseOutlines() throws Exception {
		final int SIZE = 10;
		ResourceDao resourceDAO = getResourceDAO();
		
		for (int i  = 0; i < SIZE; i++) {
			COSerialized course = newCourseOutline("" + i);
			resourceDAO.createOrUpdateCourseOutline(course);			
		}
		
		List<COSerialized> courses = resourceDAO.getCourseOutlines();
		assertEquals(
				"getCourseOutlines(): incorrect number of courses.",
				SIZE,
				courses.size());
	}
}
