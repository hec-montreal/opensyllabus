package org.sakaiquebec.opensyllabus.common.impl;

import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.VoidAction;
import org.sakaiproject.authz.api.FunctionManager;

final class SakaiFunctionManagerMock extends AbstractMock<FunctionManager> {
	
	private List<String> functions;
	
	protected SakaiFunctionManagerMock(Mockery mockery) {
		super(mockery, FunctionManager.class);
		this.functions = new LinkedList<String>();
	}

	@Override
	protected Expectations getExpectations(final FunctionManager mock) {
		return new Expectations() {            
            {
                allowing(equal(mock)).
                method("getRegisteredFunctions");
                will(returnValue(functions));
            }
            {
                allowing(equal(mock)).
                method("registerFunction").
                with(any(String.class));
                will(new VoidAction() {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        String f = (String)invocation.getParameter(0);
                        functions.add(f);
                        return null;
                    }                    
                });
            }
        };
	}
}
