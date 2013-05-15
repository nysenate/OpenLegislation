package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.qa.ReportReader;
import gov.nysenate.openleg.qa.model.ProblemBill;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

/**
 * Servlet implementation class ReportServlet
 */
public class ReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String VIEW_PATH = "/report/index.jsp";

    private static ReportReader rr = null;

    public ReportServlet() {
        super();
        rr = new ReportReader();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String format = request.getParameter("format");
        String uri = request.getRequestURI();

        List<ProblemBill> list = rr.getProblemBills();

        if (uri.indexOf(".")!=-1)
            format = uri.substring(uri.indexOf(".")+1);

        if (format != null) {
            if (format.equals("json")) {
                JsonGenerator gen = ApiHelper.getMapper().getJsonFactory().createJsonGenerator(response.getWriter());
                gen.setPrettyPrinter(new DefaultPrettyPrinter());
                ApiHelper.getMapper().writeValue(gen, list);
            }
        }
        else {
            getServletContext().getRequestDispatcher(VIEW_PATH).forward(request, response);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
