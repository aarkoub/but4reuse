package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class RegisterServiceParser {
	
	public static void main(String[] args){
		parse("C:\\Users\\L-C\\Desktop\\Activator.java");
	}
	
	public static void parse(String filepath){
		char[] source = convertFileIntoCharArray(filepath);
		
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    parser.setSource(source);
	    parser.setResolveBindings(true);
	    CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	    cu.accept(new ASTVisitor() {
	        
	  
	    	
	        @SuppressWarnings("unchecked")
			public boolean visit(MethodInvocation node) {
	        	if(!node.getName().toString().equals("registerService")){
	        		return true;
	        	}
	        	System.out.print("Method:"+node.getName()+"\t");
	        	List<Object> args = node.arguments();
	        	if(!args.isEmpty()){
	        		for(Object arg: args.subList(0, 2)){
	        			if(arg instanceof SimpleName){
	        				System.out.print(((SimpleName)arg).getFullyQualifiedName()+"\t");
	        			} else if(arg instanceof QualifiedName){
	        				System.out.print(((QualifiedName)arg).getFullyQualifiedName()+"\t");
	        			} else if(arg instanceof MethodInvocation){
	        				//if this is a instance call to getClass()
	        				String itf = ((MethodInvocation)arg).toString();
	        				itf = itf.substring(0,itf.indexOf('.'));
	        				System.out.print(itf+"\t");
	        			}else{
	        				System.out.print("N/A:"+arg.getClass()+"\t");
	        			}
	        			
	        		}
	        	}
	        	
	        	System.out.println();
	        	
	            return true;
	        }

	        
	        public boolean visit(SingleVariableDeclaration node){
	        	System.out.println("Type:"+node.getType()+"\tname:"+node.getName());
	        	return true;
	        }
	        
	        public boolean visit(VariableDeclarationStatement node){
	        	System.out.println(node.toString());
	        	return true;
	        }
	        
	    });
	}
	
	
	public static char[] convertFileIntoCharArray(String filename){
		List<Character> l = new ArrayList<>();
		FileReader fr = null;
		BufferedReader br = null;
		String s;
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			s = br.readLine();
			while(s != null){
				for(int i = 0; i < s.length(); i++){
					l.add(s.charAt(i));
				}
				l.add(new Character('\n'));
				s = br.readLine();
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertCharacterListIntoChar(l);
	}
	
	public static char[] convertCharacterListIntoChar(List<Character> chars){
		char[] sources = new char[chars.size()];
		for(int i = 0; i < sources.length; i++){
			sources[i] = chars.get(i).charValue();
		}
		return sources;
	}
}
