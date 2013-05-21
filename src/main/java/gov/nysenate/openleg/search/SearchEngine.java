package gov.nysenate.openleg.search;

import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.Lucene;
import gov.nysenate.openleg.lucene.LuceneResult;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.util.serialize.JsonSerializer;
import gov.nysenate.openleg.util.serialize.XmlSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;

public class SearchEngine extends Lucene implements OpenLegConstants {

    public static void main(String[] args) throws Exception {
        SearchEngine engine = new SearchEngine();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line = null;
        System.out.print("openlegLuceneConsole> ");
        while (!(line = reader.readLine()).equals("quit"))
        {
            if (line.startsWith("optimize"))
                engine.optimize();
            else if (line.startsWith("delete"))
            {
                StringTokenizer cmd = new StringTokenizer(line.substring(line.indexOf(" ")+1)," ");
                String type = cmd.nextToken();
                String ids = (cmd.hasMoreTokens() ? cmd.nextToken() : null);

                if(ids != null) {
                    String tokens[] = ids.split(",");
                    for(String id:tokens) {
                        engine.deleteSenateObjectById(type, id);
                    }
                }
            }
            else if (line.startsWith("create"))
                engine.createIndex();
            else {
                SenateResponse sr = engine.search(line, "xml", 1, 10, null, false);
                if(sr != null && !sr.getResults().isEmpty()) {
                    for(Result r:sr.getResults()) {
                        System.out.println(r.getOid());
                    }
                }
            }

            System.out.print("openleg search > ");
        }
        System.out.println("Exiting Search Engine");
    }

    private static SearchEngine searchEngine = null;

    public static synchronized SearchEngine getInstance() {
        if(searchEngine == null) {
            searchEngine = new SearchEngine();
        }
        return searchEngine;
    }

    private SearchEngine() {
        super(Application.getConfig().getValue("data.lucene"));
        logger = Logger.getLogger(SearchEngine.class);
    }

    protected DateFormat DATE_FORMAT_MEDIUM = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);

    public void deleteSenateObject (ILuceneObject obj) throws Exception
    {
        if (obj instanceof Agenda) {
            Agenda agenda = (Agenda)obj;
            if (agenda.getAddendums() != null)
                for( Addendum addendum : agenda.getAddendums() )
                    for( Meeting meeting : addendum.getMeetings() ) {
                        deleteSenateObject( meeting );
                    }
        }
        else {
            if(obj instanceof Bill) {
                Bill bill = (Bill)obj;
                if(bill.getActions() != null) {
                    for(Action be:bill.getActions()) {
                        deleteSenateObject(be);
                    }
                }
                if(bill.getVotes() != null) {
                    for(Vote vote:bill.getVotes()) {
                        deleteSenateObject(vote);
                    }
                }
            }

            if(obj.luceneOid() == null) return;

            deleteSenateObjectById(obj.luceneOtype(), obj.luceneOid());
        }
    }

    public void deleteSenateObjectById (String type, String id) throws Exception {
        closeSearcher();
        deleteDocuments(type, id);
        openSearcher();
    }

    public boolean indexSenateObject(SenateObject senObj) throws IOException {
        return indexSenateObjects(
                new ArrayList<SenateObject>(
                        Arrays.asList(senObj)),
                        new LuceneSerializer[]{
                    new XmlSerializer(),
                    new JsonSerializer()});
    }

    public  boolean indexSenateObjects (Collection<SenateObject> objects, LuceneSerializer[] ls) throws IOException
    {
        createIndex ();
        IndexWriter indexWriter = new IndexWriter(getDirectory(), getConfig());

        Iterator<SenateObject> it = objects.iterator();
        while (it.hasNext()) {
            ILuceneObject obj = it.next();

            if(obj instanceof Agenda) {
                Agenda agenda = (Agenda)obj;

                if (agenda.getAddendums() != null) {
                    for( Addendum addendum : agenda.getAddendums()) {
                        addendum.setAgenda(agenda);
                        for( Meeting meeting : addendum.getMeetings() ) {
                            meeting.setAddendums(new ArrayList<Addendum>(Arrays.asList(addendum)));
                            meeting.setModified(agenda.getModified());
                            addDocument(meeting, ls, indexWriter);
                        }
                    }
                }
            }
            else if(obj instanceof Bill) {
                Bill bill = (Bill)obj;

                this.deleteDocumentsByQuery("otype:action AND billno:" + bill.getSenateBillNo(), indexWriter);

                if(bill.getActions() != null) {
                    for(Action be:bill.getActions()) {
                        be.setModified(bill.getModified());
                        be.setBill(bill);
                        addDocument(be, ls, indexWriter);
                    }
                }

                this.deleteDocumentsByQuery("otype:vote AND billno:" + bill.getSenateBillNo(), indexWriter);

                if(bill.getVotes() != null) {
                    for(Vote vote: bill.getVotes()) {
                        vote.setModified(bill.getModified());
                        vote.setBill(bill);
                        addDocument(vote, ls, indexWriter);
                    }
                }

                addDocument(bill, ls, indexWriter);
            }
            else {
                addDocument(obj, ls, indexWriter);
            }
        }

        indexWriter.commit();

        logger.info("done indexing objects(" + objects.size() + "). Closing index.");
        indexWriter.close();
        return true;
    }

    public SenateResponse search(String searchText, String format, int start, int max, String sortField, boolean reverseSort) throws ParseException, IOException {
        searchText = searchText.replaceAll("otype:resolution", "(otype:bill AND oid:(R* OR E* OR J* OR K* OR L*))");
        String data = "o"+format.toLowerCase()+"";

        LuceneResult result = search(searchText,start,max,sortField,reverseSort);

        SenateResponse response = new SenateResponse();

        if (result == null)
        {
            response.addMetadataByKey("totalresults", 0 );
        }
        else
        {
            response.addMetadataByKey("totalresults", result.total );

            for (Document doc : result.results) {
                String lastModified = doc.get("modified");
                if (lastModified == null || lastModified.length() == 0)
                    lastModified = new Date().getTime()+"";

                HashMap<String,String> fields = new HashMap<String,String>();

                for(Fieldable field : doc.getFields()) {
                    fields.put(field.name(), doc.get(field.name()));
                }

                response.addResult(new Result(
                        doc.get("otype"),
                        doc.get(data),
                        doc.get("oid"),
                        Long.parseLong(lastModified),
                        Boolean.parseBoolean(doc.get("active")),
                        fields));
            }
        }

        return response;
    }

    public Bill getBill(String oid) {
        return getSenateObject(Bill.formatBillNo(oid), "bill", Bill.class);
    }

    public Meeting getMeeting(String oid) {
        return getSenateObject(oid, "meeting", Meeting.class);
    }

    public Transcript getTranscript(String oid) {
        return getSenateObject(oid, "transcript", Transcript.class);
    }

    public Calendar getCalendar(String oid) {
        return getSenateObject(oid, "calendar", Calendar.class);
    }

    public <T extends SenateObject> T getSenateObject(String oid, String type, Class<T> clazz) {
        T ret = null;

        QueryBuilder queryBuilder = null;
        try {
            queryBuilder = QueryBuilder.build().otype(type).and().oid(oid, true);
        } catch (QueryBuilderException e) {
            logger.error(e);
            return ret;
        }

        ArrayList<T> senateObjects = getSenateObjects(queryBuilder.query(), clazz);

        if(senateObjects.isEmpty())
            return ret;

        return senateObjects.get(0);
    }

    public <T extends SenateObject> ArrayList<T> getSenateObjects(String query) {
        return getSenateObjects(query, null);
    }

    public <T extends SenateObject> ArrayList<T> getSenateObjects(String query, Class<T> clazz) {
        ArrayList<T> senateObjects = new ArrayList<T>();

        SenateObjectSearch<T> longSearch = new SenateObjectSearch<T>().clazz(clazz).query(query);

        for(T senateObject:longSearch) {
            senateObjects.add(senateObject);
        }

        return senateObjects;
    }

    public Bill getNewestAmendment(String oid) {
        oid = Bill.formatBillNo(oid);
        String[] billParts = oid.split("-");

        return getNewestAmendment(billParts[0], billParts[1]);
    }

    private Bill getNewestAmendment(String billNumber, String year) {
        ArrayList<Bill> bills = getRelatedBills(billNumber, year);

        int size = bills.size();

        if(bills.isEmpty())
            return null;
        if(size == 1)
            return bills.get(0);

        Collections.sort(bills);

        return bills.get(size-1);
    }

    private ArrayList<Bill> getRelatedBills(String billNumber, String year) {
        int length = billNumber.length();
        if(!Character.isDigit(billNumber.charAt(length-1))) {
            billNumber = billNumber.substring(0, length-1);
        }

        String query = TextFormatter.append("otype:bill AND oid:((",
                billNumber, "-", year,
                " OR [", billNumber, "A-", year,
                " TO ", billNumber, "Z-", year,
                "]) AND ", billNumber, "*-", year, ")");

        return getSenateObjects(query);
    }
}