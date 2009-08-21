package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import archimate.util.JavaClass;
import archimate.util.TagNode;
import archimate.util.TagTree;

/**
 * Interface defining the required methods for a Pattern specification
 * 
 * @author Samuel Esposito
 * 
 */
public interface ICodeGenerator {

	/**
	 * Returns the name of the pattern
	 * 
	 * @return The name of the pattern
	 */
	public String name();

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
	 * Returns the progressmonitor for the generator
	 * 
	 * @return The progressmonitor for the generator
	 */
	public IProgressMonitor monitor();

	/**
	 * Estimates the number of tasks to execute for generating code
	 * 
	 * @return The number of estimated tasks for generating code
	 */
	public int estimateTasks();

	/**
	 * Generates source code for the pattern
	 */
	public void generate_code(final IProgressMonitor monitor);

	/**
	 * Validates the source code in the workspace
	 */
	public void validate_code();

}
