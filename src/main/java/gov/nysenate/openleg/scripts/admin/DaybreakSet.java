package gov.nysenate.openleg.scripts.admin;

import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DaybreakSet implements Comparable<DaybreakSet>{

    private Map<DaybreakDocType, Message> reportMessages;
    private DateTime reportDate;
    private DateTime anchorDate;
    private String prefix;

    DaybreakSet(Message firstMessage) throws IllegalArgumentException, MessagingException{
        DaybreakDocType messageType = DaybreakDocType.getMessageType(firstMessage.getSubject());
        if(messageType==null){
            throw new IllegalArgumentException("Attempt to create report group from invalid message");
        }
        reportMessages = new HashMap<DaybreakDocType, Message>();
        reportMessages.put(messageType, firstMessage);
        reportDate = new DateTime(firstMessage.getSentDate());
        anchorDate = reportDate;
        prefix = null;
    }

    /**
     * Attempts to add a message to the message group
     * @param message
     * @return true if the message fits in the group, false if it isnt added
     * @throws Exception
     */
    public boolean addToGroup(Message message) throws MessagingException{
        DaybreakDocType messageType = DaybreakDocType.getMessageType(message.getSubject());
        if(messageType==null || reportMessages.keySet().contains(messageType)){
            return false;
        }
        DateTime sentDate = new DateTime(message.getSentDate());
        if(Days.daysBetween(sentDate, anchorDate).getDays()>1 || Days.daysBetween(sentDate, anchorDate).getDays()<-1){
            return false;
        }

        // If it has gotten this far, add the message
        if(sentDate.isAfter(reportDate.toInstant())){
            reportDate = sentDate;
            prefix = null;
        }
        reportMessages.put(messageType, message);
        return true;
    }

    /**
     * Indicates whether or not the set contains all necessary daybreak messages
     * @return
     */
    public boolean completeSet(){
        return DaybreakDocType.containsAllMessages(reportMessages.keySet());
    }

    /**
     * Returns the date as a string prefix for use in saving the message attachments locally
     * @return
     */
    public String getPrefix(){
        if(prefix==null){
            prefix = new SimpleDateFormat("yyyyMMdd").format(reportDate.toDate());
        }
        return prefix;
    }

    public Message getMessage(DaybreakDocType messageType){
        return reportMessages.get(messageType);
    }

    @Override
    public int compareTo(DaybreakSet o) {
        return this.reportDate.compareTo(o.reportDate);
    }
}
