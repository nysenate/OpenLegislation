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
     public ArrayList<Error> displayReports()
    {    int i=1;
         ArrayList<Error> error=new ArrayList<Error>();
        
         con= DbConnect.connect();
         query="select * from error";
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

}
