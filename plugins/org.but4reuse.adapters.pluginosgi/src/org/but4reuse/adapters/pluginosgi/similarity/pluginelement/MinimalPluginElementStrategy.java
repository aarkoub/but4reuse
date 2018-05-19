package org.but4reuse.adapters.pluginosgi.similarity.pluginelement;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;
import org.but4reuse.adapters.pluginosgi.similarity.packageelement.MinimalPackageElementStrategy;

public class MinimalPluginElementStrategy implements ISimilarity {

	@Override
	public double similarity(IElement currentElement, IElement anotherElement) {
		if(currentElement instanceof PluginElement && anotherElement instanceof PluginElement){
			PluginElement currentplugin = (PluginElement)currentElement;
			PluginElement anotherplugin = (PluginElement)anotherElement;
			
			// Same symbolic name
			if (currentplugin.getSymbName().equals(anotherplugin.getSymbName())) {
					return 1;
			}
			
			for(PackageElement pe: currentplugin.getExport_packages()){
				
				pe.setSimilarityStrategy(new MinimalPackageElementStrategy());
				for(PackageElement pee: anotherplugin.getExport_packages()){
					pee.setSimilarityStrategy(new MinimalPackageElementStrategy());
					if(pe.similarity(pee) == 1){
						return 1;
					}
				}
			}
		}
		
		return 0;
	}

}
