package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.swt.widgets.DateTime;

import archimate.uml.UMLAdapter;
import archimate.util.InterfaceType;
import archimate.util.JavaClass;
import archimate.util.JavaMethod;
import archimate.util.Restriction;
import archimate.util.TagNode;

/**
 * This utility class provides methods for accessing and editing source code
 * parsed by the {@link ASTParser}
 * 
 * @author Samuel Esposito
 * 
 */
public class JavaHelper {

	/**
	 * TagName for the archiMateTag
	 */
	public static final String ARCHIMATETAG = "@archiMateTag";
	/**
	 * Constant defining a method implementation in a class
	 */
	public static final String METHOD_IMPLEMENTATION = "method_implementation";
	/**
	 * Constant defining a method invocation in a class
	 */
	public static final String METHOD_INVOCATION = "method_invocation";
	/**
	 * Constant defining a method declaration in an interface
	 */
	public static final String METHOD_DECLARATION = "method_declaration";
	// The current pattern
	private String pattern;
	// The status
	private MultiStatus status;

	/**
	 * Creates a new {@link JavaHelper}
	 * 
	 * @param status
	 *            the {@link MultiStatus}
	 * @param currentPattern
	 *            the pattern currently processed
	 */
	public JavaHelper(MultiStatus status, String currentPattern) {
		this.status = status;
		pattern = currentPattern;
	}

	/**
	 * Searches for an archiMateTag in the javadoc of the given node.
	 * 
	 * @param node
	 *            {@link ASTNode} to be searched
	 * @return The found archiMateTag
	 */
	public String getArchiMateTag(BodyDeclaration node) {
		if (node.getJavadoc() != null) {
			List<TagElement> tags = node.getJavadoc().tags();
			for (Iterator<TagElement> iter = tags.iterator(); iter.hasNext();) {
				TagElement tag = iter.next();
				if (tag.getTagName() != null
						&& tag.getTagName().equals(ARCHIMATETAG)) {
					List<TextElement> fragments = tag.fragments();
					String archiMateTag = "";
					for (Iterator<TextElement> ite2 = fragments.iterator(); ite2
							.hasNext();) {
						TextElement name = ite2.next();
						if (name.getText().length() > 0)
							archiMateTag += name.getText().substring(1);
					}
					return archiMateTag;
				}
			}
		}
		return "";
	}

	/**
	 * Returns the name of the {@link TypeDeclaration}
	 * 
	 * @param node
	 *            a {@link TypeDeclaration}
	 * @return The name of the {@link TypeDeclaration}
	 */
	public String getName(TypeDeclaration node) {
		return node.getName().getIdentifier();
	}

	/**
	 * Returns the name of the {@link MethodDeclaration}
	 * 
	 * @param node
	 *            a {@link MethodDeclaration}
	 * @return The name of the {@link MethodDeclaration}
	 */
	public String getName(MethodDeclaration node) {
		return node.getName().getIdentifier();
	}

	/**
	 * Adds the given imports to the given {@link CompilationUnit} if it doesn't
	 * already contain them.
	 * 
	 * @param unit
	 *            The {@link CompilationUnit} to add the imports to.
	 * @param imports
	 *            A list of import names to be added
	 */
	public void addImports(CompilationUnit unit, ArrayList<String> imports) {
		AST ast = unit.getAST();
		for (Iterator<String> iter = imports.iterator(); iter.hasNext();) {
			String importName = iter.next();
			if (!hasImport(unit, importName)) {
				ImportDeclaration importDeclaration = ast
						.newImportDeclaration();
				importDeclaration.setName(ast
						.newName(getSimpleNames(importName)));
				if (importName.indexOf("*") > 0)
					importDeclaration.setOnDemand(true);
				else
					importDeclaration.setOnDemand(false);
				unit.imports().add(importDeclaration);
			}
		}
	}

	// Checks whether the unit has already got an import
	private boolean hasImport(CompilationUnit unit, String importName) {
		for (Iterator<ImportDeclaration> iter = unit.imports().iterator(); iter
				.hasNext();) {
			ImportDeclaration importDeclaration = iter.next();
			String name = importDeclaration.getName().getFullyQualifiedName();
			if (importDeclaration.isOnDemand()) {
				name += ".*";
			}
			if (name.equals(importName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a class declaration to the {@link CompilationUnit}.
	 * 
	 * @param unit
	 *            The {@link CompilationUnit} to add the class declaration to
	 * @param javaClass
	 *            The {@link JavaClass} defining the class to create
	 */
	public void addClass(CompilationUnit unit, JavaClass javaClass) {
		// add package declaration
		AST ast = unit.getAST();
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		unit.setPackage(packageDeclaration);
		packageDeclaration.setName(ast.newName(javaClass.packageName()));
		// add imports
		addImports(unit, javaClass.imports());
		// add class declaration
		TypeDeclaration classType = ast.newTypeDeclaration();
		classType.setInterface(javaClass.isInterface());
		setModifier(ast, classType, Modifier.PUBLIC);
		if (javaClass.isAbstract())
			setModifier(ast, classType, Modifier.ABSTRACT);
		classType.setName(ast.newSimpleName(javaClass.className()));
		// add superclass
		if (javaClass.hasSuperClass()) {
			classType.setSuperclassType(ast.newSimpleType(ast
					.newSimpleName(javaClass.superClass().className())));
		}
		// add implemented interfaces
		if (!javaClass.isInterface()) {
			for (Iterator<InterfaceType> iter = javaClass.interfaces()
					.iterator(); iter.hasNext();) {
				classType.superInterfaceTypes().add(
						ast.newSimpleType(ast.newSimpleName(iter.next()
								.interfaceName())));
			}
		}
		unit.types().add(classType);
		// add javadoc
		addJavaDoc(classType, javaClass);
	}

	/**
	 * Adds methods defined by the {@link TagNode}s {@link ICodeElement}s to the
	 * {@link TypeDeclaration} node
	 * 
	 * @param node
	 *            {@link TypeDeclaration} node to add the methods to
	 * @param tagnode
	 *            {@link TagNode} with a list of {@link ICodeElement}s
	 */
	public void addMethods(TypeDeclaration node, ICodeElement code,
			TagNode tagnode) {
		for (Iterator<ICodeElement> iter = tagnode.source().iterator(); iter
				.hasNext();) {
			ICodeElement element = iter.next();
			if (!element.visited()) {
				if (element instanceof JavaMethod
						&& code.children().contains(element)) {
					JavaMethod method = (JavaMethod) element;
					addMethod(node, method);
					createStatus(method, tagnode);
				}
			}
		}
	}

	// Adds a status with info to the multistatus object
	private void createStatus(JavaMethod method, TagNode tagnode) {
		String container = "";
		TagNode parent = tagnode.parent();
		if (parent.source().size() == 1) {
			ICodeElement parentElement = parent.source().get(0);
			if (parentElement instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) parentElement;
				container = " in the \"" + javaClass.className() + "\" "
						+ (javaClass.isInterface() ? "interface" : "class");
			}
		}
		status.add(new Status(IStatus.INFO, status.getPlugin(), 1, pattern
				+ ": Method " + method.type() + " added for the \""
				+ method.name() + "()\" method" + container
				+ ".                          ", null));
	}

	/**
	 * Adds a method to a {@link TypeDeclaration} based on the settings of the
	 * {@link JavaMethod}. If the {@link JavaMethod}s type is a
	 * {@link JavaMethod#DECLARATION}, a method declaration is added. If the
	 * {@link JavaMethod}s type is a {@link JavaMethod#IMPLEMENTATION}, a method
	 * with a method block is added. If the {@link JavaMethod}s type is a
	 * {@link JavaMethod#INVOCATION}, a method with a method invocation in its
	 * block is added.
	 * 
	 * @param node
	 *            {@link TypeDeclaration} node to add the methods to
	 * @param method
	 *            {@link JavaMethod} object containing all the settings for the
	 *            new method
	 */
	public void addMethod(TypeDeclaration node, JavaMethod method) {
		AST ast = node.getAST();
		// Add the method declaration
		MethodDeclaration md;
		md = ast.newMethodDeclaration();
		md.setConstructor(false);
		setModifier(ast, md, Modifier.PUBLIC);
		if (method.type().equals(JavaMethod.INVOCATION)) {
			md.setName(ast.newSimpleName(method.invocationMethod()));
		} else {
			md.setName(ast.newSimpleName(method.name()));
		}
		node.bodyDeclarations().add(md);
		// Add method block
		if (!method.type().equals(JavaMethod.DECLARATION)) {
			Block methodBlock = ast.newBlock();
			md.setBody(methodBlock);
			// Add method invocation
			if (method.type().equals(JavaMethod.INVOCATION)) {
				String objectClass = method.className();
				String objectName = method.invocationObject();
				addObject(methodBlock, objectClass, objectName,
						new ArrayList<String>());
				addMethodInvocation(methodBlock, objectName, method.name(),
						new ArrayList<String>());
			}
		}
		// Add the JavaDoc
		addJavaDoc(md, method);
	}

	/**
	 * Adds JavaDoc to a {@link BodyDeclaration} based on the settings of the
	 * {@link ICodeElement}
	 * 
	 * @param body
	 *            the {@link BodyDeclaration} to add the JavaDoc to
	 * @param element
	 *            the {@link ICodeElement} to derive the documentation from
	 */
	public void addJavaDoc(BodyDeclaration body, ICodeElement element) {
		AST ast = body.getAST();
		Javadoc jc = ast.newJavadoc();
		TagElement tag;
		TextElement te;
		if (element.commentDefined()) {
			tag = ast.newTagElement();
			te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(element.comment());
			jc.tags().add(tag);
			tag = ast.newTagElement();
			te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText("");
			jc.tags().add(tag);
		}
		if (element.archiMateTagsDefined()) {
			tag = ast.newTagElement();
			te = ast.newTextElement();
			tag.setTagName(ARCHIMATETAG);
			te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(element.archiMateTag());
			jc.tags().add(tag);
		}
		body.setJavadoc(jc);
	}

	/**
	 * Adds a statement that creates an object. For instance: {@code Object
	 * object = new Object(params)}
	 * 
	 * @param methodBlock
	 *            the methodblock to add the object to
	 * @param type
	 *            the type of the object
	 * @param name
	 *            the name of the object
	 * @param arglist
	 *            the arguments for the object instantiation
	 */
	public void addObject(Block methodBlock, String type, String name,
			ArrayList<String> arglist) {
		AST ast = methodBlock.getAST();
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(name));
		VariableDeclarationStatement vds = ast
				.newVariableDeclarationStatement(vdf);
		methodBlock.statements().add(vds);
		vds.setType(ast.newSimpleType(ast.newSimpleName(type)));

		ClassInstanceCreation cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName(type)));
		for (Iterator<String> iter = arglist.iterator(); iter.hasNext();) {
			cc.arguments().add(ast.newSimpleName(iter.next()));
		}
		vdf.setInitializer(cc);
	}

	/**
	 * Adds a method invocation to the method block
	 * 
	 * @param methodBlock
	 *            The {@link Block} to add the invocation to
	 * @param objectName
	 *            The name of the object on which to invoke the method
	 * @param methodName
	 *            The name of the method to invoke
	 * @param arglist
	 *            The list of arguments for the method call
	 */
	public void addMethodInvocation(Block methodBlock, String objectName,
			String methodName, ArrayList<String> arglist) {
		AST ast = methodBlock.getAST();
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName(objectName));
		mi.setName(ast.newSimpleName(methodName));
		methodBlock.statements().add(ast.newExpressionStatement(mi));
	}

	// helper method for setting a BodyDeclaration modifier
	private void setModifier(AST ast, BodyDeclaration classType, int modifier) {
		switch (modifier) {
		case Modifier.PUBLIC:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			break;
		case Modifier.PRIVATE:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
			break;
		case Modifier.STATIC:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
			break;
		case Modifier.ABSTRACT:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
			break;
		}
	}

	// helper method for adding dot-separated names
	private String[] getSimpleNames(String qualifiedName) {
		StringTokenizer st = new StringTokenizer(qualifiedName, ".");
		ArrayList list = new ArrayList();
		while (st.hasMoreTokens()) {
			String name = st.nextToken().trim();
			if (!name.equals("*"))
				list.add(name);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Camelizes a name
	 * 
	 * @param name
	 *            The name to camelize
	 * @return The camelized name
	 */
	public static String camelize(String name) {
		if (name.length() > 1) {
			return name.toLowerCase().substring(0, 1) + name.substring(1);
		}
		return name.toLowerCase();
	}

	/**
	 * Compares the {@link TagNode}s source elements with the given
	 * {@link TypeDeclaration} and sets the {@link MultiStatus} according to the
	 * result
	 * 
	 * @param node
	 *            the {@link TypeDeclaration} to compare with
	 * @param tagnode
	 *            the given {@link TagNode}
	 */
	public void compare(TypeDeclaration node, TagNode tagnode) {
		ICodeElement element = tagnode.getSource(getName(node));
		// A matching element is found and will be checked for differences
		if (element != null) {
			tagnode.setVisited(element);
			element.diff(node, status, pattern);
		}
	}

	/**
	 * Compares the {@link TagNode}s source elements with the given
	 * {@link MethodDeclaration} and sets the {@link MultiStatus} according to
	 * the result
	 * 
	 * @param node
	 *            the {@link MethodDeclaration} to compare with
	 * @param tagnode
	 *            the given {@link TagNode}
	 */
	public void compare(MethodDeclaration node, TagNode tagnode) {
		ICodeElement element = tagnode.getSource(getName(node));
		// A matching element is found and will be checked for differences
		if (element != null) {
			tagnode.setVisited(element);
			element.diff(node, status, pattern);
		}
	}

	/**
	 * Checks whether the {@link TypeDeclaration} is a violation of an interface
	 * restriction
	 * 
	 * @param node
	 *            the {@link TypeDeclaration} to analyze
	 * @param tagnode
	 *            the current {@link TagNode}
	 * @param interfaces
	 *            the restricted interfaces to trace
	 */
	public void checkRestricted(TypeDeclaration node, TagNode tagnode,
			ArrayList<Restriction> interfaces) {
		if (!node.isInterface()) {
			for (Iterator iter = node.superInterfaceTypes().iterator(); iter
					.hasNext();) {
				Object object = iter.next();
				if (object instanceof SimpleType) {
					SimpleType simpleType = (SimpleType) object;
					IBinding binding = simpleType.resolveBinding();
					if (binding instanceof ITypeBinding) {
						ITypeBinding type = (ITypeBinding) binding;
						IPackageBinding packageBinding = type.getPackage();
						checkRestricted(node, tagnode, interfaces, type
								.getName(), packageBinding.getName());
					}
				}
			}
		}
	}

	// Checks whether the interface implementation was intended
	private void checkRestricted(TypeDeclaration node, TagNode tagnode,
			ArrayList<Restriction> interfaces, String interfaceName,
			String packageName) {
		for (Iterator<Restriction> iter = interfaces.iterator(); iter.hasNext();) {
			Restriction interfaceRest = iter.next();
			if (interfaceRest.name().equals(interfaceName)
					&& interfaceRest.packageName().equals(packageName)) {
				checkRestricted(node, tagnode, interfaceName, packageName);
			}
		}
	}

	// Checks whether the interface implementation was intended
	private void checkRestricted(TypeDeclaration node, TagNode tagnode,
			String interfaceName, String packageName) {
		boolean found = false;
		// Check whether the interface implementation was intended
		for (Iterator<ICodeElement> iter = tagnode.source().iterator(); iter
				.hasNext();) {
			ICodeElement element = iter.next();
			if (element instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) element;
				for (Iterator<InterfaceType> ite2 = javaClass.interfaces()
						.iterator(); ite2.hasNext();) {
					InterfaceType interfaceImpl = ite2.next();
					if (interfaceImpl.interfaceName().equals(interfaceName)
							&& interfaceImpl.packageName().equals(packageName)) {
						found = true;
					}
				}
			}
		}
		// The interface implementation was not intended
		if (!found) {
			reportError(node, interfaceName);
		}
	}

	// Reports the violation of an interface implementation
	private void reportError(TypeDeclaration node, String interfaceName) {
		status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, pattern
				+ ": The implementation of the \"" + interfaceName
				+ "\" interface is not allowed in the class \"" + getName(node)
				+ "\"." + "                             ", null));
	}

	/**
	 * Checks whether the {@link MethodInvocation} is a violation of a method
	 * restriction
	 * 
	 * @param node
	 *            the {@link MethodInvocation} to analyze
	 * @param tagnode
	 *            the current {@link TagNode}
	 * @param methods
	 *            the restricted methods to trace
	 */
	public void checkRestricted(MethodInvocation node, TagNode tagnode,
			ArrayList<Restriction> methods) {
		for (Iterator<Restriction> iter = methods.iterator(); iter.hasNext();) {
			Restriction method = iter.next();
			if (node.getName().getFullyQualifiedName().equals(method.name())) {
				ITypeBinding type = null;
				// the method is invoked on a variable
				if (node.getExpression() instanceof SimpleName) {
					SimpleName simpleName = (SimpleName) node.getExpression();
					IBinding binding = simpleName.resolveBinding();
					if (binding instanceof IVariableBinding) {
						IVariableBinding variable = (IVariableBinding) binding;
						type = variable.getType();
					}
				}
				// the method is invoked on an instancecreation
				if (node.getExpression() instanceof ClassInstanceCreation) {
					ClassInstanceCreation instance = (ClassInstanceCreation) node
							.getExpression();
					IBinding binding = instance.getType().resolveBinding();
					if (binding instanceof ITypeBinding) {
						type = (ITypeBinding) binding;
					}
				}
				// check the type of the object on which the method is invoked
				if (type != null) {
					IPackageBinding packageBinding = type.getPackage();
					if (method.type().equals(type.getName())
							&& method.packageName().equals(
									packageBinding.getName())) {
						checkRestricted(node, tagnode, method);
					}
				}
			}
		}
	}

	// Checks whether the method invocation was intended by the pattern
	private void checkRestricted(MethodInvocation node, TagNode tagnode,
			Restriction method) {
		boolean found = false;
		// Check whether the invocation was intended
		for (Iterator<ICodeElement> iter = tagnode.source().iterator(); iter
				.hasNext();) {
			ICodeElement element = iter.next();
			if (element instanceof JavaMethod) {
				JavaMethod javaMethod = (JavaMethod) element;
				if (javaMethod.name().equals(method.name())
						&& javaMethod.packageName()
								.equals(method.packageName())) {
					found = true;
				}
			}
		}
		// The invocation was not intended
		if (!found) {
			reportError(node, method);
		}
	}

	// Reports the violation of a method invocation
	private void reportError(MethodInvocation node, Restriction method) {
		String container = "";
		ASTNode parent = node;
		// Get the containing class
		while (parent.getParent() != null) {
			parent = parent.getParent();
			if (parent instanceof TypeDeclaration) {
				container = " in the class \""
						+ getName((TypeDeclaration) parent) + "\"";
				break;
			}
		}
		// Report the violation
		status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, pattern
				+ ": The invocation of the method \"" + method.name()
				+ "()\" is not allowed" + container + "."
				+ "                             ", null));
	}

	/**
	 * Searches for methods which are not yet represented in the UML model
	 * 
	 * @param node
	 *            TODO
	 * @param self
	 *            the {@link MethodDeclaration}
	 * @param umlReader
	 *            the {@link UMLAdapter}
	 */
	public void findNewMethod(MethodDeclaration node, TagNode self,
			UMLAdapter umlReader) {
		// Checks whether the method is a declaration
		String archiMateTag = "";
		for (Iterator<ICodeElement> iter = self.source().iterator(); iter
				.hasNext();) {
			ICodeElement element = iter.next();
			if (element instanceof JavaClass
					&& ((JavaClass) element).isInterface()) {
				archiMateTag = self.tag();
			}
		}
		// Tries to match the method with a UML element
		if (!archiMateTag.equals("")) {
			String name = node.getName().getFullyQualifiedName();
			boolean match = false;
			for (Iterator<TagNode> iter = self.children().iterator(); iter
					.hasNext();) {
				for (Iterator<ICodeElement> ite2 = iter.next().source()
						.iterator(); ite2.hasNext();) {
					ICodeElement element = ite2.next();
					if (element instanceof JavaMethod) {
						JavaMethod method = (JavaMethod) element;
						if (name.equals(method.name())) {
							match = true;
							break;
						}
					}
				}
			}
			// Adds the method to the UML model
			if (!match) {
				// TODO complete implementation
				// String tag = umlReader.addMessage(archiMateTag, name);
				// addArchimateTag(node, tag);
				// reportAddedMessage(name);
			}
		}
	}

	private void reportAddedMessage(String name) {
		// TODO Auto-generated method stub

	}

	private void addArchimateTag(MethodDeclaration node, String archiMateTag) {
		if (getArchiMateTag(node).equals("")) {
			AST ast = node.getAST();
			Javadoc javadoc = node.getJavadoc();
			if (javadoc == null) {
				javadoc = ast.newJavadoc();
			}
			TagElement tag = ast.newTagElement();
			TextElement te = ast.newTextElement();
			tag.setTagName(ARCHIMATETAG);
			te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(archiMateTag);
			javadoc.tags().add(tag);
			node.setJavadoc(javadoc);
		}
	}
}
