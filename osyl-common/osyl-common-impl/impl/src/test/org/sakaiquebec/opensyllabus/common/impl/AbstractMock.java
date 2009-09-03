package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;

abstract class AbstractMock<T> {
	
	private Mockery mockery;
	private T mock;
	
	//Meh...bad hack to avoid construction problems with getExpectation()
	//when the later needs some class initialized fields
	private boolean expectationSet;
	
	protected AbstractMock(Mockery mockery, Class<T> mocked) {
		super();
		this.expectationSet = false;
		this.mockery = mockery;
		this.mock = mockery.mock(mocked);		
	}
	
	/** Returns all the Expectations for this Mock. 
	 * 	The method is lazily called when the actual mock
	 * 	is requested (through getMock()).
	 * @param mock
	 * @return
	 */
	protected Expectations getExpectations(T mock) {
		return new Expectations();
	}	
	
	protected final T getMock() {
		if (!expectationSet) {
			this.mockery.checking(getExpectations(mock));
			expectationSet = true;
		}
		return this.mock;
	}
}
