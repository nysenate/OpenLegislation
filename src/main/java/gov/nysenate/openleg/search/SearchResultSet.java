package gov.nysenate.openleg.search;

import java.util.ArrayList;

public class SearchResultSet {

	int totalHitCount;
	
	ArrayList<SearchResult> results;

	/**
	 * @return the totalHitCount
	 */
	public int getTotalHitCount() {
		return totalHitCount;
	}

	/**
	 * @param totalHitCount the totalHitCount to set
	 */
	public void setTotalHitCount(int totalHitCount) {
		this.totalHitCount = totalHitCount;
	}

	/**
	 * @return the results
	 */
	public ArrayList<SearchResult> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(ArrayList<SearchResult> results) {
		this.results = results;
	}

	
}
