package archimate.codegen;

import org.eclipse.jdt.core.dom.*;

public class JavaVisitor extends ASTVisitor {
	
	private JavaEdit edits;
	
	public JavaVisitor(JavaEdit edits){
		super(true);
		this.edits = edits;
	}
	
	public void preVisit(ASTNode node) {		
//		System.out.println(node);
	}
	
	public boolean visit(TypeDeclaration node) {
		JavaHelper helper = new JavaHelper();
		String archiMateTag = helper.getArchiMateTag(node);
		System.out.println(archiMateTag);
		AST ast = node.getAST(); 
		
		// private Point minimumSize;
		
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("minimumSize"));
		FieldDeclaration fd = ast.newFieldDeclaration(vdf);
		setModifier(ast, fd, Modifier.PRIVATE);
		fd.setType(ast.newSimpleType(ast.newSimpleName("Point")));
		node.bodyDeclarations().add(fd); 
		
		// constructor declaration
		// ControlAdapterImpl(Point size) {
		
		MethodDeclaration methodConstructor = ast.newMethodDeclaration();
		methodConstructor.setConstructor(true);
		methodConstructor.setName(ast.newSimpleName("ControlAdapterImpl"));
		node.bodyDeclarations().add(methodConstructor);
		
		// constructor parameter
		
		SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast.newSimpleName("Point")));
		variableDeclaration.setName(ast.newSimpleName("size"));
		methodConstructor.parameters().add(variableDeclaration);
			
		Block constructorBlock = ast.newBlock();
		methodConstructor.setBody(constructorBlock);
		
		//	this.minimumSize = size;
		
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
		setModifier(ast, md, Modifier.PUBLIC);
		md.setName(ast.newSimpleName("controlResized"));
		node.bodyDeclarations().add(md);
		
		variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast.newSimpleName("ControlEvent")));
		variableDeclaration.setName(ast.newSimpleName("e"));
		md.parameters().add(variableDeclaration);
		Block methodBlock = ast.newBlock();
		md.setBody(methodBlock);
		
		// Shell shell = (Shell)e.widget;
		
		vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("shell"));
		VariableDeclarationStatement vds = ast.newVariableDeclarationStatement(vdf);
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
		return false;
	}
	
	private void setModifier(AST ast, BodyDeclaration classType, int modifier){
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
