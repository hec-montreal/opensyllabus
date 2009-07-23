package org.sakaiquebec.opensyllabus.common.impl;

import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.sakaiproject.id.api.IdManager;

final class SakaiIdManagerMock extends AbstractMock<IdManager> {

	protected SakaiIdManagerMock(Mockery mockery) {
		super(mockery, IdManager.class);
	}

	@Override
	protected Expectations getExpectations(final IdManager mock) {
		return new Expectations() {
			{
				allowing(mock).createUuid();
				will(new CustomAction("createUuid") {
					// @Override
					public Object invoke(Invocation invocation)	throws Throwable {
						return UUID.randomUUID().toString();
					}
				});
			}
		};
	}
}
