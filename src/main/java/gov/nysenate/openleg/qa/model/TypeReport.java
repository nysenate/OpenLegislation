package gov.nysenate.openleg.qa.model;
public class TypeReport {
	String type;
	int occurred;
	int updated;
	int total;
	
	public TypeReport() {
		
	}
	
	public TypeReport(String type, int occurred, int updated, int total) {
		this.type = type;
		this.occurred = occurred;
		this.updated = updated;
		this.total = total;
	}

	public String getType() {
		return type;
	}

	public int getOccurred() {
		return occurred;
	}

	public int getUpdated() {
		return updated;
	}

	public int getTotal() {
		return total;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setOccurred(int occurred) {
		this.occurred = occurred;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}