package gov.nysenate.openleg.common.script;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.SessionMember;

import java.time.Year;

public class MemberEntry{
    String chamber;
    String districtCode;
    String personID;
    String firstName;
    String middleName;
    String lastName;
    String suffix;
    String shortname;
    String isVacant;

    public MemberEntry(String chamber, String districtCode, String personID, String firstName,
                       String middleName, String lastName, String suffix, String shortname, String isVacant){
        this.chamber=chamber;
        this.districtCode=districtCode;
        this.personID=personID;
        this.firstName=firstName;
        this.middleName=middleName;
        this.lastName=lastName;
        this.suffix=suffix;
        this.shortname=shortname;
        this.isVacant=isVacant;
    }


    public String getDistrictCode(){
        return districtCode;
    }

    public String getChamber(){
        return chamber;
    }

    public String getPersonID(){return personID;}

    public String getShortname(){return shortname;}

    public String getPersonIDForSQL(){return (personID + ", ");}

    public String getShortnameForSQL(){
        if (shortname.contains("'")){
            String tempShortname = "";
            for(int i=0; i<shortname.length()-1; i++){
                if(shortname.substring(i,i+1).equals("'")){
                    tempShortname += "''";
                }
                else {
                    tempShortname += shortname.substring(i, i + 1);
                }
            }
            return ("'"+tempShortname+"'"+", ");
        }
        return ("'"+shortname+"'"+", ");
    }

    public String getDistrictCodeForSQL(){return (", " + districtCode);}

    public String getFullNameForSQL(){return ("'" + firstName + " " + lastName + "', ");}

    public String getFirstNameForSQL(){return ("'" + firstName + "', ");}

    public String getMiddleNameForSQL() {return ("' " + middleName + "', ");}

    public String getLastNameForSQL(){return ("'" + lastName + "', ");}

    public String getSuffixForSQL(){return ("'" + suffix + "', ");}

    public String getPrefixForSQL(){
        String chamberForPrefix;
        if(chamber.equalsIgnoreCase("assembly")){
            chamberForPrefix = "Assembly";
        }
        else if(chamber.equalsIgnoreCase("senate")){
            chamberForPrefix = "Senate";
        }
        else{
            chamberForPrefix = "";
        }
        return ("'" + chamberForPrefix + " Member', ");
    }

    public String printValueForSessionMemberSQL(){
        int year= Year.now().getValue();
        return "("+getPersonIDForSQL()+getShortnameForSQL()+year+getDistrictCodeForSQL()+"),\n";
    }

    public String printValueForMemberSQL(){
        return " '"+chamber+"', true, " + "'" + firstName +" " + lastName + "')";
    }

    public String printValueForPersonSQL(){
        return "("+getFullNameForSQL()+getFirstNameForSQL()+getMiddleNameForSQL()+getLastNameForSQL()+
                "'', " + getPrefixForSQL() + getSuffixForSQL() + "'no_image.jpg')";
    }

    public String newSessionMemberForSQL(){
        int year = Year.now().getValue();
        String output = "";
        output += "WITH p AS (\n" +
                "  INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, " +
                "suffix, img_name)\n" + "VALUES " + printValueForPersonSQL() +"\n" + "RETURNING id\n),\n"+
                "m AS (\n" +
                "  INSERT INTO public.member(person_id, chamber, incumbent, full_name)\n" +
                "    VALUES ((SELECT id from p), "+ printValueForMemberSQL() +")\n" +
                "    RETURNING id\n" +
                ")\n\n" +
                "INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)\n" +
                "  VALUES ((SELECT id from m), "+ getShortnameForSQL() + year  + getDistrictCodeForSQL()+ ");\n\n";
        return output;
    }


    public static MemberEntry SessionMemberToMemberEntry(SessionMember input){
        String vacancy;
        if(input.getMember().isIncumbent()){
            vacancy="false";
        }
        else{
            vacancy="true";
        }

        return  new MemberEntry(input.getMember().getChamber().toString(), input.getDistrictCode().toString(),
                input.getMember().getPerson().personId().toString(), input.getMember().getPerson().name().firstName(),
                input.getMember().getPerson().name().middleName(), input.getMember().getPerson().name().lastName(),
                input.getMember().getPerson().name().suffix(), input.getLbdcShortName(), vacancy);

    }

    @Override
    public String toString(){
        return chamber + " " + districtCode + " " + personID + " " + firstName + " " +
                middleName + " " + lastName + " " + suffix + " " + shortname + " " + isVacant;
    }
}
