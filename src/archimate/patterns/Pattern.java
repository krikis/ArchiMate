package archimate.patterns;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;

import archimate.codegen.ICodeElement;
import archimate.codegen.ICodeGenerator;
import archimate.codegen.SourceInspector;
import archimate.uml.UMLAdapter;
import archimate.util.InterfaceImpl;
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
	// Base of the package
	protected String packageBase;
	// Tree defining the structure of the MVC pattern key elements
	protected TagTree tree;
	// ProgressMonitor
	protected IProgressMonitor monitor;
	// Status
	protected MultiStatus status;
	// UML reader
	protected UMLAdapter umlreader;

	// Returns the name of the pattern
	public String name() {
		return name;
	}

	// Returns the package base
	public String packageBase() {
		return packageBase;
	}

	// Returns the TagTree of the pattern
	public TagTree tree() {
		return tree;
	}

	// Returns the progressmonitor of the pattern
	public IProgressMonitor monitor() {
		return monitor;
	}

	// Returns the status of the pattern
	public MultiStatus status() {
		return status;
	}

	// Estimates the number of tasks to execute
	public int estimateTasks() {
		if (tasks == 0) {
			FileHandler handler = new FileHandler();
			int count = handler.countFiles(packageBase);
			count += tree.nodes();
			return count;
		} else {
			return tasks;
		}
	}

	// Generates code for the pattern
	public void generate_code(final IProgressMonitor monitor, MultiStatus status) {
		// Set progressmonitor
		this.monitor = monitor;
		// Set status
		this.status = status;

		// Traverses the source and adds missing elements
		SourceInspector inspector = new SourceInspector(this);
		inspector.updateSource();
	}

	// Validates the code in the project source folder
	public void validate_code(final IProgressMonitor monitor, MultiStatus status) {
		// Set progressmonitor
		this.monitor = monitor;
		// Set status
		this.status = status;

		// Traverses the source and validates its elements
		SourceInspector inspector = new SourceInspector(this);
		inspector.validateSource();
	}

	// Creates a Class object with the given settings
	protected JavaClass createClass(TagNode node,
			ArrayList<String> stereotypes, String packageName,
			ArrayList<String> imports, String type, String defaultName,
			ArrayList<InterfaceImpl> interfaces, String comment) {
		String name = "";
		for (Iterator<String> iter = stereotypes.iterator(); iter.hasNext();) {
			String stereotype = iter.next();
			name = umlreader.getElementName(stereotype);
			if (!name.equals("")) {
				break;
			}
		}
		String className = name.equals("") ? defaultName : name;
		// add restricted interface
		if (type.equals(JavaClass.INTERFACE) && !name.equals("")) {
			tree.addRestrictedInterface(className, className, packageName);
		}
		JavaClass newClass = new JavaClass(packageName, className, node.tag(),
				type);
		if (name.equals(""))
			newClass.setOptional();
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
			String defaultName, String type, String comment) {
		ArrayList<String> names = umlreader.getElementNames(stereotype);
		if (names.size() == 0)
			names.add("");
		for (int index = 0; index < names.size(); ++index) {
			String name = names.get(index);
			String methodName = (name.equals("") ? defaultName
					+ (index == 0 ? "" : index) : name);
			JavaMethod method = new JavaMethod(methodName, node.tag(), type,
					null, null);
			if (name.equals(""))
				method.setOptional();
			method.setComment(comment);
			System.out.println(method.name() + " | " + method.optional());
			node.addSource(method);
		}
	}

	// Clones the Method objects in the list, adds the given settings and adds
	// the list to the TagNodes sourcelist
	protected void addMethods(TagNode node, ArrayList<ICodeElement> methods,
			String type, String className, String packageName, String comment) {
		for (Iterator<ICodeElement> iter = methods.iterator(); iter.hasNext();) {
			ICodeElement element = iter.next();
			if (element instanceof JavaMethod) {
				JavaMethod method = (JavaMethod) element;
				if (type.equals(JavaMethod.INVOCATION) && !method.optional()) {
					tree.addRestrictedMethod(method.name(), className,
							packageName);
				}
				JavaMethod newMethod = new JavaMethod(method.name(),
						node.tag(), type, className, packageName);
				if (method.optional())
					newMethod.setOptional();
				newMethod.setComment(comment);
				System.out.println(newMethod.name() + " | "
						+ newMethod.optional());
				node.addSource(newMethod);
			}
		}
	}

}
