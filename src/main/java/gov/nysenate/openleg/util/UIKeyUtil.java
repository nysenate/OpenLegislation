package gov.nysenate.openleg.util;

import org.apache.shiro.crypto.hash.Md5Hash;

import javax.servlet.http.HttpServletRequest;

public class UIKeyUtil
{
    public static void setUIKey(HttpServletRequest request, String apiSecret, String attributeName) {
        String hash = hash(generateHashInput(request, apiSecret));
        request.setAttribute(attributeName, hash);
    }

    public static boolean validateUIKey(HttpServletRequest request, String apiSecret, String headerAttribute) {
        String hashFromHeader = request.getHeader(headerAttribute);
        String actualHash = hash(generateHashInput(request, apiSecret));
        return hashFromHeader != null && hashFromHeader.equals(actualHash);
    }

    private static String generateHashInput(HttpServletRequest request, String apiSecret) {
        return request.getRemoteAddr() + apiSecret;
    }

    private static String hash(String input) {
        return new Md5Hash(input).toBase64();
    }
}