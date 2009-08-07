package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.util.FileHandler;
import archimate.util.TagTree;

public class SourceInspector {

	TagTree tree;
	IGenModel model;
	private ASTEngine astEngine;

	public SourceInspector(ICodeGenerator generator) {
		this.tree = generator.tree();
		this.model = generator.model();
	}

	public TagTree tree() {
		return tree;
	}

	public void updateSource() {
		// Traverses the source and calls back when key source elements are
		// missing
		inspect();
		// Adds the source files that are missing
		ArrayList<String> tags = TagTree.getUnvisited(tree.root());
		createSourceFiles(tags);
	}

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
			}
		}
	}

	/**
	 * Creates source files for every tag in the list.
	 */
	private void createSourceFiles(ArrayList<String> tags) {
		for (Iterator<String> iter = tags.iterator(); iter.hasNext();) {
			ASTEngine engine = new ASTEngine(this);
			engine.createSourceFile(model, iter.next());
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
		}
	}

}
