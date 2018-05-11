package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.but4reuse.adapters.pluginosgi.PluginElement;

public class ActivatorServiceBundleExtractor {
	//ANCIEN PARSER D'ACTIVATOR UTILISANT UN STREAM DE STRING ET PARSANT LES STRING
	//A NE PLUS CONSIDERER AU PROFIT D'UN PARSER QUI UTILISE JAVA JDT ET EST PLUS PERFORMANT
	
	public static boolean findActivatorServiceFile(File rep, List<File> toAnalyze){
		//System.out.println("Seek Activator in: "+rep.getAbsolutePath());
		boolean found = false;
		File[] files=rep.listFiles();
		for(File file : files){
			if(file.isDirectory()){
					found = findActivatorServiceFile(file, toAnalyze);
			}else{
				if( (file.getName().contains("Publisher") || file.getName().contains("Activator") || file.getName().contains("activator"))
						&& file.getName().contains(".java")){
					System.out.println("Activator found: "+file.getAbsolutePath());
					toAnalyze.add(file);
					found = true;
				}
			}
		}
		
		return found;
		
	}
	
	
	public static boolean analyzeServicePlugin(PluginElement pe){
		BufferedReader br = null;
		List<File> toAnalyze = new ArrayList<File>();
		File f = new File(pe.getUri());
		String path = f.getAbsolutePath();
		File todo = null;
		//System.out.println("Analyzing PluginElement services: "+f.getAbsolutePath());
		
		
		if(pe.isJar()){
			try {
				File unzip = new File(path.substring(0,path.length()-4));
				ZipExtractor.unZipAll(f, unzip);
				todo = unzip;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}else{
			todo = f;
		}
		
		
		findActivatorServiceFile(todo, toAnalyze);
		Map<String,String> services = pe.getServices();
		boolean foundService = false;
		String itf;
		String service;
		
		for(File file: toAnalyze){
			try {
				System.out.println("Analyze Activator in: "+file.getAbsolutePath());
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				String line;
				
				while( (line =br.readLine())!=null){
					
					if(line.contains("registerService")){
						itf = getInterface(line);
						service = getService(line,file);
						System.out.println("Interface: "+itf+ "\tService: " +service);
						if(itf.compareTo("") != 0 && service.compareTo("") != 0){
							services.put(itf, service);
							foundService = true;
						}
					}
				}
				
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		if(pe.isJar()){
			//System.out.println("delete: "+todo.getAbsolutePath());
			ZipExtractor.deleteDirectory(todo);
		}
		
		return foundService;
		
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
		
		if(indend == -1 || indstart == -1) return "";
		return line.substring(indstart, indend);
	}
	
	
	
	public static String getServiceNew(String line) {
		StringBuilder sb = new StringBuilder();
		line = line.replace("new ", "");
		line = line.replaceAll("[\\s]", "");
		
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == '(') {
				return sb.toString();
			}
			sb.append(line.charAt(i));
		}
		return sb.toString();
	}
	
	
	public static String getService(String line, File file) {
		
		BufferedReader br = null;
		System.out.println(line);
		String tofilter = extractBetween(line, ',', ',');
		System.out.println(line);
		if(tofilter == "") return "";
		
		if(tofilter.contains("new ")) {
			return getServiceNew(tofilter);
		}else {
			tofilter = tofilter.replaceAll("[()\\s]", "");
			if(tofilter.compareTo("this")==0) {
				return file.getName().substring(0, file.getName().length()-5);
			}
			
			if(tofilter.contains(".")) return tofilter;
			
			
			String s = "";
			try {
				@SuppressWarnings("resource")
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				while( (s = br.readLine())!=null){
					if(s.contains(tofilter) && s.contains("new ") && s.contains("=") && s.contains("(")){
						return (s.indexOf("new ")+4<s.indexOf("("))?s.substring(s.indexOf("new ")+4, s.indexOf("(")):"";
					}
					if(s.contains(tofilter) && s.contains("(") && s.contains(")") && s.contains("=")) {
						return s.substring(s.indexOf("(")+1,s.indexOf(")"));
					}
				}
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "";
			
		}
	}

	public static String getInterface(String line) {
		int index = line.indexOf("registerService(")+16;
		StringBuilder sb = new StringBuilder();
		for(int i = index; i < line.length(); i++) {
			if(line.charAt(i) == '.') {
				return sb.toString();
			}
			sb.append(line.charAt(i));
		}
		return sb.toString();
	}
	

}
