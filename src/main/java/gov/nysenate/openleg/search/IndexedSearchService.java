package gov.nysenate.openleg.search;

import java.util.Collection;

public interface IndexedSearchService<T> {
    /**
     * Update the search index with the given content, replacing an existing entry if it exists.
     */
    void updateIndex(T content);

    /**
     * Update the search index with the given collection of items, replacing each existing one with
     * the one in the collection.
     */
    void updateIndex(Collection<T> content);

    /**
     * Clears all entries from the search index(ices) that are managed by the implementation.
     */
    void clearIndex();

    /**
     * Clears and fully constructs the search index(ices) using data from the canonical backing store.
     */
    void rebuildIndex();

    /**
     * Handle a rebuild search index event by checking to see if event affects any of the indices managed
     * by the implementation and recreating them in full from the backing store.
     */
    void handleRebuildEvent(RebuildIndexEvent event);

    /**
     * Handle a clear search index event by checking to see if event affects any of the indices managed
     * by the implementation and clearing them from the backing store.
     */
    void handleClearEvent(ClearIndexEvent event);
}