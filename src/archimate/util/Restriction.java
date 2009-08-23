package archimate.util;

/**
 * This class implements a restricted source code element. A restricted element
 * can only be accessed the way the pattern or primitive defined it. An
 * interface for instance can only be implemented by certain classes. A method
 * can only be invoked in certain classes.
 * 
 * @author Samuel Esposito
 */
public class Restriction {
	// The name of the restricted code element
	private String name;
	// The type of the restricted code element
	private String type;
	// The package of the restricted code element
	private String packageName;

	/**
	 * Creates a new restriction
	 * 
	 * @param name
	 *            The name of the restricted code element
	 * @param type
	 *            The type of the restricted code element
	 * @param packageName
	 *            The package of the restricted code element
	 */
	public Restriction(String name, String type, String packageName) {
		this.name = name;
		this.type = type;
		this.packageName = packageName;
	}

	/**
	 * Returns the name of the restricted element
	 * 
	 * @return The name of the restricted element
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the type of the restricted element
	 * 
	 * @return The type of the restricted element
	 */
	public String type() {
		return type;
	}

	/**
	 * Returns the package of the restricted element
	 * 
	 * @return The package of the restricted element
	 */
	public String packageName() {
		return packageName;
	}

	/*
	 * (non-Javadoc) prints the restricted elements type and name for debugging
	 * purposes
	 */
	public String toString() {
		return packageName + "." + type + " | " + name;
	}
}
