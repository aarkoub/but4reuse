package org.but4reuse.adapters.pluginosgi.similarity.packageelement;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.ServiceElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;

public class MinimalPackageElementStrategy implements ISimilarity {

	@Override
	public double similarity(IElement currentElement, IElement anotherElement) {
		if (currentElement instanceof PackageElement && anotherElement instanceof PackageElement) {
			PackageElement currentPackageElement = ((PackageElement) currentElement);
			PackageElement anotherPackageElement = ((PackageElement) anotherElement);
			
			for(ServiceElement se: currentPackageElement.getServices()){
				for(ServiceElement see: anotherPackageElement.getServices()){
					if(se.similarity(see) == 1){
						return 1;
					}
				}
			}
		}
		
		return 0;
	}

}
