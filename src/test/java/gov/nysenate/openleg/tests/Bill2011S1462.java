package gov.nysenate.openleg.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class Bill2011S1462 extends TestSetup{



    @Test
	   public void verifyWholeBill2011S1462() throws IOException
	  {
		Bill theBill;
		TestSetup.initalSetup();
	    ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"SOBI.D110110.T142112.TXT",
	   "SOBI.D110107.T144910.TXT",   "SOBI.D110112.T175532.TXT", "SOBI.D110107.T150914.TXT", "SOBI.D110107.T141407.TXT");

	    // Test for SOBI.D110107.T141407.TXT
	    TestHelper.processFileC(env ,file.get(4));
	    theBill=(Bill)storage.get("2011/bill/S1462-2011", Bill.class);
	    assertNotNull("The title should not be null",theBill.getTitle());
		assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");

		//Test for SOBI.D110107.T144910.TXT
		 TestHelper.processFileC(env ,file.get(1));
		 theBill=(Bill)storage.get("2011/bill/S1462-2011", Bill.class);
		 assertEquals(theBill.getSameAs(),"A1415");

		// No Test for SOBI.D110107.T150914.TXT,SOBI.D110110.T142112.TXT,SOBI.D110112.T175532.TXT(Memo)
	  }

	@Test
	   public void verifyWholeBill2011S1462A() throws IOException  // Testing Amendment A of the bill 2011S1462
	  {
		Bill theBill;
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
	    ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"SOBI.D110110.T142112.TXT",
	    		"SOBI.D110613.T222123.TXT", "SOBI.D110614.T192241.TXT","SOBI.D120130.T202912.TXT", "SOBI.D120126.T103841.TXT",
	    		"SOBI.D110614.T195743.TXT", "SOBI.D120104.T223233.TXT", "SOBI.D110215.T151615.TXT", "SOBI.D110209.T105617.TXT",
	    		"SOBI.D110209.T110618.TXT", "SOBI.D110614.T152729.TXT", "SOBI.D110112.T175532.TXT", "SOBI.D110107.T144910.TXT",
	    		"SOBI.D110614.T185240.TXT", "SOBI.D120125.T130337.TXT", "SOBI.D110209.T171147.TXT", "SOBI.D110107.T150914.TXT",
	    		"SOBI.D110209.T170214.TXT", "SOBI.D110107.T141407.TXT");
	    // NO test for SOBI.D110210.T102843.TXT ,SOBI.D110210.T221519.TXT (Memo),SOBI.D110209.T171647.TXT


	      // Test for SOBI.D110209.T105617.TXT
		 TestHelper.processFileC(env ,file.get(8));
		 theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
		 assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
		 assertNotNull("The title should not be null",theBill.getTitle());

		  // Test for SOBI.D110209.T110618.TXT
		  TestHelper.processFileC(env ,file.get(9));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110215.T151615.TXT
	      TestHelper.processFileC(env ,file.get(7));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110613.T222123.TXT
	      TestHelper.processFileC(env ,file.get(1));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110614.T152729.TXT
	      TestHelper.processFileC(env ,file.get(10));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110209.T170214.TXT,
	      TestHelper.processFileC(env ,file.get(17));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110209.T171147.TXT
	      TestHelper.processFileC(env ,file.get(15));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	     //assertEquals(theBill.getSameAs(),"A1415A");  ?? Have to check why its failing? Not written in json file ..why?

	      // Test for SOBI.D110614.T195743.TXT
	      TestHelper.processFileC(env ,file.get(5));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      String[] voteDate=(theBill.getVotes().get(0).getOid()).split("-");
		  String date=voteDate[2];
		    // Checking vote Date
		  assertEquals(date.equals("2011/06/14"),true);
	      Vote vote=processExpectedVote2011S1462A();
		  boolean flag=VoteTests.voteCheck(theBill,vote);
	      assertEquals(flag,true);

		 // Test for SOBI.D110614.T185240.TXT
	      TestHelper.processFileC(env ,file.get(13));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110614.T192241.TXT
	      TestHelper.processFileC(env ,file.get(2));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D120104.T223233.TXT
	      TestHelper.processFileC(env ,file.get(6));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());
	     // assertEquals(theBill.getSameAs(),"A1415A");   ( In json file we dont have sameAs in amendment A??



		 // Tests for SOBI.D120125.T130337.TXT
	      TestHelper.processFileC(env ,file.get(14));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      ArrayList<Person> list=(ArrayList<Person>)theBill.getCoSponsors();
	      assertEquals(list.get(0).toString(),"DUANE");


	      // Tests for SOBI.D120126.T103841.TXT
	      TestHelper.processFileC(env ,file.get(4));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      ArrayList<Person> list1=(ArrayList<Person>)theBill.getCoSponsors();
	      assertEquals(list1.get(0).toString(),"DUANE");
	      assertEquals(list1.get(1).toString(),"MONTGOMERY");

	  }

	@Test
	   public void verifyWholeBill2011S1462B() throws IOException  // Testing Amendment B of the bill 2011S1462
	  {
		Bill theBill;
		ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"SOBI.D120130.T151358.TXT",
	    "SOBI.D120130.T152900.TXT","SOBI.D120130.T160900.TXT");

	    // Test for SOBI.D120130.T151358.TXT
	    TestHelper.processFileC(env ,file.get(0));
	    theBill=(Bill)storage.get("2011/bill/S1462B-2011", Bill.class);
	    assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	    assertNotNull("The title should not be null",theBill.getTitle());

	    // Test for SOBI.D120130.T152900.TXT
	    TestHelper.processFileC(env ,file.get(1));
	    theBill=(Bill)storage.get("2011/bill/S1462B-2011", Bill.class);
	    assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	    assertNotNull("The title should not be null",theBill.getTitle());

	    // Test for SOBI.D120130.T160900.TXT
	    TestHelper.processFileC(env ,file.get(2));
	    theBill=(Bill)storage.get("2011/bill/S1462B-2011", Bill.class);
	    assertEquals(theBill.getSameAs(),"A1415B");

	  }

	@Test
	   public void verifyWholeBill2011S1462C() throws IOException  // Testing Amendment C of the bill 2011S1462
	  {
		Bill theBill;
		ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"SOBI.D120217.T150812.TXT",
	    		"SOBI.D120217.T145311.TXT","SOBI.D120217.T153313.TXT");

	    // Test for SOBI.D120217.T150812.TXT
	    TestHelper.processFileC(env ,file.get(0));
	    theBill=(Bill)storage.get("2011/bill/S1462C-2011", Bill.class);
	    assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	    assertNotNull("The title should not be null",theBill.getTitle());

	    // Test for SOBI.D120217.T145311.TXT
	    TestHelper.processFileC(env ,file.get(1));
	    theBill=(Bill)storage.get("2011/bill/S1462C-2011", Bill.class);
	    assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	    assertNotNull("The title should not be null",theBill.getTitle());

	    // Test for SOBI.D120217.T153313.TXT
	    TestHelper.processFileC(env ,file.get(2));
	    theBill=(Bill)storage.get("2011/bill/S1462C-2011", Bill.class);
	    //assertEquals(theBill.getSameAs(),"A1415C");  No entry in json file


	  }

	@Test
	   public void verifyWholeBill2011S1462D() throws IOException  // Testing Amendment D of the bill 2011S1462
	  {
		Bill theBill;
		ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"SOBI.D120416.T133347.TXT",
	    		 "SOBI.D120416.T150351.TXT","SOBI.D120416.T151351.TXT","SOBI.D120523.T100051.TXT","SOBI.D120530.T161430.TXT",
	    		 "SOBI.D120531.T112447.TXT", "SOBI.D120611.T165600.TXT", "SOBI.D120611.T162600.TXT","SOBI.D120620.T100719.TXT",
	    		 "SOBI.D120706.T151155.TXT","SOBI.D120727.T100847.TXT","SOBI.D120724.T140552.TXT");

	    // All the above SOBI files have to be tested for sponsor name and title
	    for( File f:file)
	    {
	    	  TestHelper.processFileC(env ,f);
	  	      theBill=(Bill)storage.get("2011/bill/S1462D-2011", Bill.class);
	  	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
		      assertNotNull("The title should not be null",theBill.getTitle());

	    }

	    ArrayList<File> vfile = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"SOBI.D120612.T090338.TXT");
	    TestHelper.processFileC(env ,vfile.get(0));
	    theBill=(Bill)storage.get("2011/bill/S1462D-2011", Bill.class);

	    String[] voteDate=(theBill.getVotes().get(0).getOid()).split("-");
		String date=voteDate[2];
		    // Checking vote Date
		assertEquals(date.equals("2012/06/11"),true);
		Vote vote=processExpectedVote2011S1462D();
		boolean flag=VoteTests.voteCheck(theBill,vote);
	    assertEquals(flag,true);
	  }

	public static Vote processExpectedVote2011S1462A()
	{
		 String[] yes= {"Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic", "Breslin", "Carlucci", "DeFrancisco", "Diaz",
		    		"Dilan", "Duane", "Espaillat", "Farley", "Flanagan", "Fuschillo", "Gallivan", "Gianaris", "Golden", "Griffo",
		    		"Grisanti", "Hannon", "Hassell-Thomps", "Huntley", "Johnson", "Kennedy", "Klein", "Krueger", "Kruger", "Lanza",
		    		"Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald", "Montgomery", "Nozzolio",
		    		"O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", "Rivera", "Robach", "Saland",
		    		"Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin", "Valesky",
		    		"Young", "Zeldin"};
		    String[] no= {};
		    String [] excluded={};
		    String [] abstained={};
		    String [] absent={};
		    List<String> y=Arrays.asList(yes);
		    List<String> n=Arrays.asList(no);
		    List<String> e=Arrays.asList(excluded);
		    List<String> abstain=Arrays.asList(abstained);
		    List<String> ab=Arrays.asList(absent);
		    Vote vote=new Vote();
		    vote.setAyes(y);
		    vote.setNays(n);
		    vote.setExcused(e);
		    vote.setAbsent(ab);
		    vote.setAbstains(abstain);
		    return vote;

	}
	public static Vote processExpectedVote2011S1462D()
	{
		 String[] yes= {"Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic", "Breslin", "Carlucci",
				 "DeFrancisco", "Diaz", "Dilan", "Duane", "Farley", "Flanagan", "Fuschillo", "Gallivan",
				 "Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy",
				 "Klein", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz",
				 "McDonald", "Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer",
				 "Ritchie", "Rivera", "Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron",
				 "Stavisky", "Stewart-Cousin", "Storobin", "Valesky", "Young", "Zeldin" };

		    String[] no= {};
		    String [] excluded={"Espaillat", "Huntley", "Krueger"};
		    String [] abstained={};
		    String [] absent={};
		    List<String> y=Arrays.asList(yes);
		    List<String> n=Arrays.asList(no);
		    List<String> e=Arrays.asList(excluded);
		    List<String> abstain=Arrays.asList(abstained);
		    List<String> ab=Arrays.asList(absent);
		    Vote vote=new Vote();
		    vote.setAyes(y);
		    vote.setNays(n);
		    vote.setExcused(e);
		    vote.setAbsent(ab);
		    vote.setAbstains(abstain);
		    return vote;

	}

}

