package org.sakaiquebec.opensyllabus.common.impl;

import junit.framework.TestCase;

import org.hamcrest.Description;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.api.Expectation;
import org.jmock.internal.ExpectationBuilder;

public abstract class AbstractServiceTest extends TestCase {
    
    private OsylMockery mockery;
    
    protected final OsylMockery getMockery() {
        return mockery;
    }
    
    protected void onServiceSetUp() throws Exception {}
    
    protected void onServiceTearDown() throws Exception {}

    @Override
    protected final void setUp() throws Exception {
        mockery = new OsylMockery();
        onServiceSetUp();
    }
    
    @Override
    protected final void tearDown() throws Exception  {
        onServiceTearDown();
    }    

    protected final <T> T mock(Class<T> clazz) {
        return mockery.mock(clazz);
    }

    protected final void addExpectation(Expectation expectation) {
        mockery.addExpectation(expectation);
    }

    protected final void assertIsSatisfied() {
        mockery.assertIsSatisfied();
    }

    protected final void checking(ExpectationBuilder expectations) {
        mockery.checking(expectations);
    }

    protected final void describeTo(Description description) {
        mockery.describeTo(description);
    }

    protected final Sequence sequence(String name) {
        return mockery.sequence(name);
    }

    protected final States states(String name) {
        return mockery.states(name);
    }
}
