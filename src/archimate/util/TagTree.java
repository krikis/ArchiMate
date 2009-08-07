package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class implements the tree containing all <code>archiMateTags</code> in a hierarchical
 * structure
 * 
 * @author Samuel Esposito
 * 
 */
public class TagTree {
	/**
	 * Tree root tag
	 */
	public static final String ROOT = "root";
	// tree root
	private TagNode root;
	// currently selected node
	private TagNode current;

	/**
	 * Creates a new tree, sets the tree root and marks it as selected
	 */
	public TagTree() {
		root = new TagNode(ROOT);
		current = root;
	}

	/**
	 * @return the root of the tree
	 */
	public TagNode root() {
		return root;
	}

	/**
	 * @return the node currently selected
	 */
	public TagNode current() {
		return current;
	}

	/**
	 * Marks a node as selected
	 * 
	 * @param node
	 *            the node you want to select
	 */
	public void setCurrent(TagNode node) {
		if (node != null)
			current = node;
	}

	/**
	 * Prints a list of all tree nodes and their state to the standard output
	 * for debug purposes
	 */
	public void printTree() {
		printNodes(root.children());
	}

	// Prints debug information for all children of a node
	private void printNodes(ArrayList<TagNode> children) {
		for (Iterator<TagNode> iter = children.iterator(); iter.hasNext();) {
			TagNode node = iter.next();
			System.out.println(node.tag()
					+ (node.visited() ? " :: visited" : ""));
			printNodes(node.children());
		}
	}

	/**
	 * Searches for a node with a certain tag in the nodes children
	 * 
	 * @param node
	 *            The node which children are searched
	 * @param tag
	 *            The tag to match
	 * @return A child having a matching tag
	 */
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

	/**
	 * Searches the node children for all unvisited nodes
	 * 
	 * @param node
	 *            The node which children are searched
	 * @return A list of nodes which are unvisited
	 */
	public static ArrayList<String> getUnvisited(TagNode node) {
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

	/**
	 * Marks all nodes in the tree as visited
	 */
	public void resetVisited() {
		resetVisited(root);
	}

	// Recursive method setting the state of all nodes in a tree to unvisited
	private void resetVisited(TagNode node) {
		node.setVisited(false);
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			resetVisited(iter.next());
		}
	}

}
