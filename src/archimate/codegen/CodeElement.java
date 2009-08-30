package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.uml2.uml.NamedElement;

public abstract class CodeElement implements ICodeElement {

	// The associated UML element
	protected NamedElement umlElement;
	// The parent of the java class
	protected ICodeElement parent;
	// The children of the java class
	protected ArrayList<ICodeElement> children;
	// Whether the class has been visited
	protected boolean visited;
	// Whether the class is an optional code element
	protected boolean optional;
	// The comment going with the method
	protected String comment;

	// Sets the UML element associated with the code element
	public void setUmlElement(NamedElement umlElement) {
		this.umlElement = umlElement;
	}

	// Returns the UML element associated with the code element
	public NamedElement umlElement() {
		return umlElement;
	}

	// Returns the parent of the code element
	public ICodeElement parent() {
		return parent;
	}

	// Sets the parent of the code element
	public void setParent(ICodeElement parent) {
		this.parent = parent;
	}

	// Returns the children of the code element
	public ArrayList<ICodeElement> children() {
		return children;
	}

	// Adds the given code element to the children of the code element
	public void addChild(ICodeElement child) {
		child.setParent(this);
		children.add(child);
	}

	// Adds a collection of code elements to the children of the code element
	public void addChildren(ArrayList<ICodeElement> children) {
		for (ICodeElement element : children) {
			addChild(element);
		}
	}

	// Returns whether the java class has been visited in the source code
	public boolean visited() {
		return visited;
	}

	// Marks the java class as visited
	public void setVisited() {
		visited = true;
	}

	// Returns whether the javaClass is optional
	public boolean optional() {
		return optional;
	}

	// Marks the javaClass as optional
	public void setOptional(boolean value) {
		optional = value;
	}

	// Sets the comment going with the method
	public void setComment(String comment) {
		this.comment = comment;
	}

	// Whether a comment is defined or not
	public boolean commentDefined() {
		return comment.length() > 0;
	}

	// Returns the comment going with the method
	public String comment() {
		return comment;
	}

}
