package gov.nysenate.openleg.stupid;

import org.junit.Test;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by kinbote on 7/3/14.
 */
public class StupidTests {
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
}
