package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;

final class SakaiToolManagerMock extends AbstractMock<ToolManager> {
	
	private Placement toolPlacement;
	
	protected SakaiToolManagerMock(Mockery mockery) {
		super(mockery, ToolManager.class);
		this.toolPlacement = mockery.mock(Placement.class);
	}

	@Override
	protected Expectations getExpectations(final ToolManager mock) {
		 return new Expectations() {
            {
                allowing(equal(toolPlacement)).
                method("getContext"); 
                will(returnValue(OsylMockery.PLACEMENT_CONTEXT));
            }
            {
                allowing(mock).getCurrentPlacement(); 
                will(returnValue(toolPlacement));
            }            
        };
	}
}
