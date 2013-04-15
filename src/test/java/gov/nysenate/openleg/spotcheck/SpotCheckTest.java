package gov.nysenate.openleg.spotcheck;


import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.scripts.SpotCheck;
import gov.nysenate.openleg.scripts.SpotCheck.SpotCheckBill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpotCheckTest extends SpotCheckBill
{
    Bill bill;
    static HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
    protected static Storage storage;
    
   
    @ BeforeClass
    public static void checkFiles() throws IOException{
      storage  = new Storage("/home/shweta/test/processed/lbdc_test/json");
       String args[]= { "/home/shweta/test/processed/lbdc_test/files/20130323.assembly.low.html",
               "/home/shweta/test/processed/lbdc_test/files/20130323.assembly.high.html",
               "/home/shweta/test/processed/lbdc_test/files/20130323.senate.low.html",
               "/home/shweta/test/processed/lbdc_test/files/20130323.senate.high.html" };
       
        for (String arg : args) {
            bills.putAll(SpotCheck.readDaybreak(new File(arg)));
        }
        System.out.println("Total number of bills: "+bills.size());
     }
    
    
   @Test
    public  void testBillTitles(){
        for(String id : bills.keySet()) {
            String billNo = id+"-2013";
            bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
            SpotCheckHelper.testBillTitles(bill, bills,id);
            }
       
          
    }
   @Test
    public void checkSummary()
    {
       for(String id : bills.keySet()) {
           String billNo = id+"-2013";
           bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
           SpotCheckHelper.checkSummary(bill, bills,id);
           }
     }
    
    
    @Test
    public void checkSponsors(){
            for(String id : bills.keySet()) {
                String billNo = id+"-2013";
                bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
                SpotCheckHelper.checkSponsors(bill, bills, id);
                }   
    }
      
    
    @Test
    public void checkCoSponsors(){
            for(String id : bills.keySet()) {
                String billNo = id+"-2013";
                bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
                SpotCheckHelper.checkCoSponsors(bill, bills, id);
                }   
    }
    
    @Test
    public void checkEvents(){
            for(String id : bills.keySet()) {
                String billNo = id+"-2013";
                bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
                SpotCheckHelper.checkEvents(bill, bills, id);
                }   
    }
    
    @AfterClass
    public static void errorCount()
    {
        int count[]=SpotCheckHelper.getErrorCount();
        System.out.println("Actual Summary mismatches:Ignoring P/S Encoding :" +count[1]);
        System.out.println("Summary mismatches:Not ignoring P/S Encoding :" +count[0]);
        System.out.println("Total Title mismatches: " +count[3]);
        System.out.println("Total Sponsor Error Count: " +count[2]);
        System.out.println("Total CoSponsor Error Count :" +count[4]);
        System.out.println("Total Event Error Count  :" +count[5]);
        
     }     
  }
        
        
    
    
    
 
