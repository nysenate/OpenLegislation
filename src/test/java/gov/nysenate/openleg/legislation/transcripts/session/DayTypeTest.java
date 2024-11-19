package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DayTypeTest {
    private static final String TEST_DIR = "src/test/resources/transcriptFiles/";

    @Test
    public void test() throws IOException {
        test("forPdfParser/011701.V1", DayType.SESSION);
        test("forPdfParser/101599.v1", DayType.LEGISLATIVE);
        test("t0.txt", null);
        test("t0v1.txt", null);
    }

    private static void test(String filePath, DayType expected) throws IOException {
        String text = Files.readString(Path.of(TEST_DIR, filePath), Charset.forName("CP850"));
        assertEquals(expected, DayType.from(text));
    }
}
