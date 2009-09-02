package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.operations.ICompositeOperation;

import archimate.codegen.ICodeElement;

/**
 * This class implements the tree containing all <code>archiMateTags</code> in a
 * hierarchical structure
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
	// currently selected code
	private ICodeElement currentCode;
	// restricted interfaces
	ArrayList<Restriction> interfaces = new ArrayList<Restriction>();
	// restricted methods
	ArrayList<Restriction> methods = new ArrayList<Restriction>();

	/**
	 * Creates a new tree, sets the tree root and marks it as selected
	 */
	public TagTree() {
		root = new TagNode(ROOT);
		current = root;
	}

	/**
	 * Adds an interface name to the list of restricted interfaces
	 * 
	 * @param interfaceName
	 *            name of the restricted interface
	 * @param type
	 *            the type of the interface
	 * @param packageName
	 *            the name of the package
	 */
	public void addRestrictedInterface(String interfaceName, String type,
			String packageName) {
		interfaces.add(new Restriction(interfaceName, type, packageName));
	}

	/**
	 * Adds an method to the list of restricted methods
	 * 
	 * @param method
	 *            name of the restricted method
	 * @param type
	 *            the type of the class the method is defined in
	 * @param packageName
	 *            the package of the class the method is defined in
	 */
	public void addRestrictedMethod(String method, String type,
			String packageName) {
		methods.add(new Restriction(method, type, packageName));
	}

	/**
	 * Returns the list of restricted interfaces
	 * 
	 * @return The list of restricted interfaces
	 */
	public ArrayList<Restriction> restrictedInterfaces() {
		return interfaces;
	}

	/**
	 * Returns the list of restricted methods
	 * 
	 * @return The list of restricted methods
	 */
	public ArrayList<Restriction> restrictedMethods() {
		return methods;
	}

	/**
	 * @return the root of the tree
	 */
	public TagNode root() {
		return root;
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
	 * @return the node currently selected
	 */
	public TagNode current() {
		return current;
	}

	/**
	 * Sets the current code element to the given element
	 * 
	 * @param code
	 *            the code element to be marked as current
	 */
	public void setCurrentCode(ICodeElement code) {
		currentCode = code;
	}

	/**
	 * Returns the currently selected code element
	 * 
	 * @return The currently selected code element
	 */
	public ICodeElement currentCode() {
		return currentCode;
	}

	/**
	 * Returns the number of nodes in the tree
	 * 
	 * @return The number of nodes in the tree
	 */
	public int nodes() {
		return nodes(root);
	}

	// Recursively counts the number of nodes in the tree
	private int nodes(TagNode node) {
		int nodes = node.nrOfChildren();
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			nodes += nodes(iter.next());
		}
		return nodes;
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
	 * Searches the children of the current node for all unvisited nodes
	 * 
	 * @return A list of nodes which are unvisited
	 */
	public ArrayList<TagNode> getUnvisited() {
		ArrayList<TagNode> unvisited = new ArrayList<TagNode>();
		for (Iterator<TagNode> iter = current.children().iterator(); iter
				.hasNext();) {
			TagNode child = iter.next();
			if (!child.visited()) {
				unvisited.add(child);
			}
		}
		return unvisited;
	}

	/**
	 * Searches the tree for all unvisited nodes
	 * 
	 * @return A list of nodes which are unvisited
	 */
	public ArrayList<TagNode> getAllUnvisited() {
		ArrayList<TagNode> unvisited = new ArrayList<TagNode>();
		for (Iterator<TagNode> iter = root.children().iterator(); iter
				.hasNext();) {
			TagNode child = iter.next();
			if (!child.visited()) {
				unvisited.add(child);
			}
			unvisited.addAll(getAllUnvisited(child));
		}
		return unvisited;
	}

	// Recursively searches for all unvisited nodes in a tree
	private ArrayList<TagNode> getAllUnvisited(TagNode node) {
		ArrayList<TagNode> unvisited = new ArrayList<TagNode>();
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			TagNode child = iter.next();
			if (!child.visited()) {
				unvisited.add(child);
			}
			unvisited.addAll(getAllUnvisited(child));
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
		node.resetVisited();
		for (Iterator<TagNode> iter = node.children().iterator(); iter
				.hasNext();) {
			resetVisited(iter.next());
		}
	}

	// Returns a list of all tree nodes and their state for debug purposes
	public String toString() {
		return printRestrictions() + printNodes(root.children(), "");
	}

	// Returns a list of all restrictions for debug purposes
	private String printRestrictions() {
		String out = "";
		for (Restriction interfaceRestriction : interfaces) {
			out += interfaceRestriction.toString();
		}
		for (Restriction methodRestriction : methods) {
			out += methodRestriction.toString();
		}
		return out;
	}

	// Recursively returns the state of all nodes in the tree for debug purposes
	private String printNodes(ArrayList<TagNode> children, String prefix) {
		String out = "";
		for (Iterator<TagNode> iter = children.iterator(); iter.hasNext();) {
			TagNode node = iter.next();
			out += prefix + node.toString();
			out += printNodes(node.children(), prefix + "-");
		}
		return out;
	}

}
