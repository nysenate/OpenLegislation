package testing_utils;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class TestUtils {
    public static File openTestResource(String path) throws URISyntaxException {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        return new File(classLoader.getResource(path).toURI());
    }

    public static <T> void basicEqualsTest(@Nonnull T object, @Nonnull T expectedEqual, @Nonnull T expectedNotEqual) {
        assertEquals(object, object);
        assertNotEquals(object, null);
        assertNotEquals(object, new Object());
        assertEquals(object, expectedEqual);
        assertNotEquals(object, expectedNotEqual);
    }
}
