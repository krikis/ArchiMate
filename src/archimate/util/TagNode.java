package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

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
	// wether the node has been visited
	private boolean visited = false;

	/**
	 * Creates a new node with the given tag
	 * 
	 * @param tag
	 *            Tag for the new node
	 */
	public TagNode(String tag) {
		this.tag = tag;
		children = new ArrayList<TagNode>();
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
	 * Sets the nodes state
	 * 
	 * @param value
	 *            The new value for the nodes state
	 */
	public void setVisited(boolean value) {
		visited = value;
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
	 * @return Whether the node has a parent or not
	 */
	public boolean hasParent() {
		return parent != null;
	}
}
