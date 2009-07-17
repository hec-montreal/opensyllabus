package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;


public final class COConfigSerializedSerializableTest extends AbstractSerializableTest {

    @Override
    public Serializable getSerializable() {
        return new COConfigSerialized();
    }
}
