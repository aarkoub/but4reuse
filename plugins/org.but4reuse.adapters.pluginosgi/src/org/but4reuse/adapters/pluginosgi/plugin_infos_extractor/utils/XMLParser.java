package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder docBuilder ;
	
	private static void initialize(){
		
		if(docBuilder==null){
		
			try {
				docBuilder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String getImplementationClass(Document doc){
		
		String implClass = null;

		NodeList nodeList = doc.getElementsByTagName("implementation");
			
		if(nodeList != null){
			if(nodeList.getLength()>1){
				System.out.println("\nON A EN PLUSIEURS "+nodeList.getLength()+" !!!\n");
			}
			implClass = nodeList.item(0).getAttributes().getNamedItem("class").getNodeValue();
		}
			
		
		return implClass;
		
		
	}
	
	public static List<String> getReferencedInterfaceNames(Document doc){
		
		List<String> interfacesNames = new ArrayList<>();
		
		NodeList nodeList = doc.getElementsByTagName("reference");
		
		for(int i=0; i<nodeList.getLength(); i++){
			
			if(nodeList.item(i).getAttributes().getNamedItem("interface")!=null){
				interfacesNames.add(nodeList.item(i).getAttributes().getNamedItem("interface").getNodeValue());
			}
			
		}
		
		
		return interfacesNames;
		
	}
	
	public static List<String> getProvidedInterfaceNames(Document doc){

		
		List<String> interfacesNames = new ArrayList<>();
		
		NodeList nodeList = doc.getElementsByTagName("provide");
			
		for(int i=0; i<nodeList.getLength(); i++){
			interfacesNames.add(nodeList.item(i).getAttributes().getNamedItem("interface").getNodeValue());
		}
		
		return interfacesNames;
	}

	public static List<List<String>> getInformations(String path) {
		
		initialize();
		
		List<List<String>> infos = new ArrayList<>();
		List<String> impl = new ArrayList<String>();
		
		Document doc;
		try {
			
			File f = new File(path);
			
			if(f.canRead()){
				doc = docBuilder.parse(new File(path));
				impl.add(getImplementationClass(doc));
				infos.add(impl);
				infos.add(getProvidedInterfaceNames(doc));
				infos.add(getReferencedInterfaceNames(doc));
			}
			else
				return null;
			
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return infos;
	}

}
