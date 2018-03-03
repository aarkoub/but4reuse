package org.but4reuse.adapters.pluginosgi;

import java.util.ArrayList;
import java.util.List;

public class PackageElement extends FileElement {
	
	private String name;
	private List<ServiceElement> services = new ArrayList<>();
	
	public PackageElement(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public List<ServiceElement> getServices(){
		return services;
	}
	
	public void addService(ServiceElement serv){
		//equals method of ServiceElement has been redefined
		if(!services.contains(serv))
			services.add(serv);
	}
	
}
