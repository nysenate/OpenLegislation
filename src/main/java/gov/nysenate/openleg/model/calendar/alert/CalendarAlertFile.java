package gov.nysenate.openleg.model.calendar.alert;

import gov.nysenate.openleg.model.base.BaseSourceData;

import java.io.File;
import java.io.FileNotFoundException;

public class CalendarAlertFile extends BaseSourceData {

    /** Reference to the actual file. */
    private final File file;

    private boolean archived;

    public CalendarAlertFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            this.file = file;
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        // By default, a file is unprocessed.
        this.setPendingProcessing(true);
    }

    public File getFile() {
        return file;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
