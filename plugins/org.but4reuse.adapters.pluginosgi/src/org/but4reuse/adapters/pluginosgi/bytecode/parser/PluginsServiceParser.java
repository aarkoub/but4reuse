package org.but4reuse.adapters.pluginosgi.bytecode.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.but4reuse.adapters.pluginosgi.ServiceElement;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PluginsServiceParser {
	
	static String pluginName;
	static String jarEntry;
	
	public static void main(String[] args) throws IOException {
		String folder = "C:\\Users\\L-C\\Desktop\\version_eclipse\\eclipse2\\plugins";
		
		File f = new File(folder);
		
		File[] plugins = f.listFiles();
		

		
		for(File plugin : plugins) {
			if(! plugin.isDirectory()) {
				if(plugin.getName().endsWith(".jar")) {
					JarFile jarFile = new JarFile(plugin.getAbsolutePath());  
			         Enumeration<JarEntry> enumOfJar = jarFile.entries();   
			         while (enumOfJar.hasMoreElements()) {
			            JarEntry je = enumOfJar.nextElement();
			            if(je.getName().endsWith(".class")) {
			            	pluginName = plugin.getName();
			            	jarEntry = je.getName();
			            	InputStream is = jarFile.getInputStream(je);			            	
			            	parsePluginClass(is, new ArrayList<ServiceElement>());
			            }
			         }
				}
			}
		}
		System.out.println("Nombre total de registerService : "+cpt);
		cpt = 0;
	}
	
	static int cpt = 0;
	public static List<ServiceElement> parsePluginClass(InputStream is, List<ServiceElement> lse) throws IOException {
				
		ClassReader cr = new ClassReader(is);
		
		ClassNode cn = new ClassNode();		
		cr.accept(cn, 0);
		
		List<MethodNode> methods = cn.methods;
		
		
		for(MethodNode mn : methods) {			
			//System.out.println("- Méthode analysée : "+mn.name+" "+mn.desc);
			
			InsnList instructions = mn.instructions;
			
			for(int i = 0 ; i < instructions.size(); i++) {
				AbstractInsnNode instr = instructions.get(i);				
				//System.out.println("# : "+instr.getOpcode()+" "+instr.getClass());
				//if(instr instanceof MethodInsnNode) {System.out.println("## : "+((MethodInsnNode)instr).name);}
				//if(instr instanceof TypeInsnNode) {System.out.println("## : "+((TypeInsnNode)instr).desc);}
				if(instr.getOpcode() == Opcodes.INVOKEVIRTUAL || instr.getOpcode() == Opcodes.INVOKEINTERFACE) { // Ne prendre en compte que les invocations de méthodes
					MethodInsnNode minstr = (MethodInsnNode)instr;
					//System.out.println("Trouvé une invocation de la méthode (instruction n° "+i+") : "+minstr.owner+"."+minstr.name+minstr.desc);
					if(minstr.name.equals("registerService")) { // Remplacer "n" par "registerSerivce" ou inversement (pour tester le code tout en bas)
						//System.out.println("=> Trouve une invocation a registerService ");
						//System.out.println("=> dans la methode "+mn.name+" de la classe : <"+jarEntry+"> dans le plugin <"+pluginName+"> :");
						cpt++;
						AbstractInsnNode ains = instructions.get(i-4); // Pour connaître le 2ème argument de l'invocation à registerService, il faut remonter à l'instruction -4
						
						//System.out.println("\t Operation precedente, correspondant au 2eme argument passe (instruction "+(i-4)+") :"+ ains.getOpcode());
						//System.out.println("\t Type du service enregistre : ");
						switch(ains.getOpcode()) {						
						case -1 :
						case 1 :
							ains = ains.getNext().getNext();
							if(ains.getOpcode() == 25) {
								VarInsnNode ins = (VarInsnNode)ains;
								List<LocalVariableNode> lvars = mn.localVariables;
								boolean found = false;
								for(LocalVariableNode lvar : lvars) {
									if(lvar.index == ins.var) {
										//System.out.println("\t Local Variable/Param Type : "+lvar.desc);
										lse.add(new ServiceElement("",convertService(lvar.desc)));
										found = true;
										break;
									}
								}
								if(! found) System.out.println("\t Local Variable/Param Type not found"); 
								
							}
							else {
								if(ains.getOpcode() == 182 || ains.getOpcode() == 184 || ains.getOpcode() == 185) {
									MethodInsnNode insm = (MethodInsnNode)ains;
									System.out.println("\t Method/Constructor Returned Object Type : "+insm.desc);
									lse.add(new ServiceElement("",convertService(insm.desc)));
									break;
								}								
							}
							break;
						case 18 : // LDC (Load Constant)
							LdcInsnNode lins = (LdcInsnNode)ains;							
							//System.out.println("\t Constant Type : "+lins.cst);
							lse.add(new ServiceElement("",convertService(lins.cst.toString())));
							break;							
						case 25 : // Accès à une variable locale ou bien un paramètre : VarInsnNode
							VarInsnNode ins = (VarInsnNode)ains;
							List<LocalVariableNode> lvars = mn.localVariables;
							boolean found = false;
							for(LocalVariableNode lvar : lvars) {
								if(lvar.index == ins.var) {
									//System.out.println("\t Local Variable/Param Type : "+lvar.desc);
									lse.add(new ServiceElement("",convertService(lvar.desc)));
									found = true;
									break;
								}
							}
							if(! found) System.out.println("\t Local Variable/Param Type not found"); 
							
							break;
						case 89: // Dupliquer la valeur au sommet de la pile
							ains = ains.getNext();
							if(ains.getOpcode() == 183) {
								MethodInsnNode insm = (MethodInsnNode)ains;
								if(insm.name.equals("<init>")) { // Appel de constructeur
									TypeInsnNode inst = (TypeInsnNode)(insm.getPrevious().getPrevious()); // En cas d'instanciation avec new, il faut remonter deux instructions plus haut pour retrouver l'expression de Type utilisée
									//System.out.println("\t Instantiation Type : "+inst.desc);
									lse.add(new ServiceElement("",convertService(inst.desc)));
									break;
								}
							}			
							else {
								if(ains.getOpcode() == 25) {
									ins = (VarInsnNode)ains;
									lvars = mn.localVariables;
									found = false;
									for(LocalVariableNode lvar : lvars) {
										if(lvar.index == ins.var) {
											//System.out.println("\t Local Variable/Param Type : "+lvar.desc);
											lse.add(new ServiceElement("",convertService(lvar.desc)));
											found = true;
											break;
										}
									}
									if(! found) System.out.println("\t Local Variable/Param Type not found"); 
									
									break;
								}
							}
							
						case 178: // Accès à un attribut statique : FieldInsnNode
						case 180: // Accès à un attribut d'instance : FieldInsnNode
							
							if(ains instanceof FieldInsnNode){
								FieldInsnNode insf = (FieldInsnNode)ains;
								//System.out.println("\t Field Type : "+insf.desc);
								lse.add(new ServiceElement("",convertService(insf.desc)));
							}
							
							break;
						
						case 183: // Invocation de méthode spéciale : MethodInsnNode
							MethodInsnNode insm = (MethodInsnNode)ains;
							if(insm.name.equals("<init>")) { // Appel de constructeur
								TypeInsnNode inst = (TypeInsnNode)(insm.getPrevious().getPrevious()); // En cas d'instanciation avec new, il faut remonter deux instructions plus haut pour retrouver l'expression de Type utilisée
								//System.out.println("\t Instantiation Type : "+inst.desc);
								lse.add(new ServiceElement("",convertService(inst.desc)));
								break;
							}							
						case 182: // Invocation de méthode : MethodInsnNode
						case 184: // Invocation de méthode statique : MethodInsnNode
						case 185: // Invocation de méthode d'interface : MethodInsnNode
							insm = (MethodInsnNode)ains;
							//System.out.println("\t Method/Constructor Returned Object Type : "+insm.desc);
							lse.add(new ServiceElement("",convertService(insm.desc)));
							break;
						case 192: // Cast de type
						case 187: // Instanciation de type
							TypeInsnNode inst = (TypeInsnNode)ains;
							//System.out.println("\t Instantiation Type : "+inst.desc);
							lse.add(new ServiceElement("",convertService(inst.desc)));
							break;
						default: //System.out.println("# Type non trouvé pour "+ains.getOpcode()+" : "+ains.getClass()); 
								throw new RuntimeException("Façon de passer un argument non prise en compte : "+
									ains.getOpcode()+"\n Instruction de type "+ains.getClass());
						
						}
					}
				}
			}
		}
		return lse;
		
	}
	
	
	public static String convertService(String name){
		String service = name.replace("/", ".").replace("(", "").replace(")","").replace(";","").replace("[", "");
		service = service.charAt(0)=='L'?service.substring(1):service;
		System.out.println("Service bytecode: "+service);
		return service;
	}
}
