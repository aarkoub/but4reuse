package org.but4reuse.adapters.pluginosgi.similarity.pluginelement;

import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;
import org.but4reuse.adapters.pluginosgi.similarity.packageelement.OnOffControlPackageElementStrategy;

public class OnOffControlPluginElementStrategy implements ISimilarity {

	@Override
	public double similarity(IElement currentElement, IElement anotherElement) {
		
		if(currentElement instanceof PluginElement && anotherElement instanceof PluginElement){
			PluginElement current = (PluginElement)currentElement;
			PluginElement another = (PluginElement)anotherElement;
			if(current.getSymbName().equals(another.getSymbName())) return 1;
			
			List<PackageElement> lsec = new ArrayList<>(current.getImport_packages());
			List<PackageElement> lsea = new ArrayList<>(another.getImport_packages());
			
			for(PackageElement pelt: lsec){
				pelt.setSimilarityStrategy(new OnOffControlPackageElementStrategy());
			}
			for(PackageElement pelt: lsea){
				pelt.setSimilarityStrategy(new OnOffControlPackageElementStrategy());
			}
			
			int todelete = 0;
			boolean found = false;
			while(!lsec.isEmpty()){
				found = false;
				PackageElement search = lsec.remove(0);
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
			
			if(lsec.size() != 0 || lsea.size() != 0){
				return 0;
			}
			
			lsec = new ArrayList<>(current.getExport_packages());
			lsea = new ArrayList<>(another.getExport_packages());
			
			
			for(PackageElement pelt: lsec){
				pelt.setSimilarityStrategy(new OnOffControlPackageElementStrategy());
			}
			for(PackageElement pelt: lsea){
				pelt.setSimilarityStrategy(new OnOffControlPackageElementStrategy());
			}
			
			todelete = 0;
			found = false;
			while(!lsec.isEmpty()){
				found = false;
				PackageElement search = lsec.remove(0);
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
			
			if(lsec.size() != 0 || lsea.size() != 0){
				return 0;
			}
			
			return 1;
			
		}
		
		
		return 0;
	}

}
