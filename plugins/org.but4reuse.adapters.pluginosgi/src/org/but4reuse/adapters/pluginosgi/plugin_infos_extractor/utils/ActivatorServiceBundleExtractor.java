package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.utils.files.FileUtils;

public class ActivatorServiceBundleExtractor {
	
	
	public static void findActivatorServiceFile(File rep, List<File> toAnalyze){
		System.out.println(rep.getAbsolutePath());
		File[] files=rep.listFiles();
		for(File file : files){
			if(file.isDirectory()){
					findActivatorServiceFile(file, toAnalyze);
			}else{
				
				if( (file.getName().contains("Publisher") || file.getName().contains("Activator") || file.getName().contains("activator"))
						&& file.getName().contains(".java")){
					toAnalyze.add(file);
				}
			}
		}
		
		return;
		
	}
	
	
	public static void analyzeServicePlugin(PluginElement pe){
		BufferedReader br = null;
		List<File> toAnalyze = new ArrayList<File>();
		findActivatorServiceFile(new File(pe.getUri()), toAnalyze);
		Map<String,String> services = pe.getServices();
		
		for(File file: toAnalyze){
			try {
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				String line;
				while( (line =br.readLine())!=null){
					if(line.contains("registerService")){
						System.out.println("Interface: "+getInterface(line) + "\tService: " + getService(line,file));
						services.put(getInterface(line), getService(line, file));
					}
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return;
		
	}
	
	
	public static String extractBetween(String line, char start, char end) {
		int indstart = -1;
		int indend = -1;
		boolean mark = false;
		
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == start && !mark) {
				indstart = i+1;
				mark = true;
			}else if(line.charAt(i) == end && mark) {
				indend = i;
				break;
			}	
		}
		
		return line.substring(indstart, indend);
	}
	
	
	public static String getService(String line, File file) {
		
		BufferedReader br = null;
		String tofilter = extractBetween(line, ',', ',');
		if(tofilter.contains("new ")) {
			tofilter = tofilter.replace("new ", "");
			tofilter = tofilter.replaceAll("[()\\s]", "");
			return tofilter;
		}else {
			tofilter = tofilter.replaceAll("[()\\s]", "");
			String s = "";
			try {
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				while( (s = br.readLine())!=null){
					if(s.contains(tofilter) && s.contains("new ") && s.contains("=")){
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			tofilter = s;
			tofilter = tofilter.substring(tofilter.indexOf("new ")+4, tofilter.indexOf("("));
			return tofilter;
			
		}
	}

	public static String getInterface(String line) {
		return extractBetween(line, '(', '.');
	}
	

}
