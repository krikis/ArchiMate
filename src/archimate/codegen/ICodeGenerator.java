package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.util.TagTree;

public interface ICodeGenerator {
	
	public TagTree tree();
	
	public Config config();
	
	public void generate_code();
	
	public void createSourceFiles(ArrayList<String> tags);
	
	public void addSourceElements(TypeDeclaration node, ArrayList<String> tags);
	
}
