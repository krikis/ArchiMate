package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.swt.widgets.DateTime;

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
	 * @param model
	 *            The {@link IGenModel} containing all settings for the
	 *            generated source code
	 * @param archiMateTag
	 *            The archiMateTag identifying the desired class
	 */
	public void addClass(CompilationUnit unit, IGenModel model,
			String archiMateTag) {
		// add package declaration
		AST ast = unit.getAST();
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		unit.setPackage(packageDeclaration);
		packageDeclaration
				.setName(ast.newName(model.packageName(archiMateTag)));
		// add imports
		addImports(unit, model.imports(archiMateTag));
		// add class declaration
		TypeDeclaration classType = ast.newTypeDeclaration();
		classType.setInterface(model.isInterface(archiMateTag));
		setModifier(ast, classType, Modifier.PUBLIC);
		classType.setName(ast.newSimpleName(model.className(archiMateTag)));
		for (Iterator<String> iter = model.interfaces(archiMateTag).iterator(); iter
				.hasNext();) {
			classType.superInterfaceTypes().add(
					ast.newSimpleType(ast.newSimpleName(iter.next())));
		}
		unit.types().add(classType);
		// add javadoc
		Javadoc jc = ast.newJavadoc();
		TagElement tag = ast.newTagElement();
		TextElement te = ast.newTextElement();
		tag.fragments().add(te);
		te.setText(model.classComment(archiMateTag));
		jc.tags().add(tag);
		tag = ast.newTagElement();
		te = ast.newTextElement();
		tag.fragments().add(te);
		te.setText("");
		jc.tags().add(tag);
		tag = ast.newTagElement();
		tag.setTagName(ARCHIMATETAG);
		te = ast.newTextElement();
		tag.fragments().add(te);
		te.setText(archiMateTag);
		jc.tags().add(tag);
		classType.setJavadoc(jc);
	}

	/**
	 * Adds methods identified by the archiMateTag to the
	 * {@link TypeDeclaration} node using the {@link IGenModel}s settings
	 * 
	 * @param model
	 *            IGenModel specifying all settings for code generation
	 * @param node
	 *            {@link TypeDeclaration} node to add the methods to
	 * @param archiMateTag
	 *            archiMateTag identifying the methods to add
	 */
	public void addMethods(IGenModel model, TypeDeclaration node,
			String archiMateTag) {
		ArrayList<String> methods = model.methods(archiMateTag);
		AST ast = node.getAST();
		MethodDeclaration md;
		for (Iterator<String> iter = methods.iterator(); iter.hasNext();) {
			md = ast.newMethodDeclaration();
			md.setConstructor(false);
			setModifier(ast, md, Modifier.PUBLIC);
			md.setName(ast.newSimpleName(iter.next()));
			node.bodyDeclarations().add(md);
			Block methodBlock = ast.newBlock();
			md.setBody(methodBlock);
			Javadoc jc = ast.newJavadoc();
			TagElement tag = ast.newTagElement();
			tag.setTagName(ARCHIMATETAG);
			TextElement te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(archiMateTag);
			jc.tags().add(tag);
			md.setJavadoc(jc);
		}
	}

	/**
	 * Adds method declarations identified by the archiMateTag to the
	 * {@link TypeDeclaration} node using the {@link IGenModel}s settings
	 * 
	 * @param model
	 *            IGenModel specifying all settings for code generation
	 * @param node
	 *            {@link TypeDeclaration} node to add the method declarations to
	 * @param archiMateTag
	 *            archiMateTag identifying the method declarations to add
	 */
	public void addMethodDeclarations(IGenModel model, TypeDeclaration node,
			String archiMateTag) {
		ArrayList<String> methods = model.methods(archiMateTag);
		AST ast = node.getAST();
		MethodDeclaration md;
		for (Iterator<String> iter = methods.iterator(); iter.hasNext();) {
			md = ast.newMethodDeclaration();
			md.setConstructor(false);
			setModifier(ast, md, Modifier.PUBLIC);
			md.setName(ast.newSimpleName(iter.next()));
			node.bodyDeclarations().add(md);
			Javadoc jc = ast.newJavadoc();
			TagElement tag = ast.newTagElement();
			tag.setTagName(ARCHIMATETAG);
			TextElement te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(archiMateTag);
			jc.tags().add(tag);
			md.setJavadoc(jc);
		}
	}

	/**
	 * Adds method invocations identified by the archiMateTag to the
	 * {@link TypeDeclaration} node using the {@link IGenModel}s settings
	 * 
	 * @param model
	 *            IGenModel specifying all settings for code generation
	 * @param node
	 *            {@link TypeDeclaration} node to add the method invocations to
	 * @param archiMateTag
	 *            archiMateTag identifying the method invocations to add
	 */
	public void addMethodInvocations(IGenModel model, TypeDeclaration node,
			String archiMateTag) {
		CompilationUnit unit = (CompilationUnit) node.getRoot();
		addImports(unit, model.imports(archiMateTag));
		ArrayList<String> methods = model.methods(archiMateTag);
		ArrayList<String> methodInvocations = model
				.methodInvocations(archiMateTag);
		Iterator<String> iter = methodInvocations.iterator();
		AST ast = node.getAST();
		MethodDeclaration md;
		for (Iterator<String> iter2 = methods.iterator(); iter2.hasNext();) {
			md = ast.newMethodDeclaration();
			md.setConstructor(false);
			setModifier(ast, md, Modifier.PUBLIC);
			md.setName(ast.newSimpleName(iter2.next()));
			node.bodyDeclarations().add(md);
			Block methodBlock = ast.newBlock();
			md.setBody(methodBlock);
			String objectClass = model.objectClass(archiMateTag);
			String objectName = model.objectName(archiMateTag);
			addObject(methodBlock, objectClass, objectName,
					new ArrayList<String>());
			addMethodInvocation(methodBlock, objectName, iter.next(),
					new ArrayList<String>());
			Javadoc jc = ast.newJavadoc();
			TagElement tag = ast.newTagElement();
			tag.setTagName(ARCHIMATETAG);
			TextElement te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(archiMateTag);
			jc.tags().add(tag);
			md.setJavadoc(jc);
		}
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

	/**
	 * Adds a method to a {@link TypeDeclaration} node
	 * 
	 * @param node
	 *            The {@link TypeDeclaration} node to add the method to
	 * @param name
	 *            The name of the method
	 * @param tag
	 *            The archiMateTag identifying the method
	 */
	public void addMethod(TypeDeclaration node, String name, String tag) {
		AST ast = node.getAST();
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setConstructor(false);
		setModifier(ast, md, Modifier.PUBLIC);
		md.setName(ast.newSimpleName("getData"));
		node.bodyDeclarations().add(md);
		Block methodBlock = ast.newBlock();
		md.setBody(methodBlock);
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
