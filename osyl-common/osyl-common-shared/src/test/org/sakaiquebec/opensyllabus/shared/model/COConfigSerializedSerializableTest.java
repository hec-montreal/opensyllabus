package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;

/** Tests the Serializable behaviour of COConfigSerialized.*/
public final class COConfigSerializedSerializableTest extends AbstractSerializableTest {

    //FIXME: that's where it gets complicated... 
    //@see https://www.hibernate.org/109.html
    
    //Should COConfigSerialized.hashCode() and equals() be implemented?
    //For Hibernate out-of-session object manipulation, it should in order to avoid having to resort to flush() on the DAO layer.
    //Since we don't really have problems with this at the moment, we go with the solution of NOT doing anything about it.
    @Override
    public boolean getCheckOverrides() {
        return false;
    }
    
    @Override
    public Serializable getSerializable() {
        return new COConfigSerialized();
    }
}
