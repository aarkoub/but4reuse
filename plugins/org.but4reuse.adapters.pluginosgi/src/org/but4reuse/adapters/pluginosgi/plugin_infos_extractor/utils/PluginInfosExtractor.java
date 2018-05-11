package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.adapters.pluginosgi.ServiceElement;
import org.but4reuse.adapters.pluginosgi.bytecode.parser.PluginsServiceParser;
import org.but4reuse.utils.files.FileUtils;

public class PluginInfosExtractor {
	public static final String BUNDLESINFO_RELATIVEPATH = "configuration/org.eclipse.equinox.simpleconfigurator/bundles.info";
	private static final String BUNDLE_VERSION = "Bundle-Version";
	private static final String BUNDLE_NAME = "Bundle-Name";
	private static final String REQUIRE_BUNDLE = "Require-Bundle";
	private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
	private static final String BUNDLE_LOCALIZATION = "Bundle-Localization";
	
	
	// as used in org.google.guava for example
	private static final String DEFAULT_LOCALIZATION = "OSGI-INF/l10n/bundle";

	private static final String FRAGMENT_HOST = "Fragment-Host";

	private static String currentLocalization = null;

	
	//ADD PSTL
	private static final String IMPORT_PACKAGE = "Import-Package";
	private static final String EXPORT_PACKAGE = "Export-Package";
	private static final String SERVICE_COMPONENT=  "Service-Component";
	private static final String BUNDLE_ACTIVATOR = "Bundle-Activator";
	private static final boolean exhaustive = true;
	private static int nbservices = 0;
	private static String PATH = null;
	
	
	
	private static void fillPluginElementInfo(PluginElement plugin, Manifest manifest) {
		Attributes attributes = manifest.getMainAttributes();
		String value = attributes.getValue(BUNDLE_SYMBOLIC_NAME);
		int i = value.indexOf(';');
		if (i != -1)
			value = value.substring(0, i);
		plugin.setSymbName(value);

		// Fragment info
		String fragmentHost = attributes.getValue(FRAGMENT_HOST);
		if (fragmentHost != null) {
			i = fragmentHost.indexOf(';');
			if (i != -1)
				fragmentHost = fragmentHost.substring(0, i);
			plugin.setFragmentHost(fragmentHost);
		}

		String version = attributes.getValue(BUNDLE_VERSION);
		plugin.setVersion(version);
		value = attributes.getValue(REQUIRE_BUNDLE);
		if (value != null) {
			getRequireBundlesSymbNames(value, plugin);
		}
		
		
		//ADD PSTL
		//Import and export packages
		
		String import_package = attributes.getValue(IMPORT_PACKAGE);
		
		if(import_package != null){
			String[] package_names;
			List<PackageElement> limport = plugin.getImport_packages();
			package_names = import_package.split(",\\s+|,");
			for(String name: package_names){
				
				String p = name.split("\"|;")[0];
				
				if(p.contains(")"))
					continue;
				
				limport.add(new PackageElement(p));
								
			}
		}
		
	
		String export_package = attributes.getValue(EXPORT_PACKAGE);
		if(export_package != null){
			String[] package_names;
			List<PackageElement> lexport = plugin.getExport_packages();
			package_names = export_package.split(",\\s+|,");
			for(String name: package_names){
				
				String p = name.split("\"|;")[0];
				
				if(p.contains(")") || p.contains("META-INF"))
					continue;
				
				lexport.add(new PackageElement(p));

				
			}
		}
		
		
		//extraction du jar
		boolean deleteDirectory = false;
		
		PATH = plugin.getAbsolutePath();
		if(PATH.endsWith(".jar")){
			try {
				ZipExtractor.unZipAll(new File(PATH), new File(PATH.substring(0, PATH.length()-4)));
				PATH = PATH.substring(0, PATH.length()-4);
			} catch (IOException e) {
				e.printStackTrace();
			}
			deleteDirectory = true;
		}
		
		

		String activatorbundle = attributes.getValue(BUNDLE_ACTIVATOR);
		if(activatorbundle != null) {
			String activators[] = activatorbundle.split(",\\s+|,");
			for(String name: activators) {
				String tm = PATH+File.separator+name.replace(".", File.separator)+".class";
				System.out.println("BUNDLE-ACTIVATOR: "+tm);
				File f = new File(tm);
				if(f.exists()) {
					List<ServiceElement> l = new ArrayList<>();
					try {
						PluginsServiceParser.parsePluginClass(new FileInputStream(f), l);
						nbservices += l.size();
						for(ServiceElement selts: l) {
							addPackagesFromByteCode(selts, plugin.getExport_packages());
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		if(exhaustive) {
			List<ServiceElement> l = new ArrayList<>();
			parseActivator(new File(PATH), plugin.getAbsolutePath(), l);
			nbservices += l.size();
			for(ServiceElement selts: l) {
				List<String> itfs = new ArrayList<>();
				itfs.add(selts.getInterfaceName());
				addPackagesFromActivatorParser(selts, plugin.getExport_packages());
			}
		}
		System.out.println("NB SERVICE:"+nbservices);
		String service_component = attributes.getValue(SERVICE_COMPONENT);
		
		if(service_component != null){
			
			String[] uri_xml ;
			List<PackageElement> lexport_packages = plugin.getExport_packages();
			List<PackageElement> limport_packages = plugin.getImport_packages();
			
			uri_xml = service_component.split(",\\s+|,");
			
			
			for(String uri : uri_xml){
				//System.out.println(uri);
				
				List<List<String>> infos = parserDS(uri);
				String implClass = null;
				
				List<String> implClassList = infos.get(0);
				if(!implClassList.isEmpty())
					implClass = implClassList.get(0);
				
				//System.out.println("Implem : "+implClass);
				
				//for provided interfaces
				addPackages(implClass, infos.get(1), lexport_packages);
				
				//for required interfaces
				addPackages(implClass, infos.get(2), limport_packages);	
				

			}
			
			//for(ServiceElement serv : p.getServices()) System.out.println(serv.getInterfaceName());
			
		}

		
		
		if(deleteDirectory){
			ZipExtractor.deleteDirectory(new File(PATH));
		}
		// Name
		currentLocalization = attributes.getValue(BUNDLE_LOCALIZATION);
		if (currentLocalization == null) {
			currentLocalization = DEFAULT_LOCALIZATION;
		}
		String name = attributes.getValue(BUNDLE_NAME);
		plugin.setName(name);
	}

	
	
	
	public static List<ServiceElement> parseActivator(File f, String classpath, List<ServiceElement> lse){
		//System.out.println(f.getAbsolutePath());
		if(f.isDirectory()){
			File[] listfiles = f.listFiles();
			for(File tmpf: listfiles){
				if(tmpf.isDirectory()){
					parseActivator(tmpf, classpath, lse);
				}else if(existRegisterParser(tmpf.getAbsolutePath()) && tmpf.getName().contains(".java")){
					System.out.println("ACTIVATOR TROUVE "+tmpf.getAbsolutePath());
					lse.addAll(RegisterServiceParser.computeServiceElement(tmpf.getAbsolutePath(), classpath));
					
				}
			}
		}
		return lse;
	}
	
	/**
	 * Find if a file contains an invocation of registerService
	 * @param file Path of the file
	 * @return true if contains registerService/ false otherwise
	 */
	public static boolean existRegisterParser(String file) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(file));
			String currentLine = "";
			while(sc.hasNextLine()){
				currentLine = sc.nextLine();
				if(currentLine.contains("registerService")) return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		
		return false;
		
	}
	
	/**
	 * Browse all the file of the current file and apply the bytecode parser to .class file
	 * @param f the current file
	 * @param lse list of services found
	 * @return list of services found
	 */
	public static List<ServiceElement> parseBytecode(File f, List<ServiceElement> lse){
		if(f.isDirectory()){
			File[] listfiles = f.listFiles();
			for(File tmpf: listfiles){
				if(tmpf.isDirectory()){
					parseBytecode(tmpf, lse);
				}else if(tmpf.getName().contains(".class")){
					//System.out.println("BYTECODE TROUVE "+tmpf.getAbsolutePath());
					try {
						PluginsServiceParser.parsePluginClass(new FileInputStream(tmpf), lse);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return lse;
	}
	
	/*
	 * FOLDERS
	 */
	/**
	 * Extracts the plugin infos from its MANIFEST.MF file
	 * 
	 * @param manifestFile
	 *            the absolute path to the manifest
	 * @return a PluginElement containing all the required informations
	 * @throws FileNotFoundException
	 */
	public static PluginElement getPluginInfosFromManifest(String manifestFile) {
		PluginElement plugin = new PluginElement();
		plugin.setJar(false);
		File f = new File(manifestFile);
		f = f.getParentFile().getParentFile();
		plugin.setAbsolutePath(f.getAbsolutePath());
		try {
			InputStream ips = new FileInputStream(manifestFile);
			Manifest manifest = new Manifest(ips);
			fillPluginElementInfo(plugin, manifest);
			ips.close();
			manifest = null;
			if (plugin.getName() != null && plugin.getName().contains("%")) {
				File localizationFile = new File(f, currentLocalization + ".properties");
				if (localizationFile.exists()) {
					Properties prop = new Properties();
					InputStream input = new FileInputStream(localizationFile);
					prop.load(input);
					// remove also whitespaces, as the problem with
					// org.eclipse.cdt.dsf.gdb.ui
					String name = prop.getProperty(plugin.getName().substring(1).replaceAll("\\s", ""));
					plugin.setName(name);
					input.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plugin;
	}

	/*
	 * JARS
	 */

	/**
	 * Extracts the plugin infos, considering that it is a jar file
	 * 
	 * @param jarFile
	 *            the absolute path to the jar file
	 * @return the plugin element
	 */
	public static PluginElement getPluginInfosFromJar(String jarFile) {
		PluginElement plugin = new PluginElement();
		plugin.setJar(true);
		plugin.setAbsolutePath(jarFile);
		try {
			File f = new File(jarFile);
			JarFile jar = new JarFile(f);
			fillPluginElementInfo(plugin, jar.getManifest());
			if (plugin.getName() != null && plugin.getName().contains("%")) {
				ZipEntry zipEntry = jar.getEntry(currentLocalization + ".properties");
				if (zipEntry != null) {
					Properties prop = new Properties();
					prop.load(jar.getInputStream(zipEntry));
					String name = prop.getProperty(plugin.getName().substring(1).replaceAll("\\s", ""));
					plugin.setName(name);
				}
			}
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plugin;
	}

	/*
	 * BOTH
	 */

	/**
	 * Extracts all the required plugins' symbolic names from the Require-Bundle
	 * field's value
	 * 
	 * @param value
	 *            a String containing the whole Require-Bundle field's value
	 * @param plugin
	 *            the PluginElement for which we are searching the required
	 *            plugins
	 */
	private static void getRequireBundlesSymbNames(String value, PluginElement plugin) {
		String[] values = value.split(",");
		String previous = "";
		for (String val : values) {
			if (!val.matches("\\s*[0-9].*")) {
				int i = val.indexOf(';');
				if (i != -1)
					val = val.substring(0, i);
				val = val.replaceAll("\\s", "");
				previous = val;
				plugin.addRequire_bundle(val);
			} else if (val.contains("resolution:=optional")) {
				plugin.removeRequire_bundle(previous);
			}
		}
	}

	public static Map<String, String> createBundlesInfoMap(URI uri) {
		Map<String, String> map = new HashMap<String, String>();
		File file = FileUtils.getFile(uri);
		File bundlesInfo = new File(file.getAbsolutePath() + "/" + BUNDLESINFO_RELATIVEPATH);
		if (bundlesInfo.exists()) {
			List<String> bundles = FileUtils.getLinesOfFile(bundlesInfo);
			for (String info : bundles) {
				int comma = info.indexOf(",");
				if (comma != -1) {
					map.put(info.substring(0, comma), info);
				}
			}
		}
		return map;
	}

	/**
	 * Check if a file is a plugin
	 * 
	 * @param file
	 * @return true if it is a plugin
	 */
	public static boolean isAPlugin(File file) {
		if (file.getParentFile().getName().equals("plugins") || file.getParentFile().getName().equals("dropins")) {
			if (file.isDirectory()) {
				File manif = new File(file.getAbsolutePath() + "/META-INF/MANIFEST.MF");
				if (manif.exists()) {
					return true;
				}
			} else if (FileUtils.getExtension(file).equalsIgnoreCase("jar")) {
				return true;
			}
		}
		return false;
	}
	
	
	private static List<List<String>> parserDS(String uri){
		
		List<List<String>> infos = new ArrayList<>();
		
		
		
		if(uri.charAt(0)!='/')
			uri = '/'+uri;
		
		if(uri.contains("*.xml")){	
			String newPath = PATH+uri.split("\\*.xml")[0];
			File file = new File(newPath);

			String[] list = file.list();
			
			for(int i=0; i<list.length; i++){
				if(list[i].endsWith(".xml")){
					infos.addAll(XMLParser.getInformations(newPath+list[i]));
				}
			}
			
		}
		else{
		
			infos.addAll(XMLParser.getInformations(PATH+uri));
		}
		
		
		return infos;
	}

	private static PackageElement findPackage(String name, List<PackageElement> packages){
		for(PackageElement p : packages){
			if(name.contains(p.getName())){
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Creates the packages and services along a list of interfaces implemented by the implemention class, 
	 * and adds the created packages to the list of packages 
	 * @param implClass the name of the implemented class of the services
	 * @param interfaceNames the names of all the interfaces implemented 
	 * @param lpackages the list of packages where the new packages need to be added
	 */
	private static void addPackages(String implClass, List<String> interfaceNames, List<PackageElement> lpackages){
		

		PackageElement p ;
		
		for(String n : interfaceNames ) {
			
			
			String[] words = n.split("\\.");
			
			String packageName = n.substring(0, n.length()- words[words.length-1].length()-1);

			//System.out.println("Interface name "+n);
			
			if((p=findPackage(packageName, lpackages))==null){
				//System.out.println("Package name "+packageName);
				
				p = new PackageElement(packageName);
				lpackages.add(p);
			}
			p.addService(new ServiceElement(n, implClass));
		}
	}
	
	
	private static void addPackagesFromByteCode(ServiceElement se, List<PackageElement> lpackages){
		PackageElement p;
		String n = se.getObjName();
		String[] words = n.split("\\.");
		
		String packageName = n.substring(0, n.length()- words[words.length-1].length()-1);
		if((p=findPackage(packageName, lpackages))==null){
			//System.out.println("Package name "+packageName);
			
			p = new PackageElement(packageName);
			lpackages.add(p);
		}
		p.addService(se);
	}
	
	private static void addPackagesFromActivatorParser(ServiceElement se, List<PackageElement> lpackages){
		PackageElement p ;
		if(se.getObjName().equals("")) {
			String n = se.getInterfaceName();
			
			String[] words = n.split("\\.");
			
			String packageName = n.substring(0, n.length()- words[words.length-1].length()-1);
			
			if((p=findPackage(packageName, lpackages))==null){
				//System.out.println("Package name "+packageName);
				
				p = new PackageElement(packageName);
				lpackages.add(p);
			}
			p.addService(se);
			return;
		}
		if(se.getInterfaceName().equals("")) {
			String n = se.getObjName();
			String[] words = n.split("\\.");
			String packageName = n.substring(0, n.length()- words[words.length-1].length()-1);
			
			if((p=findPackage(packageName, lpackages))==null){
				//System.out.println("Package name "+packageName);
				
				p = new PackageElement(packageName);
				lpackages.add(p);
			}
			p.addService(se);
			return;
		}
		String n = se.getInterfaceName();
		
		String[] words = n.split("\\.");
		
		String packageName = n.substring(0, n.length()- words[words.length-1].length()-1);

		//System.out.println("Interface name "+n);
		
		if((p=findPackage(packageName, lpackages))==null){
			//System.out.println("Package name "+packageName);
			
			p = new PackageElement(packageName);
			lpackages.add(p);
		}
		p.addService(se);
		
	}
	
	
	
	/**
	 * Add a list of services to the package referenced by its name, making sure that the package nor the service already exists
	 * @param packageName the name of the package
	 * @param lpackages the list of the packages where it is to be added
	 * @param lservices the list of services to add to the package
	 */
	private static void addPackagesServices(String packageName, List<PackageElement> lpackages, List<ServiceElement> lservices){
		
		PackageElement p;
		
		if((p=findPackage(packageName, lpackages))==null){
			
			p = new PackageElement(packageName);
			lpackages.add(p);
		}
		
		for(ServiceElement servToAdd : lservices){
			boolean needToAdd = true;
			for(ServiceElement serv : p.getServices()){
				if(servToAdd.equals(serv)){
					needToAdd = false;
					break;
				}
			}
			
			if(needToAdd){
				p.getServices().add(servToAdd);
				System.out.println("Ajout du service "+servToAdd.getInterfaceName()+" dans "+p.getName());
			}
			
		}

	}
	
}
