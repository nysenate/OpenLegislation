package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Random;

import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class FileIOUtilsTest {

    @Test (expected = NullPointerException.class)
    public void throwsExceptionIfFileIsNull() throws IOException {
        FileIOUtils.gzipFile(null);
    }

    @Test
    public void gzippedFileShouldBeSmallerThanOriginal() throws IOException {
        File originalFile = createTempFile("temp", ".tmp", 1024);
        File gzipFile = FileIOUtils.gzipFile(originalFile);
        assertTrue(fileSize(gzipFile) < fileSize(originalFile));
    }

    @Test
    public void gzippedFileShouldHave_gz_FileExtension() throws IOException {
        File originalFile = createTempFile("temp", ".tmp", 1024);
        File gzipFile = FileIOUtils.gzipFile(originalFile);
        assertTrue(gzipFile.getName().endsWith(".tmp.gz"));
    }

    @Test
    public void emptyFileCanBeGzipped() throws IOException {
        File originalFile = createTempFile("temp", ".tmp", 0);
        File gzipFile = FileIOUtils.gzipFile(originalFile);
        assertTrue(gzipFile.exists());
    }

    // Return the file size in bytes.
    private long fileSize(File file) throws IOException {
        return Files.size(file.toPath());
    }

    private String randomString(int stringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(stringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private File createTempFile(String prefix, String suffix, int length) throws IOException {
        File file = File.createTempFile(prefix, suffix);
        String string = randomString(length);
        FileUtils.write(file, string, Charset.defaultCharset());
        return file;
    }
}
