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

import archimate.util.JavaClass;
import archimate.util.JavaMethod;
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

	/**
	 * Creates a new {@link JavaHelper}
	 */
	public JavaHelper() {

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
		classType.setName(ast.newSimpleName(javaClass.className()));
		// add implemented interfaces
		if (!javaClass.isInterface()) {
			for (Iterator<String> iter = javaClass.interfaces().iterator(); iter
					.hasNext();) {
				classType.superInterfaceTypes().add(
						ast.newSimpleType(ast.newSimpleName(iter.next())));
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
	public void addMethods(TypeDeclaration node, TagNode tagnode,
			MultiStatus status) {
		for (Iterator<ICodeElement> iter = tagnode.source().iterator(); iter
				.hasNext();) {
			ICodeElement element = iter.next();
			if (!element.visited()) {
				if (element instanceof JavaMethod) {
					JavaMethod method = (JavaMethod) element;
					addMethod(node, method);
					createStatus(method, tagnode, status);
				}
			}
		}
	}

	// Adds a status with info to the multistatus object
	private void createStatus(JavaMethod method, TagNode tagnode,
			MultiStatus status) {
		String container = "";
		TagNode parent = tagnode.parent();
		if (parent.source().size() == 1) {
			ICodeElement parentElement = parent.source().get(0);
			if (parentElement instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) parentElement;
				container = " in the " + javaClass.className()
						+ (javaClass.isInterface() ? " interface" : " class");
			}
		}
		JavaClass javaClass = (JavaClass) parent.source().get(0);
		status.add(new Status(IStatus.INFO, status.getPlugin(), 1, "Method "
				+ method.type() + " added for the " + method.name() + " method"
				+ container + ".                          ", null));
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
				String objectClass = method.invocationClass();
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

}
