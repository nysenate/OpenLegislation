package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.AbstractDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class JsonOpenlegDaoUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegDaoUtils.class);

    public static HttpURLConnection setConnection(String URL, String requestMethod, boolean useCaches, boolean doOutput) {

        try {
            HttpURLConnection connection = null;
            java.net.URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type",  "application/json; charset=utf-8");
            connection.setUseCaches(useCaches);
            connection.setDoOutput(doOutput);
            return connection;
        } catch (Exception e) {
            logger.error("A connection could not be made to URL " + URL);
            e.printStackTrace();
        }
        return null;
    }

    public static void readInputStream(HttpURLConnection connection,StringBuffer response) {
        logger.info(connection.getContentType());
        InputStream is = null;
        try {
            is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while (( line = rd.readLine()) != null ) {
                response.append(line);
            }
            rd.close();
        } catch (IOException e) {
            logger.error("The StringBuffer could not read the incoming stream ");
            e.printStackTrace();
        }
    }
}
