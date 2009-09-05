package archimate.codegen;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;

import archimate.uml.UMLAdapter;
import archimate.util.SourceInspector;
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
	 * Returns the {@link UMLAdapter}
	 * 
	 * @return the {@link UMLAdapter}
	 */
	public UMLAdapter umlReader();

	/**
	 * Returns the {@link IProgressMonitor} for the generator
	 * 
	 * @return The {@link IProgressMonitor} for the generator
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
	 * @param mode
	 *            the mode in which the plugin will run, either
	 *            {@link SourceInspector#GENERATE},
	 *            {@link SourceInspector#VALIDATE} or
	 *            {@link SourceInspector#UPDATE}
	 * @return The number of estimated tasks for generating code
	 */
	public int estimateTasks(String mode);

	/**
	 * Generates source code for the pattern
	 * 
	 * @param monitor
	 *            the {@link IProgressMonitor} object
	 * @param status
	 *            the {@link MultiStatus} object
	 */
	public void generate_code(final IProgressMonitor monitor, MultiStatus status);

	/**
	 * Validates the source code in the workspace
	 * 
	 * @param monitor
	 *            the {@link IProgressMonitor} object
	 * @param status
	 *            the {@link MultiStatus} object
	 */
	public void validate_code(final IProgressMonitor monitor, MultiStatus status);

	/**
	 * Updates the currently selecte UML model
	 * 
	 * @param monitor
	 *            the {@link IProgressMonitor} object
	 * @param status
	 *            the {@link MultiStatus} object
	 */
	public void update_model(final IProgressMonitor monitor, MultiStatus status);

}
