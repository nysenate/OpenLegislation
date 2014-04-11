package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.lucene.Lucene;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class TranscriptServlet extends HttpServlet
{
    private static Logger logger = Logger.getLogger(TranscriptServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Calendar cal = Calendar.getInstance();

        String year = request.getParameter("year");
        if (year == null) {
            year = String.valueOf(cal.get(Calendar.YEAR));
        }

        String month = request.getParameter("month");
        if (month == null) {
            month = "";
        }

        String query = "otype:transcript AND publish_date:"+year+"-"+month+"*";
        String searchtext = request.getParameter("searchtext");
        if (searchtext != null && searchtext.trim().length() != 0) {
            query += " AND full:("+searchtext+")";
        }
        else {
            searchtext = "";
        }

        Lucene lucene = Application.getLucene();
        SenateResponse luceneResults = lucene.search(query, 0, 1000, "published", true);
        ApiHelper.buildSearchResultList(luceneResults);
        ArrayList<Transcript> transcripts = new ArrayList<Transcript>();
        for (Result result : luceneResults.getResults()) {
            transcripts.add((Transcript)result.getObject());
        }

        request.setAttribute("year", year);
        request.setAttribute("month", month);
        request.setAttribute("searchtext", searchtext);
        request.setAttribute("transcripts", transcripts);
        request.getSession().getServletContext().getRequestDispatcher("/views/transcripts.jsp").forward(request, response);
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
