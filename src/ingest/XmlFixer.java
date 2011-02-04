package ingest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlFixer {
	public static void main(String[] args) {
		String data = "qwqw<sections><section name=\"BILLS ON THIRD READING\" type=\"C\" cd=\"0400\">etc</section></sections>qwqw";
		Pattern p = Pattern.compile("\\<sections\\>.+?\\<\\/sections\\>");
		Matcher m = p.matcher(data);
		if(!m.find()) {
			data = data.replace("</sections>", "");
		}
		
		Pattern.compile("\\<section.*?\\>.+?\\<\\/section\\>");
		m = p.matcher(data);
		if(!m.find()) {
			data = data.replace("</section>", "");
		}
	}
	public static void fixCalendar(File file) {
		String data = flatten(file);
		
		Pattern p = Pattern.compile("\\<sections\\>.+?\\<\\/sections\\>");
		Matcher m = p.matcher(data);
		if(!m.find()) {
			data = data.replace("</sections>", "");
		}
		
		Pattern.compile("\\<section.*?\\>.+?\\<\\/section\\>");
		m = p.matcher(data);
		if(!m.find()) {
			data = data.replace("</section>", "");
		}
				
		write(data.replaceAll("&newl;", "\n"), file);
	}
	
	private static void write(String data, File file) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(data);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String flatten(File file) {
		String ret = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String in = null;
			while((in = br.readLine()) != null) {
				ret += in + "&newl;";
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
