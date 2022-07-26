package gov.nysenate.openleg.config.annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class CreateTestFiles {
    private CreateTestFiles() {}

    private static final Pattern INTEGRATION_PATTERN = Pattern.compile("@Autowired|extends BaseTest");
    private static final Pattern UNIT_PATTERN = Pattern.compile("assert");
    private static final String ANNOTATION_PACKAGE = "gov.nysenate.openleg.config.annotation";
    
    private static int count = 0;
    
    /**
     * The main program that calls the recursion
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) throws IOException {
        String testDirName = "src/test/java/gov/nysenate/openleg";
        File testDir = new File(testDirName);
        recursion(testDir);
        System.out.println(count);
    }
    
    /**
     * Goes through the package "dir" and lists all files
     * If the file is a Java file and not a test file,
     * it sees if it contains an @Test, if it does, it
     * creates a tester file for it
     *
     * @param dir the package
     */
    private static void recursion(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("Error! " + dir + " isn't a directory.");
        }
        Arrays.sort(files, Collections.reverseOrder());
        
        for (File file : files){
            if (file.isDirectory()) {
                recursion(file);
            }
            else {
                String name = file.getName();
                
                if (name.endsWith("java")){
                    name = name.substring(0, name.indexOf(".java"));
                    
                    if (!name.endsWith("Test") && !name.endsWith("IT")){
                        try (Scanner scanner = new Scanner(file).useDelimiter("\\Z")){
                            String contents = scanner.next();
                            if (contents.contains("@Test")){
                                count++;
                                makeFile(dir, name, contents);
                            }
                        }
                        catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * It determines the file suffix (Test or IT) based on the contents
     * It then creates a outline for the file and prints it out.
     *
     * @param dir      the package
     * @param name     the filename
     * @param contents the contents of the file
     */
    private static void makeFile(File dir, String name, String contents){
        String dirAbsolute = dir.getAbsolutePath();
        String pack = dirAbsolute.substring(dirAbsolute.indexOf("gov")).replace("/", ".");
        
        CategoryTypes category = CategoryTypes.SillyTest;
        
        // Integration Test
        if (INTEGRATION_PATTERN.matcher(contents).find()){
            category = CategoryTypes.IntegrationTest;
        }
        
        // Unit Test
        else if (UNIT_PATTERN.matcher(contents).find()){
            category = CategoryTypes.UnitTest;
        }

        String sb = "package " + pack + ";\n" +
                "import " + ANNOTATION_PACKAGE + '.' + category + ";\n" +
                "import org.junit.Assert;\n" +
                "import org.junit.Test;\n" +
                "import org.junit.experimental.categories.Category;\n" +
                "\n" +
                "@Category(" + category + ".class)\n" +
                "public class " + name + category.getSuffix() + " {}\n";
        System.out.println(sb);
    }
}
