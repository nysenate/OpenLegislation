package gov.nysenate.openleg.api.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class SenatorsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DISTRICT_JSON_FOLDER_PATH = "WEB-INF/classes/data/districts/";

    private static final String VIEW_PATH = "/senators/index.jsp";

    private static Logger logger = Logger.getLogger(SenatorsServlet.class);

    private static ArrayList<JSONObject> districts = null;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String format = request.getParameter("format");
        String uri = request.getRequestURI();

        if (uri.indexOf(".") != -1)
            format = uri.substring(uri.indexOf(".") + 1);

        if (format != null) {
            if (format.equals("json")) {
                displayJSON(request, response);
            }
        } else {
            request.setAttribute("districts", districts);
            getServletContext().getRequestDispatcher(VIEW_PATH).forward(
                    request, response);
        }
    }

    private void displayJSON(HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("ISO-8859-1");
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    response.getOutputStream(), "ISO-8859-1"));

            out.println("[");

            Iterator<JSONObject> it = districts.iterator();
            JSONObject district = null;

            while (it.hasNext()) {
                district = it.next();
                out.println(district.toString());

                if (it.hasNext())
                    out.println(",");
            }

            out.println("]");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void init() throws ServletException {
        super.init();

        String encoding = "ISO-8859-1";

        try {
            districts = new ArrayList<JSONObject>();

            for (int i = 1; i <= 62; i++) {
                String jsonPath = DISTRICT_JSON_FOLDER_PATH + "sd" + i
                        + ".json";

                URL jsonUrl = getServletContext().getResource(jsonPath);

                StringBuilder jsonb = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(jsonUrl.openStream(), encoding));

                char[] buf = new char[1024];
                int numRead = 0;
                while ((numRead = reader.read(buf)) != -1) {
                    jsonb.append(buf, 0, numRead);
                    buf = new char[1024];
                }
                reader.close();

                JSONObject jsono = new JSONObject(jsonb.toString());

                JSONObject jSenator = jsono.getJSONObject("senator");

                String senatorName = jSenator.getString("name");

                jSenator.put("name", senatorName);

                String senatorKey = senatorName.replaceAll(
                        "(?i)( (jr|sr)\\.?)", "");
                String[] tuple = senatorKey.split(" ");
                senatorKey = tuple[tuple.length - 1].toLowerCase();

                jSenator.put("key", senatorKey);

                districts.add(jsono);

                logger.info(jsono.get("district"));
                logger.info(jsono.getJSONObject("senator").get("name"));

            }

            Collections.sort(districts, new byLastName());
        } catch (Exception e) {
            logger.error("error loading json district files", e);
        }
    }

    class byLastName implements java.util.Comparator<Object> {
        @Override
        public int compare(Object districtA, Object districtB) {
            int sdif = 0;

            try {
                JSONObject senatorA = ((JSONObject) districtA)
                        .getJSONObject("senator");
                JSONObject senatorB = ((JSONObject) districtB)
                        .getJSONObject("senator");

                sdif = senatorA.getString("key").compareTo(
                        senatorB.getString("key"));
            } catch (Exception e) {
                logger.error("error sorting districts", e);
            }

            return sdif;
        }
    }
}
