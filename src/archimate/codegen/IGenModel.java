package archimate.codegen;

import java.util.ArrayList;

public interface IGenModel {
	// returns the name of the containing folder for a source file
	public String targetFolder(String archiMateTag);
	// returns the file name for a source file
	public String targetFile(String archiMateTag);
	// returns the package name for a source file
	public String packageName(String archiMateTag);
	// returns the imports for a snippet of code
	public ArrayList<String> imports(String archiMateTag);
	// returns the comments for a class
	public String classComment(String archiMateTag);
	// returns the class name for a source file
	public String className(String archiMateTag);
	// returns a list of implemented interfaces
	public ArrayList<String> interfaces(String archiMateTag);
	// returns whether a source file contains a class or an interface
	public boolean isInterface(String archiMateTag);
	// returns the class of an object
	public String objectClass(String archiMateTag);
	// returns the name of an object
	public String objectName(String archiMateTag);
	// returns a list of methods in a source file
	public ArrayList<String> methods(String archiMateTag);
	// returns a list of methods invoking another method in a source file
	public ArrayList<String> methodInvocations(String archiMateTag);

}
