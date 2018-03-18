package org.but4reuse.adapters.pluginosgi;

import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adapters.IElement;

public class PackageElement extends FileElement {
	
	private String name;
	private List<ServiceElement> services = new ArrayList<>();
	
	public PackageElement(String name){
		this.name = name;
	}
	
	@Override
	public double similarity(IElement anotherElement) {
		
		double quotient = 0;
		
		//similarity relies on the offered services & needed services
		if (anotherElement instanceof PackageElement) {
			PackageElement anotherPackageElement = ((PackageElement) anotherElement);
			
			for(ServiceElement serv : services){
				double sum=0;
				for(ServiceElement serv2 : anotherPackageElement.getServices()){
					sum += serv.similarity(serv2);
				}
				
				if(sum!=0)
					quotient += sum/anotherPackageElement.getServices().size();
			}
			if(quotient==0)
				return 0;
			
			return quotient/services.size();
		}
		return 0;
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
