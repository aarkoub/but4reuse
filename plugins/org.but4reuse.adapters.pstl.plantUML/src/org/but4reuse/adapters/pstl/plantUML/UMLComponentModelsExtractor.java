package org.but4reuse.adapters.pstl.plantUML;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.but4reuse.adaptedmodel.AdaptedModel;
import org.but4reuse.adaptedmodel.Block;
import org.but4reuse.adaptedmodel.ElementWrapper;
import org.but4reuse.adaptedmodel.helpers.AdaptedModelHelper;
import org.but4reuse.adapters.IDependencyObject;
import org.but4reuse.adapters.IElement;

import org.but4reuse.adapters.pluginosgi.uml.UML;

/**
 * UML model
 * 
 */
public class UMLComponentModelsExtractor {

	// TODO add these options to a preferences page
	static boolean KEEP_INTRINSIC_IDS = true;
	static boolean KEEP_EXTRENSIC_IDS = true;

	public static void createComponentModels(String constructionURI, AdaptedModel adaptedModel) {

			System.out.println("Start construct plant UML component models");

			List<IElement> elements ;
			List<Block> blocks = adaptedModel.getOwnedBlocks();
			
			 File directory = new File(constructionURI);
			 directory.mkdirs();
			
			for(int i=0; i<blocks.size(); i++){
				elements = AdaptedModelHelper.getElementsOfBlock(blocks.get(i));
				UML.generateUMLDiagram(elements, constructionURI+"Block"+i+"Uml.txt", constructionURI+"Block"+i+"Uml.png");
			}
			
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
