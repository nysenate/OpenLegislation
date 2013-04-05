package gov.nysenate.openleg.spotcheck;

import static org.junit.Assert.*;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.scripts.SpotCheck;
import gov.nysenate.openleg.scripts.SpotCheck.SpotCheckBill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class SpotCheckTest extends SpotCheckBill
{
    Bill bill;
    HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
   
    @ Test
    public void checkFiles() throws IOException{
        Storage storage = new Storage("/home/shweta/test/processed/lbdc_test/json");
       String args[]= { "/home/shweta/test/processed/lbdc_test/files/20130323.assembly.low.html",
               "/home/shweta/test/processed/lbdc_test/files/20130323.assembly.high.html",
               "/home/shweta/test/processed/lbdc_test/files/20130323.senate.low.html",
               "/home/shweta/test/processed/lbdc_test/files/20130323.senate.high.html" };
       
        for (String arg : args) {
            bills.putAll(SpotCheck.readDaybreak(new File(arg)));
        }
        System.out.println(bills.size());
        for(String id : bills.keySet()) {
        String billNo = id+"-2013";
         bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
         testBillTitles(bill, bills,id);
         
       }
    }
    
   
    public  void testBillTitles(Bill bill,HashMap<String, SpotCheckBill> bills,String id){
     // Compare the titles, ignore white space differences
        String jsonTitle = SpotCheck.unescapeHTML(bill.getTitle());
        String lbdcTitle = bills.get(id).getTitle();
         boolean check=SpotCheck.stringEquals(jsonTitle, lbdcTitle, true, true) ;
         if(check== false)
         {
            System.out.println(bill.getSenateBillNo()+" :Title mismatch") ;  
         }
            assertEquals(check,true);
       }
        
        
    
    
    
} 
