package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.NamedElement;

import archimate.codegen.CodeElement;
import archimate.codegen.ICodeElement;

/**
 * Class modeling a Java class
 * 
 * @author Samuel Esposito
 */
public class JavaClass extends CodeElement implements ICodeElement {
	// Constants defining the type of the class
	public static final String INTERFACE = "interface";
	public static final String CLASS = "class";
	// The name of the package
	private String packageName;
	// List of the class imports
	private ArrayList<String> imports = new ArrayList<String>();
	// Author of the class
	private String author;
	// List of tags going with the class
	private ArrayList<String> archiMateTags = new ArrayList<String>();
	// Name of the class
	private String className;
	// Name candidate for the class
	private ArrayList<String> recordeds;
	// Type of the class, either CLASS or INTERFACE
	private String type;
	// Whether the class is abstract
	private boolean isAbstract;
	// The superclass that is extended
	private JavaClass superClass;
	// List of implemented interfaces
	private ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();

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
		umlElements = new ArrayList<NamedElement>();
		visited = false;
		optional = false;
		isAbstract = false;
		superClass = null;
		this.packageName = packageName;
		this.className = className;
		recordeds = new ArrayList<String>();
		archiMateTags.add(tag);
		this.type = type;
		children = new ArrayList<ICodeElement>();
		comment = "";
	}

	// Returns the classname
	public String identifier() {
		return className;
	}

	// Method defining whether the java class matches the identifier
	public boolean equals(String identifier, String packageName) {
		return className.equals(identifier)
				&& this.packageName.equals(packageName);
	}

	// Method defining whether the java class matches the given code element
	public boolean isInstanceof(ICodeElement element) {
		if (element instanceof JavaClass) {
			JavaClass javaClass = (JavaClass) element;
			boolean found = className.equals(javaClass.className)
					&& this.packageName.equals(javaClass.packageName);
			if (!found) {
				for (JavaClass interfaceClass : javaClass.interfaces) {
					found = className.equals(interfaceClass.className)
							&& this.packageName
									.equals(interfaceClass.packageName);
					if (found)
						break;
				}
			}
			return found;
		}
		return false;
	}

	// Compares itself with the given ASTNode for differences
	public void diff(ASTNode node, MultiStatus status, String pattern) {
		if (node instanceof TypeDeclaration) {
			TypeDeclaration javaClass = (TypeDeclaration) node;
			// Checks the class type
			checkClassType(javaClass, status, pattern);
			// Checks the superclass type
			checkSuperClassType(javaClass, status, pattern);
			// Checks the interfaces
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

	// Checks the superclass
	private void checkSuperClassType(TypeDeclaration javaClass,
			MultiStatus status, String pattern) {
		if (!isInterface()
				&& hasSuperClass()
				&& (!superClass.optional() || !superClass.className().equals(
						superClass.intendedName()))) {
			boolean found = false;
			if (javaClass.getSuperclassType() instanceof SimpleType) {
				SimpleType simpleType = (SimpleType) javaClass
						.getSuperclassType();
				IBinding binding = simpleType.resolveBinding();
				if (binding instanceof ITypeBinding) {
					ITypeBinding type = (ITypeBinding) binding;
					IPackageBinding packageBinding = type.getPackage();
					if (superClass.intendedName().equals(type.getName())
							&& superClass.packageName().equals(
									packageBinding.getName())) {
						found = true;
					}
				}
			}
			if (!found) {
				status.add(new Status(IStatus.WARNING, status.getPlugin(), 1,
						pattern + ": The \"" + className
								+ "\" class doesn't extend the \""
								+ superClass.intendedName() + "\" class."
								+ "                             ", null));
			}
		}
	}

	// Checks the interfaces
	private void checkInterfaces(TypeDeclaration javaClass, MultiStatus status,
			String pattern) {
		if (isInterface()) {
			checkExtendedInterfaces(javaClass, status, pattern);
		} else {
			checkImplementedInterfaces(javaClass, status, pattern);
		}
	}

	// Checks the extended interfaces
	private void checkExtendedInterfaces(TypeDeclaration javaClass,
			MultiStatus status, String pattern) {
		if (hasSuperClass()
				&& (!superClass.optional() || !superClass.className().equals(
						superClass.intendedName()))) {
			boolean found = false;
			for (Iterator ite2 = javaClass.superInterfaceTypes().iterator(); ite2
					.hasNext();) {
				Object object = ite2.next();
				if (object instanceof SimpleType) {
					SimpleType simpleType = (SimpleType) object;
					IBinding binding = simpleType.resolveBinding();
					if (binding instanceof ITypeBinding) {
						ITypeBinding type = (ITypeBinding) binding;
						IPackageBinding packageBinding = type.getPackage();
						if (superClass.intendedName().equals(type.getName())
								&& superClass.packageName().equals(
										packageBinding.getName())) {
							found = true;
							break;
						}
					}
				}
			}
			if (!found) {
				status.add(new Status(IStatus.WARNING, status.getPlugin(), 1,
						pattern + ": The \"" + className
								+ "\" interface doesn't extend the \""
								+ superClass.intendedName() + "\" interface."
								+ "                             ", null));
			}
		}
	}

	// Checks the implemented interfaces
	private void checkImplementedInterfaces(TypeDeclaration javaClass,
			MultiStatus status, String pattern) {
		for (Iterator<JavaClass> iter = interfaces.iterator(); iter.hasNext();) {
			JavaClass interfaceType = iter.next();
			if (!interfaceType.optional()) {
				boolean found = false;
				for (Iterator ite2 = javaClass.superInterfaceTypes().iterator(); ite2
						.hasNext();) {
					Object object = ite2.next();
					if (object instanceof SimpleType) {
						SimpleType simpleType = (SimpleType) object;
						IBinding binding = simpleType.resolveBinding();
						if (binding instanceof ITypeBinding) {
							ITypeBinding type = (ITypeBinding) binding;
							IPackageBinding packageBinding = type.getPackage();
							if (interfaceType.intendedName().equals(
									type.getName())
									&& interfaceType.packageName().equals(
											packageBinding.getName())) {
								found = true;
								break;
							}
						}
					}
				}
				if (!found) {
					status.add(new Status(IStatus.WARNING, status.getPlugin(),
							1, pattern + ": The \"" + className
									+ "\" class doesn't implement the \""
									+ interfaceType.intendedName()
									+ "\" interface."
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

	/**
	 * Records the identfier for the archiMateTag
	 * 
	 * @param name
	 *            the identfier for the archiMateTag
	 */
	public void recordIdentifier(String identifier, String packageName,
			String tag) {
		if (this.archiMateTag().equals(tag)
				&& this.packageName.equals(packageName)) {
			recordeds.add(identifier);
		}
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
		// return if import is in same package
		if (samePackage(importName, packageName))
			return;
		if (!imports.contains(importName))
			imports.add(importName);
	}

	// takes the package from the import
	private boolean samePackage(String importName, String packageName) {
		String[] parts = importName.split("\\.");
		String out = "";
		for (String part : parts) {
			if (!part.equals(parts[parts.length - 1]))
				out += part;
			if (out.equals(packageName))
				return true;
			if (parts.length > 1 && !part.equals(parts[parts.length - 2]))
				out += ".";
		}
		return false;
	}

	/**
	 * Adds a collection of imports to the list of imports
	 * 
	 * @param imports
	 *            the collection of imports to add
	 */
	public void addImports(ArrayList<String> imports) {
		for (String importName : imports) {
			addImport(importName);
		}
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
	 * Returns the intended name of the class. When the class is optional and
	 * only one name was recorded, this is returned. Else the className derived
	 * from the UML diagram is returned.
	 * 
	 * @return the intended name
	 */
	public String intendedName() {
		if (optional && recordeds.size() == 1) {
			return recordeds.get(0);
		}
		return className;
	}

	/**
	 * Marks the class as an abstract class
	 */
	public void setAbstract(boolean value) {
		isAbstract = value;
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
	public void setSuperClass(JavaClass superClass) {
		this.superClass = superClass;
	}

	/**
	 * Returns whether the class has a superclass
	 * 
	 * @return Whether the class has a superclass
	 */
	public boolean hasSuperClass() {
		return superClass != null;
	}

	/**
	 * Returns the superclass
	 * 
	 * @return The superclass
	 */
	public JavaClass superClass() {
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
	public void addInterface(JavaClass interfaceName) {
		interfaces.add(interfaceName);
	}

	/**
	 * Adds a collection of interfaces to the list of interfaces
	 * 
	 * @param interfaces
	 *            the collection of interfaces to add
	 */
	public void addInterfaces(ArrayList<JavaClass> interfaces) {
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
	public ArrayList<JavaClass> interfaces() {
		return interfaces;
	}

	/**
	 * Returns the names of the implemented interfaces
	 * 
	 * @return the names of the implemented interfaces
	 */
	public ArrayList<String> interfaceNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (JavaClass interfaceClass : interfaces) {
			names.add(interfaceClass.className());
		}
		return names;
	}

	// Returns the specifications for debug purposes
	public String toString() {
		String out = "";
		out += packageName + "." + className + (visited ? " :: visited" : "")
				+ (optional ? " | optional -> " + intendedName() : "") + "\n";
		return out;
	}

}
