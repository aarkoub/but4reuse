package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtractor {
	
	public static void unZipAll(File source, File destination) throws IOException 
	{
	    //System.out.println("Unzipping - " + source.getName());
	    int BUFFER = 2048;

	    ZipFile zip = new ZipFile(source);
	    try{
	        destination.getParentFile().mkdirs();
	        Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

	        // Process each entry
	        while (zipFileEntries.hasMoreElements())
	        {
	            // grab a zip file entry
	            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	            String currentEntry = entry.getName();
	            File destFile = new File(destination, currentEntry);
	            //destFile = new File(newPath, destFile.getName());
	            File destinationParent = destFile.getParentFile();

	            // create the parent directory structure if needed
	            destinationParent.mkdirs();

	            if (!entry.isDirectory())
	            {
	                BufferedInputStream is = null;
	                FileOutputStream fos = null;
	                BufferedOutputStream dest = null;
	                try{
	                    is = new BufferedInputStream(zip.getInputStream(entry));
	                    int currentByte;
	                    // establish buffer for writing file
	                    byte data[] = new byte[BUFFER];

	                    // write the current file to disk
	                    fos = new FileOutputStream(destFile);
	                    dest = new BufferedOutputStream(fos, BUFFER);

	                    // read and write until last byte is encountered
	                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
	                        dest.write(data, 0, currentByte);
	                    }
	                } catch (Exception e){
	                    System.out.println("unable to extract entry:" + entry.getName());
	                    throw e;
	                } finally{
	                    if (dest != null){
	                        dest.close();
	                    }
	                    if (fos != null){
	                        fos.close();
	                    }
	                    if (is != null){
	                        is.close();
	                    }
	                }
	            }else{
	                //Create directory
	                destFile.mkdirs();
	            }

	        }
	    } catch(Exception e){
	        e.printStackTrace();
	        System.out.println("Failed to successfully unzip:" + source.getName());
	    } finally {
	        zip.close();
	    }
	    //System.out.println("Done Unzipping:" + source.getName());
	}
	
	public static void deleteDirectory(File file){
		if(file.isDirectory()){
			String[]entries = file.list();
			for(String s: entries){
			    File currentFile = new File(file.getPath(),s);
			    deleteDirectory(currentFile);
			}
			file.delete();
		}
		file.delete();
	}
	
}
