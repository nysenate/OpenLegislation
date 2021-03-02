package testing_utils;

import java.io.File;
import java.net.URISyntaxException;

public class TestUtils {

    public static File openTestResource(String path) throws URISyntaxException {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        return new File(classLoader.getResource(path).toURI());
    }
}
