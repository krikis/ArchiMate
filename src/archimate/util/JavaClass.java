package archimate.util;

import java.util.ArrayList;

import archimate.Activator;
import archimate.codegen.ICodeElement;

/**
 * Class modelling a Java class
 * 
 * @author Samuel Esposito
 * 
 */
public class JavaClass implements ICodeElement {

	public static final String INTERFACE = "interface";
	public static final String CLASS = "class";
	
	private boolean visited;

	private String packageName;

	private ArrayList<String> imports = new ArrayList<String>();

	private String comment;

	private String author;

	private ArrayList<String> archiMateTags = new ArrayList<String>();

	private String className;

	private String type;

	private ArrayList<String> interfaces = new ArrayList<String>();

	public JavaClass(String packageName, String className, String tag,
			String type) {
		visited = false;
		this.packageName = packageName;
		this.className = className;
		archiMateTags.add(tag);
		this.type = type;
	}
	
	// Method defining whether the java class matches the identifier
	public boolean equals(String identifier) {
		return className.equals(identifier);
	}

	// Returns whether the java class has been visited in the source code
	public boolean visited(){
		return visited;
	}

	// Marks the java class as visited
	public void setVisited(){
		visited = true;
	}

	public String targetFile() {
		return className + ".java";
	}

	public void setPackage(String packageName) {
		this.packageName = packageName;
	}

	public boolean packageDefined() {
		return packageName.length() > 0;
	}

	public String packageName() {
		return packageName;
	}

	public void addImport(String importName) {
		imports.add(importName);
	}

	public void addImports(ArrayList<String> imports) {
		this.imports.addAll(imports);
	}

	public boolean importsDefined() {
		return imports.size() > 0;
	}

	public ArrayList<String> imports() {
		return imports;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean commentDefined() {
		return comment.length() > 0;
	}

	public String comment() {
		return comment;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean authorDefined() {
		return author.length() > 0;
	}

	public String author() {
		return author;
	}

	public void addArchiMateTag(String archiMateTag) {
		archiMateTags.add(archiMateTag);
	}

	public void addArchiMateTags(ArrayList<String> archiMateTags) {
		this.archiMateTags.addAll(archiMateTags);
	}

	public boolean archiMateTagsDefined() {
		return archiMateTags.size() > 0;
	}

	public String archiMateTag() {
		if (archiMateTagsDefined())
			return archiMateTags.get(0);
		return "";
	}

	public ArrayList<String> archiMateTags() {
		return archiMateTags;
	}

	public void setClass(String className) {
		this.className = className;
	}

	public boolean classDefined() {
		return className.length() > 0;
	}

	public String className() {
		return className;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isInterface() {
		return type.equals(INTERFACE);
	}

	public void addInterface(String interfaceName) {
		interfaces.add(interfaceName);
	}

	public void addInterfaces(ArrayList<String> interfaces) {
		this.interfaces.addAll(interfaces);
	}

	public boolean interfacesDefined() {
		return interfaces.size() > 0;
	}

	public ArrayList<String> interfaces() {
		return interfaces;
	}

}
