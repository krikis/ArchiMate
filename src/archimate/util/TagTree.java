package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

public class TagTree {

	public static final String ROOT = "root";

	private TagNode root;

	private TagNode current;

	public TagTree() {
		root = new TagNode(ROOT);
		current = root;
	}

	public TagNode root() {
		return root;
	}

	public TagNode current() {
		return current;
	}

	public void setCurrent(TagNode node) {
		if (node != null)
			current = node;
	}

	public void printTree() {
		printNodes(root.children());
	}

	private void printNodes(ArrayList<TagNode> children) {
		for (Iterator<TagNode> iter = children.iterator(); iter.hasNext();) {
			TagNode node = iter.next();
			System.out.println(node.tag() + (node.visited()? " :: visited" : ""));
			printNodes(node.children());
		}
	}

	public TagNode getNode(TagNode node, String tag) {
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			TagNode child = iter.next();
			if (child.tag().equals(tag)) {
				return child;
			}
		}
		return null;
	}

	public ArrayList<String> getUnvisited(TagNode node) {
		ArrayList<String> unvisited = new ArrayList<String>();
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			TagNode child = iter.next();
			if (!child.visited()) {
				unvisited.add(child.tag());
			}
		}
		return unvisited;
	}

	public void resetVisited() {
		resetVisited(root);
	}

	private void resetVisited(TagNode node) {
		node.setVisited(false);
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			resetVisited(iter.next());
		}
	}

}
