package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.util.EasyReader;

import java.io.File;
import java.util.HashMap;

public class Test {
	public static void main(String[] args) {
		EasyReader er = new EasyReader(new File("dump.out")).open();
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		String bn;
		String t;
		
		while((bn = er.readLine()) != null && (t = er.readLine()) != null) {
			
			if(t.contains("non matching")) {
				t = t.replace("non matching:", "").trim();
				String[] ts = t.split(",");
				
				for(String val:ts) {
					val = val.trim();
					if(map.containsKey(val)) {
						map.put(val, map.get(val) + 1);
					}
					else {
						map.put(val, 1);
					}
				}
			}
		}
		
		er.close();
		
		System.out.println(map);
	}
}
