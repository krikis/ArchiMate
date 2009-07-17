package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

public class Method {
	
	private String name;
	
	private String archiMateTag;
	
	private String calledMethod;
	
	public Method(String name, String tag) {
		this.name = name;
		this.archiMateTag = tag;
	}
	
	public static ArrayList<Method> create(ArrayList<String> names, String tag) {
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Iterator<String> iter = names.iterator(); iter.hasNext();) {
			methods.add(new Method(iter.next(), tag));
		} 
		return methods;
	}
	
	public void setCalledMethod(String name){
		calledMethod = name;
	}
	
	public String name(){
		return name;
	}
	
	public String archiMateTag(){
		return archiMateTag;
	}
	
	public String calledMethod(){
		return calledMethod;
	}

}
