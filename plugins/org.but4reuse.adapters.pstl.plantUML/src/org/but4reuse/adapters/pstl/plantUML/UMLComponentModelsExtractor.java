package org.but4reuse.adapters.pstl.plantUML;

import java.util.List;

import org.but4reuse.adaptedmodel.AdaptedModel;
import org.but4reuse.adaptedmodel.Block;
import org.but4reuse.adaptedmodel.helpers.AdaptedModelHelper;
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
			
			//////////*************************************/////
			/// To be adapted for UML component Model ///
			
			//////////*************************************/////
			
			
			/**
			 * Construct the Base Model
			 */
			// TODO For the construction of the BaseModel refactor to use
			// org.but4reuse.adapters.emf.helper.EMFHelper.constructMaximalEMFModel(AdaptedModel,
			// List<IElement>, URI)
			// Get emf adapter
			// retrieve the common block
			Block baseBlock = AdaptedModelHelper.getCommonBlocks(adaptedModel).get(0);
			List<IElement> elements = AdaptedModelHelper.getElementsOfBlock(baseBlock);
			List<Block> blocks = AdaptedModelHelper.getCommonBlocks(adaptedModel);
			
			UML.generateUMLDiagram(elements, "./Block0Uml.txt", "./Block0Uml.png");
			
			for(int i=1; i<blocks.size(); i++){
				elements = AdaptedModelHelper.getElementsOfBlock(blocks.get(i));
				UML.generateUMLDiagram(elements, "./Block"+i+"Uml.txt", "./Block"+i+"Uml.png");
			}
			
			System.out.println("END construct plant UML component models");
	}

}
