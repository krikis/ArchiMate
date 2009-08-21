package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import archimate.codegen.ICodeElement;

/**
 * This class implements a node of a {@link TagTree}
 * 
 * @author Samuel Esposito
 * 
 */
public class TagNode {
	// the nodes parent
	private TagNode parent;
	// the nodes children
	private ArrayList<TagNode> children;
	// the nodes tag
	private String tag;
	// whether the node has been visited
	private boolean visited = false;
	// the associated source code elements
	private ArrayList<ICodeElement> source;
	// count of the unvisited source elements
	private int unvisited;

	/**
	 * Creates a new node with the given tag
	 * 
	 * @param tag
	 *            Tag for the new node
	 */
	public TagNode(String tag) {
		this.tag = tag;
		children = new ArrayList<TagNode>();
		source = new ArrayList<ICodeElement>();
		unvisited = 0;
	}

	/**
	 * @return The nodes children
	 */
	public ArrayList<TagNode> children() {
		return children;
	}

	/**
	 * Returns the number of children in a node
	 * 
	 * @return The number of children in a node
	 */
	public int nrOfChildren() {
		return children.size();
	}

	/**
	 * Adds a child to the node
	 * 
	 * @param node
	 *            The new child
	 */
	public void addChild(TagNode node) {
		node.parent = this;
		children.add(node);
	}

	/**
	 * @return The nodes parent
	 */
	public TagNode parent() {
		return parent;
	}

	/**
	 * Sets the nodes parent
	 * 
	 * @param parent
	 *            The nodes parent
	 */
	public void setParent(TagNode parent) {
		this.parent = parent;
	}

	/**
	 * @return The nodes tag
	 */
	public String tag() {
		return tag;
	}

	/**
	 * @return Whether the node has been visited or not
	 */
	public boolean visited() {
		return visited;
	}

	/**
	 * Sets the visited state to true
	 */
	public void resetVisited() {
		visited = false;
	}

	/**
	 * Sets the visited state to true
	 */
	public void setVisited() {
		if (unvisited == 0)
			visited = true;
	}

	/**
	 * @return Whether the node has children or not
	 */
	public boolean hasChildren() {
		return children.size() > 0;
	}

	/**
	 * Searches the nodes children for a child with a certain tag
	 * 
	 * @param tag
	 *            The tag to match
	 * @return Whether the node has a child with a matching tag
	 */
	public boolean hasChild(String tag) {
		for (Iterator<TagNode> iter = children.iterator(); iter.hasNext();) {
			if (iter.next().tag().equals(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the node has a parent or not
	 * 
	 * @return Whether the node has a parent or not
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Adds a {@link ICodeElement} to the list of source elements
	 * 
	 * @param code
	 *            {@link ICodeElement} that will be added to the list of source
	 *            elements
	 */
	public void addSource(ICodeElement code) {
		source.add(code);
		++unvisited;
	}

	/**
	 * Returns the list of {@link ICodeElement}s
	 * 
	 * @return The list of {@link ICodeElement}s
	 */
	public ArrayList<ICodeElement> source() {
		return source;
	}

	/**
	 * Marks the encountered source as visited
	 * 
	 * @param name
	 *            The name identifying the encountered source
	 */
	public void tickOffSource(String name) {
		for (Iterator<ICodeElement> iter = source.iterator(); iter.hasNext();) {
			ICodeElement element = iter.next();
			if (element.equals(name)) {
				element.setVisited();
				--unvisited;
			}
		}
	}

}
