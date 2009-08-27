package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.codegen.ICodeElement;
import archimate.codegen.ICodeGenerator;
import archimate.codegen.JavaHelper;
import archimate.uml.UMLAdapter;

/**
 * This class analyses the source code in the project source folder using the
 * given {@link TagTree} and commands the generation of missing source elements
 * 
 * @author Samuel Esposito
 * 
 */
public class SourceInspector {

	// Constants defining the current action of the plugin,
	public static final String GENERATE = "generate";
	public static final String VALIDATE = "validate";
	public static final String UPDATE = "update";
	// Mode of the sourceInspector
	private String mode;
	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// The umlReader
	private UMLAdapter umlReader;
	// IGenModel of the ICodeGenerator at hand
	private String packageBase;
	// ASTEngine for traversing the code
	private ASTEngine astEngine;
	// ProgressMonitor
	private IProgressMonitor monitor;
	// Status
	private MultiStatus status;
	// Current pattern
	private String pattern;

	/**
	 * Creates a new {@link SourceInspector} and sets its {@link TagTree},
	 * packageBase and monitor from the given {@link ICodeGenerator}
	 * 
	 * @param generator
	 *            The {@link ICodeGenerator} at hand
	 */
	public SourceInspector(ICodeGenerator generator) {
		tree = generator.tree();
		packageBase = generator.packageBase();
		monitor = generator.monitor();
		status = generator.status();
		pattern = generator.name();
		umlReader = generator.umlReader();
	}

	/**
	 * Returns the current {@link TagTree}
	 * 
	 * @return The current {@link TagTree}
	 */
	public TagTree tree() {
		return tree;
	}

	/**
	 * Returns the current {@link UMLAdapter}
	 * 
	 * @return The current {@link UMLAdapter}
	 */
	public UMLAdapter umlReader() {
		return umlReader;
	}

	/**
	 * Returns the progressmonitor
	 * 
	 * @return The progressmonitor
	 */
	public IProgressMonitor monitor() {
		return monitor;
	}

	/**
	 * Returns the status
	 * 
	 * @return The status
	 */
	public MultiStatus status() {
		return status;
	}

	/**
	 * Traverses the source and adds missing source elements and files
	 */
	public void updateSource() {
		// Set the mode to code generation
		mode = GENERATE;
		// Traverse the source and calls back when key source elements are
		// missing
		inspect();
		// Add the source files that are missing
		ArrayList<TagNode> tags = tree.getUnvisited();
		createSourceFiles(tags);
	}

	/**
	 * Traverses the source and validates the source elements
	 */
	public void validateSource() {
		// Set the mode to validation
		mode = VALIDATE;
		// Traverses the
		inspect();
		// Report the source files that are missing
		ArrayList<TagNode> tags = tree.getAllUnvisited();
		reportMissing(tags);
	}

	/**
	 * Traverses the source and updates the UML model
	 */
	public void updateModel() {
		// Set the mode to validation
		mode = UPDATE;
		// Traverses the source and updates the model when UML elements are
		// missing
		inspect();
	}

	// Traverses the source and calls back when key source elements are
	// missing
	private void inspect() {
		FileHandler handler = new FileHandler();
		IContainer container = handler.findOrCreateContainer(packageBase);
		IResource[] members = null;
		try {
			members = container.members();
		} catch (CoreException e) {
			System.out.println("Could not access members of the container "
					+ container.getFullPath() + ".");
			e.printStackTrace();
		}
		traverseSourceFiles(members);
	}

	// Recursively traverses all source files in the project source folder and
	// adds source elements when they are missing
	private void traverseSourceFiles(IResource[] members) {
		for (int index = 0; index < members.length; index++) {
			if (monitor.isCanceled()) {
				return;
			}
			IResource resource = members[index];
			if (resource instanceof IContainer) {
				IContainer container = (IContainer) resource;
				IResource[] newMembers = null;
				try {
					newMembers = container.members();
				} catch (CoreException e) {
					System.out.println("Could not access members "
							+ "of the container " + container.getFullPath()
							+ ".");
					e.printStackTrace();
				}
				traverseSourceFiles(newMembers);
			}
			if (resource instanceof IFile) {
				astEngine = new ASTEngine((IFile) resource, this, mode, pattern);
				astEngine.traverseSource();
				monitor.worked(1);
			}
		}
	}

	// Adds the source files that are missing
	private void createSourceFiles(ArrayList<TagNode> tags) {
		for (Iterator<TagNode> iter = tags.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			ASTEngine engine = new ASTEngine(this, mode, pattern);
			engine.createSourceFile(iter.next(), status);
			monitor.worked(1);
		}
	}

	/**
	 * Creates source elements in the node for every tag in the list.
	 * 
	 * @param node
	 *            the node to add the source elements to
	 * @param tags
	 *            the tags to create source for
	 */
	public void addSourceElements(TypeDeclaration node, ArrayList<TagNode> tags) {
		JavaHelper helper = new JavaHelper(status, pattern);
		for (Iterator<TagNode> iter = tags.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			TagNode tagnode = iter.next();
			helper.addMethods(node, tagnode);
			monitor.worked(1);
		}
	}

	/**
	 * Reports the missing source elements for every tag
	 * 
	 * @param tags
	 *            The tags to report missing source elements for
	 */
	public void reportMissing(ArrayList<TagNode> tags) {
		for (Iterator<TagNode> iter = tags.iterator(); iter.hasNext();) {
			TagNode node = iter.next();
			for (Iterator<ICodeElement> ite2 = node.source().iterator(); ite2
					.hasNext();) {
				ICodeElement elem = ite2.next();
				if ((!elem.optional() || node.onlyOptional())
						&& !elem.visited()) {
					if (elem instanceof JavaMethod && node.parent() != null) {
						reportMissingMethod((JavaMethod) elem, node.parent());
					}
					if (elem instanceof JavaClass) {
						reportMissingFile((JavaClass) elem);
					}
				}
			}
		}
	}

	// Reports a missing source file
	private void reportMissingFile(JavaClass javaClass) {
		status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, pattern
				+ ": Sourcefile for \"" + javaClass.className() + "\" "
				+ (javaClass.isInterface() ? "interface" : "class")
				+ " missing. Try to generate code or to update the model."
				+ "                             ", null));
	}

	// Reports a missing method
	private void reportMissingMethod(JavaMethod method, TagNode node) {
		String container = "";
		if (node.source().size() == 1) {
			ICodeElement parentElement = node.source().get(0);
			if (parentElement instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) parentElement;
				container = " in the \"" + javaClass.className() + "\" "
						+ (javaClass.isInterface() ? "interface" : "class");
			}
		}
		status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, pattern
				+ ": Method " + method.type() + " for the \"" + method.name()
				+ "()\" method missing" + container
				+ ". Try to generate code or to update the model."
				+ "                          ", null));
	}

}
