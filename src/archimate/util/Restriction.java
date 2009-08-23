package archimate.util;

public class Restriction {

	private String name;

	private String type;

	private String packageName;

	public Restriction(String name, String type, String packageName) {
		this.name = name;
		this.type = type;
		this.packageName = packageName;
	}

	public String name() {
		return name;
	}

	public String type() {
		return type;
	}

	public String packageName() {
		return packageName;
	}

	public String toString() {
		return packageName + "." + type + " | " + name;
	}
}
