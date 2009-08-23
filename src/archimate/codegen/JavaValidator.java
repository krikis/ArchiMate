package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.dom.*;

import archimate.util.*;

/**
 * This class implements an ASTVisitor which traverses the parsed source code
 * like a tree. Every source element is visited twice, once with the
 * <code>visit</code> method, before the children are visited, and once with the
 * <code>endVisit</code> method, after the children are visited.
 * 
 * @author Samuel Esposito
 * 
 */
public class JavaValidator extends ASTVisitor {

	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// SourceInspector object to call back
	private SourceInspector inspector;
	// JavaHelper for accessing the source code
	private JavaHelper helper;
	// ProgressMonitor
	private IProgressMonitor monitor;
	// Status
	private MultiStatus status;
	// The current pattern
	private String pattern;

	/**
	 * Creates a new {@link JavaValidator} and sets the {@link TagTree} and
	 * {@link SourceInspector} and initializes the {@link JavaHelper}
	 * 
	 * @param inspector
	 *            The given {@link SourceInspector}
	 * @param pattern
	 *            the pattern currently processed
	 */
	public JavaValidator(SourceInspector inspector, String pattern) {
		super(true);
		this.inspector = inspector;
		tree = inspector.tree();
		monitor = inspector.monitor();
		status = inspector.status();
		this.pattern = pattern;
		helper = new JavaHelper(status, pattern);
	}

	/*
	 * (non-Javadoc) Called before a node was visited
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	public void preVisit(ASTNode node) {

	}

	/*
	 * (non-Javadoc) Called after a node is visited
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#postVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	public void postVisit(ASTNode node) {

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
			helper.compare(node, self);
			self.setVisited();
			monitor.worked(1);
			tree.setCurrent(self);
		}
		helper.checkRestricted(node, tree.current(), tree
				.restrictedInterfaces());
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
		if ((!tag.equals("")) && current.hasChild(tag)) {
			TagNode self = tree.getNode(current, tag);
			helper.compare(node, self);
			self.setVisited();
			tree.setCurrent(self);
		}
		return true;
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

	public boolean visit(MethodInvocation node) {
		helper.checkRestricted(node, tree.current(), tree.restrictedMethods());
		return false;
	}
}
