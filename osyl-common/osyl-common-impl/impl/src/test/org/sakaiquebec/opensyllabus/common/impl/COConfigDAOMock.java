package org.sakaiquebec.opensyllabus.common.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.action.VoidAction;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;

final class COConfigDAOMock extends AbstractMock<COConfigDao> {
	private final Map<String, COConfigSerialized> configs;
	
	protected COConfigDAOMock(Mockery mockery) {
		super(mockery, COConfigDao.class);		
		configs = new HashMap<String, COConfigSerialized>();
	}
	
	protected Expectations getExpectations(final COConfigDao dao) {
		return new Expectations() {    	
    		{
    			allowing(dao).
    			getConfigs();
    			will(new CustomAction("COConfigDao.getConfigs()") {
                	//@Override
                    public Object invoke(Invocation invocation) throws Throwable {
                    	List<COConfigSerialized> returned = new LinkedList<COConfigSerialized>();
                    	returned.addAll(configs.values());
                    	return Collections.unmodifiableList(returned);                         
                    } 
                });
    		}
    		{
    			allowing(equal(dao)).
    			method("createConfig").
    			with(any(COConfigSerialized.class));
    			will(new VoidAction() {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                    	COConfigSerialized co = (COConfigSerialized)invocation.getParameter(0);
                    	if (null == co) {
                    		throw new IllegalArgumentException("Null COConfigSerialized");
                    	}
                    	if (StringUtils.isBlank(co.getConfigId())) {
                    		throw new IllegalArgumentException("Empty COConfigSerialized.configId");
                    	}
                    	configs.put(co.getConfigId(), co);
                        return null;
                    }                    
                });
    		}
    		{
    			allowing(equal(dao)).
    			method("removeConfig").
    			with(any(String.class));
    			will(new VoidAction() {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                    	String id = (String)invocation.getParameter(0);
                    	if (StringUtils.isBlank(id)) {
                    		throw new IllegalArgumentException("Blank COConfigSerialized ID.");
                    	}
                    	
                    	COConfigSerialized config = configs.get(id);                    	
                    	if (null == config) {
                    		throw new IllegalArgumentException("Unknown COConfigSerialized ID " + id);
                    	}
                    	configs.remove(id);
                        return null;
                    }                    
                });
    		}
    		{
    			allowing(equal(dao)).
    			method("updateConfig").
    			with(any(COConfigSerialized.class));
    			will(new VoidAction() {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                    	COConfigSerialized newConfig = (COConfigSerialized)invocation.getParameter(0);
                    	if (null == newConfig) {
                    		throw new IllegalArgumentException("Null COConfigSerialized.");
                    	}
                    	String id = newConfig.getConfigId();
                    	if (StringUtils.isBlank(id)) {
                    		throw new IllegalArgumentException("Empty COConfigSerialized.configId");
                    	}              	
                    	COConfigSerialized config = configs.get(id);                    	
                    	if (null == config) {
                    		throw new IllegalArgumentException("Unknown COConfigSerialized ID " + id);
                    	}
                    	configs.put(id, newConfig);                    	
                        return null;
                    }                    
                });
    		}
    	};
	}		
}
