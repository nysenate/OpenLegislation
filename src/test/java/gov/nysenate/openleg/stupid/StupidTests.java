package gov.nysenate.openleg.stupid;

import org.junit.Test;

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
}
