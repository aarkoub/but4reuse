package org.but4reuse.adapters.pluginosgi;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.impl.AbstractElement;

public class ServiceElement extends AbstractElement {
	
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
	
	@Override
	public double similarity(IElement anotherElement) {
		//if they implements the same interface
			// Same interface -> see redef of equals method
		if (this.equals(anotherElement)) {
			return 1;
		}
		return 0;
	}
	
	public String getInterfaceName(){
		return interfaceName;
	}
	
	public String getObjName(){
		return objName;
	}
	
	public void setInterfaceName(String name){
		interfaceName = name;
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
		if(this.interfaceName==""){
			return this.objName.equals( ((ServiceElement) o).objName);
		}
		return this.interfaceName.equals( ((ServiceElement) o).interfaceName);
	}

	@Override
	public String getText() {
		if(isInterface)
			return interfaceName;
		return objName;
	}

}
