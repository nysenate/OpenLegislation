package gov.nysenate.openleg.search;

public class Result {
	
	public String otype;
	public String data;
	public String oid;
	public long lastModified;
	public String active;

	public Result(String otype, String data, String oid, long lastModified, String active) {
		this.otype = otype;
		this.data = data;
		this.oid  = oid;
		this.lastModified = lastModified;
		this.active = active;
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
	
	public String getActive() {
		return active;
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

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public void setActive(String active) {
		this.active = active;
	}
	
}
