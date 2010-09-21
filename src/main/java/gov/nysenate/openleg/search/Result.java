package gov.nysenate.openleg.search;

public class Result {
	
	public String otype;
	public String data;
	public String oid;
	
	public Result(String otype, String data, String oid) {
		this.otype = otype;
		this.data = data;
		this.oid  = oid;
		
	}

	public String getOtype() {
		return otype;
	}

	public String getData() {
		return data;
	}

	public String getOid() {
		return oid;
	}

	public void setOtype(String otype) {
		this.otype = otype;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
}
