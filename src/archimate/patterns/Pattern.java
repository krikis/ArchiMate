package archimate.patterns;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;

import archimate.codegen.ICodeElement;
import archimate.codegen.ICodeGenerator;
import archimate.uml.UMLAdapter;
import archimate.util.JavaClass;
import archimate.util.FileHandler;
import archimate.util.JavaMethod;
import archimate.util.SourceInspector;
import archimate.util.TagNode;
import archimate.util.TagTree;

/**
 * This class implements the generic methods for Patterns and Primitives
 * 
 * @author Samuel Esposito
 */
public abstract class Pattern implements ICodeGenerator {

	// Nr. of estimated tasks
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
	protected UMLAdapter umlReader;

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

	// Returns the UML reader
	public UMLAdapter umlReader() {
		return umlReader;
	}

	// Returns the progress monitor of the pattern
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
		// Set progress monitor
		this.monitor = monitor;
		// Set status
		this.status = status;

		// Traverses the source and adds missing elements
		SourceInspector inspector = new SourceInspector(this);
		inspector.updateSource();
	}

	// Validates the code in the project source folder
	public void validate_code(final IProgressMonitor monitor, MultiStatus status) {
		// Set progress monitor
		this.monitor = monitor;
		// Set status
		this.status = status;

		// Traverses the source and validates its elements
		SourceInspector inspector = new SourceInspector(this);
		inspector.validateSource();
	}

	// Updates the currently selected UML model
	public void update_model(final IProgressMonitor monitor, MultiStatus status) {
		// Set progress monitor
		this.monitor = monitor;
		// Set status
		this.status = status;

		// Traverses the source and validates its elements
		SourceInspector inspector = new SourceInspector(this);
		inspector.updateModel();
	}

	// Add a number of JavaClass objects with the given settings to the TagNode
	protected void createClasses(TagNode node, String stereotype,
			String packageName, ArrayList<String> imports, boolean isAbstract,
			String type, String defaultName, String format,
			JavaClass superClass, ArrayList<JavaClass> interfaces,
			String comment) {
		if (stereotype == null || stereotype.equals(""))
			stereotype = node.stereotype();
		boolean hasrun = false;
		int count = 0;
		for (NamedElement element : umlReader.getElements(stereotype)) {
			String name = element.getName();
			String[] formatParts = format.split("#");
			String className = (name.equals("") ? defaultName : formatParts[0]
					+ name + (formatParts.length == 2 ? formatParts[1] : ""));
			JavaClass javaClass = createClass(node, element, packageName,
					imports, type, className, superClass, interfaces, comment,
					name.equals(""));
			javaClass.setAbstract(isAbstract);
			hasrun = true;
			++count;
		}
		if (!hasrun) {
			JavaClass javaClass = createClass(node, null, packageName, imports,
					type, defaultName, superClass, interfaces, comment, true);
			javaClass.setAbstract(isAbstract);
		}
	}

	// Creates a JavaClass object with the given settings
	protected JavaClass createClass(TagNode node, NamedElement umlElement,
			String packageName, ArrayList<String> imports, String type,
			String className, JavaClass superClass,
			ArrayList<JavaClass> interfaces, String comment, boolean optional) {
		JavaClass javaClass = new JavaClass(packageName, className, node.tag(),
				type);
		if (imports != null)
			javaClass.addImports(imports);
		if (interfaces != null)
			javaClass.addInterfaces(interfaces);
		if (comment != null)
			if (comment.matches("#name#")) {
				String[] temp = comment.split("#name#");
				String newComment = temp[0] + className + " " + temp[1];
				javaClass.setComment(newComment);
			} else {
				javaClass.setComment(comment);
			}
		if (superClass != null)
			javaClass.setSuperClass(superClass);
		// add restricted interface
		if (type.equals(JavaClass.INTERFACE)
				&& (!javaClass.optional() || javaClass.className() != javaClass
						.intendedName())) {
			tree.addRestrictedInterface(className, className, packageName);
		}
		javaClass.setOptional(optional);
		javaClass.setUmlElement(umlElement);
		node.addSource(javaClass);
		return javaClass;
	}

	// Creates a list of Method objects with the given settings and adds it to
	// the TagNodes sourcelist
	protected void addMethods(TagNode node, String defaultName, String type,
			String comment) {
		if (node.parent() != null) {
			TagNode parent = node.parent();
			ArrayList<ICodeElement> source = parent.source();
			for (ICodeElement element : source) {
				if (element instanceof JavaClass) {
					ArrayList<NamedElement> messages = new ArrayList<NamedElement>();
					if (element.umlElement() instanceof NamedElement) {
						messages = umlReader.getReceived(element.umlElement(),
								node.stereotype());
					}
					addMethods(node, element, messages, defaultName, type,
							comment);
				}
			}
		}
	}

	// Creates a list of Methods for the current ICodeElement and adds it to the
	// TagNode
	private void addMethods(TagNode node, ICodeElement element,
			ArrayList<NamedElement> messages, String defaultName, String type,
			String comment) {
		boolean hasrun = false;
		for (int index = 0; index < messages.size(); ++index) {
			String name = messages.get(index).getName();
			String methodName = (name.equals("") ? defaultName
					+ (index == 0 ? "" : index) : name);
			JavaMethod method = new JavaMethod(methodName, node.tag(), type,
					null, null);
			if (name.equals(""))
				method.setOptional(true);
			method.setComment(comment);
			element.addChild(method);
			node.addSource(method);
			hasrun = true;
		}
		if (!hasrun && element.optional()) {
			JavaMethod method = new JavaMethod(defaultName, node.tag(), type,
					null, null);
			method.setOptional(true);
			method.setComment(comment);
			element.addChild(method);
			node.addSource(method);
		}
	}

	// Clones the Method objects in the list, adds the given settings and adds
	// the list to the TagNodes sourcelist
	protected void addMethods(TagNode node, ICodeElement javaClass,
			ICodeElement interfaceClass, String type, String className,
			String packageName, String comment) {
		for (ICodeElement code : interfaceClass.children()) {
			if (code instanceof JavaMethod) {
				JavaMethod method = (JavaMethod) code;
				if (type.equals(JavaMethod.INVOCATION) && !method.optional()) {
					tree.addRestrictedMethod(method.name(), className,
							packageName);
				}
				JavaMethod newMethod = new JavaMethod(method.name(),
						node.tag(), type, className, packageName);
				newMethod.setOptional(method.optional());
				newMethod.setComment(comment);
				javaClass.addChild(newMethod);
				node.addSource(newMethod);
			}
		}
	}

}
