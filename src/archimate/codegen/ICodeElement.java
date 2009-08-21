package archimate.codegen;

public interface ICodeElement {

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
	 * Method defining whether a source element matches the identifier
	 * 
	 * @param identifyer
	 *            String to match the source element with
	 * @return Whether the source element matches the identifier
	 */
	public boolean equals(String identifier);

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

}
