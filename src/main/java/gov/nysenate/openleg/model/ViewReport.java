package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.DbConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.sql.PreparedStatement;

public class ViewReport
{
    Connection con=null;
    String query;
    PreparedStatement psmt;
     public ArrayList<Error> displayReports(int id)
    {   
         ArrayList<Error> error=new ArrayList<Error>();
        
         con= DbConnect.connect();
         query="select * from error where report_id="+id+" order by bill_id";
         try {
           
            psmt=con.prepareStatement(query);
            ResultSet rs=psmt.executeQuery();
           
            while(rs.next())
            {
                Error object=new Error();
               
                object.setErrorId(rs.getInt(1));
                object.setReportId(rs.getInt(2));
                object.setBillId(rs.getString(3));
                object.setErrorType(rs.getString(4));
                object.setLbdc(rs.getString(5));
                object.setJson(rs.getString(6));
                error.add(object);
                
            }
            
        }
        catch (SQLException e) {
            
            System.out.println(e.getMessage());
        }
        return error;
         
         
         
    }
     
     public ArrayList<Report> displayReportOption()
     {   
          ArrayList<Report> report=new ArrayList<Report>();
         
          con= DbConnect.connect();
          query="select * from report";
          try {
            
             psmt=con.prepareStatement(query);
             ResultSet rs=psmt.executeQuery();
            
             while(rs.next())
             {
                 Report object=new Report();
                
                 object.setReportId(rs.getInt(1));
                 object.setDate(rs.getDate(2));
                 report.add(object);
                 
             }
             
         }
         catch (SQLException e) {
             
             System.out.println(e.getMessage());
         }
         return report;
          
          
          
     }

}
