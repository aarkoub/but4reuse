package org.but4reuse.adapters.pluginosgi;

import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;

public class PackageElement extends FileElement {
	
	private String name;
	private List<ServiceElement> services = new ArrayList<>();
	private ISimilarity strategy;
	
	public PackageElement(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public List<ServiceElement> getServices(){
		return services;
	}
	
	public void setSimilarityStrategy(ISimilarity strategy){
		this.strategy = strategy;
	}
	public void addService(ServiceElement serv){
		//equals method of ServiceElement has been redefined
		if(!services.contains(serv))
			services.add(serv);
	}
	
	@Override
	public double similarity(IElement anotherElement) {
		return strategy.similarity(this, anotherElement);
	}
}
