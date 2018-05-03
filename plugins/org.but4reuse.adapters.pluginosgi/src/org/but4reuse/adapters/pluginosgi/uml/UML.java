package org.but4reuse.adapters.pluginosgi.uml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PackageElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;
import org.but4reuse.adapters.pluginosgi.ServiceElement;

import net.sourceforge.plantuml.SourceStringReader;


public class UML {
	
	private static FileWriter writer;


	private static String makeUMLDiagram(List<IElement> elements, String destPath){
		
		StringBuilder sb = new StringBuilder();
		
		try {
			writer = new FileWriter(new File(destPath));
			writer.write("@startuml\n\n");
			sb.append("@startuml\n\n");
			for(IElement elem : elements){
				if(elem instanceof PluginElement){
					PluginElement plugElem = (PluginElement) elem;
					boolean noServices = true;
					
					for(PackageElement packElem : plugElem.getExport_packages()){
						
						
						
						for(ServiceElement serv : packElem.getServices()){
							if(!serv.isInterface()) continue;
							noServices = false;
							writer.write("["+plugElem.getName()+"]  - "+serv.getInterfaceName()+"\n");
							sb.append("["+plugElem.getName()+"]  - "+serv.getInterfaceName()+"\n");
						}
					}
					
					for(PackageElement packElem : plugElem.getImport_packages()){
						
						
						
						for(ServiceElement serv : packElem.getServices()){	
							if(!serv.isInterface()) continue;
							noServices = false;
							writer.write(serv.getInterfaceName()+" <- ["+plugElem.getName()+"]\n");
							sb.append(serv.getInterfaceName()+" <- ["+plugElem.getName()+"]\n");
						}
					}
					
					if(noServices){
						writer.write("["+plugElem.getName()+"]\n");
						sb.append("["+plugElem.getName()+"]\n");
					}
					
										
				}
			}
			writer.write("\n@enduml");
			sb.append("\n@enduml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return sb.toString();
		
	}
	
	
	public static void generateUMLDiagram(List<IElement> elements, String source, String pngDestination){
		
		String content = makeUMLDiagram(elements, source+".txt");
		
		SourceStringReader reader = new SourceStringReader(content);
		
		// Write the first image to "png"
		File pngFile = new File(pngDestination+"png");
		try {
			reader.outputImage(pngFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public static void generateUMLDiagramThreshold(List<IElement> elements, String source, String pngDestination, int threshold){
		
		int nb=0;
		int j=0;
		List<IElement> elementsToConsider = new ArrayList<IElement>();
		
		
		for(int i=0; i<elements.size(); i++){
						
			if(nb>=threshold){
				generateUMLDiagram(elementsToConsider, source+"_"+j, pngDestination+"_"+j);
				nb=0;
				j++;
				elementsToConsider.clear();
			}
			
			if(elements.get(i) instanceof PluginElement){
				PluginElement plugin = (PluginElement) elements.get(i);
				nb++;
				elementsToConsider.add(elements.get(i));	
				
			}
			
			
		}
		
		if( ! elementsToConsider.isEmpty()){
			generateUMLDiagram(elementsToConsider, source+"_"+j, pngDestination+"_"+j);
		}
		
	
	}


	public static List<List<IElement>> sortElementsByPackageName(List<IElement> elements, int nbLevel) {
		List<List<IElement>> sortedElements = new ArrayList<>();
		
		for(IElement elem : elements){
			boolean newList=true;
			if(elem instanceof PluginElement ){
				PluginElement plugin = (PluginElement) elem;
								
				String myPackageName = plugin.getSymbName();
				String [] myStructure = myPackageName.split("\\.");
				
				for(List<IElement> lelem : sortedElements){
					
					if(lelem.get(0) instanceof PluginElement){
						PluginElement otherPlugin = (PluginElement) lelem.get(0);
						String packageName = otherPlugin.getSymbName();
						String [] structure = packageName.split("\\.");
						
						boolean same=true;
											
						for(int k=0 ; k<nbLevel && k<myStructure.length && k<structure.length ; k++){
							if(!myStructure[k].equals(structure[k]) ){
								same=false;
							}
						}
						
						if(same){
							lelem.add(elem);
							newList = false;
							break;
						}
					}
					
				}
				
			}
			if(newList){
				List<IElement> nList = new ArrayList<IElement>();
				nList.add(elem);
				sortedElements.add(nList);
			}
		}
		
		return sortedElements;
	}
	
}
