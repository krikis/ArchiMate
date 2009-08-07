package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.*;

import archimate.patterns.mvc.MVCPattern;
import archimate.util.*;

public class JavaInspector extends ASTVisitor {

	private TagTree tree;
	private SourceInspector inspector;
	private JavaHelper helper;

	public JavaInspector(SourceInspector inspector) {
		super(true);
		this.tree = inspector.tree();
		this.inspector = inspector;
		helper = new JavaHelper();
	}

	public void preVisit(ASTNode node) {

	}

	public void postVisit(ASTNode node) {

	}

	public boolean visit(TypeDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasChild(tag)) {
			TagNode self = tree.getNode(current, tag);
			self.setVisited(true);
			tree.setCurrent(self);
			if (self.hasChildren()) {
				return true;
			}
		}
		return false;
	}

	public void endVisit(TypeDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasParent() && current.parent().hasChild(tag)) {
			if (current.hasChildren()) {
				ArrayList<String> tags = tree.getUnvisited(current);
				inspector.addSourceElements(node, tags);
			}
			tree.setCurrent(current.parent());
		}
	}

	public boolean visit(MethodDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasChild(tag)) {
			TagNode self = tree.getNode(current, tag);
			self.setVisited(true);
			tree.setCurrent(self);
			if (self.hasChildren()) {
				return true;
			}
		}
		return false;
	}

	public void endVisit(MethodDeclaration node) {
		String tag = helper.getArchiMateTag(node);
		TagNode current = tree.current();
		if ((!tag.equals("")) && current.hasParent() && current.parent().hasChild(tag)) {
			tree.setCurrent(current.parent());
		}
	}
}
