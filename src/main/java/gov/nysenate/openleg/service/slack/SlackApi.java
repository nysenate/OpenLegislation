package gov.nysenate.openleg.service.slack;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonObject;


public class SlackApi {

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
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            System.out.println(response.toString());
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

}
