package archimate.codegen;

import org.eclipse.jdt.core.dom.*;

public class JavaVisitor extends ASTVisitor {

	private JavaEdit edits;

	public JavaVisitor(JavaEdit edits) {
		super(true);
		this.edits = edits;
	}

	public void preVisit(ASTNode node) {
	}

	public boolean visit(TypeDeclaration node) {
		JavaHelper helper = new JavaHelper();
		String archiMateTag = helper.getArchiMateTag(node);
		return true;
	}
}
