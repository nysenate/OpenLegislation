package gov.nysenate.openleg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class EasyReader {
	private Logger logger = Logger.getLogger(EasyReader.class);
	public BufferedReader br = null;
	public File file;
	
	public EasyReader(File file) {
		this.file = file;
	}
	
	public EasyReader open() {
		try {
			this.close();
			
			br = new BufferedReader(new FileReader(file));
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
}
