package gov.nysenate.openleg;

import java.util.Collection;

public class QueryResult {

	public Collection<?> result;
	/**
	 * @return the result
	 */
	public Collection<?> getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Collection<?> result) {
		this.result = result;
	}
	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	public int total;
	
}
