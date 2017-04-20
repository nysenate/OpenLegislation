package gov.nysenate.openleg.dao.sourcefiles.xml;

import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import org.springframework.dao.DataAccessException;

import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public interface XmlDao {

    /**
     * Retrieves an archived XmlFile object with the given file name.
     *
     * @param filename String - The file name of the XML file.
     * @return XmlFile
     * @throws DataAccessException - If there was an error while retrieving the SobiFile.
     */
    XmlFile getFile(String filename) throws DataAccessException;

    /**
     * Method archives XMLFile. Moves SourceFile from staging to archive.
     *
     * @param xmlFile SourceFile(XML) to be archived
     * @throws IOException
     */
    void archiveXmlFile(SourceFile xmlFile) throws IOException;

    /**
     * Method returns the directory of the incoming files
     *
     * @param fileName
     * @return
     */
    File getFileInIncomingDir(String fileName);

    /**
     * Get file handle from the xml archive directory.
     */
    File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime);
}