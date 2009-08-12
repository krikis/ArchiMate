package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.util.FileHandler;
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
	private IGenModel model;
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
		this.tree = generator.tree();
		this.model = generator.model();
		this.monitor = generator.monitor();
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
		ArrayList<String> tags = TagTree.getUnvisited(tree.root());
		createSourceFiles(tags);
	}

	// Traverses the source and calls back when key source elements are
	// missing
	private void inspect() {
		FileHandler handler = new FileHandler();
		IContainer container = handler.findOrCreateContainer(model
				.targetFolder(), model.packageBase());
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
			if (monitor.isCanceled()) {
				return;
			}
		}
	}

	// Adds the source files that are missing
	private void createSourceFiles(ArrayList<String> tags) {
		for (Iterator<String> iter = tags.iterator(); iter.hasNext();) {
			ASTEngine engine = new ASTEngine(this);
			engine.createSourceFile(model, iter.next());
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}
		}
	}

	/**
	 * Creates source elements in the node for every tag in the list.
	 */
	public void addSourceElements(TypeDeclaration node, ArrayList<String> tags) {
		JavaHelper helper = new JavaHelper();
		for (Iterator<String> iter = tags.iterator(); iter.hasNext();) {
			String tag = iter.next();
			String type = model.sourceType(tag);
			if (type.equals(JavaHelper.METHOD_DECLARATION)) {
				helper.addMethodDeclarations(model, node, tag);
			} else if (type.equals(JavaHelper.METHOD_IMPLEMENTATION)) {
				helper.addMethods(model, node, tag);
			} else if (type.equals(JavaHelper.METHOD_INVOCATION)) {
				helper.addMethodInvocations(model, node, tag);
			}
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}
		}
	}

}
