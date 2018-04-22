package org.but4reuse.adapters.pluginosgi.similarity.packageelement;

import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.ServiceElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;

public class OnOffControlPackageElementStrategy implements ISimilarity {

	@Override
	public double similarity(IElement currentElement, IElement anotherElement) {
		
		if(currentElement instanceof PackageElement && anotherElement instanceof PackageElement){
			PackageElement current = (PackageElement)currentElement;
			PackageElement another = (PackageElement)anotherElement;
			
			List<ServiceElement> lsec = new ArrayList<>(current.getServices());
			List<ServiceElement> lsea = new ArrayList<>(another.getServices());
			
			int todelete = 0;
			boolean found = false;
			while(!lsec.isEmpty()){
				found = false;
				ServiceElement search = lsec.remove(0);
				for(int i = 0; i < lsea.size(); i++){
					if(search.similarity(lsea.get(i)) == 1){
						found = true;
						todelete = i;
						break;
					}
				}
				
				if(found){
					lsea.remove(todelete);
				}else{
					return 0;
				}
			}
			
			if(lsec.size() == 0 && lsea.size() == 0){
				return 1;
			}
			
		}
		
		return 0;
	}

}
