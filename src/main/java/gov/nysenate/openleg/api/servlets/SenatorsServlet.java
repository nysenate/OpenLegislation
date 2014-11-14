package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.util.RequestUtils;
import gov.nysenate.openleg.util.RequestUtils.FORMAT;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.services.model.Senator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class SenatorsServlet extends HttpServlet
{
    private static Logger logger = Logger.getLogger(SenatorsServlet.class);

    private static Pattern pathPattern = Pattern.compile("/([0-9]{4}).*");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        int sessionYear = SessionYear.getSessionYear();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            Matcher pathMatcher = pathPattern.matcher(pathInfo);
            if (pathMatcher.find()) {
                sessionYear = SessionYear.getSessionYear(pathMatcher.group(1));
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        File senatorsBase = new File(java.net.URLDecoder.decode(SenatorsServlet.class.getClassLoader().getResource("data/senators/").getPath()));
        File senatorsDir = new File(senatorsBase, String.valueOf(sessionYear));
        if (!senatorsDir.exists()) senatorsDir.mkdirs();

        ArrayList<Senator> senators = new ArrayList<Senator>();
        for (File senatorFile : FileUtils.listFiles(senatorsDir, new String[]{"json"}, false)) {
            senators.add(mapper.readValue(senatorFile, Senator.class));
        }

        Collections.sort(senators, new Comparator<Senator>() {
            public int compare(Senator a, Senator b) {
                return a.getShortName().compareToIgnoreCase(b.getShortName());
            }
        });

        FORMAT format = RequestUtils.getFormat(request, FORMAT.HTML);
        switch (format) {
        case JSON:
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getOutputStream(), senators);
            break;
        case HTML:
            request.setAttribute("senators", senators);
            request.setAttribute("sessionStart", sessionYear);
            request.setAttribute("sessionEnd", sessionYear+1);
            request.getRequestDispatcher("/views/senators.jsp").forward(request, response);
            break;
        case XML:
            // There are no plans to add XML support at this time
            break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    @Override
    public void init() throws ServletException
    {
        super.init();
    }
}
