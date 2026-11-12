package gov.nysenate.openleg;

import gov.nysenate.openleg.config.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Surefire runs classes named *Test or *Tests as unit tests, and failsafe runs classes named *IT
 * as integration tests. A test class named any other way never runs, and one that needs the
 * database but isn't named *IT fails in a plain unit test run. This catches both.
 */
public class TestClassNamesTest {

    @Test
    public void testClassesAreNamedForThePluginThatRunsThem() throws Exception {
        Path root = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        List<String> problems = new ArrayList<>();
        int checked = 0;
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : files.filter(TestClassNamesTest::isTopLevelClassFile).toList()) {
                String name = root.relativize(file).toString()
                        .replaceAll("\\.class$", "").replace(file.getFileSystem().getSeparator(), ".");
                Class<?> cls = Class.forName(name, false, getClass().getClassLoader());
                checked++;
                problem(cls).ifPresent(problems::add);
            }
        }
        assertTrue("Found no test classes under " + root, checked > 0);
        assertTrue("Misnamed test classes:\n" + String.join("\n", problems), problems.isEmpty());
    }

    private static boolean isTopLevelClassFile(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.endsWith(".class") && !fileName.contains("$");
    }

    private static Optional<String> problem(Class<?> cls) {
        if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()) || isSilly(cls)) {
            return Optional.empty();
        }
        String name = cls.getSimpleName();
        boolean integrationName = name.endsWith("IT");
        boolean unitName = name.endsWith("Test") || name.endsWith("Tests");
        if (BaseTests.class.isAssignableFrom(cls) && !integrationName) {
            return Optional.of(cls.getName() + " extends BaseTests, so it needs the database: name it *IT");
        }
        boolean hasTests = Arrays.stream(cls.getMethods()).anyMatch(m -> m.isAnnotationPresent(Test.class));
        if (hasTests && !integrationName && !unitName) {
            return Optional.of(cls.getName() + " has @Test methods but no plugin runs it: name it *Test or *IT");
        }
        return Optional.empty();
    }

    /** SillyTest classes are excluded from both plugins, whatever their names. */
    private static boolean isSilly(Class<?> cls) {
        Category category = cls.getAnnotation(Category.class);
        return category != null && Arrays.asList(category.value()).contains(SillyTest.class);
    }
}
