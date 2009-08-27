package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.codegen.ICodeElement;

/**
 * Class modeling a Java class
 * 
 * @author Samuel Esposito
 */
public class JavaClass implements ICodeElement {
	// Constants defining the type of the class
	public static final String INTERFACE = "interface";
	public static final String CLASS = "class";
	// Whether the class has been visited
	private boolean visited;
	// Whether the class is an optional code element
	private boolean optional;
	// The name of the package
	private String packageName;
	// List of the class imports
	private ArrayList<String> imports = new ArrayList<String>();
	// Comment going with the class
	private String comment;
	// Author of the class
	private String author;
	// List of tags going with the class
	private ArrayList<String> archiMateTags = new ArrayList<String>();
	// Name of the class
	private String className;
	// Type of the class, either CLASS or INTERFACE
	private String type;
	// Whether the class is abstract
	private boolean isAbstract;
	// The superclass that is extended
	private String superClass;
	// List of implemented interfaces
	private ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();

	/**
	 * Creates a new {@link JavaClass} object
	 * 
	 * @param packageName
	 *            the name of the package
	 * @param className
	 *            the name of the class
	 * @param tag
	 *            the tag going with the class
	 * @param type
	 *            the type of the class, either {@link JavaClass#CLASS} or
	 *            {@link JavaClass#INTERFACE}
	 */
	public JavaClass(String packageName, String className, String tag,
			String type) {
		visited = false;
		optional = false;
		isAbstract = false;
		superClass = "";
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
	public void setOptional(boolean value) {
		optional = value;
	}

	/**
	 * Returns the file in which the class is implemented
	 * 
	 * @return the file in which the class is implemented
	 */
	public String targetFile() {
		return className + ".java";
	}

	/**
	 * Sets the package
	 * 
	 * @param packageName
	 *            the package
	 */
	public void setPackage(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Returns whether a package is defined
	 * 
	 * @return whether a package is defined
	 */
	public boolean packageDefined() {
		return packageName.length() > 0;
	}

	/**
	 * Returns the package
	 * 
	 * @return the package
	 */
	public String packageName() {
		return packageName;
	}

	/**
	 * Adds an import to the list of imports
	 * 
	 * @param importName
	 *            the import to add
	 */
	public void addImport(String importName) {
		imports.add(importName);
	}

	/**
	 * Adds a collection of imports to the list of imports
	 * 
	 * @param imports
	 *            the collection of imports to add
	 */
	public void addImports(ArrayList<String> imports) {
		this.imports.addAll(imports);
	}

	/**
	 * Returns whether imports have been defined
	 * 
	 * @return whether imports have been defined
	 */
	public boolean importsDefined() {
		return imports.size() > 0;
	}

	/**
	 * Returns the list of imports
	 * 
	 * @return the list of imports
	 */
	public ArrayList<String> imports() {
		return imports;
	}

	/**
	 * Sets the comment for the class
	 * 
	 * @param comment
	 *            the comment for the class
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/*
	 * (non-Javadoc) Returns whether a comment has been defined
	 * 
	 * @see archimate.codegen.ICodeElement#commentDefined()
	 */
	public boolean commentDefined() {
		return comment.length() > 0;
	}

	/*
	 * (non-Javadoc) Returns the comment going with this class
	 * 
	 * @see archimate.codegen.ICodeElement#comment()
	 */
	public String comment() {
		return comment;
	}

	/**
	 * Sets the java class author
	 * 
	 * @param author
	 *            the class author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns whether an author has been defined
	 * 
	 * @return whether an author has been defined
	 */
	public boolean authorDefined() {
		return author.length() > 0;
	}

	/**
	 * Returns the author
	 * 
	 * @return the author
	 */
	public String author() {
		return author;
	}

	/**
	 * Adds an archiMateTag to the list of tags
	 * 
	 * @param archiMateTag
	 *            the archiMateTag to add to the list of tags
	 */
	public void addArchiMateTag(String archiMateTag) {
		archiMateTags.add(archiMateTag);
	}

	/**
	 * Adds a list of archiMateTags to the list of tags
	 * 
	 * @param archiMateTags
	 *            the list of archiMateTags to be added to the list of tags
	 */
	public void addArchiMateTags(ArrayList<String> archiMateTags) {
		this.archiMateTags.addAll(archiMateTags);
	}

	/*
	 * (non-Javadoc) Returns whether archiMateTags have been defined
	 * 
	 * @see archimate.codegen.ICodeElement#archiMateTagsDefined()
	 */
	public boolean archiMateTagsDefined() {
		return archiMateTags.size() > 0;
	}

	/*
	 * (non-Javadoc) Returns the first archiMateTag going with the class
	 * 
	 * @see archimate.codegen.ICodeElement#archiMateTag()
	 */
	public String archiMateTag() {
		if (archiMateTagsDefined())
			return archiMateTags.get(0);
		return "";
	}

	/**
	 * Returns the list of archiMateTags going with the class
	 * 
	 * @return the list of archiMateTags going with the class
	 */
	public ArrayList<String> archiMateTags() {
		return archiMateTags;
	}

	/**
	 * Sets the name of the class
	 * 
	 * @param className
	 *            the name of the class
	 */
	public void setClass(String className) {
		this.className = className;
	}

	/**
	 * Returns whether the name of the class is defined
	 * 
	 * @return whether the name of the class is defined
	 */
	public boolean classDefined() {
		return className.length() > 0;
	}

	/**
	 * Returns the class name
	 * 
	 * @return the class name
	 */
	public String className() {
		return className;
	}

	/**
	 * Marks the class as an abstract class
	 */
	public void setAbstract() {
		isAbstract = true;
	}

	/**
	 * Returns whether the class is abstract
	 * 
	 * @return Whether the class is abstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * Sets the superclass
	 * 
	 * @param className
	 *            the name of the superclass
	 */
	public void setSuperClass(String className) {
		superClass = className;
	}

	/**
	 * Returns whether the class has a superclass
	 * 
	 * @return Whether the class has a superclass
	 */
	public boolean hasSuperClass() {
		return superClass.length() > 0;
	}

	/**
	 * Returns the superclass
	 * 
	 * @return The superclass
	 */
	public String superClass() {
		return superClass;
	}

	/**
	 * Sets the class type, either {@link JavaClass#CLASS} or
	 * {@link JavaClass#INTERFACE}
	 * 
	 * @param type
	 *            the class type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns whether the class is an interface
	 * 
	 * @return whether the class is an interface
	 */
	public boolean isInterface() {
		return type.equals(INTERFACE);
	}

	/**
	 * Adds an interface to the list of interfaces
	 * 
	 * @param interfaceName
	 *            the interface to add
	 */
	public void addInterface(InterfaceImpl interfaceName) {
		interfaces.add(interfaceName);
	}

	/**
	 * Adds a collection of interfaces to the list of interfaces
	 * 
	 * @param interfaces
	 *            the collection of interfaces to add
	 */
	public void addInterfaces(ArrayList<InterfaceImpl> interfaces) {
		this.interfaces.addAll(interfaces);
	}

	/**
	 * Returns whether interfaces have been defined
	 * 
	 * @return whether interfaces have been defined
	 */
	public boolean interfacesDefined() {
		return interfaces.size() > 0;
	}

	/**
	 * Returns the implemented interfaces
	 * 
	 * @return the implemented interfaces
	 */
	public ArrayList<InterfaceImpl> interfaces() {
		return interfaces;
	}

}
