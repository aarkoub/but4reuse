package org.but4reuse.adapters.pluginosgi;

public class ServiceElement extends FileElement {
	
	private String interfaceName;
	private String objName;
	
	public ServiceElement(String interfaceName, String objName){
		this.interfaceName = interfaceName;
		this.objName = objName;
	}
	
	public String getInterfaceName(){
		return interfaceName;
	}
	
	public String getObjName(){
		return objName;
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(o==this) return true;
		if(!(o instanceof ServiceElement)) return false;
		return this.interfaceName.equals( ((ServiceElement) o).interfaceName);
	}

}
