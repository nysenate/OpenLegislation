package gov.nysenate.openleg.spotchecks.sensite;

import com.google.common.collect.ComparisonChain;

import java.util.Collection;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class SenateSiteDump implements Comparable<SenateSiteDump> {

    /** Uniquely Ids this dump */
    private final SenateSiteDumpId dumpId;

    /** The fragments that make up this dump, categorized by their sequence number */
    private final TreeMap<Integer, SenateSiteDumpFragment> fragmentMap = new TreeMap<>();

    public SenateSiteDump(SenateSiteDumpId dumpId) {
        this.dumpId = dumpId;
    }

    /** --- Functional Getters / Setters --- */

    public void addDumpFragment(SenateSiteDumpFragment fragment) {
        fragmentMap.put(fragment.getSequenceNo(), fragment);
    }

    /**
     * @return boolean - true iff this dump contains a complete set of fragments
     */
    public boolean isComplete() {
        return IntStream.rangeClosed(1, dumpId.fragmentCount())
                .allMatch(fragmentMap::containsKey);
    }

    /**
     * @return Collection<SenateSiteBillDumpFragment> - All received fragments of this dump
     */
    public Collection<SenateSiteDumpFragment> getDumpFragments() {
        return fragmentMap.values();
    }


    /** --- Overridden Methods --- */

    @Override
    public int compareTo(SenateSiteDump o) {
        return ComparisonChain.start()
                .compare(this.dumpId, o.dumpId)
                .result();
    }

    /** --- Getters --- */

    public SenateSiteDumpId getDumpId() {
        return dumpId;
    }
}
