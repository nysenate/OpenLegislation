package gov.nysenate.openleg.common.script;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.dao.SqlMemberDao;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.ProcessDataCLI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.*;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.monitor.os.OsStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import java.io.*;
import java.sql.Array;
import java.time.MonthDay;
import java.time.Year;
import java.util.*;

@Component
public class MemberUpdateScript extends BaseScript{
    @Autowired SqlMemberDao sqlMemberDao;

    @Override
    protected void execute(CommandLine opts) throws Exception {
        String filename = "";
        if(opts.hasOption("processcsv")) {
        filename = opts.getOptionValue("processcsv");
        }
        readcsv(filename);

    }

    public static void main(String[] args) throws Exception{

        SCRIPT_NAME = MemberUpdateScript.class.getName();
        AnnotationConfigApplicationContext ctx = init();
        MemberUpdateScript memberUpdateScript = ctx.getBean(MemberUpdateScript.class);
        CommandLine cmd = getCommandLine(memberUpdateScript.getOptions(), args);
        memberUpdateScript.execute(cmd);
        shutdown(ctx);
    }



    @Override
    protected Options getOptions() {
        Options options = new Options();
        options.addOption(null, "processcsv", true, "Process specified csv file");
        return options;
    }

    public void readcsv(String filename)throws Exception{
        String[] HEADERS = { "chamber", "district_code" , "person_id" , "first_name" ,
                "middle_name" , "last_name" , "suffix" , "shortname" , "isvacant"};

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();


        Reader file = new FileReader(filename);
        File newFile = new File("/home/nystech");
        ArrayList<MemberEntry> existingMembers = new ArrayList<>();
        ArrayList<MemberEntry> senateEntry = new ArrayList<>();
        ArrayList<MemberEntry> assemblyEntry = new ArrayList<>();
        ArrayList<MemberEntry> vacantEntry = new ArrayList<>();
        Iterable<CSVRecord> records = csvFormat.parse(file);
        for(CSVRecord record: records){

            String chamber = record.get("chamber");
            String districtcode = record.get("district_code");
            String personid = record.get("person_id");
            String firstname = record.get("first_name");
            String middlename = record.get("middle_name");
            String lastname = record.get("last_name");
            String suffix = record.get("suffix");
            String shortname = record.get("shortname");
            String isvacant = record.get("isvacant");
            MemberEntry currentEntry = new MemberEntry(chamber, districtcode, personid, firstname, middlename,
                    lastname, suffix, shortname, isvacant);
            if(!currentEntry.getPersonID().equals("")){
                existingMembers.add(currentEntry);
            }
            else if(currentEntry.getChamber().equalsIgnoreCase("assembly")
                    &&!currentEntry.isVacant.equalsIgnoreCase("true")){
                assemblyEntry.add(currentEntry);
            }
            else if(currentEntry.getChamber().equalsIgnoreCase("senate")
                    &&!currentEntry.isVacant.equalsIgnoreCase("true")){
                senateEntry.add(currentEntry);
            }
            else if(currentEntry.isVacant.equalsIgnoreCase("true")){
                vacantEntry.add(currentEntry);
            }
        }
        ArrayList<MemberEntry> currentMembers = getCurrentMembers(existingMembers);
        removeVacant(vacantEntry,currentMembers);

        String output = GenerateSQL(currentMembers, assemblyEntry, senateEntry, existingMembers);
        System.out.println(output);
        createSQLFile(output);
    }

    //Does QA on data entry to make sure there are no entries in which two entries
    // have the same district code and the same chamber
    public boolean checkDistrictCodeChamber(ArrayList<MemberEntry>csvData){
        HashSet<String>duplicates=new HashSet<String>();
    for(int i=0; i<csvData.size(); i++){
    csvData.get(i).getDistrictCode();
    if(!duplicates.contains(csvData.get(i).getDistrictCode())){
        duplicates.add(csvData.get(i).getDistrictCode());
        }
    else {
        for(int j=0; j<csvData.size(); j++){
            if(csvData.get(j).getDistrictCode()==csvData.get(i).getDistrictCode()){
                if(csvData.get(j).getChamber()==csvData.get(i).getChamber()){
                    System.out.print("Entry: " + csvData.get(j).toString() + "\n" +
                            "Shares the same district code and chamber with \nEntry: " + csvData.get(i).toString());
                    return false;
                    }
                }
            }
            return false;
        }
    }

    return true;

    }

    public void removeVacant(ArrayList<MemberEntry>vacantEntry, ArrayList<MemberEntry>currentMembers){
        Iterator<MemberEntry> i = currentMembers.iterator();
        while(i.hasNext()){
            MemberEntry k = i.next();
            for(MemberEntry j: vacantEntry) {
                if (k.getDistrictCode().equals(j.getDistrictCode())&&k.getChamber().equalsIgnoreCase(j.getChamber())){
                    i.remove();
                }
            }
        }
    }

    public void createSQLFile(String fileData) throws IOException {
        int year = Year.now().getValue();
        
        FileOutputStream fos = new FileOutputStream("/home/nystech/"+year+"_members.sql");
        fos.write(fileData.getBytes());
        fos.flush();
        fos.close();

    }

    public ArrayList<MemberEntry> getCurrentMembers(ArrayList<MemberEntry>ExistingMembers){
        List<SessionMember> MemberList;
        MemberList = sqlMemberDao.getAllSessionMembers(SortOrder.ASC, LimitOffset.ALL);
        //What do the two parameters mean? sortOrder and limOff
        int year= Year.now().getValue()-1;
        ArrayList<MemberEntry> MemberOutput = new ArrayList<MemberEntry>();
        for(SessionMember i: MemberList){ //Filters list for current year Session Members
            boolean flag = false;
            for(MemberEntry j: ExistingMembers) {
                if(i.getMember().getPerson().personId().equals(Integer.parseInt(j.getPersonID()))){
                    flag = true;
                }
                else if(i.getDistrictCode().equals(Integer.parseInt(j.getDistrictCode())) &&
                        i.getMember().getChamber().toString().equalsIgnoreCase(j.getChamber())){
                    flag = true;
                }
            }
            if(i.getSessionYear().year()==year && !flag){
            MemberOutput.add(MemberEntry.SessionMemberToMemberEntry(i));
            }
        }

        return MemberOutput;
    }



    public boolean CheckData(ArrayList<MemberEntry>csvData){
        if(checkDistrictCodeChamber(csvData)){
            return true;
        }
        else{
            return false;
        }
    }


    public String memberListToSQLValues(ArrayList<MemberEntry>MemberList){
        int year= Year.now().getValue();
        String output="";
        for(MemberEntry i: MemberList){
            output+= i.printValueForSessionMemberSQL();
        }
        return output;
    }

    public String updateNewMembers(ArrayList<MemberEntry>MemberList){
        String output = "";
        for(MemberEntry i: MemberList){
            output+=i.newSessionMemberForSQL();
        }
        return output;
    }


    public String GenerateSQL(ArrayList<MemberEntry>MemberList, ArrayList<MemberEntry>newAssembly,
                                           ArrayList<MemberEntry>newSenate, ArrayList<MemberEntry>existingMember){
        ArrayList<MemberEntry>AssemblyMember = new ArrayList<>();
        ArrayList<MemberEntry>SenateMember = new ArrayList<>();
        for(MemberEntry i:MemberList){
            if(i.getChamber().equalsIgnoreCase("senate")){
                SenateMember.add(i);
            }
            else if(i.getChamber().equalsIgnoreCase("assembly")){
                AssemblyMember.add(i);
            }
        }
        String output = "";

        //Update Existing Assembly Members
        output+= "-- Assembly session_members\n\n";
        output += "INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)\n" +
                "VALUES\n";
        output+=memberListToSQLValues(AssemblyMember);
        output=StringUtils.chop(output);
        output=StringUtils.chop(output);
        output+="\nON CONFLICT DO NOTHING;\n\n";



        //Add New Assembly Members
        output+= "-- Add other new members\n\n";
        output+= updateNewMembers(newAssembly);



        //Update Existing Senate Members
        output+= "\n\n-- Senate session_members\n\n";
        output += "INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)\n" +
                "VALUES\n";
        output+=memberListToSQLValues(SenateMember);
        output=StringUtils.chop(output);
        output=StringUtils.chop(output);
        output+="\nON CONFLICT DO NOTHING;\n\n";


        //Add New Senate Members
        output+= "-- Add other new members\n\n";
        output+= updateNewMembers(newSenate);

        //Add Members that already have a person and member entry
        output+= "-- New members who are already in the person and member tables.\n\n";
        output+= "INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)\n" +
                "VALUES\n";
        output+= memberListToSQLValues(existingMember);
        output=StringUtils.chop(output);
        output=StringUtils.chop(output);
        output+="\nON CONFLICT DO NOTHING;\n\n";

        return output;
    }

}
