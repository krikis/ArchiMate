package archimate.patterns;

import org.eclipse.core.runtime.IProgressMonitor;

import archimate.codegen.ICodeGenerator;
import archimate.codegen.IGenModel;
import archimate.codegen.SourceInspector;
import archimate.util.FileHandler;
import archimate.util.TagTree;

public abstract class Pattern implements ICodeGenerator {

	// Nr of estimated tasks
	protected int tasks = 0;
	// The name of the pattern
	protected String name;
	// Tree defining the structure of the MVC pattern key elements
	protected TagTree tree;
	// Model containing all data for the MVC framework to create
	protected IGenModel model;
	// ProgressMonitor
	protected IProgressMonitor monitor;

	// Returns the name of the pattern
	public String name() {
		return name;
	}

	// Returns the TagTree of the pattern
	public TagTree tree() {
		return tree;
	}

	// Returns the IGenModel of the pattern
	public IGenModel model() {
		return model;
	}

	// Returns the progressmonitor of the pattern
	public IProgressMonitor monitor() {
		return monitor;
	}

	// Estimates the number of tasks to execute
	public int estimateTasks() {
		if (tasks == 0) {
			FileHandler handler = new FileHandler();
			int count = handler.countFiles(model.targetFolder(), model
					.packageBase());
			count += tree.nodes();
			return count;
		} else {
			return tasks;
		}
	}

	// Generates code for the pattern
	public void generate_code(final IProgressMonitor monitor) {
		// Set progressmonitor
		this.monitor = monitor;
		// Reset the tree containing MVC pattern key elements
		tree.resetVisited();

		// Traverses the source and adds missing elements
		SourceInspector inspector = new SourceInspector(this);
		inspector.updateSource();
	}

	// Validates the code in the project source folder
	public void validate_code() {
	}

}
