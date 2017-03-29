package gov.nysenate.openleg.model.sourcefiles.sobi;

import gov.nysenate.openleg.model.sourcefiles.BaseSourceFile;

/**
 * This exception is thrown when the contents of a sobi file cannot be read
 */
public class UnreadableSobiEx extends RuntimeException {
    
    private static final long serialVersionUID = 8708541650408827491L;
    
    private BaseSourceFile baseSourceFile;
    
    public UnreadableSobiEx(BaseSourceFile baseSourceFile, Throwable cause){
        super("Could not read text from sobi file: " + baseSourceFile, cause);
    }
    
    public BaseSourceFile getSobiFile(){
        return baseSourceFile;
    }
}
