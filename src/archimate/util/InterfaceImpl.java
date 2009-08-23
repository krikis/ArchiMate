package archimate.util;

public class InterfaceImpl {

	private String interfaceName;

	private String packageName;

	private boolean optional;

	public InterfaceImpl(String interfaceName, String packageName,
			boolean optional) {
		this.interfaceName = interfaceName;
		this.packageName = packageName;
		this.optional = optional;
	}

	public String interfaceName() {
		return interfaceName;
	}

	public String packageName() {
		return packageName;
	}

	// Returns whether the javaClass is optional
	public boolean optional() {
		return optional;
	}

}
