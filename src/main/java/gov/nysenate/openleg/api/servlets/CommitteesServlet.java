package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.util.CommitteeWriter;
import gov.nysenate.services.model.Committee;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public class CommitteesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String COMMITTEE_JSON_FOLDER_PATH = "WEB-INF/classes/data/committees/committees.json";

    private static final String VIEW_PATH = "/committees/index.jsp";

    private static Logger logger = Logger.getLogger(CommitteesServlet.class);

    ArrayList<Committee> committees;

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
            request.setAttribute("committees", committees);
            getServletContext().getRequestDispatcher(VIEW_PATH).forward(
                    request, response);
        }
    }

    private void displayJSON(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("ISO-8859-1");

        try {
            response.getWriter().write(ApiHelper.getMapper().writeValueAsString(committees));
        } catch (JsonGenerationException e) {
            logger.error(e);
        } catch (JsonMappingException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
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

        CommitteeWriter cw = new CommitteeWriter();
        File file = new File(getServletContext().getRealPath(COMMITTEE_JSON_FOLDER_PATH));
        committees = cw.getCommitteesFromJson(file, ApiHelper.getMapper());
    }

}
