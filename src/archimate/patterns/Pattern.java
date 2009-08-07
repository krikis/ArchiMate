package archimate.patterns;

import archimate.codegen.ICodeGenerator;
import archimate.codegen.IGenModel;
import archimate.codegen.SourceInspector;
import archimate.util.TagTree;

public abstract class Pattern implements ICodeGenerator {

	// Tree defining the structure of the MVC pattern key elements
	protected TagTree tree;
	// Model containing all data for the MVC framework to create
	protected IGenModel model;

	// Returns the TagTree of the pattern
	public TagTree tree() {
		return tree;
	}

	// Returns the IGenModel of the pattern
	public IGenModel model() {
		return model;
	}

	// Generates code for the pattern
	public void generate_code() {
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
