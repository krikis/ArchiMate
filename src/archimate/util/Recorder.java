package archimate.util;

import org.eclipse.jdt.core.dom.*;

import archimate.codegen.JavaHelper;

/**
 * This class implements an ASTVisitor which traverses the parsed source code
 * and records the identifiers of code elements which represent the structure of
 * a design pattern or primitive.
 * 
 * @author Samuel Esposito
 * 
 */
public class Recorder extends ASTVisitor {

	// TagTree of the ICodeGenerator at hand
	private TagTree tree;
	// JavaHelper for accessing the source code
	private JavaHelper helper;

	/**
	 * Creates a new {@link Recorder}, sets the {@link TagTree} and initializes
	 * the {@link JavaHelper}
	 * 
	 * @param inspector
	 *            The given {@link SourceInspector}
	 * @param pattern
	 *            the pattern currently processed
	 */
	public Recorder(SourceInspector inspector, String pattern) {
		super(true);
		this.tree = inspector.tree();
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
			String packageName = helper.getPackage(node);
			// Record the identifier for the encountered archiMateTag
			self.recordIdentifier(name, packageName, tag);
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
	}
}
