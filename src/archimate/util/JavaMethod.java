package archimate.util;

import archimate.codegen.ICodeElement;
import archimate.codegen.JavaHelper;

/**
 * Class modelling a java method
 * 
 * @author Samuel Esposito
 */
public class JavaMethod implements ICodeElement {

	public static final String DECLARATION = "declaration";
	public static final String IMPLEMENTATION = "implementation";
	public static final String INVOCATION = "invocation";

	private String name;

	private String type;

	private String archiMateTag;

	private String className;

	private String comment;

	public JavaMethod(String name, String tag, String type, String className) {
		this.name = name;
		archiMateTag = tag;
		this.type = type;
		this.className = className;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setArchiMateTag(String tag) {
		archiMateTag = tag;
	}

	public void setClassName(String name) {
		className = name;
	}

	public String name() {
		return name;
	}

	public String type() {
		return type;
	}

	public boolean archiMateTagsDefined() {
		return archiMateTag.length() > 0;
	}

	public String archiMateTag() {
		return archiMateTag;
	}

	public String invocationMethod() {
		return name + "Invocation";
	}

	public String invocationClass() {
		return className;
	}

	public String invocationObject() {
		return JavaHelper.camelize(className);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean commentDefined() {
		return comment.length() > 0;
	}

	public String comment() {
		return comment;
	}

}
