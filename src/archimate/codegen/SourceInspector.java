package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// IGenModel of the ICodeGenerator at hand
	private String packageBase;
	// ASTEngine for traversing the code
	private ASTEngine astEngine;
	// ProgressMonitor
	private IProgressMonitor monitor;

	/**
	 * Creates a new {@link SourceInspector} and sets its {@link TagTree} and
	 * {@link IGenModel} from the given {@link ICodeGenerator}
	 * 
	 * @param generator
	 *            The {@link ICodeGenerator} at hand
	 */
	public SourceInspector(ICodeGenerator generator) {
		tree = generator.tree();
		packageBase = generator.packageBase();
		monitor = generator.monitor();
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
		// Traverses the source and calls back when key source elements are
		// missing
		inspect();
		// Adds the source files that are missing
		ArrayList<TagNode> tags = TagTree.getUnvisited(tree.root());
		createSourceFiles(tags);
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
				astEngine = new ASTEngine((IFile) resource, this);
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
			ASTEngine engine = new ASTEngine(this);
			engine.createSourceFile(iter.next());
			monitor.worked(1);
		}
	}

	/**
	 * Creates source elements in the node for every tag in the list.
	 */
	public void addSourceElements(TypeDeclaration node, ArrayList<TagNode> tags) {
		JavaHelper helper = new JavaHelper();
		for (Iterator<TagNode> iter = tags.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			TagNode tagnode = iter.next();
			helper.addMethods(node, tagnode);
			monitor.worked(1);
		}
	}

}
