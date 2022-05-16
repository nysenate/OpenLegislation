package gov.nysenate.openleg.common.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class HttpUtils {
    private static final class UnsuccessfulHttpReqException extends RuntimeException {
        private UnsuccessfulHttpReqException(String message) {
            super(message);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private HttpUtils() {}

    /**
     * Makes a simple GET request to the given URL and returns the response
     * content as a String.
     *
     * @param url
     * @return
     * @throws IOException
     * @throws UnsuccessfulHttpReqException if status code is unsuccessful (not 2xx).
     */
    public static String urlToString(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());
            if (statusCode < 200 || statusCode >= 300) {
                String msg = "A http request to url: " + url + " was unsuccessful. " +
                        "Response had a status code of: " + statusCode + "\n" +
                        "Response content was: \n" +
                        content;
                logger.info(msg);
                throw new UnsuccessfulHttpReqException(msg);
            }
            return content;
        }
    }
}
