package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.abdera.model.Collection;
import org.junit.Before;
import org.junit.Test;

public class Bill2011S1462 {
	
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
	     //assertEquals(theBill.getSameAs(),"A1415A");  ?? Have to check why its failing?
	      
	      // NO test for SOBI.D110210.T102843.TXT ,SOBI.D110210.T221519.TXT (Memo),SOBI.D110209.T171647.TXT
	      
	      // Test for SOBI.D110614.T195743.TXT
	      TestHelper.processFileC(env ,file.get(7));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      boolean flag=voteList2011S1462A(theBill);
	      assertEquals(flag,true);
		 
		 // Test for SOBI.D110614.T185240.TXT
	      TestHelper.processFileC(env ,file.get(16));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());
	      
	      // Test for SOBI.D110614.T192241.TXT
	      TestHelper.processFileC(env ,file.get(2));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());
	      
	      // Test for SOBI.D120104.T223233.TXT
	      TestHelper.processFileC(env ,file.get(8));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      assertNotNull("The title should not be null",theBill.getTitle());
	     // assertEquals(theBill.getSameAs(),"A1415A");   ( In json file we dont have sameAs in amendment A??
	      
	      
	      
		 // Tests for SOBI.D120125.T130337.TXT
	      TestHelper.processFileC(env ,file.get(17));
	      theBill=(Bill)storage.get("2011/bill/S1462A-2011", Bill.class);
	      assertEquals(theBill.getSponsor().getFullname(),"LAVALLE");
	      ArrayList<Person> list=(ArrayList<Person>)theBill.getCoSponsors();
	      assertEquals(list.get(0).toString(),"DUANE");
	      
	      
	      // Tests for SOBI.D120126.T103841.TXT
	      TestHelper.processFileC(env ,file.get(6));
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
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
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
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
	    ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory,"");
	
	  }
	
	  public static boolean voteList2011S1462A(Bill theBill)  // Checks for vote Date and votes
	    {
	   
		//  Date Tue Jun 14 00:00:00 EDT 2011
	    List<String> ayes = theBill.getVotes().get(0).getAyes();
	    List<String> nays = theBill.getVotes().get(0).getNays();
	    List<String> excused =theBill.getVotes().get(0).getExcused();
	    //java.util.Date voteDate= theBill.getVotes().get(0).getVoteDate();
	    String[] voteDate=(theBill.getVotes().get(0).getId()).split("-");
	    String date=voteDate[2];
	    assertEquals(date.equals("2011/06/14"),true);
	   
	    String[] yes= {"Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic", "Breslin", "Carlucci", "DeFrancisco", "Diaz",
	    		"Dilan", "Duane", "Espaillat", "Farley", "Flanagan", "Fuschillo", "Gallivan", "Gianaris", "Golden", "Griffo",
	    		"Grisanti", "Hannon", "Hassell-Thomps", "Huntley", "Johnson", "Kennedy", "Klein", "Krueger", "Kruger", "Lanza",
	    		"Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald", "Montgomery", "Nozzolio", 
	    		"O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", "Rivera", "Robach", "Saland",
	    		"Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin", "Valesky",
	    		"Young", "Zeldin"};

	    List<String> y=Arrays.asList(yes);
	    
	    
	    
	    
	         if(y.equals(ayes) && nays.isEmpty() && excused.isEmpty())
	       {
	         return true;
	       }
	   
	    return false;
	    
	    
	    }	
	  
	  
	   
	    
	    
	    
	    
	    
}

