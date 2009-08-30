package archimate.util;

/**
 * This class defines an implemented interface
 * 
 * @author Samuel Esposito
 */
public class SuperClassType {
	// The name of the interface
	private String className;
	// The package
	private String packageName;
	// Whether the interface is optional
	private boolean optional;

	/**
	 * Creates a new superclass type
	 * 
	 * @param className
	 *            the name of the superclass
	 * @param packageName
	 *            the package
	 * @param optional
	 *            whether extending the superclass is optional
	 */
	public SuperClassType(String className, String packageName, boolean optional) {
		this.className = className;
		this.packageName = packageName;
		this.optional = optional;
	}

	/**
	 * Returns the name of the superclass
	 * 
	 * @return the name of the superclass
	 */
	public String className() {
		return className;
	}

	/**
	 * Returns the package of the superclass
	 * 
	 * @return the package of the superclass
	 */
	public String packageName() {
		return packageName;
	}

	/**
	 * Returns whether extending the superclass is optional
	 * 
	 * @return whether extending the superclass is optional
	 */
	public boolean optional() {
		return optional;
	}

}
