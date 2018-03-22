package org.but4reuse.adapters.pluginosgi.uml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
						
						noServices = false;
						
						for(ServiceElement serv : packElem.getServices()){							
							writer.write("["+plugElem.getName()+"]  - "+serv.getInterfaceName()+"\n");
							sb.append("["+plugElem.getName()+"]  - "+serv.getInterfaceName()+"\n");
						}
					}
					
					for(PackageElement packElem : plugElem.getImport_packages()){
						
						noServices = false;
						
						for(ServiceElement serv : packElem.getServices()){							
							writer.write(serv.getInterfaceName()+" -> ["+plugElem.getName()+"]\n");
							sb.append(serv.getInterfaceName()+" -> ["+plugElem.getName()+"]\n");
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
		
		String content = makeUMLDiagram(elements, source);
		
		SourceStringReader reader = new SourceStringReader(content);
		
		// Write the first image to "png"
		File pngFile = new File(pngDestination);
		try {
			reader.outputImage(pngFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
