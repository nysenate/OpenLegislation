package gov.nysenate.openleg.dao.sourcefiles.xml;

import org.springframework.dao.DataAccessException;

import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;

public interface XmlDao {
    
    /**
     * Retrieves an archived XmlFile object with the given file name.
     *
     * @param filename String - The file name of the XML file.
     *
     * @return XmlFile
     *
     * @throws DataAccessException - If there was an error while retrieving the SobiFile.
     */
    XmlFile getXmlFile(String filename) throws DataAccessException;
}