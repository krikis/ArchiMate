package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.Activator;
import archimate.codegen.ICodeElement;
import archimate.codegen.JavaHelper;

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

	private boolean optional;

	private String packageName;

	private ArrayList<String> imports = new ArrayList<String>();

	private String comment;

	private String author;

	private ArrayList<String> archiMateTags = new ArrayList<String>();

	private String className;

	private String type;

	private ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();

	public JavaClass(String packageName, String className, String tag,
			String type) {
		visited = false;
		optional = false;
		this.packageName = packageName;
		this.className = className;
		archiMateTags.add(tag);
		this.type = type;
	}

	// Returns the classname
	public String identifier() {
		return className;
	}

	// Method defining whether the java class matches the identifier
	public boolean equals(String identifier) {
		return className.equals(identifier);
	}

	// Compares itself with the given ASTNode for differences
	public void diff(ASTNode node, MultiStatus status, String pattern) {
		if (node instanceof TypeDeclaration) {
			TypeDeclaration javaClass = (TypeDeclaration) node;
			// Checks the class type
			checkClassType(javaClass, status, pattern);
			// Checks the implemented interfaces
			checkInterfaces(javaClass, status, pattern);
			ASTNode root = javaClass.getRoot();
			if (root instanceof CompilationUnit) {
				CompilationUnit unit = (CompilationUnit) root;
				// Checks the package
				checkPackage(unit, status, pattern);
				// Checks the imports
				checkImports(unit, status, pattern);
			}
		}
	}

	// Checks the class type
	private void checkClassType(TypeDeclaration javaClass, MultiStatus status,
			String pattern) {
		if (javaClass.isInterface() != isInterface()) {
			status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, pattern
					+ ": \"" + className + "\" should be "
					+ (isInterface() ? "an interface" : "a class")
					+ ".                             ", null));
		}
	}

	// Checks the implemented interfaces
	private void checkInterfaces(TypeDeclaration javaClass, MultiStatus status,
			String pattern) {
		for (Iterator<InterfaceImpl> iter = interfaces.iterator(); iter
				.hasNext();) {
			InterfaceImpl interfaceImpl = iter.next();
			if (!interfaceImpl.optional()) {
				String interfaceName = interfaceImpl.interfaceName();
				boolean found = false;
				for (Iterator ite2 = javaClass.superInterfaceTypes().iterator(); ite2
						.hasNext();) {
					Object object = ite2.next();
					if (object instanceof SimpleType) {
						SimpleType type = (SimpleType) object;
						if (type.getName().getFullyQualifiedName().equals(
								interfaceName)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					status.add(new Status(IStatus.WARNING, status.getPlugin(),
							1, pattern + ": The \"" + className
									+ "\" class doesn't implement the \""
									+ interfaceName + "\" interface."
									+ "                             ", null));
				}
			}
		}
	}

	// Checks the package
	private void checkPackage(CompilationUnit unit, MultiStatus status,
			String pattern) {
		if (!packageName.equals(unit.getPackage().getName()
				.getFullyQualifiedName())) {
			status.add(new Status(IStatus.WARNING, status.getPlugin(), 1,
					pattern + ": The \"" + className + "\" "
							+ (isInterface() ? "interface" : "class")
							+ " should be in the \"" + packageName
							+ "\" package." + "                             ",
					null));
		}
	}

	// Checks the imports
	private void checkImports(CompilationUnit unit, MultiStatus status,
			String pattern) {
		for (Iterator<String> iter = imports.iterator(); iter.hasNext();) {
			String importName = iter.next();
			boolean found = false;
			for (Iterator ite2 = unit.imports().iterator(); ite2.hasNext();) {
				Object object = ite2.next();
				if (object instanceof ImportDeclaration) {
					ImportDeclaration declaration = (ImportDeclaration) object;
					if (declaration.getName().getFullyQualifiedName().equals(
							importName)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				status.add(new Status(IStatus.WARNING, status.getPlugin(), 1,
						pattern + ": Import \"" + importName
								+ "\" is missing in the \"" + className + "\" "
								+ (isInterface() ? "interface" : "class")
								+ ".                             ", null));
			}
		}
	}

	// Returns whether the java class has been visited in the source code
	public boolean visited() {
		return visited;
	}

	// Marks the java class as visited
	public void setVisited() {
		visited = true;
	}

	// Returns whether the javaClass is optional
	public boolean optional() {
		return optional;
	}

	// Marks the javaClass as optional
	public void setOptional() {
		optional = true;
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

	public void addInterface(InterfaceImpl interfaceName) {
		interfaces.add(interfaceName);
	}

	public void addInterfaces(ArrayList<InterfaceImpl> interfaces) {
		this.interfaces.addAll(interfaces);
	}

	public boolean interfacesDefined() {
		return interfaces.size() > 0;
	}

	public ArrayList<InterfaceImpl> interfaces() {
		return interfaces;
	}

}
