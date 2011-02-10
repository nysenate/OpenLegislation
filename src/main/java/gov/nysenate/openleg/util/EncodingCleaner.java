package gov.nysenate.openleg.util;

public class EncodingCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	  /// <summary>
    /// This removes characters that are invalid for xml encoding
    /// </summary>
    /// <param name="text">Text to be encoded.</param>
    /// <returns>Text with invalid xml characters removed.</returns>
    public static String CleanInvalidXmlChars(String text)
    {
        // From xml spec valid chars:
        // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]  3  
        // any Unicode character, excluding the surrogate blocks, FFFE, and FFFF.
    	
    //	String re = "[^\\x09\\x0A\\x0D\\x20-\\xD7FF\\xE000-\\xFFFD\\x10000-x10FFFF]";
      //  return Regex.Replace(text, re, "");
    	text = text.replace((char)0x0C,' ');
    	text = text.replace((char)0x20,' ');
    	
    	return text;
    }
    
    

}
