package org.sakaiquebec.opensyllabus.shared.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

/** This TestCase tests Serializable objects and make sure the implementation of java.io.Serializable is valid. That is
 *  <br/>
 *  <ol>
 *    <li>The object implements the Serializable interface.</li>
 *    <li>The Serializable can be serialized and deserialized properly.</li>
 *    <li>The Serializable object overrides equals() and hashCode( ), and their implementations are consistent.</li>
 *  </ol>
 *  <br/>
 *  In addition, a test can ensure a maximum serialized size to avoid serialization overhead 
 *  when big objects need to be sent accross the wire.
 */
public abstract class AbstractSerializableTest extends TestCase {
        
    private Serializable tested = null;
        
    /** This method must return a Class that represents a Serializable object with a default constructor.
     *  The test will use a new instance of the given Class as the tested object.
     * */
    public abstract Serializable getSerializable();
    
    /** Returns the max size in bytes the tested Serializable should fit in without being a test failure.
     *  A 0 or negative value indicates that the size should not be tested.<br/>
     *  The default implementation returns 0L.
     * */
    public long getMaxSize() {
        return 0;
    }

    /** if returning TRUE, this test will make sure that the hashCode() and equals() methods of the tested Serializable
     *  are consistent (that is, the serialised and de-serialised objects should have the same hash code and should be equal.
     *  This is a requirement for proper serialisation.<br/>
     *  The default implementation returns TRUE.
     * @see http://onjava.com/pub/a/onjava/excerpt/JavaRMI_10/index.html?page=3#66063
     */
    public boolean getCheckOverrides() {
        return true;
    }
    
    public void onSetUp(){}
    
    public void onTearDown(){}
    
    public final void testSerializable() throws Exception {        
        byte[] bytes = null;
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bout);
            out.writeObject(tested);
            out.flush();
            bytes = bout.toByteArray();
        }        
        finally {
            close(out);            
        }
        
        ObjectInputStream in = null;        
        try {            
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bin);
            Serializable saved = (Serializable)in.readObject();
            
            assertEquals(
                    "Incorrect serialized object Class.",
                    tested.getClass(), saved.getClass());
            
            if (getCheckOverrides()) {
                assertTrue(
                    "Serialized object not equal to original one (the equals() method of " + tested.getClass() + " is not implemented correctly).",
                    tested.equals(saved));
                
                assertEquals(
                    "Hashcodes don't match (the hashCode() method of " + tested.getClass() + " is not implemented correctly).",
                    tested.hashCode(), saved.hashCode());
            }
            
            long maxSize = getMaxSize();            
            if ((maxSize > 0) && (bytes.length > maxSize)) {
                fail(String.format(
                        "Serialized object is larger than maximum allowed size: expected %s bytes; got %s bytes",
                        maxSize, bytes.length));            
            }
            onTestSerializable(tested, saved);
        }
        catch (ClassCastException e) {
            fail("Serialized classes don't match (not a Serializable object): " +  e.getLocalizedMessage());
        }
        catch (ClassNotFoundException e) {
            fail("Object did not serialize correctly: " +  e.getLocalizedMessage());
        }
        finally {
            close(in);
        }        
    }
        
    
    /** Called after testSerializable() has done its checks and intented for clients to add specific conditions on serialized objects.
     *  Default implementation does nothing.
     * */
    protected void onTestSerializable(Serializable tested, Serializable retrieved) {}
    
    @Override
    protected final void setUp() throws Exception {
        onSetUp();
        this.tested = getSerializable();
        assertNotNull(tested);
    }

    @Override
    protected final void tearDown() throws Exception {
        this.tested = null;
        onTearDown();
    }
    
    private static final void close(InputStream in) {
        if (null != in) {
            try {
                in.close();
            }
            catch (IOException ignored) {}
        }
    }
    
    private static final void close(OutputStream out) {
        if (null != out) {
            try {
                out.close();
            }
            catch (IOException ignored) {}
        }
    }
}
