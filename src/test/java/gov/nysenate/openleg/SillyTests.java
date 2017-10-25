package gov.nysenate.openleg;

import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.util.AsyncUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Category(SillyTest.class)
public class SillyTests extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Autowired
    AsyncUtils asyncUtils;

    /** --- Your silly tests go here --- */

    /*
                      __     __,
                      \,`~"~` /
      .-=-.           /    . .\
     / .-. \          {  =    Y}=
    (_/   \ \          \      /
           \ \        _/`'`'`b
            \ `.__.-'`        \-._
             |            '.__ `'-;_
             |            _.' `'-.__)
              \    ;_..--'/     //  \
              |   /  /   |     //    |
              \  \ \__)   \   //    /
               \__)        './/   .'
                             `'-'`
    */

    @Test
    public void testTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> first = asyncUtils.get(() -> {
            logger.info("1 sec..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("1 and done");
            return 1;
        });

        CompletableFuture<Integer> second = asyncUtils.get(() -> {
            logger.info("take 5");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("got 5");
            return 5;
        });


        CompletableFuture.allOf(first, second).get();
        logger.info("got {} {}", first.get(), second.get());
    }
}