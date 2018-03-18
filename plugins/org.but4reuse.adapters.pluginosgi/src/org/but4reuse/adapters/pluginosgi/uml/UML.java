package org.but4reuse.adapters.pluginosgi.uml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;

import net.sourceforge.plantuml.SourceStringReader;


public class UML {
	
	private static FileWriter writer;


	private static void makeUMLDiagram(List<IElement> elements, String destPath){
		try {
			writer = new FileWriter(new File(destPath));
			writer.write("@startuml\n");
			for(IElement elem : elements){
				if(elem instanceof PluginElement){
					
					
				}
			}
			writer.write("@enduml");
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
		
	}
	
	
	public static void generateUMLDiagram(List<IElement> elements, String source, String pngDestination){
		
		makeUMLDiagram(elements, source);
		
		SourceStringReader reader = new SourceStringReader(source);
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
