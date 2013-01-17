package gov.nysenate.openleg.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {
    private static Pattern formatPattern = Pattern.compile("\\.(json|xml)$");

    public enum FORMAT { XML, JSON, HTML };

    public static FORMAT getFormat(HttpServletRequest request) {
        return getFormat(request, FORMAT.HTML);
    }

    /**
     * Checks the request parameters and URI for an authorized format and returns
     * the appropriate RequestUtils.FORMAT value. If both are present, the URI
     * extension wins. If no suitable format is found the defaultFormat is returned.
     *
     * @param request
     * @param defaultFormat
     * @return
     */
    public static FORMAT getFormat(HttpServletRequest request, FORMAT defaultFormat) {
        String format = request.getParameter("format");
        Matcher formatMatcher = formatPattern.matcher(request.getRequestURI());
        if (formatMatcher.find()) {
            format = formatMatcher.group(1);
        }

        try {
            if(format != null) {
                return FORMAT.valueOf(format.toUpperCase());
            } else {
                return defaultFormat;
            }
        } catch (IllegalArgumentException e) {
            return defaultFormat;
        }
    }
}
