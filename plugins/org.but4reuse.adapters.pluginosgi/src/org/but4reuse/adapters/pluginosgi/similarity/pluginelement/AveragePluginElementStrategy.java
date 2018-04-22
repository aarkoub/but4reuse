package org.but4reuse.adapters.pluginosgi.similarity.pluginelement;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.adapters.pluginosgi.similarity.ISimilarity;
import org.but4reuse.adapters.pluginosgi.similarity.packageelement.AveragePackageElementStrategy;

public class AveragePluginElementStrategy implements ISimilarity {

	@Override
	public double similarity(IElement currentElement, IElement anotherElement) {
		double quotient = 0;
		int i=0;
		
		if (currentElement instanceof PluginElement && anotherElement instanceof PluginElement) {
			PluginElement currentPluginElement = ((PluginElement) currentElement);
			PluginElement anotherPluginElement = ((PluginElement) anotherElement);

			// Same symbolic name
			if (currentPluginElement.getSymbName().equals(anotherPluginElement.getSymbName())) {
				return 1;
			}
			else{
				if(anotherPluginElement.getImport_packages().size()!=0){
					for(PackageElement importPack : currentPluginElement.getImport_packages()){
						double sum=0;
						importPack.setSimilarityStrategy(new AveragePackageElementStrategy());
						for(PackageElement importPack2 : anotherPluginElement.getImport_packages()){
							importPack2.setSimilarityStrategy(new AveragePackageElementStrategy());
							sum += importPack.similarity(importPack2);
							i++;
						}
						
						quotient+=sum;
					}
				}
				if(anotherPluginElement.getExport_packages().size()!=0){
					for(PackageElement exportPack : currentPluginElement.getExport_packages()){
						double sum=0;
						exportPack.setSimilarityStrategy(new AveragePackageElementStrategy());
						for(PackageElement exportPack2 : anotherPluginElement.getExport_packages()){
							exportPack2.setSimilarityStrategy(new AveragePackageElementStrategy());
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

}
