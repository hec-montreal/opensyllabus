package org.sakaiquebec.opensyllabus.common.dao;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class AbstractDAOTest extends AbstractTransactionalDataSourceSpringContextTests {
	
	public AbstractDAOTest() {
		super();
		setDefaultRollback(true);//just making sure
	}
	
	protected String[] getConfigLocations() {
		return new String[]{
				"classpath:hibernate-test.xml", 
				"classpath:spring-hibernate.xml"};
	}
}
