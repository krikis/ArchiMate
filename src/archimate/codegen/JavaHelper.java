package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.swt.widgets.DateTime;

public class JavaHelper {

	public static final String ARCHIMATETAG = "@archiMateTag";
	public static final String METHOD_IMPLEMENTATION = "method_implementation";
	public static final String METHOD_INVOCATION = "method_invocation";
	public static final String METHOD_DECLARATION = "method_declaration";		

	public JavaHelper() {

	}

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

	private boolean hasImport(CompilationUnit unit, String importName) {
		for (Iterator<ImportDeclaration> iter = unit.imports().iterator(); iter
				.hasNext();) {
			String name = iter.next().getName().getFullyQualifiedName();
			System.out.println(name);
			if (name.equals(importName)) {
				return true;
			}
		}
		return false;
	}

	public void addClass(CompilationUnit unit, IGenModel model,
			String archiMateTag) {
		AST ast = unit.getAST();
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		unit.setPackage(packageDeclaration);
		packageDeclaration
				.setName(ast.newName(model.packageName(archiMateTag)));

		addImports(unit, model.imports(archiMateTag));

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
	 * Adds a method that invokes another method
	 * 
	 * @param model
	 * @param node
	 * @param archiMateTag
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
	 * Adds a statement that creates an object
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

	public void addMethodInvocation(Block methodBlock, String objectName,
			String methodName, ArrayList<String> arglist) {
		AST ast = methodBlock.getAST();
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName(objectName));
		mi.setName(ast.newSimpleName(methodName));
		methodBlock.statements().add(ast.newExpressionStatement(mi));
	}

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

	public static String camelize(String name) {
		if (name.length() > 1) {
			return name.toLowerCase().substring(0, 1) + name.substring(1);
		}
		return name.toLowerCase();
	}

}
