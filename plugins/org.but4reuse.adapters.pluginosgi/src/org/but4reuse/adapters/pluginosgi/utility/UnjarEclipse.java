package org.but4reuse.adapters.pluginosgi.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;	
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;


public class UnjarEclipse {

	public static void main(String[] args) {
		unjarEclipsePlugin("/Vrac/eclipse_distributions_but4reuse/eclipse", "/Vrac/tteesstt");

	}
	
	public static void unjarEclipsePlugin(String source, String destination) {
		System.out.println("Extracting: "+source);
		System.out.println("To: "+destination);
		File eclipse = new File(source);
		File[] folders = eclipse.listFiles();
		File plugins = null;
		String dst = destination+File.separator+"eclipse";
		new File(dst).mkdirs();
		for(File tmpf: folders) {
			if(tmpf.isDirectory() && tmpf.getName().equals("plugins")) {
				plugins = tmpf;
			}else {
				File cp = new File(dst+File.separator+tmpf.getName());
				try {
					System.out.println("-------------------------");
					System.out.println("Copy: "+tmpf.getPath());
					System.out.println("To: "+cp.getPath());
					System.out.println("-------------------------");
					Files.copy(tmpf.toPath(), cp.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		folders = plugins.listFiles();
		new File(dst+File.separator+"plugins").mkdirs();
		for(File tmpf: folders) {
			if(tmpf.getName().endsWith(".jar")) {
				System.out.println("-------------------------");
				System.out.println("Extracting jar: "+tmpf.getAbsolutePath());
				System.out.println("To: "+dst+File.separator+"plugins"+File.separator+tmpf.getName().substring(0, tmpf.getName().length()-4));
				File cp = new File(dst+File.separator+"plugins"+File.separator+tmpf.getName().substring(0, tmpf.getName().length()-4));
				extractJarFile(tmpf.getAbsolutePath(), cp.getAbsolutePath());
				System.out.println("-------------------------");
				
			}else {
				System.out.println("-------------------------");
				System.out.println("Copy: "+tmpf.getPath());
				System.out.println("To: "+dst+File.separator+"plugins"+File.separator+tmpf.getName());
				System.out.println("-------------------------");
				File cp = new File(dst+File.separator+"plugins"+File.separator+tmpf.getName());
				try {
					Files.copy(tmpf.toPath(), cp.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
	
				}
			}
		}
		System.out.println("Extraction done.");
		
	}
	
	public static void extractJarFile(String src, String dst) {
		try {
			java.util.jar.JarFile jar = new java.util.jar.JarFile(src);
			java.util.Enumeration<JarEntry> enume = jar.entries();
			
			while(enume.hasMoreElements()) {
				JarEntry je = enume.nextElement();
				File f = new java.io.File(dst + java.io.File.separator + je.getName());
				if(je.isDirectory()) {
					f.mkdirs();
					continue;
				}
				f.getParentFile().mkdirs();
				f.createNewFile();
				System.out.println("copying: "+je.getName());
				Files.copy(jar.getInputStream(je), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			
			jar.close();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		

	}

}
