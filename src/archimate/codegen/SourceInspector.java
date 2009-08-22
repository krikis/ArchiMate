package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.util.FileHandler;
import archimate.util.TagNode;
import archimate.util.TagTree;

/**
 * This class analyses the source code in the project source folder using the
 * given {@link TagTree} and commands the generation of missing source elements
 * 
 * @author Samuel Esposito
 * 
 */
public class SourceInspector {
	
	public static final String GENERATE = "generate";
	public static final String VALIDATE = "validate";
	
	// Mode of the sourceInspector
	private String mode;
	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
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
	 * Returns the progressmonitor
	 * 
	 * @return The progressmonitor
	 */
	public IProgressMonitor monitor() {
		return monitor;
	}

	/**
	 * Traverses the source and adds missing source elements and files
	 */
	public void updateSource() {
		// Sets the mode to code generation
		mode = GENERATE;
		// Traverses the source and calls back when key source elements are
		// missing
		inspect();
		// Adds the source files that are missing
		ArrayList<TagNode> tags = TagTree.getUnvisited(tree.root());
		createSourceFiles(tags);
	}
	
	/**
	 * Traverses the source and validates the source elements
	 */
	public void validateSource() {
		// Sets the mode to validation
		mode = VALIDATE;
		// Traverses the 
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
	 */
	public void addSourceElements(TypeDeclaration node, ArrayList<TagNode> tags) {
		JavaHelper helper = new JavaHelper(pattern);
		for (Iterator<TagNode> iter = tags.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			TagNode tagnode = iter.next();
			helper.addMethods(node, tagnode, status);
			monitor.worked(1);
		}
	}

}
