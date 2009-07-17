package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;

public class DefaultSerializableTest extends AbstractSerializableTest {
    
    private Serializable serializable;
    private long maxSize;
    
    protected DefaultSerializableTest(Serializable serializable) {
        this(serializable, 0);
    }
    
    protected DefaultSerializableTest(Serializable serializable, long maxSize) {
        super();        
        this.serializable = serializable;
        this.maxSize = maxSize;
    }
    
    @Override
    public final long getMaxSize() {
        return this.maxSize;
    }

    @Override
    public final Serializable getSerializable() {
        return this.serializable;
    }
    
    //This doesn't work well unfortunately and error reporting doesn't show anything else than "null"
    /*public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new DefaultSerializableTest(new COSerialized()));
        suite.addTest(new DefaultSerializableTest(new COConfigSerialized()));
        suite.addTest(new DefaultSerializableTest(new COContent()));
        suite.addTest(new DefaultSerializableTest(new COContentUnit()));
        suite.addTest(new DefaultSerializableTest(new COModeled()));
        
        return suite;
    }*/
}
