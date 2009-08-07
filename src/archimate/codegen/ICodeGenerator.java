package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.util.TagTree;

public interface ICodeGenerator {

	/**
	 * Returns a tree containing all <code>archiMateTag</code>s for the key
	 * source elements
	 * 
	 * @return The <code>TagTree</code> of the current pattern
	 */
	public TagTree tree();

	/**
	 * Returns the model containing all settings for code generation
	 * 
	 * @return The <code>IGenModel</code> of the current pattern
	 */
	public IGenModel model();

	/**
	 * Generates source code for the pattern
	 */
	public void generate_code();
	
	/**
	 * Validates the source code in the workspace
	 */
	public void validate_code();
}
