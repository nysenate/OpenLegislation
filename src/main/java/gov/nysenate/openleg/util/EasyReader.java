package gov.nysenate.openleg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class EasyReader implements Iterator<String>, Iterable<String> {
    private final Logger logger = Logger.getLogger(EasyReader.class);
    public BufferedReader br = null;
    public final File file;
    public final String charsetName;

    public EasyReader(File file) {
        this(file, Charset.defaultCharset().toString());
    }

    public EasyReader(File file, String charsetName) {
        this.file = file;
        this.charsetName = charsetName;
    }

    public EasyReader open() {
        try {
            this.close();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
        } catch (IOException e) {
            logger.error(e);
        }
        return this;
    }

    public void close() {
        if(isOpen()) {
            try {
                br.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public String readLine() {
        if(isOpen()) {
            try {
                return br.readLine();
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return null;
    }

    public void mark(int readAheadLimit) {
        if(isOpen()) {
            try {
                br.mark(readAheadLimit);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public void reset() {
        if(isOpen()) {
            try {
                br.reset();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public boolean isOpen() {
        try {
            if(br != null && br.ready()) {
                return true;
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return false;
    }

    private String curLine = null;
    private String nextLine = null;

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        curLine = nextLine;
        nextLine = readLine();

        return nextLine != null;
    }

    @Override
    public String next() {
        if(curLine == null) {
            curLine = nextLine = readLine();
        }
        return curLine;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
