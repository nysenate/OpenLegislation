package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class BillEventLineParser implements LineParser {
    private static Logger logger = Logger.getLogger(BillEventLineParser.class);

    private final static DateFormat DATE_PARSER = new SimpleDateFormat ("MM/dd/yy");

    private ArrayList<Action> actions = new ArrayList<Action>();

    private String sameAs = null;

    private String currentCommittee = null;
    private ArrayList<String> pastCommittees = new ArrayList<String>();

    private boolean stricken = false;

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        Bill bill = billParser.getCurrentBill();

        Date actionDate = null;
        try {
            actionDate = DATE_PARSER.parse(lineData.substring(0,8));
        } catch (ParseException e) {
            logger.error(e);
        }
        finally {
            if(actionDate == null) return;
        }


        String actionText = lineData.substring(9);

        Action action = new Action(bill, actionDate, actionText);

        Calendar c = Calendar.getInstance();
        c.setTime(actionDate);

        /*
         * this fixes instances where two identical events occur
         * on the same day, in the past the second instance
         * was left out
         */
        while(actions.contains(action)) {
            c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 1);

            action = new Action(bill, c.getTime(), actionText);
        }

        /*
         * preserves ordering of billevents that occur on
         * the same day, otherwise order is at the mercy of
         * the jvm higher ups
         */
        for(Action a:actions) {
            if(a.getDate().equals(action.getDate())) {
                c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 1);
                action = new Action(bill, c.getTime(), actionText);
            }
        }

        actions.add(action);

        String actionTextTemp = actionText.toUpperCase();
        if (actionText.indexOf("REFERRED TO ")!=-1) {
            int subIdx = actionText.indexOf("REFERRED TO ") + 12;
            String newCommittee = actionText.substring(subIdx).trim();
            if(currentCommittee != null) {
                pastCommittees.add(currentCommittee);
            }
            currentCommittee = newCommittee;
        }
        else if (actionText.indexOf("COMMITTED TO ")!=-1) {
            int subIdx = actionText.indexOf("COMMITTED TO ") + 13;
            String newCommittee = actionText.substring(subIdx).trim();
            if(currentCommittee != null) {
                pastCommittees.add(currentCommittee);
            }
            currentCommittee = newCommittee;
        }
        else if (actionText.indexOf("RECOMMIT TO ")!=-1) {
            int subIdx = actionText.indexOf("RECOMMIT TO ") + 12;
            String newCommittee = actionText.substring(subIdx).trim();
            if(currentCommittee != null) {
                pastCommittees.add(currentCommittee);
            }
            currentCommittee = newCommittee;
        }
        else if(actionText.contains("REPORT CAL")
                || actionText.contains("THIRD READING")
                || actionText.contains("RULES REPORT")) {

            if(currentCommittee != null) {
                pastCommittees.add(currentCommittee);
            }
            currentCommittee = null;
        }
        else if (actionTextTemp.startsWith("SUBSTITUTED FOR "))	{
            String substituted = actionText.substring(16).trim().toUpperCase();
            sameAs = formatSameAs(sameAs,substituted);
        }
        else if (actionTextTemp.startsWith("SUBSTITUTED BY ")) {
            String substituted = actionText.substring("SUBSTITUTED BY ".length()).trim().toUpperCase();
            sameAs = formatSameAs(sameAs,substituted);
        }

        stricken = actionTextTemp.contains("ENACTING CLAUSE STRICKEN");

        //currently we don't want to keep track of assembly committees
        if(bill.getSenateBillNo().startsWith("A")) {
            currentCommittee = null;
            pastCommittees.clear();
        }
    }

    @Override
    public void saveData(Bill bill) {
        bill.setActions(actions);
        bill.setSameAs(sameAs);
        bill.setCurrentCommittee(currentCommittee);
        bill.setPastCommittees(pastCommittees);
        bill.setStricken(stricken);
    }

    @Override
    public void clear() {
        actions = new ArrayList<Action>();

        sameAs = null;

        currentCommittee = null;
        pastCommittees = new ArrayList<String>();

        stricken = false;
    }

    private String formatSameAs(String sameAs, String billNo) {
        StringTokenizer st  = null;
        String newSameAs = null;
        SortedSet<String> set = new TreeSet<String>();

        if(sameAs == null)
            sameAs = "";
        if(billNo != null)
            set.add(billNo);

        sameAs = sameAs.replaceAll(" ,",",");
        sameAs = sameAs.replaceAll(",,", ",");
        st  = new StringTokenizer(sameAs, ",");

        while(st.hasMoreElements()) {
            String token = st.nextToken().trim();
            if(!token.equals("")) {
                set.add(token);
            }
        }

        for(String s:set) {
            if(newSameAs == null) {
                newSameAs = s;
            }
            else {
                newSameAs += ", " + s;
            }
        }

        return newSameAs;
    }
}
