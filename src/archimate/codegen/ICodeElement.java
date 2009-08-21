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

}
