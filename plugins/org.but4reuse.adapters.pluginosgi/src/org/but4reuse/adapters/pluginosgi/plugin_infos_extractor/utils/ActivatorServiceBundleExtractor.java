package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.but4reuse.utils.files.FileUtils;

public class ActivatorServiceBundleExtractor {
	
	private File repository;
	private List<File> classes = new ArrayList<File>() ;
	
	public ActivatorServiceBundleExtractor(URI uriFile){
		repository = FileUtils.getFile(uriFile);
		
	}
	
	public void findActivatorServiceFile(File rep){
		
		File[] files=rep.listFiles();
		for(File file : files){
			if(file.isDirectory()){
					findActivatorServiceFile(file);
			}	
				
			else{
				if( (file.getName().contains("Publisher") || file.getName().contains("Activator"))
						&& file.getName().contains(".java")){
					classes.add(file);
					
				}
			}
		}
		
		
	}
	
	
	public void analyseFile(File file){
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while( (line =br.readLine())!=null){
				if(line.contains("registerService")){
					break;
				}
			}
			
			if(line!=null){
				
			}
			
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
