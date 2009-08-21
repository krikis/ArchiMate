package archimate.patterns;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;

import archimate.codegen.ICodeElement;
import archimate.codegen.ICodeGenerator;
import archimate.codegen.IGenModel;
import archimate.codegen.SourceInspector;
import archimate.uml.UMLAdapter;
import archimate.util.JavaClass;
import archimate.util.FileHandler;
import archimate.util.JavaMethod;
import archimate.util.TagNode;
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

	protected UMLAdapter umlreader;

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

	// Creates a Class object with the given settings
	protected JavaClass createClass(TagNode node,
			ArrayList<String> stereotypes, String packageName,
			ArrayList<String> imports, String type, String defaultName,
			ArrayList<String> interfaces, String comment) {
		String className = "";
		for (Iterator<String> iter = stereotypes.iterator(); iter.hasNext();) {
			String stereotype = iter.next();
			className = umlreader.getElementName(stereotype);
			if (!className.equals("")) {
				break;
			}
		}
		className = className.equals("") ? defaultName : className;
		JavaClass newClass = new JavaClass(packageName, className, node.tag(),
				type);
		if (imports != null)
			newClass.addImports(imports);
		if (interfaces != null)
			newClass.addInterfaces(interfaces);
		if (comment != null)
			newClass.setComment(comment);
		return newClass;
	}

	// Creates a list of Method objects with the given settings and adds it to
	// the TagNodes sourcelist
	protected void addMethods(TagNode node, String stereotype,
			String defaultName, String type, String className, String comment) {
		ArrayList<String> names = umlreader.getElementNames(stereotype);
		for (int index = 0; index < names.size(); ++index) {
			String name = names.get(index);
			name = (name.equals("") ? defaultName + index : name);
			JavaMethod method = new JavaMethod(name, node.tag(), type,
					className);
			method.setComment(comment);
			node.addSource(method);
		}
	}

	// Clones the Method objects in the list, adds the given settings and adds
	// the list to the TagNodes sourcelist
	protected void addMethods(TagNode node, ArrayList<ICodeElement> methods,
			String type, String className, String comment) {
		for (Iterator<ICodeElement> iter = methods.iterator(); iter.hasNext();) {
			ICodeElement element = iter.next();
			if (element instanceof JavaMethod) {
				JavaMethod method = (JavaMethod) element;
				JavaMethod newMethod = new JavaMethod(method.name(), method
						.archiMateTag(), type, className);
				newMethod.setComment(comment);
				node.addSource(newMethod);
			}
		}
	}

}
