package gov.nysenate.openleg.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class EasyWriter {
    private final Logger logger = Logger.getLogger(EasyWriter.class);
    public BufferedWriter bw = null;
    public File file;

    public EasyWriter(File file) {
        this.file = file;
    }

    public EasyWriter open() {
        if(!this.isOpen()) {
            try {
                bw = new BufferedWriter(new FileWriter(file, true));
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return this;
    }

    public EasyWriter close() {
        if(isOpen()) {
            try {
                bw.close();
                bw = null;
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return this;
    }

    public EasyWriter writeLine(String data) {
        return write(TextFormatter.append(data, "\n"));
    }

    public EasyWriter write(String data) {
        if(isOpen()) {
            try {
                bw.write(data);
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return this;
    }

    public boolean isOpen() {
        if(bw != null) {
            return true;
        }
        return false;
    }
}
