package gov.nysenate.openleg.scripts.admin;


import org.apache.log4j.Logger;

import javax.mail.Message;
import java.util.LinkedList;
import java.util.List;

public class DaybreakSetContainer {
    public static final Logger logger = Logger.getLogger(DaybreakSetContainer.class);

    /** List containing sets of daybreak emails */
    List<DaybreakSet> daybreakSetList;

    public DaybreakSetContainer(){
        daybreakSetList = new LinkedList<DaybreakSet>();
    }

    /**
     * Attempts to add a message to a daybreak message set
     * if the message is a valid daybreak email and doesnt fit into a set, a new set is created
     * @param message
     * @throws Exception
     */
    public void addMessage(Message message) throws Exception{
        boolean messageFit = false;  // This is set to true when a message is fit into a daybreak set
        for(DaybreakSet daybreakSet : daybreakSetList){
            if(daybreakSet.addToGroup(message)){
                messageFit = true;
                break;
            }
        }
        if(!messageFit){
            try{
                daybreakSetList.add(new DaybreakSet(message));
            }
            catch(IllegalArgumentException ex){
                logger.error("Could not identify as a daybreak email: " + message.getSubject());
            }
        }
    }

    /**
     * Returns a list of all daybreak sets that contain all necessary daybreak messages
     * @return List<DaybreakSet>
     */
    public List<DaybreakSet> getCompleteSets(){
        List<DaybreakSet> completeSets = new LinkedList<DaybreakSet>();
        for(DaybreakSet daybreakSet : daybreakSetList){
            if(daybreakSet.completeSet()){
                completeSets.add(daybreakSet);
            }
        }
        return completeSets;
    }

    /**
     * Returns all sets that contain some but not all necessary daybreak messages
     * @return List<DaybreakSet>
     */
    public List<DaybreakSet> getPartialSets(){
        List<DaybreakSet> partialSets = new LinkedList<DaybreakSet>();
        for(DaybreakSet daybreakSet : daybreakSetList){
            if(!daybreakSet.completeSet()){
                partialSets.add(daybreakSet);
            }
        }
        return partialSets;
    }
}
