package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.File;
import java.net.URI;

import org.but4reuse.utils.files.FileUtils;

public class ActivatorServiceBundleExtractor {
	
	private File repository;
	
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
				if(file.getName().contains("Publisher")){
					publisherFile = file;
				}
				else{
					if(file.getName().contains("");
				}
			}
		}
		
		
	}

}
