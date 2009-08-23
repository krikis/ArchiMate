package archimate.util;

/**
 * This class defines an implemented interface
 * 
 * @author Samuel Esposito
 */
public class InterfaceImpl {
	// The name of the interface
	private String interfaceName;
	// The package
	private String packageName;
	// Whether the interface is optional
	private boolean optional;

	/**
	 * Creates a new implemented interface
	 * 
	 * @param interfaceName
	 *            the name of the interface
	 * @param packageName
	 *            the package
	 * @param optional
	 *            whether the interface is optional
	 */
	public InterfaceImpl(String interfaceName, String packageName,
			boolean optional) {
		this.interfaceName = interfaceName;
		this.packageName = packageName;
		this.optional = optional;
	}

	/**
	 * Returns the name of the implemented interface
	 * 
	 * @return the name of the implemented interface
	 */
	public String interfaceName() {
		return interfaceName;
	}

	/**
	 * Returns the package of the interface
	 * 
	 * @return the package of the interface
	 */
	public String packageName() {
		return packageName;
	}

	/**
	 * Returns whether the interface is optional
	 * 
	 * @return whether the interface is optional
	 */
	public boolean optional() {
		return optional;
	}

}
