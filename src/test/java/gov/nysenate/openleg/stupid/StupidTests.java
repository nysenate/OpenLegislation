package gov.nysenate.openleg.stupid;

import org.junit.Test;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.Timestamp;
import java.util.Optional;


public class StupidTests
{
    @Test
    public void stupid(){
        String a[] = {"0000001", "", null};
        for(String s : a){
            System.out.println(s.isEmpty());
        }
    }

    private class A{}
    private class B extends A{}
    private void justAFunc(A a){
        System.out.println("Function received an A");
    }
    private void justAFunc(B b){
        System.out.println("Function received a B");
    }
    @Test
    public void subclassOverload(){
        A a=null;
        B b=null;
        justAFunc(a);
        justAFunc(b);
    }

    @Test
    public void allCapsCheckTest(){
        String text = "REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS1234329038789)@#$&#$@).<>";
        String text2 = "assembly action 1234329038789)@#$&#$@).<>";
        assert(StringUtils.isAllUpperCase(text.replaceAll("[^a-zA-Z]+", "")));
        assert(!StringUtils.isAllUpperCase(text2.replaceAll("[^a-zA-Z]+", "")));
    }

    @Test
    public void pLopezTest(){
        String regex = "([A-Z])\\. ([A-Za-z\\-' ]*)";
        System.out.println(" P. Lopez".trim().replaceAll(regex, "$2 $1"));
    }

    @Test
    public void StringReplaceTest() {
        String str = "--- %1$d - %1$d ---";
        System.out.println(String.format(str, new Integer("1234")));
    }

    private enum DumbEnum {
        HERP,
        DERP
    }
    @Test
    public void enumParseTest() {
        assert(DumbEnum.valueOf("DERP") == DumbEnum.DERP);
        System.out.println(DumbEnum.valueOf("GLERP"));
    }

    @Test
    public void regexSplitTest() {
        String[] strings = str.split("(?<=00000.SO DOC VETO\\d{4}        \\*END\\*.{45})\n");
        for (String string : strings) {
            System.out.println(string);
            System.out.println("jalalalalalalal");
        }
        System.out.println(strings.length);
    }

    @Test
    public void optionalTest() {
        Optional<String> opt = Optional.of(null);
        opt.get().toUpperCase();
    }

    private static String str = "00000.SO DOC VETO0002                                 VETO                 2014\n" +
            "00001\n" +
            "00002                  STATE OF NEW YORK--EXECUTIVE CHAMBER\n" +
            "00003\n" +
            "00004TO THE ASSEMBLY:                                        April 11, 2014\n" +
            "00005\n" +
            "00006     I  hereby transmit pursuant to the provisions of section 7 of Arti-\n" +
            "00007cle IV and section 4 of Article VII of the Constitution, a statement  of\n" +
            "00008items  to which I object and which I do not approve, contained in Assem-\n" +
            "00009bly Bill Number 8550--E, entitled:\n" +
            "00010\n" +
            "00011CHAPTER 50\n" +
            "00012\n" +
            "00013LINE VETO #2\n" +
            "00014\n" +
            "00015       \"AN ACT making appropriations for the support of government\n" +
            "00016\n" +
            "00017                          STATE OPERATIONS BUDGET\"\n" +
            "00018\n" +
            "00019Bill Page 85, Line 6, inclusive\n" +
            "00020\n" +
            "00021NOT APPROVED\n" +
            "00022____________\n" +
            "00023\n" +
            "00024           DEPARTMENT OF CORRECTIONS AND COMMUNITY SUPERVISION\n" +
            "00025\n" +
            "00026 \"Personal service ... 2,000,000 ........................ (re. $263,000)\"\n" +
            "00027\n" +
            "00028     This item passed by the Legislature, to which I object and  do  not\n" +
            "00029approve,  is  not needed because adequate funding for State agency oper-\n" +
            "00030ations is already provided for in the budget. Accordingly, this item  is\n" +
            "00031disapproved.\n" +
            "00032\n" +
            "00033                                              (signed) ANDREW M. CUOMO\n" +
            "00000.SO DOC VETO0002        *END*    A8550           VETO                 2014\n" +
            "00000.SO DOC VETO0001                                 VETO                 2014\n" +
            "00001\n" +
            "00002                  STATE OF NEW YORK--EXECUTIVE CHAMBER\n" +
            "00003\n" +
            "00004TO THE ASSEMBLY:                                        April 11, 2014\n" +
            "00005\n" +
            "00006     I  hereby transmit pursuant to the provisions of section 7 of Arti-\n" +
            "00007cle IV and section 4 of Article VII of the Constitution, a statement  of\n" +
            "00008items  to which I object and which I do not approve, contained in Assem-\n" +
            "00009bly Bill Number 8550--E, entitled:\n" +
            "00010\n" +
            "00011CHAPTER 50\n" +
            "00012\n" +
            "00013LINE VETO #1\n" +
            "00014\n" +
            "00015       \"AN ACT making appropriations for the support of government\n" +
            "00016\n" +
            "00017                          STATE OPERATIONS BUDGET\"\n" +
            "00018\n" +
            "00019Bill Page 36, Line 39 through Line 41, inclusive\n" +
            "00020\n" +
            "00021NOT APPROVED\n" +
            "00022____________\n" +
            "00023\n" +
            "00024                           COUNCIL ON THE ARTS\n" +
            "00025\n" +
            "00026 \"For  administration of programs funded from the national endowment for\n" +
            "00027    the arts federal grant award.\n" +
            "00028  Nonpersonal service ... 100,000 ....................... (re. $100,000)\"\n" +
            "00029\n" +
            "00030     This item passed by the Legislature, to which I object and  do  not\n" +
            "00031approve,  is  not needed because adequate funding for State agency oper-\n" +
            "00032ations is already provided for in the budget. Accordingly, this item  is\n" +
            "00033disapproved.\n" +
            "00034\n" +
            "00035                                              (signed) ANDREW M. CUOMO\n" +
            "00000.SO DOC VETO0001        *END*    A8550           VETO                 2014";
}
