package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Vote;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class VoteLineParser implements LineParser {
    private final static DateFormat DATE_PARSER = new SimpleDateFormat ("MM/dd/yy");

    private Vote vote = null;

    private Date voteDate = null;
    private int ayeCount = -1;
    private int nayCount = -1;
    private ArrayList<String> ayes = new ArrayList<String>();
    private ArrayList<String> nays = new ArrayList<String>();
    private ArrayList<String> abstains = new ArrayList<String>();
    private ArrayList<String> excused = new ArrayList<String>();

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        StringTokenizer st = new StringTokenizer(lineData," ");
        String token = st.nextToken();

        if (token.equals("Senate"))	{
            //we're on a new vote
            //see comment in saveData()
            this.clear();

            //create new vote
            st.nextToken();//Vote
            st.nextToken();//Bill:
            st.nextToken();//Sxxx
            st.nextToken();//Date:

            try	{
                voteDate = DATE_PARSER.parse(st.nextToken());
            }
            catch (ParseException pe) {
                pe.printStackTrace();
            }

            st.nextToken();//Aye
            st.nextToken();//-

            ayeCount = Integer.parseInt(st.nextToken());//Aye Count #

            st.nextToken();//Nay
            st.nextToken();//-

            nayCount = Integer.parseInt(st.nextToken());//Nay Count #

        }
        else {
            //add to existing vote
            String vote = token;
            String voter = null;
            String nextToken = null;

            while (st.hasMoreTokens()) {
                voter = st.nextToken();

                if (voter.equals("Hassell-Thompso")) {
                    voter = "Hassell-Thompson";
                }
                else if (voter.equals("Johnson")) { //something Johnson, Johnson C, or Johnson O
                    nextToken = st.nextToken();

                    if (nextToken.length() == 1) {
                        voter = voter + ' ' + nextToken;
                        nextToken = null;
                    }
                }

                if (vote.equalsIgnoreCase("Aye") || vote.equalsIgnoreCase("Yea"))
                    ayes.add(voter);
                else if (vote.equalsIgnoreCase("Nay"))
                    nays.add(voter);
                else if (vote.equalsIgnoreCase("Abs"))
                    abstains.add(voter);
                else if (vote.equalsIgnoreCase("Exc"))
                    excused.add(voter);

                if (nextToken != null) {
                    vote = nextToken;
                    nextToken = null;
                }
                else if (st.hasMoreTokens())
                    vote = st.nextToken();
            }
        }
    }

    @Override
    public void saveData(Bill bill) {
        vote = new Vote(bill, voteDate, ayeCount, nayCount);
        vote.setAyes(ayes);
        vote.setNays(nays);
        vote.setAbstains(abstains);
        vote.setExcused(excused);
        vote.setVoteType(Vote.VOTE_TYPE_FLOOR);

        /*
         * Floor votes come through in SOBI files.
         * A bill can only possibly have one floor vote.
         * The bill being sent in to this method is not filled out,
         * 		just what has been compiled from the current SOBI
         * LBDC occasionally sends duplicate floor votes, or incorrect
         * 		votes followed by an amended vote
         * 			ex. see S1892 in SOBI.D110119.T140802.TXT
         * 
         * Given the above, whenever we see a vote it makes sense to
         * 		set a new list and only the current vote to the list
         * Any other votes associated with the bill will be added
         * 		when the bill is merged with existing information
         */
        bill.setVotes(new ArrayList<Vote>());
        bill.addVote(vote);
    }

    @Override
    public void clear() {
        vote = null;

        voteDate = null;
        ayeCount = -1;
        nayCount = -1;
        ayes = new ArrayList<String>();
        nays = new ArrayList<String>();
        abstains = new ArrayList<String>();
        excused = new ArrayList<String>();
    }
}
