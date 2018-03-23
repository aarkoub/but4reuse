package org.but4reuse.adapters.pluginosgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.but4reuse.adapters.IElement;
import org.but4reuse.utils.strings.StringUtils;

/**
 * Plugin Element
 * 
 * @author Diana MALABARD
 * @author Jason CHUMMUN
 */
public class PluginElement extends FileElement {

	private String pluginSymbName;
	private String pluginVersion;
	private String bundleInfoLine;
	private String fragmentHost;
	private String name;
	// each pluginElement in require_Bundles, the list of require_Bundle will be
	// empty.
	// because we do not know the dependencies
	// The same thing for absoluthPath
	private ArrayList<String> require_Bundles;
	
	
	//PSTL ADD
	private ArrayList<PackageElement> import_packages;
	private ArrayList<PackageElement> export_packages;
	private Map<String,String> services;
	
	
	public Map<String,String> getServices(){
		return services;
	}
	
	public ArrayList<PackageElement> getImport_packages(){
		return import_packages;
	}
	
	public ArrayList<PackageElement> getExport_packages(){
		return export_packages;
	}
	
	
	
	
	@Override
	public double similarity(IElement anotherElement) {
		// When they have the same relative URI
		// TODO URIs can reference to the same file... check this
		
		double quotient = 0;
		int i=0;
		
		if (anotherElement instanceof PluginElement) {
			PluginElement anotherPluginElement = ((PluginElement) anotherElement);

			// Same symbolic name
			if (this.getSymbName().equals(anotherPluginElement.getSymbName())) {
				return 1;
			}
			else{
				if(anotherPluginElement.getImport_packages().size()!=0){
					for(PackageElement importPack : import_packages){
						double sum=0;
						for(PackageElement importPack2 : anotherPluginElement.getImport_packages()){
							sum += importPack.similarity(importPack2);
							i++;
						}
						
						quotient+=sum;
					}
				}
				if(anotherPluginElement.getExport_packages().size()!=0){
					for(PackageElement exportPack : export_packages){
						double sum=0;
						for(PackageElement exportPack2 : anotherPluginElement.getExport_packages()){
							sum+= exportPack.similarity(exportPack2);
							i++;
						}
						
						quotient+=sum;
					}
				}
				if(quotient==0)
					return 0;
				
				return quotient / i;
			}
		}
		return 0;
	}

	public ArrayList<String> getRequire_Bundles() {
		return require_Bundles;
	}

	private String absolutePath;
	private boolean isJar;

	public PluginElement() {
		require_Bundles = new ArrayList<String>();
		import_packages = new ArrayList<PackageElement>();
		export_packages = new ArrayList<PackageElement>();
		services = new HashMap<String, String>();
	}

	public String getSymbName() {
		return pluginSymbName;
	}

	public void setSymbName(String pluginSymbName) {
		this.pluginSymbName = pluginSymbName;
	}

	public void addRequire_bundle(String require_bundle) {
		this.require_Bundles.add(require_bundle);
	}

	public void removeRequire_bundle(String require_bundle) {
		this.require_Bundles.remove(require_bundle);
	}

	@Override
	public String getText() {
		return name + "  " + pluginSymbName + " " + pluginVersion;
	}

	public boolean isJar() {
		return isJar;
	}

	public void setJar(boolean isJar) {
		this.isJar = isJar;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getVersion() {
		return pluginVersion;
	}

	public void setVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getBundleInfoLine() {
		return bundleInfoLine;
	}

	public void setBundleInfoLine(String bundleInfoLine) {
		this.bundleInfoLine = bundleInfoLine;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFragmentHost() {
		return fragmentHost;
	}

	public void setFragmentHost(String fragmentHost) {
		this.fragmentHost = fragmentHost;
	}

	public boolean isFragment() {
		return fragmentHost != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSymbName() == null) ? 0 : getSymbName().hashCode());
		return result;
	}

	@Override
	public List<String> getWords() {
		List<String> words = new ArrayList<String>();
		if (name != null) {
			for (String s : StringUtils.tokenizeString(name)) {
				words.add(s);
			}
		}
		return words;
	}
}