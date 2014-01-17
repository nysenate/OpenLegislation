package gov.nysenate.openleg.util;

import gov.nysenate.openleg.converter.StorageJsonConverter;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Transcript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * Simple file backed key-value store with that supports both published and unpublished
 * file modes. Buffers changes in memory to reduce file system access and increase performance.
 *
 * @author GraylinKim
 */
public class Storage
{

    public static void main(String[] args) {
        List<String> test = new ArrayList<String>();
        System.out.println(test.getClass().cast(test));
    }

    protected final Logger logger;

    /**
     * Represents the current status of a key in storage:
     *
     * <ul>
     *  <li>NEW - New and not yet flushed to file.</li>
     *  <li>MODIFIED - Modified since the last flush to file.</li>
     *  <li>UNMODIFIED - Currently unmodified since last flush to file.</li>
     *  <li>DELETED - Deleted since last flush to file (not yet deleted on file).</li>
     *  <li>UNKNOWN - Key is not known to storage, possibly because a previous delete was flushed.</li>
     * </ul>
     *
     * @author GraylinKim
     */
   public static enum Status { NEW, MODIFIED, DELETED, UNMODIFIED, UNKNOWN };

    /**
     * The base directory for this storage on the file system.
     */
    protected final File storageDir;

    /**
     * The directory for published documents on the file system.
     */
    protected final File publishedDir;

    /**
     * The directory for unpublished documents on the file system.
     */
    protected final File unpublishedDir;

    /**
     * Memory buffer for cache values. Used to prevent excessive file operations.
     */
    public HashMap<String, BaseObject> memory;

    /**
     * Tracks the set of currently dirty keys that need to be flushed to the file system
     */
    protected HashSet<String> dirty;

    private final StorageJsonConverter converter;


    /**
     * Create a new storage connection to the given file path.
     *
     * @param storagePath - Base file path for the storage on the file system
     */
    public Storage(String storagePath)
    {
        this(new File(storagePath));
    }

    /**
     * Create a new storage connection to the given directory.
     *
     * @param storageDir - Base directory for the storage on the file system
     */
    public Storage(File storageDir)
    {
        this.logger  = Logger.getLogger(this.getClass());

        this.storageDir = storageDir;
        this.publishedDir = new File(storageDir, "published");
        this.unpublishedDir = new File(storageDir, "unpublished");

        this.memory  = new HashMap<String, BaseObject>();
        this.dirty   = new HashSet<String>();

        this.converter = new StorageJsonConverter(this);
    }

    /**
     * Get the current value of a key. First checks storage memory, then
     * falls back to the file system.
     *
     * @param key - The key to fetch the value for.
     * @param cls - The class interpret the value as.
     * @return - The object from storage.
     */
    public BaseObject get(String key, Class<? extends BaseObject> cls)
    {
        BaseObject value = null;
        if (memory.containsKey(key)) {
            logger.debug("Cache hit: "+key);
            value = memory.get(key);
        }
        else {
            logger.debug("Cache miss: "+key);
            File storageFile = getStorageFile(key);
            if (storageFile != null) {
                try {
                    if (cls == Bill.class) {
                        value = this.converter.readBill(storageFile);
                    }
                    else if (cls == Agenda.class) {
                        value = this.converter.readAgenda(storageFile);
                    }
                    else if (cls == Meeting.class) {
                        value = this.converter.readMeeting(storageFile);
                    }
                    else if (cls == Calendar.class) {
                        value = this.converter.readCalendar(storageFile);
                    }
                    else if (cls == Transcript.class) {
                        value = this.converter.readTranscript(storageFile);
                    }
                    else {
                        logger.error("Unable to read value of type "+cls.getName()+" from: "+storageFile);
                        return null;
                    }
                    value.setBrandNew(false);
                } catch (org.codehaus.jackson.JsonParseException e) {
                    logger.error("could not parse json", e);
                } catch (JsonMappingException e) {
                    logger.error("could not map json", e);
                } catch (IOException e) {
                    logger.debug("Storage Miss: "+storageFile);
                }
            }
            else {
                logger.debug("Missing key: "+key);
            }
        }
        return value;
    }

    /**
     * Writes the new value to system memory. To propagate these changes to the file system
     * you must first flush the key (value.getOid()).
     *
     * @param value - The new value to store
     */
    public void set(BaseObject value)
    {
        String key = this.key(value);
        memory.put(key, value);
        dirty.add(key);
    }

    /**
     * @param value - The storage key for this object
     */
    public String key(BaseObject value)
    {
        return value.getYear()+"/"+value.getOtype()+"/"+value.getOid();
    }

    /**
     * Nullifies the key in storage memory. To propagate the deletion to the file system you
     * must flush the key.
     *
     * @param key - The key to delete
     */
    public void del(String key)
    {
        logger.debug("Deleting key: "+key);
        memory.put(key, null);
        dirty.add(key);
    }

    /**
     * Clears out the storage memory. This operation does not affect changes written to
     * the file system. Make sure to flush first, all unwritten changes (including deletions!)
     * will be lost.
     */
    public void clear()
    {
        if (dirty.size() != 0) {
            logger.warn("Clearing storage with "+dirty.size()+" dirty keys.");
            dirty.clear();
        }
        else {
            logger.debug("Clearing storage of "+memory.size()+" keys.");
        }

        memory.clear();

    }

    /**
     * Clear out a specific key from storage memory. This operation does not affect changes
     * written to the file system. make sure to flush first, all unwritten changes (including
     * deletions!) will be lost.
     *
     * @param key - The key of the value to clear.
     */
    public void clear(String key)
    {
        if (dirty.contains(key)) {
            logger.warn("Clearing dirty key: "+key);
            dirty.remove(key);
        }
        memory.remove(key);
    }

    /**
     * Write all values in storage memory to file for long term storage.
     */
    public void flush()
    {
        logger.info("Flushing "+dirty.size()+" objects.");
        for(String key : dirty.toArray(new String[]{})) {
            flush(key);
        }
        dirty.clear();
    }

    /**
     * Write a specific value in storage memory to file for long term storage.
     *
     * @param key - The key of the value to write.
     */
    public void flush(String key)
    {
        logger.info("Flushing key: "+key);

        // Remove existing file record.
        FileUtils.deleteQuietly(getUnpublishedFile(key));
        FileUtils.deleteQuietly(getPublishedFile(key));

        // If the value wasn't deleted, write it to file
        BaseObject value = memory.get(key);
        if (value != null) {
            File storageFile = value.isPublished() ? getPublishedFile(key) : getUnpublishedFile(key);

            try {
                FileUtils.forceMkdir(storageFile.getParentFile());
                if (value instanceof Bill) {
                    this.converter.write((Bill)value, storageFile);
                }
                else if (value instanceof Agenda) {
                    this.converter.write((Agenda)value, storageFile);
                }
                else if (value instanceof Meeting) {
                    this.converter.write((Meeting)value, storageFile);
                }
                else if (value instanceof Calendar) {
                    this.converter.write((Calendar)value, storageFile);
                }
                else if (value instanceof Transcript) {
                    this.converter.write((Transcript)value, storageFile);
                }
                else {
                    logger.error("Unable to write value of type "+value.getClass().getName()+": "+value.getOid());
                    return;
                }
            }
            catch (IOException e) {
                logger.error("Cannot open file for writing: "+storageFile, e);
            }
        }

        // Mark the key as clean by removing from the dirty set.
        dirty.remove(key);
    }

    /**
     * @param key - The key to get Status for
     * @return - The current Status of a key
     */
    public Status status(String key)
    {
        File storageFile = getStorageFile(key);
        if (dirty.contains(key)) {
            if (memory.get(key) == null) {
                return Status.DELETED;
            }
            else if (storageFile == null) {
                return Status.NEW;
            }
            else {
                return Status.MODIFIED;
            }
        }
        else if (storageFile == null) {
            return Status.UNKNOWN;
        }
        else {
            return Status.UNMODIFIED;
        }
    }

    /**
     * @return - Base directory of the storage on the file system.
     */
    public File getStorageDir()
    {
        return storageDir;
    }

    /**
     * @param key - The key to get a storage file for.
     * @return - The storage file. null if no such file exists.
     */
    protected File getStorageFile(String key)
    {
        File storageFile = getPublishedFile(key);
        if (storageFile.exists()) {
            logger.debug("Published storage file found for key: "+key);
            return storageFile;
        }

        storageFile = getUnpublishedFile(key);
        if (storageFile.exists()) {
           logger.debug("Unpublished storage file found for key: "+key);
           return storageFile;
        }

        return null;
    }

    /**
     * @param key - The key to fetch a file for.
     * @return - File for the published key.
     */
    protected File getPublishedFile(String key)
    {
        return new File(publishedDir, key + ".json");
    }

    /**
     * @param key - The key to fetch a file for.
     * @return - File for the unpublished key.
     */
    protected File getUnpublishedFile(String key)
    {
        return new File(unpublishedDir, key + ".json");
    }

    public Bill getBill(String billId) {
        String[] parts = billId.split("-");
        return getBill(parts[0], Integer.parseInt(parts[1]));
    }

    public Bill getBill(String printNumber, int session)
    {
        String key = session+"/bill/"+printNumber+"-"+session;
        return (Bill)this.get(key, Bill.class);
    }
}
