package gov.nysenate.openleg.util;

import gov.nysenate.util.Config;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** This class manages a list of bills that should not be published */
public class UnpublishListManager {
    private static final Logger logger = Logger.getLogger(UnpublishListManager.class);

    private File unpublishFile;
    private Set<String> unpublishedBills;

    public UnpublishListManager(String pathToFile) {
        Config config = Application.getConfig();
        unpublishFile = new File(pathToFile);
        if(!unpublishFile.exists()) {
            try {
                unpublishFile.createNewFile();
            } catch (IOException e) {
                logger.error("Could not create unpublish file");
            }
        }
        unpublishedBills = null;
    }

    public UnpublishListManager() {
        this(Application.getConfig().getValue("env.unpublished"));
    }

    public Set<String> getUnpublishedBills(){
        if(unpublishedBills!=null){
            return unpublishedBills;
        }
        try {
            String unpublishFileText = FileUtils.readFileToString(unpublishFile);
            unpublishedBills = new HashSet<String>();
            String[] lines = unpublishFileText.split("\n+");
            for(String line : lines){
                unpublishedBills.add(line.trim());
            }
            return unpublishedBills;
        }
        catch (IOException ex){
            logger.error("Could not retrieve unpublished bills");
            return null;
        }
    }

    public void addUnpublishedBill(String billId){
        FileWriter fileWriter;
        try{
            if(unpublishedBills==null){
                if (getUnpublishedBills()==null){
                    throw new IOException();
                }
            }
            if(!unpublishedBills.contains(billId)){
                fileWriter = new FileWriter(unpublishFile, true);
                fileWriter.write(billId);
                fileWriter.write('\n');
                fileWriter.close();
                unpublishedBills.add(billId);
                logger.info("added " + billId + " to unpublish list");
            }
            else{
                logger.info("Unpublish list already contains " + billId);
            }
        }
        catch (IOException ex){
            logger.error("Could not unpublish bill " + billId);
        }
    }
}
