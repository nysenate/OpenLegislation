package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.model.ISenateObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class JsonDao {
	private Logger logger = Logger.getLogger(JsonDao.class);
	
	ObjectMapper mapper = null;
	
	String writeDirectory;
	
	public JsonDao(String writeDirectory) {
		this(writeDirectory, Ingest.getMapper());
	}
	
	public JsonDao(String writeDirectory, ObjectMapper mapper) {
		this.writeDirectory = writeDirectory;
		this.mapper = mapper;
	}
	
	public String writeSenateObject(ISenateObject obj) {
		if(!(obj.getYear()+"").matches("20(0[90]|1[01])")) {
			return null;
		}
		
		File yearDir = new File(writeDirectory + "/" + obj.getYear());
		File typeDir = new File(writeDirectory + "/" + obj.getYear() + "/" + obj.luceneOtype());
		File newFile = new File(writeDirectory + "/" + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		if(!yearDir.exists()) {
			logger.info("creating directory: " + yearDir.getAbsolutePath());
			yearDir.mkdir();
		}
		if(!typeDir.exists()) {
			logger.info("creating directory: " + typeDir.getAbsolutePath());
			typeDir.mkdir();
		}
		
		logger.info("Writing json to path: " + newFile.getAbsolutePath());
		try {			
			BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(newFile));
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(osw,JsonEncoding.UTF8);
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
			mapper.writeValue(generator, obj);
			osw.close();
			
			return newFile.getAbsolutePath();
		} catch (JsonGenerationException e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not parse json", e);
		} catch (IOException e) {
			logger.warn("error reading file", e);
		}
		
		return null;
	}
	
	public ISenateObject loadSenateObject(String id, String year, String type, Class<? extends ISenateObject> clazz) {
		return loadSenateObject(writeDirectory + "/" + year + "/" + type + "/" + id + ".json", clazz);
	}
	
	/**
	 * @param path to json document
	 * @param clazz class of object to be loaded
	 * @return deserialized SenateObject of type clazz
	 */
	public ISenateObject loadSenateObject(String path, Class<? extends ISenateObject> clazz) {
		try {
			logger.info("Loading object at: " + path);
			File file = new File(path);
			if(!file.exists()) 
				return null;
			return mapper.readValue(file, clazz);
		} catch (org.codehaus.jackson.JsonParseException e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not map json", e);
		} catch (IOException e) {
			logger.warn("error with file", e);
		}
		
		return null;
	}
	
	
	public boolean deleteSenateObject(ISenateObject so) {
		return deleteFile(so.luceneOid(), so.getYear() +"", so.luceneOtype());
	}
	
	public boolean deleteFile(String id, String year, String type) {
		return deleteFile(writeDirectory + "/" + year + "/" + type + "/" + id + ".json");
	}
	
	public boolean deleteFile(String path) {
		logger.info("Deleting file at: " + path);
		
		File file = new File(path);
		return file.delete();
	}
	
	
	
	
	public ISenateObject mergeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz) {
		File file = new File(writeDirectory  + "/" + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		if(file.exists()) {
			logger.info("Merging object with id: " + obj.luceneOid());
			try {
				ISenateObject oldObject = (ISenateObject)mapper.readValue(file, clazz);
				oldObject.setLuceneActive(obj.getLuceneActive());
				oldObject.merge(obj);
				obj = oldObject;
				
				
			} catch (JsonGenerationException e) {
				logger.warn("could not parse json", e);
			} catch (JsonMappingException e) {
				logger.warn("could not parse json", e);
			} catch (IOException e) {
				logger.warn("error reading file", e);
			}
		}
		
		return obj;		
	}
}
