package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.uml2.uml.NamedElement;

/**
 * Interface defining a source code element
 * 
 * @author Samuel Esposito
 */
public interface ICodeElement {

	/**
	 * Returns whether there are archiMateTags defined
	 * 
	 * @return Whether there are archiMateTags defined
	 */
	public boolean archiMateTagsDefined();

	/**
	 * Returns the code elements first archiMateTag
	 * 
	 * @return The code elements first archiMateTag
	 */
	public String archiMateTag();

	/**
	 * Returns the identifier of the source code element
	 * 
	 * @return The identifier of the source code element
	 */
	public String identifier();

	/**
	 * Returns the package the source code element is in
	 * 
	 * @return the package the source code element is in
	 */
	public String packageName();

	/**
	 * Method defining whether a source element matches the identifier and
	 * package name
	 * 
	 * @param identifier
	 *            String to match the source element with
	 * @param packageName
	 *            the package name
	 * @return Whether the source element matches the identifier
	 */
	public boolean equals(String identifier, String packageName);

	/**
	 * Method defining whether a source element matches the given source
	 * 
	 * @param element
	 *            a source element
	 * @return Whether the source element matches the given source
	 */
	public boolean equals(ICodeElement element);

	/**
	 * Compares itself to the {@link ASTNode} and writes the eventual
	 * differences in the {@link MultiStatus} object
	 * 
	 * @param node
	 *            the {@link ASTNode} to compare with
	 * @param status
	 *            the {@link MultiStatus} to write the differences in
	 */
	public void diff(ASTNode node, MultiStatus status, String pattern);

	/**
	 * Returns whether a source element has been visited in the source code
	 * 
	 * @return Whether a source element has been visited in the source code
	 */
	public boolean visited();

	/**
	 * Marks a source code element as visited
	 */
	public void setVisited();

	/**
	 * Returns whether a code element has to be visited
	 * 
	 * @return Whether a code element has to be visited
	 */
	public boolean optional();

	/**
	 * Marks a source code element as optional
	 */
	public void setOptional(boolean value);

	/**
	 * Adds a UML element to the code element
	 * 
	 * @param umlElement
	 *            the UML element to be added
	 */
	public void addUmlElement(NamedElement umlElement);

	/**
	 * Adds a collection of UML elements to the code element
	 * 
	 * @param umlElements
	 *            the UML elements to be added
	 */
	public void addUmlElements(ArrayList<NamedElement> umlElements);

	/**
	 * Returns the UML element associated with the code element
	 * 
	 * @return the UML element associated with the code element
	 */
	public ArrayList<NamedElement> umlElements();

	/**
	 * Returns the parent of the code element
	 * 
	 * @return The parent of the code element
	 */
	public ICodeElement parent();

	/**
	 * Sets the parent of the code element
	 */
	public void setParent(ICodeElement parent);

	/**
	 * Returns the children of the code element
	 * 
	 * @return The children of the code element
	 */
	public ArrayList<ICodeElement> children();

	/**
	 * Adds the given code element to the children of the code element
	 * 
	 * @param child
	 *            the code element to add
	 */
	public void addChild(ICodeElement child);

	/**
	 * Adds a collection of code elements to the children of the code element
	 * 
	 * @param children
	 *            the collection of code elements to add
	 */
	public void addChildren(ArrayList<ICodeElement> children);

	/**
	 * Sets the comment going with the method
	 * 
	 * @param comment
	 *            the comment going with the method
	 */
	public void setComment(String comment);

	/**
	 * Returns whether there is a comment defined
	 * 
	 * @return Whether there is a comment defined
	 */
	public boolean commentDefined();

	/**
	 * Returns the code elements comments
	 * 
	 * @return The code elements comments
	 */
	public String comment();

}
