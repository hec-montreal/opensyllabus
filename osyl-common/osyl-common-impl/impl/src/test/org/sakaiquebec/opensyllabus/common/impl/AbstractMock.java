package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;

abstract class AbstractMock<T> {
	
	private Mockery mockery;
	private T mock;
	
	protected AbstractMock(Mockery mockery, Class<T> mocked) {
		super();
		this.mockery = mockery;
		this.mock = mockery.mock(mocked);
		this.mockery.checking(getExpectations(mock));
	}
	
	protected Expectations getExpectations(T mock) {
		return new Expectations();
	}	
	
	protected final T getMock() {
		return this.mock;
	}
}
