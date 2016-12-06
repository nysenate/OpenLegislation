package gov.nysenate.openleg.processor.xml;

import gov.nysenate.openleg.model.xml.XmlFile;
import gov.nysenate.openleg.processor.base.ParseError;

import java.time.LocalDateTime;

public class XmlParseException extends ParseError {

    // The file that caused the exception
    private XmlFile xmlFile;

    // Time datetime at which it occurred
    private LocalDateTime parseDateTime;

    public XmlParseException(String message, XmlFile xmlFile, LocalDateTime parseDateTime) {
        super(message);
        this.xmlFile = xmlFile;
        this.parseDateTime = parseDateTime;
    }

    public XmlParseException(String message, Throwable cause, XmlFile xmlFile, LocalDateTime parseDateTime) {
        super(message, cause);
        this.xmlFile = xmlFile;
        this.parseDateTime = parseDateTime;
    }
}
