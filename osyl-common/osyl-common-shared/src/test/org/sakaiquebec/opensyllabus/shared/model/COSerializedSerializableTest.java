package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;

/** Tests the Serializable behaviour of COSerialized.*/ 
public final class COSerializedSerializableTest extends AbstractSerializableTest {
        
    @Override
    public Serializable getSerializable() {
        return new COSerialized();
    }
}
