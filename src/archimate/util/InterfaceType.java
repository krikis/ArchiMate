package archimate.util;

/**
 * This class defines an implemented interface
 * 
 * @author Samuel Esposito
 */
public class InterfaceType {
	// The name of the interface
	private String interfaceName;
	// The package
	private String packageName;
	// Whether the interface is optional
	private boolean optional;

	/**
	 * Creates a new interface type
	 * 
	 * @param interfaceName
	 *            the name of the interface
	 * @param packageName
	 *            the package
	 * @param optional
	 *            whether implementing the interface is optional
	 */
	public InterfaceType(String interfaceName, String packageName,
			boolean optional) {
		this.interfaceName = interfaceName;
		this.packageName = packageName;
		this.optional = optional;
	}

	/**
	 * Returns the name of the interface type
	 * 
	 * @return the name of the interface type
	 */
	public String interfaceName() {
		return interfaceName;
	}

	/**
	 * Returns the package of the interface type
	 * 
	 * @return the package of the interface type
	 */
	public String packageName() {
		return packageName;
	}

	/**
	 * Returns whether implementing the interface is optional
	 * 
	 * @return whether implementing the interface is optional
	 */
	public boolean optional() {
		return optional;
	}

}
