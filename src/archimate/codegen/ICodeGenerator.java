package archimate.codegen;

import javax.swing.ProgressMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;

import archimate.util.TagTree;

/**
 * Interface defining the required methods for a Pattern specification
 * 
 * @author Samuel Esposito
 */
public interface ICodeGenerator {

	/**
	 * Returns the name of the pattern
	 * 
	 * @return The name of the pattern
	 */
	public String name();

	/**
	 * Returns the package base of the pattern
	 * 
	 * @return The package base of the pattern
	 */
	public String packageBase();

	/**
	 * Returns a tree containing all <code>archiMateTag</code>s for the key
	 * source elements
	 * 
	 * @return The <code>TagTree</code> of the current pattern
	 */
	public TagTree tree();

	/**
	 * Returns the {@link ProgressMonitor} for the generator
	 * 
	 * @return The {@link ProgressMonitor} for the generator
	 */
	public IProgressMonitor monitor();

	/**
	 * Returns the {@link MultiStatus} for the generator
	 * 
	 * @return The {@link MultiStatus} for the generator
	 */
	public MultiStatus status();

	/**
	 * Estimates the number of tasks to execute for generating code
	 * 
	 * @return The number of estimated tasks for generating code
	 */
	public int estimateTasks();

	/**
	 * Generates source code for the pattern
	 */
	public void generate_code(final IProgressMonitor monitor, MultiStatus status);

	/**
	 * Validates the source code in the workspace
	 */
	public void validate_code(final IProgressMonitor monitor, MultiStatus status);

}
