package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.Application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public class ViewReport
{
    public List<Error> displayReports(int id)
    {
        try {
            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
            return runner.query("SELECT * FROM error WHERE reportId = ? ORDER BY billId", new BeanListHandler<Error>(Error.class), id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Error>();
        }
    }

    public List<Report> displayReportOption()
    {
        try {
            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
            return runner.query("SELECT * FROM report", new BeanListHandler<Report>(Report.class));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Report>();
        }
    }
}
