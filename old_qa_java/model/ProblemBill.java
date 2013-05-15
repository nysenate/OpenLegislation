package gov.nysenate.openleg.qa.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.TypeDiscriminator;

@SuppressWarnings("serial")
public class ProblemBill extends CouchDbDocument {

    @TypeDiscriminator
    String oid;
    Long modified;

    Long lastReported;

    Double rank;

    ArrayList<String> missingFields;
    HashMap<String, NonMatchingField> nonMatchingFields;

    public ProblemBill() {

    }

    public ProblemBill(String oid, Long modified) {
        this.setId(oid);
        this.oid = oid;
        this.modified = modified;
    }

    public String getOid() {
        return oid;
    }

    public Long getModified() {
        return modified;
    }

    public Long getLastReported() {
        return lastReported;
    }

    public Double getRank() {
        return rank;
    }

    public ArrayList<String> getMissingFields() {
        return missingFields;
    }

    public HashMap<String, NonMatchingField> getNonMatchingFields() {
        return nonMatchingFields;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public void setLastReported(Long lastReported) {
        this.lastReported = lastReported;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public void setMissingFields(ArrayList<String> missingFields) {
        this.missingFields = missingFields;
    }

    public void setNonMatchingFields(
            HashMap<String, NonMatchingField> nonMatchingFields) {
        this.nonMatchingFields = nonMatchingFields;
    }

    public void addMissingField(String field) {
        if(this.missingFields == null)
            this.missingFields = new ArrayList<String>();
        missingFields.add(field);
    }

    public void addNonMatchingField(NonMatchingField nonMatchingField) {
        if(this.nonMatchingFields == null)
            this.nonMatchingFields = new HashMap<String, NonMatchingField>();
        nonMatchingFields.put(nonMatchingField.getField(), nonMatchingField);
    }

    @JsonIgnore
    public static final long ONE_DAY_MS = 86400000L;

    /**
     * ranks 0-10 how problematic a bill is based on when it was last modified and
     * how many fields it is missing
     * 
     * @param newestMod ranking a list of bills, newestMod is the more recent modification made
     * @param mod the modification date of the bill being ranked
     * @param size the amount of fields missing/problematic
     * @return
     */
    @JsonIgnore
    public static double rank(long newestMod, long mod, int size) {
        if(size == 0)
            return 0.0;

        double difference = Math.abs((newestMod + 1) - mod) + 0.0;

        double daysDiff = difference / (ONE_DAY_MS + 0.0);
        if (daysDiff < 1.5)
            daysDiff = 1.25;
        if (daysDiff > 5)
            daysDiff = (daysDiff / 365) + 6;

        double heat = 10 - (((daysDiff))
                / ((Math.pow(size * daysDiff, 1.5))) * 10);

        return new BigDecimal(heat).setScale(2, BigDecimal.ROUND_UP).doubleValue();
    }

    @JsonIgnore
    public static ProblemBill merge(ProblemBill theNew, ProblemBill theOld) {
        theOld.setModified(theNew.getModified());
        theOld.setLastReported(theNew.getLastReported());

        if(theOld.getMissingFields() == null) {
            theOld.setMissingFields(theNew.getMissingFields());
        }
        else {
            if(theNew.getMissingFields() != null) {
                for(String field:theNew.getMissingFields()) {
                    if(!theOld.getMissingFields().contains(field)) {
                        theOld.addMissingField(field);
                    }
                }
            }
        }

        if(theOld.getNonMatchingFields() == null) {
            theOld.setNonMatchingFields(theNew.getNonMatchingFields());
        }
        else {
            if(theNew.getNonMatchingFields() != null) {
                for(String key: theNew.getNonMatchingFields().keySet()) {
                    theOld.addNonMatchingField(theNew.getNonMatchingFields().get(key));
                }
            }
        }

        /* assume old version already has revision and id for couch */
        return theOld;
    }

    public static ProblemBill removeNonMatchingFields(ProblemBill problemBill, FieldName[] fieldNames) {
        if(problemBill.getNonMatchingFields() != null) {
            for(FieldName fieldName:fieldNames) {
                problemBill.getNonMatchingFields().remove(fieldName.text());
            }

            if(problemBill.getNonMatchingFields().isEmpty()) {
                problemBill.setNonMatchingFields(null);
            }
        }

        return problemBill;
    }
}
