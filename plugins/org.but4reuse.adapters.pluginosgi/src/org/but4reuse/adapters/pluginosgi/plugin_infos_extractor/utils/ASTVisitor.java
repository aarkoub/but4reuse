package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class ASTVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {
	private Map<String, VariableDeclarationFragment> varmap;
	private List<MethodInvocation> invoclist;
	private Map<String, String> assignmap;
	
	public ASTVisitor(Map<String, VariableDeclarationFragment> varmap, List<MethodInvocation> invoclist, Map<String, String> assignmap){
		this.varmap = varmap;
		this.invoclist = invoclist;
		this.assignmap = assignmap;
	}
	
	public boolean visit(MethodInvocation node) {
    	if(node.getName().toString().equals("registerService")){
    		invoclist.add(node);
    	}
    	return true;
 
    }

    
    public boolean visit(VariableDeclarationFragment node){
    	varmap.put(node.getName().getFullyQualifiedName(), node);
    	return true;
    	
    }
    
    public boolean visit(Assignment node){
    	Expression left = node.getLeftHandSide();
    	if(left instanceof SimpleName){
    		String name = ((SimpleName)left).getFullyQualifiedName();
    		if(assignmap.containsKey(name)) return true;
    		Expression right = node.getRightHandSide();
    		if(right instanceof ClassInstanceCreation){
    			ClassInstanceCreation cic = ((ClassInstanceCreation)right);
    			Type t = cic.getType();
    			ITypeBinding itb = t.resolveBinding();
    			assignmap.put(name, itb!=null?itb.getQualifiedName():"");
    		}
    	}
    	return true;
    	
    }

}
