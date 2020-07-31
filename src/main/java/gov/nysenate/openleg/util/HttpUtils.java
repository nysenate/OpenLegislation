package gov.nysenate.openleg.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    /**
     * Makes a simple GET request to the given URL and returns the response
     * content as a String.
     *
     * @param url
     * @return
     * @throws IOException
     * @throws UnsuccessfulHttpReqException if status code is unsuccessful (not 2xx).
     */
    public String urlToString(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());
            if (!successfulStatusCode(statusCode)) {
                handleUnsuccessfulStatusCode(url, statusCode, content);
            }
            return content;
        }
    }


    private boolean successfulStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private String handleUnsuccessfulStatusCode(String url, int statusCode, String content) {
        String msg = "A http request to url: " + url + " was unsuccessful. " +
                "Response had a status code of: " + statusCode + "\n" +
                "Response content was: \n" +
                content;
        logger.info(msg);
        throw new UnsuccessfulHttpReqException(msg);
    }
}
