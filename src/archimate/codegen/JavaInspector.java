package archimate.codegen;

import org.eclipse.jdt.core.dom.*;

public class JavaInspector extends ASTVisitor {
	
	private JavaState state;
	private JavaHelper helper;
	
	public JavaInspector(JavaState state){
		super(true);
		this.state = state;
		helper = new JavaHelper();
	}
	
	public void preVisit(ASTNode node) {	
	}
	
	public boolean visit(TypeDeclaration node) {
		String archiMateTag = helper.getArchiMateTag(node);
		state.addTag(archiMateTag);
		return true;
	}
}
