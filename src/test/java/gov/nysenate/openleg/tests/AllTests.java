package gov.nysenate.openleg.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AllTests {

	Environment env;
	@Before
	public void prepareEnv() throws IOException
	{

	 env= new Environment("/home/shweta/testing_environment");
	env.reset();


	}

	@Test
	public void testDeleteWhole()
	{
		File sobiDirectory = new File("/home/shweta/OpenLegislation/src/test/resources/sobi");
	    Storage storage = new Storage(env.getStorageDirectory());
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T202049.TXT");
	    TestHelper.processFile(env, initialCommit);
	    File[] finalCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T202549.TXT");
	    TestHelper.processFile(env, finalCommit);
	    Bill theBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
	    assertThat(theBill,nullValue());


	}
	@Test
	public void verifyBill2013S2230() throws IOException
	{
		Bill theBill;
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
	    ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory, "SOBI.D130114.T213149.TXT",
	    "SOBI.D130115.T124554.TXT", "SOBI.D130114.T212649.TXT", "SOBI.D130115.T095048.TXT", "SOBI.D130115.T124054.TXT",
	    "SOBI.D130115.T093046.TXT", "SOBI.D130116.T122729.TXT", "SOBI.D130114.T225652.TXT", "SOBI.D130114.T233653.TXT",
	    "SOBI.D130115.T093547.TXT", "SOBI.D130116.T120230.TXT", "SOBI.D130115.T162604.TXT", "SOBI.D130114.T232653.TXT",
	    "SOBI.D130116.T115728.TXT", "SOBI.D130114.T223651.TXT", "SOBI.D130115.T162104.TXT", "SOBI.D130115.T132056.TXT",
	    "SOBI.D130115.T094047.TXT", "SOBI.D130115.T101048.TXT");


	   // Tests for SOBI.D130114.T213149.TXT
	    TestHelper.processFileC(env ,file.get(0));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    assertEquals(theBill.getSameAs(),"A2388");


		 // Tests for SOBI.D130114.T212649.TXT
		 TestHelper.processFileC(env ,file.get(2));
		 theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		 ArrayList<Person> list=(ArrayList<Person>)theBill.getCoSponsors();
		 assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		 assertEquals(list.get(0).toString(),"SMITH");

	    // Tests for the SOBI.D130114.T223651.TXT
	    TestHelper.processFileC(env ,file.get(14));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    //Should have a title:It is left empty (Memo) Dont Care
	    //assertNull("The title should not be null",theBill.getTitle());

	    // Tests for the SOBI.D130114.T225652.TXT
	    TestHelper.processFileC(env ,file.get(7));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    assertNotNull("The title should not be null",theBill.getTitle());
	    assertEquals(theBill.getSponsor().getFullname(),"KLEIN");



	    // Tests for the SOBI.D130114.T232653.TXT
	    TestHelper.processFileC(env ,file.get(12));

	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    assertNotNull("The title should not be null",theBill.getTitle());
	    assertEquals(theBill.getSponsor().getFullname(),"KLEIN");

	    // Tests for the SOBI.D130114.T233653.TXT
	    TestHelper.processFileC(env ,file.get(8));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    boolean var=voteList2013S2230(theBill);
	    assertEquals(var,true);


	 // Tests for the SOBI.D130115.T93046.TXT
	    TestHelper.processFileC(env ,file.get(5));
	     theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	     assertEquals(theBill.getLawSection().equalsIgnoreCase("Criminal Procedure Law"),true);


	   /*  Nothing to test in SOBI.D130115.T093547.TXT,SOBI.D130115.T095048.TXT,SOBI.D130115.T101048.TXT,
	     SOBI.D130115.T124554.TXT, SOBI.D130115.T132056.TXT,SOBI.D130116.T120230.TXT(Memo), SOBI.D130116.T122729.TXT */

	   //Tests for the SOBI.D130115.T094047.TXT,SOBI.D130115.T124054.TXT,SOBI.D130115.T162104.TXT,SOBI.D130116.T115728.TXT
	      TestHelper.processFileC(env ,file.get(17));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  TestHelper.processFileC(env ,file.get(4));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  TestHelper.processFileC(env ,file.get(15));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  TestHelper.processFileC(env ,file.get(13));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");

    	}

	@Test
	   public void verifyWholeBill2011S1462() throws IOException
	  {
		Bill theBill;
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
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
	    		"SOBI.D110613.T222123.TXT", "SOBI.D110614.T192241.TXT", "SOBI.D110210.T221519.TXT", "SOBI.D120130.T202912.TXT",
	    		"SOBI.D110209.T171647.TXT", "SOBI.D120126.T103841.TXT", "SOBI.D110614.T195743.TXT", "SOBI.D120104.T223233.TXT",
	    		"SOBI.D110215.T151615.TXT", "SOBI.D110209.T105617.TXT", "SOBI.D110209.T110618.TXT", "SOBI.D110614.T152729.TXT",
	    		"SOBI.D110210.T102843.TXT", "SOBI.D110112.T175532.TXT", "SOBI.D110107.T144910.TXT", "SOBI.D110614.T185240.TXT",
	    		"SOBI.D120125.T130337.TXT", "SOBI.D110209.T171147.TXT", "SOBI.D110107.T150914.TXT", "SOBI.D110209.T170214.TXT",
	    		"SOBI.D110107.T141407.TXT");



	      // Test for SOBI.D110209.T105617.TXT
		 TestHelper.processFileC(env ,file.get(10));
		 theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
		 assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
		 assertNotNull("The title should not be null",theBill.getTitle());

		  // Test for SOBI.D110209.T110618.TXT
		  TestHelper.processFileC(env ,file.get(11));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110215.T151615.TXT
	      TestHelper.processFileC(env ,file.get(9));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110613.T222123.TXT
	      TestHelper.processFileC(env ,file.get(1));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110614.T152729.TXT
	      TestHelper.processFileC(env ,file.get(12));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110209.T170214.TXT,
	      TestHelper.processFileC(env ,file.get(20));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());

	      // Test for SOBI.D110209.T171147.TXT
	      TestHelper.processFileC(env ,file.get(18));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSameAs(),"A1415A");

	      // NO test for SOBI.D110210.T102843.TXT ,SOBI.D110210.T221519.TXT (Memo)









	  }


	    public static boolean voteList2013S2230(Bill theBill)
	    {

	    List<String> ayes = theBill.getVotes().get(0).getAyes();
	    List<String> nays = theBill.getVotes().get(0).getNays();
	    List<String> excused = theBill.getVotes().get(0).getExcused();

	    String[] yes= {"Adams", "Addabbo", "Avella", "Boyle", "Breslin", "Carlucci", "Diaz", "Dilan", "Espaillat", "Felder", "Flanagan",
	  	      "Fuschillo", "Gianaris", "Gipson", "Golden", "Grisanti", "Hannon", "Hassell-Thomps", "Hoylman", "Kennedy", "Klein",
	  	      "Krueger", "Lanza", "Latimer", "LaValle", "Marcellino", "Martins", "Montgomery", "O'Brien", "Parker", "Peralta", "Perkins",
	  	      "Rivera", "Sampson", "Sanders", "Savino", "Serrano", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin","Valesky"};
	    String[] no= {"Ball", "Bonacic", "DeFrancisco", "Farley", "Gallivan", "Griffo", "Larkin", "Libous", "Little", "Marchione",
	    		"Maziarz", "Nozzolio", "O'Mara", "Ranzenhofer", "Ritchie", "Robach", "Seward", "Young"};
	    String [] excluded={ "Zeldin"};
	    List<String> y=Arrays.asList(yes);
	    List<String> n=Arrays.asList(no);
	    List<String> e=Arrays.asList(excluded);



	         if(y.equals(ayes) && n.equals(nays) && e.equals(excused))
	       {
	         return true;
	       }

	    return false;


	    }







}

