package archimate.codegen;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * This class is used as selfdocumenting reference for using the AST API
 * 
 * @author Samuel Esposito
 */
public class TestAST {

	CompilationUnit unit;
	AST ast;

	public TestAST(CompilationUnit unit) {
		this.unit = unit;
		this.ast = unit.getAST();
	}

	public void test() {
		// Package statement
		// package astexplorer;

		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		unit.setPackage(packageDeclaration);
		packageDeclaration.setName(ast.newSimpleName("astexplorer"));

		// imports statements
		// import org.eclipse.swt.SWT;
		// import org.eclipse.swt.events.*;
		// import org.eclipse.swt.graphics.*;
		// import org.eclipse.swt.layout.*;
		// import org.eclipse.swt.widgets.*;

		for (int i = 0; i < IMPORTS.length; ++i) {
			ImportDeclaration importDeclaration = ast.newImportDeclaration();
			importDeclaration.setName(ast.newName(getSimpleNames(IMPORTS[i])));
			if (IMPORTS[i].indexOf("*") > 0)
				importDeclaration.setOnDemand(true);
			else
				importDeclaration.setOnDemand(false);

			unit.imports().add(importDeclaration);
		}

		// class declaration
		// public class SampleComposite extends Composite {

		TypeDeclaration classType = ast.newTypeDeclaration();
		classType.setInterface(false);
		setModifier(classType, Modifier.PUBLIC);
		classType.setName(ast.newSimpleName("SampleComposite"));
		classType.setSuperclassType(ast.newSimpleType(ast
				.newSimpleName("Composite")));
		unit.types().add(classType);

		// comments

		Javadoc jc = ast.newJavadoc();
		TagElement tag = ast.newTagElement();
		TextElement te = ast.newTextElement();
		tag.fragments().add(te);
		te.setText("Sample SWT Composite class created using the ASTParser");
		jc.tags().add(tag);
		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_AUTHOR);
		tag.fragments().add(ast.newSimpleName("Manoel"));
		tag.fragments().add(ast.newSimpleName("Marques"));
		jc.tags().add(tag);
		classType.setJavadoc(jc);

		// private class ControlAdapterImpl extends ControlAdapter {

		TypeDeclaration innerClassType = ast.newTypeDeclaration();
		innerClassType.setInterface(false);
		setModifier(innerClassType, Modifier.PRIVATE);
		innerClassType.setName(ast.newSimpleName("ControlAdapterImpl"));
		innerClassType.setSuperclassType(ast.newSimpleType(ast
				.newSimpleName("ControlAdapter")));
		classType.bodyDeclarations().add(innerClassType);
		populateInnerClass(innerClassType);

		// constructor declaration
		// public SampleComposite(Composite parent,int style) {

		MethodDeclaration methodConstructor = ast.newMethodDeclaration();
		methodConstructor.setConstructor(true);
		setModifier(methodConstructor, Modifier.PUBLIC);
		methodConstructor.setName(ast.newSimpleName("SampleComposite"));
		classType.bodyDeclarations().add(methodConstructor);

		// constructor parameters

		SingleVariableDeclaration variableDeclaration = ast
				.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast
				.newSimpleName("Composite")));
		variableDeclaration.setName(ast.newSimpleName("parent"));
		methodConstructor.parameters().add(variableDeclaration);

		variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newPrimitiveType(PrimitiveType.INT));
		variableDeclaration.setName(ast.newSimpleName("style"));
		methodConstructor.parameters().add(variableDeclaration);

		Block constructorBlock = ast.newBlock();
		methodConstructor.setBody(constructorBlock);

		// super invocation
		// super(parent,style);

		SuperConstructorInvocation superConstructorInvocation = ast
				.newSuperConstructorInvocation();
		constructorBlock.statements().add(superConstructorInvocation);
		Expression exp = ast.newSimpleName("parent");
		superConstructorInvocation.arguments().add(exp);
		superConstructorInvocation.arguments().add(ast.newSimpleName("style"));

		// GridLayout gridLayout = new GridLayout();

		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("gridLayout"));
		VariableDeclarationStatement vds = ast
				.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newSimpleType(ast.newSimpleName("GridLayout")));
		ClassInstanceCreation cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("GridLayout")));
		vdf.setInitializer(cc);
		constructorBlock.statements().add(vds);

		// setLayout(gridLayout);

		MethodInvocation mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("setLayout"));
		mi.arguments().add(ast.newSimpleName("gridLayout"));
		constructorBlock.statements().add(ast.newExpressionStatement(mi));

		// Label label = new Label(this,SWT.NONE);

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("label"));
		vds = ast.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Label")));
		constructorBlock.statements().add(vds);

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("Label")));
		vdf.setInitializer(cc);
		cc.arguments().add(ast.newThisExpression());
		cc.arguments().add(ast.newName(getSimpleNames("SWT.NONE")));

		// label.setText("Press the button to close:");

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("label"));
		mi.setName(ast.newSimpleName("setText"));
		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue("Press the button to close:");
		mi.arguments().add(sl);
		constructorBlock.statements().add(ast.newExpressionStatement(mi));

		// label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("label"));
		mi.setName(ast.newSimpleName("setLayoutData"));

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("GridData")));
		cc
				.arguments()
				.add(
						ast
								.newName(getSimpleNames("GridData.HORIZONTAL_ALIGN_CENTER")));
		mi.arguments().add(cc);
		constructorBlock.statements().add(ast.newExpressionStatement(mi));

		// Button button = new Button(this,SWT.PUSH);

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("button"));
		vds = ast.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Button")));
		constructorBlock.statements().add(vds);

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("Button")));
		vdf.setInitializer(cc);
		cc.arguments().add(ast.newThisExpression());
		cc.arguments().add(ast.newName(getSimpleNames("SWT.PUSH")));

		// button.setText("OK");

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("button"));
		mi.setName(ast.newSimpleName("setText"));
		sl = ast.newStringLiteral();
		sl.setLiteralValue("OK");
		mi.arguments().add(sl);
		constructorBlock.statements().add(ast.newExpressionStatement(mi));

		// button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("button"));
		mi.setName(ast.newSimpleName("setLayoutData"));

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("GridData")));
		cc
				.arguments()
				.add(
						ast
								.newName(getSimpleNames("GridData.HORIZONTAL_ALIGN_CENTER")));
		mi.arguments().add(cc);
		constructorBlock.statements().add(ast.newExpressionStatement(mi));

		// button.addSelectionListener(new SelectionAdapter() {});

		mi = ast.newMethodInvocation();
		constructorBlock.statements().add(ast.newExpressionStatement(mi));
		mi.setExpression(ast.newSimpleName("button"));
		mi.setName(ast.newSimpleName("addSelectionListener"));

		ClassInstanceCreation ci = ast.newClassInstanceCreation();
		ci.setType(ast.newSimpleType(ast.newSimpleName("SelectionAdapter")));
		mi.arguments().add(ci);
		AnonymousClassDeclaration cd = ast.newAnonymousClassDeclaration();
		ci.setAnonymousClassDeclaration(cd);

		// public void widgetSelected(SelectionEvent e) {

		MethodDeclaration md = ast.newMethodDeclaration();
		md.setConstructor(false);
		setModifier(md, Modifier.PUBLIC);
		md.setName(ast.newSimpleName("widgetSelected"));
		cd.bodyDeclarations().add(md);

		variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast
				.newSimpleName("SelectionEvent")));
		variableDeclaration.setName(ast.newSimpleName("e"));
		md.parameters().add(variableDeclaration);
		Block methodBlock = ast.newBlock();
		md.setBody(methodBlock);

		// Shell shell = ((Button)e.widget).getShell();

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("shell"));
		vds = ast.newVariableDeclarationStatement(vdf);
		methodBlock.statements().add(vds);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Shell")));

		CastExpression ce = ast.newCastExpression();
		ce.setType(ast.newSimpleType(ast.newSimpleName("Button")));
		FieldAccess fa = ast.newFieldAccess();
		ce.setExpression(fa);
		fa.setExpression(ast.newSimpleName("e"));
		fa.setName(ast.newSimpleName("widget"));

		ParenthesizedExpression pe = ast.newParenthesizedExpression();
		pe.setExpression(ce);
		mi = ast.newMethodInvocation();
		mi.setExpression(pe);
		mi.setName(ast.newSimpleName("getShell"));
		vdf.setInitializer(mi);

		// shell.close();

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("close"));
		methodBlock.statements().add(ast.newExpressionStatement(mi));

		// public static void main(String[] args) {

		md = ast.newMethodDeclaration();
		classType.bodyDeclarations().add(md);
		setModifier(md, Modifier.PUBLIC);
		setModifier(md, Modifier.STATIC);
		md.setName(ast.newSimpleName("main"));
		md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast
				.newSimpleName("String"))));
		variableDeclaration.setName(ast.newSimpleName("args"));
		md.parameters().add(variableDeclaration);
		methodBlock = ast.newBlock();
		md.setBody(methodBlock);

		// try

		TryStatement tryStatement = ast.newTryStatement();
		methodBlock.statements().add(tryStatement);
		Block tryBlock = ast.newBlock();
		tryStatement.setBody(tryBlock);

		// Display display = new Display();

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("display"));
		vds = ast.newVariableDeclarationStatement(vdf);
		tryBlock.statements().add(vds);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Display")));

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("Display")));
		vdf.setInitializer(cc);

		// Shell shell = new Shell(display);

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("shell"));
		vds = ast.newVariableDeclarationStatement(vdf);
		tryBlock.statements().add(vds);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Shell")));

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("Shell")));
		cc.arguments().add(ast.newSimpleName("display"));
		vdf.setInitializer(cc);

		// shell.setText("Sample Composite");

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("setText"));
		sl = ast.newStringLiteral();
		sl.setLiteralValue("Sample Composite");
		mi.arguments().add(sl);
		tryBlock.statements().add(ast.newExpressionStatement(mi));

		// shell.setLayout(new FillLayout());

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("setLayout"));
		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("FillLayout")));
		mi.arguments().add(cc);
		tryBlock.statements().add(ast.newExpressionStatement(mi));

		// SampleComposite sampleComposite = new
		// SampleComposite(shell,SWT.NONE);

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("sampleComposite"));
		vds = ast.newVariableDeclarationStatement(vdf);
		tryBlock.statements().add(vds);
		vds.setType(ast.newSimpleType(ast.newSimpleName("SampleComposite")));

		cc = ast.newClassInstanceCreation();
		cc.setType(ast.newSimpleType(ast.newSimpleName("SampleComposite")));
		cc.arguments().add(ast.newSimpleName("shell"));
		cc.arguments().add(ast.newName(getSimpleNames("SWT.NONE")));
		vdf.setInitializer(cc);

		// shell.pack();

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("pack"));
		tryBlock.statements().add(ast.newExpressionStatement(mi));

		// shell.addControlListener(sampleComposite.new
		// ControlAdapterImpl(shell.getSize()));

		mi = ast.newMethodInvocation();
		tryBlock.statements().add(ast.newExpressionStatement(mi));
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("addControlListener"));
		ci = ast.newClassInstanceCreation();
		mi.arguments().add(ci);
		cc.setType(ast.newSimpleType(ast.newSimpleName("ControlAdapterImpl")));
		ci.setExpression(ast.newSimpleName("sampleComposite"));
		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("getSize"));
		ci.arguments().add(mi);

		// shell.open();

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("open"));
		tryBlock.statements().add(ast.newExpressionStatement(mi));

		// while (!shell.isDisposed()) {

		WhileStatement ws = ast.newWhileStatement();
		tryBlock.statements().add(ws);
		Block whileBlock = ast.newBlock();
		ws.setBody(whileBlock);

		PrefixExpression prefix = ast.newPrefixExpression();
		ws.setExpression(prefix);
		prefix.setOperator(PrefixExpression.Operator.NOT);
		mi = ast.newMethodInvocation();
		prefix.setOperand(mi);
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("isDisposed"));

		// if (!display.readAndDispatch())

		IfStatement ifs = ast.newIfStatement();
		whileBlock.statements().add(ifs);
		prefix = ast.newPrefixExpression();
		prefix.setOperator(PrefixExpression.Operator.NOT);
		mi = ast.newMethodInvocation();
		prefix.setOperand(mi);
		ifs.setExpression(prefix);
		mi.setExpression(ast.newSimpleName("display"));
		mi.setName(ast.newSimpleName("readAndDispatch"));

		// display.sleep();

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("display"));
		mi.setName(ast.newSimpleName("sleep"));
		ifs.setThenStatement(ast.newExpressionStatement(mi));

		// display.dispose();

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("display"));
		mi.setName(ast.newSimpleName("dispose"));
		tryBlock.statements().add(ast.newExpressionStatement(mi));

		// catch (Exception e) {
		CatchClause catchClause = ast.newCatchClause();
		tryStatement.catchClauses().add(catchClause);
		variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast
				.newSimpleName(("Exception"))));
		variableDeclaration.setName(ast.newSimpleName("e"));
		catchClause.setException(variableDeclaration);
		Block catchBlock = ast.newBlock();
		catchClause.setBody(catchBlock);

		// e.printStackTrace();

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("e"));
		mi.setName(ast.newSimpleName("printStackTrace"));
		catchBlock.statements().add(ast.newExpressionStatement(mi));

	}

	private void populateInnerClass(TypeDeclaration classType) {

		AST ast = classType.getAST();

		// private Point minimumSize;

		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("minimumSize"));
		FieldDeclaration fd = ast.newFieldDeclaration(vdf);
		setModifier(fd, Modifier.PRIVATE);
		fd.setType(ast.newSimpleType(ast.newSimpleName("Point")));
		classType.bodyDeclarations().add(fd);

		// constructor declaration
		// ControlAdapterImpl(Point size) {

		MethodDeclaration methodConstructor = ast.newMethodDeclaration();
		methodConstructor.setConstructor(true);
		methodConstructor.setName(ast.newSimpleName("ControlAdapterImpl"));
		classType.bodyDeclarations().add(methodConstructor);

		// constructor parameter

		SingleVariableDeclaration variableDeclaration = ast
				.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast
				.newSimpleName("Point")));
		variableDeclaration.setName(ast.newSimpleName("size"));
		methodConstructor.parameters().add(variableDeclaration);

		Block constructorBlock = ast.newBlock();
		methodConstructor.setBody(constructorBlock);

		// this.minimumSize = size;

		Assignment a = ast.newAssignment();
		a.setOperator(Assignment.Operator.ASSIGN);
		constructorBlock.statements().add(ast.newExpressionStatement(a));

		FieldAccess fa = ast.newFieldAccess();
		fa.setExpression(ast.newThisExpression());
		fa.setName(ast.newSimpleName("minimumSize"));
		a.setLeftHandSide(fa);
		a.setRightHandSide(ast.newSimpleName("size"));

		// public void controlResized(ControlEvent e) {

		MethodDeclaration md = ast.newMethodDeclaration();
		md.setConstructor(false);
		setModifier(md, Modifier.PUBLIC);
		md.setName(ast.newSimpleName("controlResized"));
		classType.bodyDeclarations().add(md);

		variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast
				.newSimpleName("ControlEvent")));
		variableDeclaration.setName(ast.newSimpleName("e"));
		md.parameters().add(variableDeclaration);
		Block methodBlock = ast.newBlock();
		md.setBody(methodBlock);

		// Shell shell = (Shell)e.widget;

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("shell"));
		VariableDeclarationStatement vds = ast
				.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Shell")));
		methodBlock.statements().add(vds);

		CastExpression ce = ast.newCastExpression();
		ce.setType(ast.newSimpleType(ast.newSimpleName("Shell")));
		fa = ast.newFieldAccess();
		ce.setExpression(fa);
		fa.setExpression(ast.newSimpleName("e"));
		fa.setName(ast.newSimpleName("widget"));
		vdf.setInitializer(ce);

		// Point size = shell.getSize();

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("size"));
		vds = ast.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newSimpleType(ast.newSimpleName("Point")));
		methodBlock.statements().add(vds);

		MethodInvocation mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("getSize"));
		vdf.setInitializer(mi);

		// boolean change = false;

		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("change"));
		vds = ast.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newPrimitiveType(PrimitiveType.BOOLEAN));
		methodBlock.statements().add(vds);
		vdf.setInitializer(ast.newBooleanLiteral(false));

		// if (size.x < this.minimumSize.x) {

		IfStatement ifs = ast.newIfStatement();
		methodBlock.statements().add(ifs);
		InfixExpression ie = ast.newInfixExpression();
		ie.setOperator(InfixExpression.Operator.LESS);
		ifs.setExpression(ie);

		fa = ast.newFieldAccess();
		ie.setLeftOperand(fa);
		fa.setExpression(ast.newSimpleName("size"));
		fa.setName(ast.newSimpleName("x"));

		FieldAccess minimumSizeFa = ast.newFieldAccess();
		fa = ast.newFieldAccess();
		fa.setExpression(ast.newThisExpression());
		fa.setName(ast.newSimpleName("minimumSize"));
		minimumSizeFa.setExpression(fa);
		minimumSizeFa.setName(ast.newSimpleName("x"));
		ie.setRightOperand(minimumSizeFa);

		Block thenBlock = ast.newBlock();
		ifs.setThenStatement(thenBlock);

		// size.x = this.minimumSize.x;

		a = ast.newAssignment();
		a.setOperator(Assignment.Operator.ASSIGN);
		thenBlock.statements().add(ast.newExpressionStatement(a));
		fa = ast.newFieldAccess();
		fa.setExpression(ast.newSimpleName("size"));
		fa.setName(ast.newSimpleName("x"));
		a.setLeftHandSide(fa);
		minimumSizeFa = ast.newFieldAccess();
		fa = ast.newFieldAccess();
		fa.setExpression(ast.newThisExpression());
		fa.setName(ast.newSimpleName("minimumSize"));
		minimumSizeFa.setExpression(fa);
		minimumSizeFa.setName(ast.newSimpleName("x"));
		a.setRightHandSide(minimumSizeFa);

		// change = true;

		a = ast.newAssignment();
		a.setOperator(Assignment.Operator.ASSIGN);
		thenBlock.statements().add(ast.newExpressionStatement(a));
		a.setLeftHandSide(ast.newSimpleName("change"));
		a.setRightHandSide(ast.newBooleanLiteral(true));

		// if (size.y < this.minimumSize.y) {

		ifs = ast.newIfStatement();
		methodBlock.statements().add(ifs);
		ie = ast.newInfixExpression();
		ie.setOperator(InfixExpression.Operator.LESS);
		ifs.setExpression(ie);

		fa = ast.newFieldAccess();
		ie.setLeftOperand(fa);
		fa.setExpression(ast.newSimpleName("size"));
		fa.setName(ast.newSimpleName("y"));

		minimumSizeFa = ast.newFieldAccess();
		fa = ast.newFieldAccess();
		fa.setExpression(ast.newThisExpression());
		fa.setName(ast.newSimpleName("minimumSize"));
		minimumSizeFa.setExpression(fa);
		minimumSizeFa.setName(ast.newSimpleName("y"));
		ie.setRightOperand(minimumSizeFa);

		thenBlock = ast.newBlock();
		ifs.setThenStatement(thenBlock);

		// size.y = this.minimumSize.y;

		a = ast.newAssignment();
		a.setOperator(Assignment.Operator.ASSIGN);
		thenBlock.statements().add(ast.newExpressionStatement(a));
		fa = ast.newFieldAccess();
		fa.setExpression(ast.newSimpleName("size"));
		fa.setName(ast.newSimpleName("y"));
		a.setLeftHandSide(fa);
		minimumSizeFa = ast.newFieldAccess();
		fa = ast.newFieldAccess();
		fa.setExpression(ast.newThisExpression());
		fa.setName(ast.newSimpleName("minimumSize"));
		minimumSizeFa.setExpression(fa);
		minimumSizeFa.setName(ast.newSimpleName("y"));
		a.setRightHandSide(minimumSizeFa);

		// change = true;

		a = ast.newAssignment();
		a.setOperator(Assignment.Operator.ASSIGN);
		thenBlock.statements().add(ast.newExpressionStatement(a));
		a.setLeftHandSide(ast.newSimpleName("change"));
		a.setRightHandSide(ast.newBooleanLiteral(true));

		// if (change)

		ifs = ast.newIfStatement();
		methodBlock.statements().add(ifs);
		ifs.setExpression(ast.newSimpleName("change"));

		// shell.setSize(size);

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("shell"));
		mi.setName(ast.newSimpleName("setSize"));
		mi.arguments().add(ast.newSimpleName("size"));
		ifs.setThenStatement(ast.newExpressionStatement(mi));
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

	private final String[] IMPORTS = { "org.eclipse.swt.SWT",
			"org.eclipse.swt.events.*", "org.eclipse.swt.graphics.*",
			"org.eclipse.swt.layout.*", "org.eclipse.swt.widgets.*" };

	private void setModifier(BodyDeclaration classType, int modifier) {
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

}
