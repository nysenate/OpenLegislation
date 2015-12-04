package gov.nysenate.openleg.model.spotcheck.senatesite;

import java.util.*;

public class SenateSiteBillDump {

    /** Uniquely Ids this bill dump */
    protected SenateSiteBillDumpId billDumpId;

    /** The fragments that make up this bill dump, categorized by their sequence number */
    protected TreeMap<Integer, SenateSiteBillDumpFragment> fragmentMap = new TreeMap<>();

    protected SenateSiteBillDump(SenateSiteBillDumpId billDumpId) {
        this.billDumpId = billDumpId;
    }

    public static Collection<SenateSiteBillDump> categorizeDumpFragments(
            Collection<SenateSiteBillDumpFragment> fragments) {
        Map<SenateSiteBillDumpId, SenateSiteBillDump> dumpMap = new HashMap<>();
        fragments.stream().forEach( fragment -> {
            SenateSiteBillDumpId dumpId = fragment.getBillDumpId();
            if (!dumpMap.containsKey(dumpId)) {
                dumpMap.put(dumpId, new SenateSiteBillDump(dumpId));
            }
            dumpMap.get(dumpId).addDumpFragment(fragment);
        });
        return dumpMap.values();
    }

    /** --- Functional Getters / Setters --- */

    protected void addDumpFragment(SenateSiteBillDumpFragment fragment) {
        fragmentMap.put(fragment.getSequenceNumber(), fragment);
    }

}
