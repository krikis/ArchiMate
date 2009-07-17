package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

public class TagNode {

	private TagNode parent;

	private ArrayList<TagNode> children;

	private String tag;

	private boolean visited = false;

	public TagNode(String tag) {
		this.tag = tag;
		children = new ArrayList<TagNode>();
	}

	public ArrayList<TagNode> children() {
		return children;
	}

	public void addChild(TagNode node) {
		node.parent = this;
		children.add(node);
	}

	public TagNode parent() {
		return parent;
	}

	public void setParent(TagNode parent) {
		this.parent = parent;
	}

	public String tag() {
		return tag;
	}

	public boolean visited() {
		return visited;
	}

	public void setVisited(boolean value) {
		visited = value;
	}
	
	public boolean hasChildren(){
		return children.size() > 0;
	}

	public boolean hasChild(String tag) {
		for (Iterator<TagNode> iter = children.iterator(); iter.hasNext();) {
			if (iter.next().tag().equals(tag)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
}
