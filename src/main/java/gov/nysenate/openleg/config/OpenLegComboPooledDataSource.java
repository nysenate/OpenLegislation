package gov.nysenate.openleg.config;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;

import javax.naming.Referenceable;
import java.io.*;
import java.util.Optional;

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
     * There is one particular thread created by the superclass' close() that may not close before
     * shutdown, which would cause an error message to be printed. This finds that thread,
     * called in com.mchange.v2.resourcepool.BasicResourcePool.close(), and waits for it to finish.
     */
    @Override
    public void close() {
        String problemThreadName = "Resource Destroyer in BasicResourcePool.close()";
        super.close();
        Optional<Thread> problemThread = Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().equals(problemThreadName)).findFirst();
        if (problemThread.isPresent()) {
            try {
                problemThread.get().join();
            }
            catch (InterruptedException ignored) {}
        }
    }
}
