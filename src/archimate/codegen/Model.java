package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class modelling a Java MVC Pattern
 * 
 * @author Samuel Esposito
 * 
 */
public class Model {

	private String dPackageName = "";

	private String dAuthor = "";

	private String dVersion = "";

	private String dArchiMateTag = "";

	private String dClassName = "";

	private ArrayList<String> dInterfaces = new ArrayList<String>();

	private ArrayList<String> dMethods = new ArrayList<String>();

	public Model() {
		super();
	}

	public void setPackage(String packageName) {
		dPackageName = packageName;
	}

	public boolean packageDefined() {
		return dPackageName.length() > 0;
	}

	public String packageName() {
		return dPackageName;
	}

	public void setAuthor(String author) {
		dAuthor = author;
	}

	public boolean authorDefined() {
		return dAuthor.length() > 0;
	}

	public String author() {
		return dAuthor;
	}

	public void setVersion(String version) {
		dVersion = version;
	}

	public boolean versionDefined() {
		return dVersion.length() > 0;
	}

	public String version() {
		return dVersion;
	}

	public void setArchiMateTag(String archiMateTag) {
		dArchiMateTag = archiMateTag;
	}

	public boolean archiMateTagDefined() {
		return dArchiMateTag.length() > 0;
	}

	public String archiMateTag() {
		return dArchiMateTag;
	}

	public void setClass(String className) {
		dClassName = className;
	}

	public boolean classDefined() {
		return dClassName.length() > 0;
	}

	public String className() {
		return dClassName;
	}

	public void addInterface(String interfaceName) {
		dInterfaces.add(interfaceName);
	}

	public boolean interfacesDefined() {
		return dInterfaces.size() > 0;
	}

	public Iterator<String> interfaces() {
		return dInterfaces.iterator();
	}

	public void addMethod(String method) {
		dMethods.add(method);
	}

	public void addMethods(ArrayList<String> methods) {
		dMethods.addAll(methods);
	}

	public boolean methodsDefined() {
		return dMethods.size() > 0;
	}

	public Iterator<String> methods() {
		return dMethods.iterator();
	}

}
