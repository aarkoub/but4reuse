package org.but4reuse.adapters.pluginosgi;

public class ServiceElement extends FileElement {
	
	private String interfaceName;
	private String objName;
	private boolean isInterface;
	private boolean isObj;
	
	public ServiceElement(String interfaceName, String objName){
		this.interfaceName = interfaceName;
		this.objName = objName;
		if(interfaceName.equals("")){
			isInterface = false;
		}else{
			isInterface = true;
		}
		
		if(objName.equals("")){
			isObj = false;
		}else{
			isObj = true;
		}
	}
	
	public String getInterfaceName(){
		return interfaceName;
	}
	
	public String getObjName(){
		return objName;
	}
	
	public boolean isInterface(){
		return isInterface;
	}
	
	public boolean isObj(){
		return isObj;
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(o==this) return true;
		if(!(o instanceof ServiceElement)) return false;
		return this.interfaceName.equals( ((ServiceElement) o).interfaceName);
	}

}
