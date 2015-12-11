package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.google.common.collect.Range;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

public class SenateSiteBillDump implements Comparable<SenateSiteBillDump> {

    /** Uniquely Ids this bill dump */
    protected SenateSiteBillDumpId billDumpId;

    /** The fragments that make up this bill dump, categorized by their sequence number */
    protected TreeMap<Integer, SenateSiteBillDumpFragment> fragmentMap = new TreeMap<>();

    protected SenateSiteBillDump(SenateSiteBillDumpId billDumpId) {
        this.billDumpId = billDumpId;
    }

    /**
     * Categorize the given collection of dump fragments into dumps
     * @param fragments Collection<SenateSiteBillDumpFragment>
     * @return Collection<SenateSiteBillDump>
     */
    public static Collection<SenateSiteBillDump> categorizeDumpFragments(
            Collection<SenateSiteBillDumpFragment> fragments) {
        Map<SenateSiteBillDumpId, SenateSiteBillDump> dumpMap = new HashMap<>();
        fragments.stream().forEach( fragment -> {
            if (!dumpMap.containsKey(fragment)) {
                dumpMap.put(fragment, new SenateSiteBillDump(fragment));
            }
            dumpMap.get(fragment).addDumpFragment(fragment);
        });
        return dumpMap.values();
    }

    /** --- Functional Getters / Setters --- */

    protected void addDumpFragment(SenateSiteBillDumpFragment fragment) {
        fragmentMap.put(fragment.getSequenceNo(), fragment);
    }

    /**
     * @return boolean - true iff this dump contains a complete set of fragments
     */
    public boolean isComplete() {
        return IntStream.rangeClosed(1, billDumpId.getFragmentCount())
                .allMatch(fragmentMap::containsKey);
    }

    /**
     * @return Collection<SenateSiteBillDumpFragment> - All received fragments of this dump
     */
    public Collection<SenateSiteBillDumpFragment> getDumpFragments() {
        return fragmentMap.values();
    }


    /** --- Overridden Methods --- */

    @Override
    public int compareTo(SenateSiteBillDump o) {
        return billDumpId.compareTo(o.billDumpId);
    }

    /** --- Getters --- */

    public SenateSiteBillDumpId getBillDumpId() {
        return billDumpId;
    }
}
