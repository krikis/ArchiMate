package archimate.codegen;

import java.util.ArrayList;

public interface IGenModel {

	/**
	 * Returns the type of the tagged source element. Types can either be one of
	 * {@link JavaHelper#METHOD_DECLARATION},
	 * {@link JavaHelper#METHOD_IMPLEMENTATION} or
	 * {@link JavaHelper#METHOD_INVOCATION}.
	 * 
	 * @param archiMateTag
	 *            The tag of the source element at hand
	 * @return the type of the tagged source element
	 */
	public String sourceType(String archiMateTag);

	/**
	 * Returns the project source folder
	 * 
	 * @return The project source folder
	 */
	public String targetFolder();

	/**
	 * Returns the source folder package base
	 * 
	 * @return The source folder package base
	 */
	public String packageBase();

	/**
	 * Returns the file name for a source file
	 * 
	 * @param archiMateTag
	 *            The tag labeling the class in the source file
	 * @return The file name for a source file
	 */
	public String targetFile(String archiMateTag);

	/**
	 * Returns the package name for a source file
	 * @param archiMateTag
	 *            The tag labeling the class in the source file
	 * @return The package name for a source file
	 */
	public String packageName(String archiMateTag);

	/**
	 * Returns the imports for a snippet of code
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return The imports for a snippet of code
	 */
	public ArrayList<String> imports(String archiMateTag);

	/**
	 * Returns the comments for a class
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return The comments for a class
	 */
	public String classComment(String archiMateTag);

	/**
	 * Returns the class name for a source file
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return The class name for a source file
	 */
	public String className(String archiMateTag);

	/**
	 * Returns a list of implemented interfaces
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return A list of implemented interfaces
	 */
	public ArrayList<String> interfaces(String archiMateTag);

	/**
	 * Returns whether a source file contains a class or an interface
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return Whether a source file contains a class or an interface
	 */
	public boolean isInterface(String archiMateTag);

	/**
	 * Returns the class of an object
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return The class of an object
	 */
	public String objectClass(String archiMateTag);

	/**
	 * Returns the name of an object
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return The name of an object
	 */
	public String objectName(String archiMateTag);

	/**
	 * Returns a list of methods in a source file
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return A list of methods in a source file
	 */
	public ArrayList<String> methods(String archiMateTag);

	/**
	 * Returns a list of methods invoking another method in a source file
	 * @param archiMateTag
	 *            The tag labeling the key source element
	 * @return A list of methods invoking another method in a source file
	 */
	public ArrayList<String> methodInvocations(String archiMateTag);

}
