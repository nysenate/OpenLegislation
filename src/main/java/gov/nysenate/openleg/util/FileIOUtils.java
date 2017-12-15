package gov.nysenate.openleg.util;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

public class FileIOUtils
{
    private static final Set<PosixFilePermission> filePermissions = ImmutableSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.OTHERS_READ);

    /**
     * Returns a collection of files sorted by file name (not file path!)
     *
     * @param directory - The directory to get files from.
     * @param recursive - true to retrieve files from sub-directories.
     * @return Collection<File>
     * @throws java.io.IOException
     */
    public static Collection<File> getSortedFiles(File directory, boolean recursive, String[] excludeDirs)
            throws IOException {
        Collection<File> files = safeListFiles(directory, recursive, excludeDirs);
        Collections.sort((List<File>) files, new Comparator<File>() {
            public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        return files;
    }

    /**
     * Any directory that we attempt to list files from should exist. If it doesn't then
     * create them. This makes the processes robust against incomplete environment setups.
     *
     * @param directory - The directory to list files from (and create if necessary)
     * @param extensions - A list of extensions to grab. null for all extensions.
     * @param recursive - true when you want to list files recursively.
     * @return A collection of matching filenames
     * @throws IOException
     */
    public static Collection<File> safeListFiles(File directory, String[] extensions, boolean recursive) throws IOException {
        FileUtils.forceMkdir(directory);
        return FileUtils.listFiles(directory, extensions, recursive);
    }

    /**
     * Any directory that we attempt to list files from should exist. If it doesn't then
     * create them. This overload can be used to exclude certain directories if recursive is true.
     *
     * @param directory - The directory to list files from (and create if necessary)
     * @param recursive - true when you want to list files recursively.
     * @param excludeDirs - Array of directory names that should be excluded from the match.
     *                      Set to null if all directories should be matched.
     * @return A collection of matching filenames
     * @throws IOException
     */
    public static Collection<File> safeListFiles(File directory, boolean recursive, String[] excludeDirs) throws IOException {
        FileUtils.forceMkdir(directory);
        IOFileFilter dirFileFilter;
        if (excludeDirs != null && excludeDirs.length > 0) {
            List<IOFileFilter> excludeDirFilters = new ArrayList<>();
            for (String excludeDir : excludeDirs) {
                excludeDirFilters.add(FileFilterUtils.nameFileFilter(excludeDir));
            }
            dirFileFilter = FileFilterUtils.notFileFilter(new OrFileFilter(excludeDirFilters));
        }
        else {
            dirFileFilter = (recursive) ? TrueFileFilter.TRUE : FalseFileFilter.FALSE;
        }
        return FileUtils.listFiles(directory, TrueFileFilter.TRUE, dirFileFilter);
    }

    /**
     * Create the specified folder if necessary and return a File handle to it.
     *
     * @param parent - The parent directory for this folder
     * @param folderName - The name of the directory to retrieve
     * @return a File handle to the requested folder.
     * @throws IOException
     */
    public static File safeGetFolder(File parent, String folderName) throws IOException {
        File directory = new File(parent, folderName);
        FileUtils.forceMkdir(directory);
        return directory;
    }

    /**
     * Moves the file into the destination quietly.
     */
    public static void moveFile(File sourceFile, File destFile) throws IOException {
        if (destFile.exists()) {
            FileUtils.deleteQuietly(destFile);
        }
        FileUtils.moveFile(sourceFile, destFile);
    }

    /**
     * Moves a file, deleting the destination file if it already exists.
     *
     * @param file File
     * @param directory File
     * @param createDirectory ?
     * @throws IOException
     */
    public static void moveFileToDirectory(File file, File directory, boolean createDirectory) throws IOException {
        File newFile = new File(directory, file.getName());
        if (newFile.exists()) {
            newFile.delete();
        }
        FileUtils.moveFileToDirectory(file, directory, true);
    }

    /**
     * Writes out an InputStream to the destination file path specified.
     *
     * @param stuffToWrite InputStream
     * @param destinationPath String
     * @throws IOException
     */
    public static void writeToFile(InputStream stuffToWrite, String destinationPath) throws IOException {
        OutputStream os = new FileOutputStream(destinationPath);
        byte[] b = new byte[2048];
        int length;
        while ((length = stuffToWrite.read(b)) != -1) {
            os.write(b, 0, length);
        }
        stuffToWrite.close();
        os.close();
    }

    public static boolean isFileClosed(File file) throws IOException {
        Process plsof = null;
        BufferedReader reader = null;
        try {
            plsof = new ProcessBuilder(new String[]{"lsof", "|", "grep", file.getAbsolutePath()}).start();
            reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
            String line;
            while((line=reader.readLine())!=null) {
                if(line.contains(file.getAbsolutePath())) {
                    reader.close();
                    plsof.destroy();
                    return false;
                }
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {}
            Optional.ofNullable(plsof).ifPresent(Process::destroy);
        }
        return true;
    }

    /**
     * Saves a character sequence to a file and sets common permissions to the file.
     * See {@code FileUtils.write} for details on file saving.
     * Permissions set are owner read + write, group read, and others read.
     * @param file The file to save to.
     * @param data The data to save to the file.
     * @throws IOException
     */
    public static void write(File file, CharSequence data) throws IOException {
        FileUtils.write(file, data);
        setCommonFilePermissions(file);
    }

    /**
     * Saves a character sequence to a file and sets common permissions to the file.
     * See {@code FileUtils.write} for details on file saving.
     * Permissions set are owner read + write, group read, and others read.
     * @param file The file to save to.
     * @param data The data to save to the file.
     * @param encoding The encoding to use.
     * @throws IOException
     */
    public static void write(File file, CharSequence data, Charset encoding) throws IOException {
        FileUtils.write(file, data, encoding);
        setCommonFilePermissions(file);
    }

    /**
     * Saves an input stream to a file and sets common permissions to the file.
     * Wraps the FileUtils.copyInputStreamToFile method.
     * @param stream
     * @param file
     * @throws IOException
     */
    public static void copyInputStreamToFile(InputStream stream, File file) throws IOException {
        FileUtils.copyInputStreamToFile(stream, file);
        setCommonFilePermissions(file);
    }

    private static void setCommonFilePermissions(File file) throws IOException {
        Files.setPosixFilePermissions(Paths.get(file.getAbsolutePath()), filePermissions);
    }
}
