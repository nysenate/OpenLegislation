package gov.nysenate.openleg.model.admin;

import gov.nysenate.openleg.util.Application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class ReportDAO
{
    private static class JoinedReportHandler implements ResultSetHandler<ArrayList<Report>> {

        @Override
        public ArrayList<Report> handle(ResultSet results) throws SQLException
        {
            // So that we can return the reports in a sorted order if necessary
            ArrayList<Report> orderedReports = new ArrayList<Report>();
            HashMap<Integer, Report> reports = new HashMap<Integer, Report>();
            HashMap<Integer, ReportError> allErrors = new HashMap<Integer, ReportError>();

            while(results.next()) {
                // Create report (if we need to)
                int reportId = results.getInt("reportId");
                if (!reports.containsKey(reportId)) {
                    Report report = new Report(reportId, results.getTimestamp("reportTime"));
                    reports.put(reportId, report);
                    orderedReports.add(report);
                }

                // Create Error (if we need to)
                int errorId = results.getInt("errorId");
                String oid = results.getString("oid");
                String field = results.getString("field");
                if (!allErrors.containsKey(errorId)) {
                    Date openedAt = results.getTimestamp("openedAt");
                    Date closedAt = results.getTimestamp("closedAt");
                    allErrors.put(errorId, new ReportError(errorId, oid, field, openedAt, closedAt));
                }

                // Create observation
                ReportObservation observation = new ReportObservation();
                observation.setId(results.getInt("observationId"));
                observation.setOid(oid);
                observation.setField(field);
                observation.setActualValue(results.getString("actualValue"));
                observation.setObservedValue(results.getString("observedValue"));
                observation.setReportId(reportId);
                observation.setErrorId(errorId);

                // Link them all together
                Report report = reports.get(reportId);
                report.addObservation(observation);
                observation.setReport(report);

                ReportError error = allErrors.get(errorId);
                error.addObservation(observation);
                observation.setError(error);
            }

            // Register all the errors to the reports appropriately
            for (Report report : reports.values()) {
                Date reportEndTime = report.getTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(reportEndTime);
                cal.add(Calendar.DATE, -5);
                Date reportStartTime = cal.getTime();

                ArrayList<ReportError> newErrors = new ArrayList<ReportError>();
                ArrayList<ReportError> openErrors = new ArrayList<ReportError>();
                ArrayList<ReportError> closedErrors = new ArrayList<ReportError>();
                ArrayList<ReportObservation> closedObservations = new ArrayList<ReportObservation>();
                for (ReportError error : allErrors.values()) {
                    Date openedAt = error.getOpenedAt();
                    Date closedAt = error.getClosedAt();

                    if (openedAt.before(reportEndTime) && (closedAt == null || closedAt.after(reportEndTime))) {
                        openErrors.add(error);
                    }
                    else if (openedAt.after(reportStartTime) && (openedAt.equals(reportEndTime) || openedAt.before(reportEndTime))) {
                        newErrors.add(error);
                    }
                    else if (closedAt != null && closedAt.after(reportStartTime) && (closedAt.equals(reportEndTime) || closedAt.before(reportEndTime))) {
                        closedErrors.add(error);
                        closedObservations.add(error.getObservations().iterator().next());
                    }
                }
                report.setNewErrors(newErrors);
                report.setOpenErrors(openErrors);
                report.setClosedErrors(closedErrors);
                report.getObservations().addAll((closedObservations));
            }

            return orderedReports;
        }

    }

    public static ArrayList<Report> getReports() throws SQLException
    {
        String sql = "SELECT "+
            "report.id as reportId, "+
            "report.time as reportTime, "+
            "report_error.oid as oid, "+
            "report_error.field as field, "+
            "report_error.id as errorId, "+
            "report_error.closedAt as closedAt, "+
            "report_error.openedAt as openedAt, "+
            "report_observation.id as observationId, "+
            "report_observation.actualValue as actualValue, "+
            "report_observation.observedValue as observedValue "+
        "FROM report " +
            "LEFT JOIN report_observation ON (report.id=report_observation.reportId) " +
            "LEFT JOIN report_error ON (report_observation.errorId=report_error.id) ";

        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
        return runner.query(sql, new JoinedReportHandler());
    }

    public static Report getReport(int reportId) throws SQLException
    {
        String sql = "SELECT "+
            "report.id as reportId, "+
            "report.time as reportTime, "+
            "report_error.oid as oid, "+
            "report_error.field as field, "+
            "report_error.id as errorId, "+
            "report_error.closedAt as closedAt, "+
            "report_error.openedAt as openedAt, "+
            "report_observation.id as observationId, "+
            "report_observation.actualValue as actualValue, "+
            "report_observation.observedValue as observedValue "+
        "FROM report " +
            "LEFT JOIN report_observation ON (report.id=report_observation.reportId) " +
            "LEFT JOIN report_error ON (report_observation.errorId=report_error.id) " +
        "WHERE report.id = ?";

        try {
            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
            return runner.query(sql, new JoinedReportHandler(), reportId).get(0);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
