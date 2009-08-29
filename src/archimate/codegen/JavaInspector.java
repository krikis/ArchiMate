package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.*;

import archimate.util.*;

/**
 * This class implements an ASTVisitor which traverses the parsed source code
 * and adds source elements where missing. Every source element is visited
 * twice, once with the <code>visit</code> method, before the children are
 * visited, and once with the <code>endVisit</code> method, after the children
 * are visited.
 * 
 * @author Samuel Esposito
 * 
 */
public class JavaInspector extends ASTVisitor {

	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// SourceInspector object to call back
	private SourceInspector inspector;
	// JavaHelper for accessing the source code
	private JavaHelper helper;
	// ProgressMonitor
	private IProgressMonitor monitor;

	/**
	 * Creates a new {@link JavaInspector} and sets the {@link TagTree} and
	 * {@link SourceInspector} and initializes the {@link JavaHelper}
	 * 
	 * @param inspector
	 *            The given {@link SourceInspector}
	 * @param pattern
	 *            the pattern currently processed
	 */
	public JavaInspector(SourceInspector inspector, String pattern) {
		super(true);
		this.tree = inspector.tree();
		this.inspector = inspector;
		this.monitor = inspector.monitor();
		helper = new JavaHelper(inspector.status(), pattern);
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
			String name = helper.getName(node);
			boolean found = self.tickOffSource(name);
			boolean toggle = self.setVisited();
			if (toggle)
				monitor.worked(1);
			tree.setCurrent(self);
			if (self.hasChildren()) {
				return found;
			}
		}
		return false;
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
			String name = helper.getName(node);
			if (current.hasChildren() && current.getSource(name) != null) {
				ArrayList<TagNode> tags = tree.getUnvisited();
				inspector.addSourceElements(node, tags);
			}
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
		if ((!tag.equals("")) && current.hasChild(tag)) {
			TagNode self = tree.getNode(current, tag);
			String name = helper.getName(node);
			self.tickOffSource(name);
			boolean toggle = self.setVisited();
			if (toggle)
				monitor.worked(1);
			tree.setCurrent(self);
			if (self.hasChildren()) {
				return true;
			}
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
