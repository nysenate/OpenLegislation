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
}
