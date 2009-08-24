package archimate.codegen;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.dom.*;

import archimate.uml.UMLAdapter;
import archimate.util.*;

/**
 * This class implements an ASTVisitor which traverses the parsed source code
 * and adds UML elements where new source code is found. Every source element is
 * visited twice, once with the <code>visit</code> method, before the children
 * are visited, and once with the <code>endVisit</code> method, after the
 * children are visited.
 * 
 * @author Samuel Esposito
 */
public class UMLUpdater extends ASTVisitor {

	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// The umlReader for manipulating the UML model
	private UMLAdapter umlReader;
	// JavaHelper for accessing the source code
	private JavaHelper helper;
	// ProgressMonitor
	private IProgressMonitor monitor;
	// Status
	private MultiStatus status;

	/**
	 * Creates a new {@link UMLUpdater} and sets the {@link TagTree} and
	 * {@link SourceInspector} and initializes the {@link JavaHelper}
	 * 
	 * @param inspector
	 *            The given {@link SourceInspector}
	 * @param pattern
	 *            the pattern currently processed
	 */
	public UMLUpdater(SourceInspector inspector, String pattern) {
		super(true);
		tree = inspector.tree();
		umlReader = inspector.umlReader();
		monitor = inspector.monitor();
		status = inspector.status();
		helper = new JavaHelper(status, pattern);
	}

	/*
	 * (non-Javadoc) Visits a TypeDeclaration node before its children are
	 * visited. If an archiMateTag is found and it is a child of the current
	 * archiMateTag, then it is marked as visited.
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TypeDeclaration)
	 */
	public boolean visit(TypeDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasChild(tag)) {
			TagNode self = tree.getNode(current, tag);
			boolean toggle = self.setVisited();
			if (toggle)
				monitor.worked(1);
			tree.setCurrent(self);
		}
		return true;
	}

	/*
	 * (non-Javadoc) Visits a TypeDeclaration node after its children are
	 * visited. If an archiMateTag is found and it is a child of the current
	 * archiMateTag, then code is generated for the missing children by calling
	 * back to the SourceInspector.
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TypeDeclaration)
	 */
	public void endVisit(TypeDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasParent()
				&& current.parent().hasChild(tag)) {
			tree.setCurrent(current.parent());
		}
	}

	/*
	 * (non-Javadoc) Visits a MethodDeclaration node before its children are
	 * visited. If an archiMateTag is found and it is a child of the current
	 * archiMateTag, then this archiMateTag becomes the current tag.
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MethodDeclaration)
	 */
	public boolean visit(MethodDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		helper.findNewMethod(node, current, umlReader);
		if ((!tag.equals("")) && current.hasChild(tag)) {
			TagNode self = tree.getNode(current, tag);
			boolean toggle = self.setVisited();
			if (toggle)
				monitor.worked(1);
			tree.setCurrent(self);
		}
		return false;
	}

	/*
	 * (non-Javadoc) Visits a MethodDeclaration node after its children are
	 * visited. If an archiMateTag is found and it is a child of the current
	 * archiMateTag, then its parent becomes the current tag.
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MethodDeclaration)
	 */
	public void endVisit(MethodDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasParent()
				&& current.parent().hasChild(tag)) {
			tree.setCurrent(current.parent());
		}
	}
}
