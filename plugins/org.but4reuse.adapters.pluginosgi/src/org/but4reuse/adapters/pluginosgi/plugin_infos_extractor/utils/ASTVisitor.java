package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
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
    
    public boolean visit(Assignment node){
    	//System.out.println(/*((FieldDeclaration)node.getParent()).getType()+" "+*/node.getName()+" "+node.getInitializer());
    	//varmap.put(node.getName().getFullyQualifiedName(), node);
    	Expression left = node.getLeftHandSide();
    	if(left instanceof SimpleName){
    		String name = ((SimpleName)left).getFullyQualifiedName();
    		if(assignmap.containsKey(name)) return true;
    		Expression right = node.getRightHandSide();
    		if(right instanceof ClassInstanceCreation){
    			ClassInstanceCreation cic = ((ClassInstanceCreation)right);
    			assignmap.put(name, cic.getType().toString());
    		}
    	}
    	return true;
    	
    }

}
