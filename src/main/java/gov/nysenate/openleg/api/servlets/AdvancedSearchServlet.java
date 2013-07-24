package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.services.model.Committee;
import gov.nysenate.services.model.Senator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class AdvancedSearchServlet extends HttpServlet
{
    private ArrayList<Senator> senators;
    private ArrayList<Committee> committees;

    public void init() throws ServletException
    {
        ObjectMapper mapper = new ObjectMapper();
        int sessionYear = SessionYear.getSessionYear();

        try {
            File committeesBase = new File(SenatorsServlet.class.getClassLoader().getResource("data/committees/").getPath());
            File committeesDir = new File(committeesBase, String.valueOf(sessionYear));
            if (!committeesDir.exists()) committeesDir.mkdirs();

            this.committees = new ArrayList<Committee>();
            for (File committeeFile : FileUtils.listFiles(committeesDir, new String[]{"json"}, false)) {
                this.committees.add(mapper.readValue(committeeFile, Committee.class));
            }

            File senatorsBase = new File(SenatorsServlet.class.getClassLoader().getResource("data/senators/").getPath());
            File senatorsDir = new File(senatorsBase, String.valueOf(sessionYear));
            if (!senatorsDir.exists()) senatorsDir.mkdirs();

            this.senators = new ArrayList<Senator>();
            for (File senatorFile : FileUtils.listFiles(senatorsDir, new String[]{"json"}, false)) {
                this.senators.add(mapper.readValue(senatorFile, Senator.class));
            }
        }
        catch (IOException e) {
            throw new ServletException(e);
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute("senators", this.senators);
        request.setAttribute("committees", this.committees);
        request.getSession().getServletContext().getRequestDispatcher("/views/advanced.jsp").forward(request, response);
    }

    /**
     * Proxy to doGet
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
