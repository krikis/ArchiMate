package archimate.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import archimate.codegen.ICodeElement;
import archimate.codegen.JavaHelper;
import archimate.util.SourceInspector;
import archimate.util.TagNode;
import archimate.util.TagTree;

/**
 * This class implements an ASTVisitor which traverses the parsed source code
 * and validates it. Every source element is visited twice, once with the
 * <code>visit</code> method, before the children are visited, and once with the
 * <code>endVisit</code> method, after the children are visited.
 * 
 * @author Samuel Esposito
 */
public class JavaValidator extends ASTVisitor {

	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// JavaHelper for accessing the source code
	private JavaHelper helper;
	// ProgressMonitor
	private IProgressMonitor monitor;
	// Status
	private MultiStatus status;

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
		tree = inspector.tree();
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
			ICodeElement element = helper.compare(node, self);
			boolean toggle = self.setVisited();
			if (toggle)
				monitor.worked(1);
			tree.setCurrent(self);
			if (self.hasChildren() && element != null) {
				tree.setCurrentCode(element);
			}
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
			if (current.hasChildren()) {
				String name = helper.getName(node);
				String packageName = helper.getPackage(node);
				ICodeElement code = current.getSource(name, packageName);
				if (code != null) {
					tree.setCurrentCode(tree.currentCode().parent());
				}
			}
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
			if (tree.currentCode() != null)
				helper.compare(node, tree.currentCode(), self);
			boolean toggle = self.setVisited();
			if (toggle)
				monitor.worked(1);
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

	/*
	 * (non-Javadoc) Visits a MethodInvocation node and checks if it misuses a
	 * restricted method
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MethodDeclaration)
	 */
	public boolean visit(MethodInvocation node) {
		helper.checkRestricted(node, tree.current(), tree.restrictedMethods());
		return false;
	}
}
