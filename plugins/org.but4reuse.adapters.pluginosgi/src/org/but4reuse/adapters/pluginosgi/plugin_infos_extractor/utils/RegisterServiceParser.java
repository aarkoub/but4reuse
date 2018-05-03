package org.but4reuse.adapters.pluginosgi.plugin_infos_extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.but4reuse.adapters.pluginosgi.ServiceElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class RegisterServiceParser {
	
	public static void main(String[] args){
		//parse("C:\\Users\\L-C\\Desktop\\Activator.java", new HashMap<String, VariableDeclarationFragment>(), new ArrayList<MethodInvocation>());
		computeServiceElement("C:\\Users\\L-C\\Desktop\\Activator.java");
	}
	
	
	
	
	public static List<ServiceElement> computeServiceElement(String filepath){
		Map<String, VariableDeclarationFragment> varmap = new HashMap<String, VariableDeclarationFragment>();
		List<MethodInvocation> invoclist = new ArrayList<MethodInvocation>();
		Map<String, String> assignmap = new HashMap<String, String>();
		
		parse(filepath, varmap, invoclist, assignmap);
		
		List<ServiceElement> servelts = new ArrayList<>();
		
		for(MethodInvocation minvoc: invoclist){
			@SuppressWarnings("unchecked")
			List<Expression> args = minvoc.arguments();
			String itf = findInterface(args.get(0), varmap);
			String obj = findObj(args.get(1), varmap, assignmap, filepath);
			
			if(itf.length()!=0){
				String [] structure = itf.split("\\.");
				
				System.out.println("SERVICE: itf: "+structure[structure.length-1]+"\t obj: "+obj);
				itf = structure[structure.length-1];
			}
			servelts.add(new ServiceElement(itf, obj));
			
		}
		
		return servelts;
	}
	
	
	public static String findInterface(Expression expr, Map<String, VariableDeclarationFragment> varmap){
		//si l'argument est directement le string d'une interface
		if(expr instanceof StringLiteral){
			return ((StringLiteral)expr).getLiteralValue();
		} else if(expr instanceof MethodInvocation && expr.toString().contains(".class.getName()")){
			//dans le cas o� on effectue un "NomClass.class.getName()"
			MethodInvocation tmpmi = (MethodInvocation)expr;
			Expression tmpexpr = tmpmi.getExpression();
			if(tmpexpr instanceof TypeLiteral){
				return ((TypeLiteral)tmpexpr).getType().toString();
			}
		} else if (expr instanceof TypeLiteral && expr.toString().contains(".class")){
			return expr.toString().substring(0, expr.toString().length()-6);
		} else if (expr instanceof SimpleName){
			String name = ((SimpleName)expr).getFullyQualifiedName();
			if(varmap.containsKey(name)){
				VariableDeclarationFragment vdf = varmap.get(name);
				Expression exprinit = vdf.getInitializer();
				if(exprinit instanceof StringLiteral){
					return ((StringLiteral)exprinit).getLiteralValue();
				}
			}
		}
		
		return "";
	}
	
	public static String findObj(Expression expr, Map<String, VariableDeclarationFragment> varmap, Map<String, String> assignmap, String path){
		if(expr instanceof SimpleName){
			String var = ((SimpleName)expr).getFullyQualifiedName();
			if(assignmap.containsKey(var)) return assignmap.get(var);
			
			VariableDeclarationFragment vdf = varmap.get(var);
			if(vdf == null){
				return "";
			}

			if(vdf.getInitializer() instanceof ClassInstanceCreation){
				ClassInstanceCreation cic = (ClassInstanceCreation)vdf.getInitializer();
				return cic.getType().toString();
			}
			
			//si non instancie
			if(vdf.getParent() instanceof VariableDeclarationStatement){
				VariableDeclarationStatement vds = (VariableDeclarationStatement)vdf.getParent();
				return vds.getType().toString();
			}

			
		} else if (expr instanceof ClassInstanceCreation){
			//si l'argument est directement "new Class()"
			ClassInstanceCreation cic = (ClassInstanceCreation)expr;
			return cic.getType().toString();
		} else if (expr instanceof ThisExpression){
			File f = new File(path);
			String name = f.getName();
			return name.substring(0, name.length()-5);
		}
		return "";
	}
	
	public static void parse(String filepath, Map<String, VariableDeclarationFragment> varmap, List<MethodInvocation> invoclist, Map<String, String> assignmap){
		char[] source = convertFileIntoCharArray(filepath);

		ASTParser parser = ASTParser.newParser(AST.JLS8);
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    parser.setSource(source);
	    parser.setResolveBindings(true);
	    CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	    cu.accept(new ASTVisitor(varmap, invoclist, assignmap));
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
