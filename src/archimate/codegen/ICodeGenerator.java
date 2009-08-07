package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.util.TagTree;

public interface ICodeGenerator {
	// returns tree containing archimate tags for key code elements
	public TagTree tree();
	// returns model containing all settings for code generation
	public IGenModel model();
	// method invoked to generate code
	public void generate_code();	
}
