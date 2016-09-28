package gov.nysenate.openleg.service.slack;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonObject;
import gov.nysenate.openleg.model.slack.SlackMessage;
import org.apache.commons.io.IOUtils;

/**
 * Copied from https://github.com/gpedro/slack-webhook with some customizations
 * (This done before that api was released via maven)
 *
 * Makes http calls to Slack's webhook api when given a {@link SlackMessage}
 */
public class SlackApi {

    private static final String channelNotFoundMessage = "channel_not_found";

    private String service;

    public SlackApi(String service) {
        if(service == null) {
            throw new IllegalArgumentException("Missing WebHook URL Configuration @ SlackApi");
        } else if (!service.startsWith("https://hooks.slack.com/services/")) {
            throw new IllegalArgumentException("Invalid Service URL. WebHook URL Format: https://hooks.slack.com/services/{id_1}/{id_2}/{token}");
        }

        this.service = service;
    }

    public void call(SlackMessage message) {
        if(message != null) {
            this.send(message.prepare());
        }
    }

    private String send(JsonObject message) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(this.service);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String payload = "payload="+URLEncoder.encode(message.toString(), "UTF-8");

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes(payload);
            wr.flush ();
            wr.close ();

            //Get Response
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                InputStream es = connection.getErrorStream();
                String errorMessage = IOUtils.toString(es);
                IOUtils.closeQuietly(es);

                if (responseCode == 404 && channelNotFoundMessage.equals(errorMessage)) {
                    throw new SlackChannelNotFoundException(message.get("channel").getAsString(), errorMessage);
                }

                throw new SlackApiException(errorMessage, responseCode);
            }
            InputStream is = connection.getInputStream();
            String responseMessage = IOUtils.toString(is);
            IOUtils.closeQuietly(is);

            return responseMessage;

        } catch (IOException e) {
            throw new SlackApiException(e);
        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

}
