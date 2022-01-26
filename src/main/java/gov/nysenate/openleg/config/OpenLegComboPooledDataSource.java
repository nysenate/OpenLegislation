package gov.nysenate.openleg.config;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;

import javax.naming.Referenceable;
import java.io.*;

/**
 * This class is copied nearly exactly from ComboPooledDataSource, except with an overridden close method.
 */
final class OpenLegComboPooledDataSource extends AbstractComboPooledDataSource implements Serializable, Referenceable {
    public OpenLegComboPooledDataSource() {
        super();
    }

    @Serial
    private static final long serialVersionUID = 1;
    private static final short VERSION = 0x0002;

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(VERSION);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        if (version != VERSION)
            throw new IOException("Unsupported Serialized Version: " + version);
    }

    /**
     * Intercepts an error thrown deep within the stack trace at shutdown.
     * This error warns of a memory leak, but since we're shutting down,
     * it can be safely ignored.
     */
    @Override
    public void close() {
        try {
            super.close();
        }
        catch (Exception ignored) {}
    }
}
