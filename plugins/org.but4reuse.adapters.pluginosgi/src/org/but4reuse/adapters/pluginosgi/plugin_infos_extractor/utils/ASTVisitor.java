package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {
	private Map<String, VariableDeclarationFragment> varmap;
	private List<MethodInvocation> invoclist;
	
	public ASTVisitor(Map<String, VariableDeclarationFragment> varmap, List<MethodInvocation> invoclist){
		this.varmap = varmap;
		this.invoclist = invoclist;
	}
	
	public boolean visit(MethodInvocation node) {
    	if(node.getName().toString().equals("registerService")){
    		invoclist.add(node);
    		/*System.out.println("Method:"+node+"\t");
        	System.out.println(node.getExpression());
        	List<Expression> args = node.arguments();
        	if(!args.isEmpty()){
        		for(Object arg: args.subList(0, 2)){
        			System.out.println("CHECK "+arg.getClass());
        			if(arg instanceof MethodInvocation){
        				TypeLiteral tl = (TypeLiteral)(((MethodInvocation)arg).getExpression());
        				System.out.println(tl.getType());
        			}
        			if(arg instanceof SimpleName){
        				System.out.println("SN "+((SimpleName)arg).getFullyQualifiedName()+"\t");
        			} else if(arg instanceof QualifiedName){
        				System.out.println(((QualifiedName)arg).getFullyQualifiedName()+"\t");
        			} else if(arg instanceof MethodInvocation){
        				//if this is a instance call to getClass()
        				String itf = ((MethodInvocation)arg).toString();
        				itf = itf.substring(0,itf.indexOf('.'));
        				System.out.println(itf+"\t");
        			}else{
        				System.out.println("N/A:"+arg.getClass()+"\t");
        			}
        			
        		}
        	}
        	
        	System.out.println();*/
    	}
    	return true;
    	
    	
 
    }

    
    public boolean visit(VariableDeclarationFragment node){
    	//System.out.println(/*((FieldDeclaration)node.getParent()).getType()+" "+*/node.getName()+" "+node.getInitializer());
    	varmap.put(node.getName().getFullyQualifiedName(), node);
    	return true;
    } 
}
