package org.but4reuse.adapters.pstl.plantUML;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adaptedmodel.AdaptedModel;
import org.but4reuse.adaptedmodel.Block;
import org.but4reuse.adaptedmodel.helpers.AdaptedModelHelper;
import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.adapters.pluginosgi.uml.UML;
import org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils.ZipExtractor;

/**
 * UML model
 * 
 */
public class UMLComponentModelsExtractor {

	// TODO add these options to a preferences page
	static boolean KEEP_INTRINSIC_IDS = true;
	static boolean KEEP_EXTRENSIC_IDS = true;

	public static void createComponentModels(String constructionURI, int threshold, int nbLevel, AdaptedModel adaptedModel) {

			System.out.println("Start construct plant UML component models");

			List<IElement> elements ;
			List<Block> blocks = adaptedModel.getOwnedBlocks();
			List<List<IElement>> blockList = new ArrayList<>();
			
			try {
				URI uri = new URI(constructionURI);
				constructionURI = uri.getPath();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			File directory = new File(constructionURI);
			
			if(directory.list()!=null){
				for(String f : directory.list()){
					ZipExtractor.deleteDirectory(new File(directory.getPath(), f));
				}
			}
			directory.mkdirs();
			for(int i=0; i<blocks.size(); i++){
				elements = AdaptedModelHelper.getElementsOfBlock(blocks.get(i));
				
				blockList.add(elements);
				
				List<List<IElement>> sortedElements = UML.sortElementsByPackageName(elements, nbLevel);
				
				for(List<IElement> listElements : sortedElements){
					String namePackageList="";
					int j=0;
					if(listElements.get(0) instanceof PluginElement){
						String [] structure  = ((PluginElement)(listElements.get(0))).getSymbName().split("\\.");
						for(int k=0 ; k<nbLevel &&  k<structure.length ; k++){
							namePackageList += structure[k];
							if(k!=nbLevel-1 && k!=structure.length-1){
								namePackageList += ".";
							}
						}
					}
					else{
						namePackageList=Integer.toString(j++);
					}
					String BlockURI = constructionURI+"/Block_"+i+"/";
					directory = new File(BlockURI);
					 directory.mkdirs();
					UML.generateUMLDiagramThreshold(listElements, BlockURI+"Block"+i+"Uml_"+namePackageList, BlockURI+"Block"+i+"Uml_"+namePackageList, threshold, namePackageList);
				}
			}
			
			
			UML.generateUmlBlocks(blockList, constructionURI+"/diagramBlocks", constructionURI+"/diagramBlocks");
			
			/*elements = AdaptedModelHelper.getElementsOfBlock(blocks.get(1));
			Map<IElement, ElementWrapper> ieewMap = AdaptedModelHelper.createMapIEEW(adaptedModel);
			for(IElement element : elements){
				List<IDependencyObject> dep = AdaptedModelHelper.getDependingOnIElement(adaptedModel, element, ieewMap);
				for(IDependencyObject d : dep){
					System.out.println(d.getDependencyObjectText());
				}
				
			}*/
			
			System.out.println("END construct plant UML component models");
	}

}
