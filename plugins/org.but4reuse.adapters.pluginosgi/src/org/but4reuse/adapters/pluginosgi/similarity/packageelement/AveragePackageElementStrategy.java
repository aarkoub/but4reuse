package org.but4reuse.adapters.pluginosgi.similarity.packageelement;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.ServiceElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;

public class AveragePackageElementStrategy implements ISimilarity {

	@Override
	public double similarity(IElement currentElement, IElement anotherElement) {
		
		double sum=0;
		
		//similarity relies on the offered services & needed services
		if (currentElement instanceof PackageElement && anotherElement instanceof PackageElement) {
			PackageElement currentPackageElement = ((PackageElement) currentElement);
			PackageElement anotherPackageElement = ((PackageElement) anotherElement);
			
			if(!currentPackageElement.getName().equals(anotherPackageElement.getName()))
				return 0;
			System.out.println(sum);
			for(ServiceElement serv : currentPackageElement.getServices()){
				
				for(ServiceElement serv2 : anotherPackageElement.getServices()){
					sum += serv.similarity(serv2);
					
				}
			}
			if(sum==0)
				return 0;
			
			return sum/(currentPackageElement.getServices().size());
		}
		return 0;
	}

}
