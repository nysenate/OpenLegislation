package gov.nysenate.openleg.search;

import gov.nysenate.openleg.xstream.XStreamBuilder;

public class Result {
	
	public static void main(String[] args) {
		
		System.out.println(XStreamBuilder.writeResponse("xml", new SearchEngine2().get("xml", "bill", "s1234-2009", null, 0, 1, true)));
		
	}

	public String otype;
	public String data;
	
	public Result(String otype, String data) {
		this.otype = otype;
		this.data = data;
	}
	
}
