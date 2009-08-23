package archimate.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import archimate.codegen.ICodeElement;
import archimate.codegen.JavaHelper;

/**
 * Class modeling a java method
 * 
 * @author Samuel Esposito
 */
public class JavaMethod implements ICodeElement {
	// Constants defining the methods type
	public static final String DECLARATION = "declaration";
	public static final String IMPLEMENTATION = "implementation";
	public static final String INVOCATION = "invocation";
	// Whether the code element has been visited
	private boolean visited;
	// Whether the code element is optional
	private boolean optional;
	// The method name
	private String name;
	// The type of the method, either DECLARATION, IMPLEMENTATION or INVOCATION
	private String type;
	// The packagename of the class in which the method is implemented
	private String packageName;
	// The archiMateTag corresponding with this method
	private String archiMateTag;
	// The name of the class in which the method is implemented
	private String className;
	// The comment going with the method
	private String comment;

	/**
	 * Creates a {@link JavaMethod} object
	 * 
	 * @param name
	 *            the name of the method
	 * @param tag
	 *            The archiMateTag corresponding with this method
	 * @param type
	 *            The type of the method, either DECLARATION, IMPLEMENTATION or
	 *            INVOCATION
	 * @param className
	 *            The name of the class in which the method is implemented
	 * @param packageName
	 *            The packagename of the class in which the method is
	 *            implemented
	 */
	public JavaMethod(String name, String tag, String type, String className,
			String packageName) {
		visited = false;
		optional = false;
		this.name = name;
		archiMateTag = tag;
		this.type = type;
		this.className = className;
		this.packageName = packageName;
	}

	// Returns the method name
	public String identifier() {
		if (type.equals(INVOCATION))
			return invocationMethod();
		return name;
	}

	// Method defining whether the java method matches the identifier
	public boolean equals(String identifier) {
		if (name.equals(identifier)) {
			return true;
		} else {
			return invocationMethod().equals(identifier);
		}
	}

	// Compares itself with the given ASTNode for differences
	public void diff(ASTNode node, MultiStatus status, String pattern) {
		if (node instanceof MethodDeclaration) {
			MethodDeclaration method = (MethodDeclaration) node;
			ASTNode parent = method.getParent();
			if (parent instanceof TypeDeclaration) {
				TypeDeclaration javaClass = (TypeDeclaration) parent;
				// Checks the type of the method
				checkType(method, javaClass, status, pattern);
			}
		}
	}

	// Checks the type of the method (declaration, implementation, invocation)
	private void checkType(MethodDeclaration method, TypeDeclaration javaClass,
			MultiStatus status, String pattern) {
		if (type.equals(INVOCATION)) {
			boolean invocation = false;
			Block methodBlock = method.getBody();
			if (methodBlock != null) {
				ArrayList<String> variables = new ArrayList<String>();
				for (Iterator iter = methodBlock.statements().iterator(); iter
						.hasNext();) {
					Object object = iter.next();
					String var = checkVariable(object);
					if (var != null) {
						variables.add(var);
					}
					invocation = checkInvocation(object, variables);
				}
			}
			if (!invocation) {
				reportTypeError(status, pattern);
			}
		}
	}

	// Reports the occurrence of a method type error
	private void reportTypeError(MultiStatus status, String pattern) {
		status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, pattern
				+ ": The method \"" + invocationMethod()
				+ "()\" doesn't implement the invocation of the method \""
				+ name + "()\".                          ", null));
	}

	// Checks the invocation of a method
	private boolean checkInvocation(Object object, ArrayList<String> variables) {
		if (object instanceof ExpressionStatement) {
			ExpressionStatement statement = (ExpressionStatement) object;
			if (statement.getExpression() instanceof MethodInvocation) {
				MethodInvocation invocation = (MethodInvocation) statement
						.getExpression();
				if (invocation.getName().getFullyQualifiedName().equals(name)) {
					if (invocation.getExpression() instanceof SimpleName) {
						SimpleName simpleName = (SimpleName) invocation
								.getExpression();
						String objectName = simpleName.getFullyQualifiedName();
						for (Iterator<String> iter = variables.iterator(); iter
								.hasNext();) {
							if (iter.next().equals(objectName)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	// Checks a variable declaration
	private String checkVariable(Object object) {
		String varName = null;
		boolean varType = false;
		boolean instance = false;
		if (object instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement variable = (VariableDeclarationStatement) object;
			// Get variable name
			if (variable.fragments().size() == 1) {
				Object fragmentObject = variable.fragments().get(0);
				if (fragmentObject instanceof VariableDeclarationFragment) {
					VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragmentObject;
					varName = fragment.getName().getFullyQualifiedName();
					// Check variable instantiation
					instance = checkInstance(fragment.getInitializer());
				}
			}
			// Check variable type
			if (variable.getType() instanceof SimpleType) {
				SimpleType type = (SimpleType) variable.getType();
				if (type.getName().getFullyQualifiedName().equals(className())) {
					varType = true;
				}
			}
		}
		if (varType && instance)
			return varName;
		return null;
	}

	// Checks a variable instantiation
	private boolean checkInstance(Expression expression) {
		if (expression instanceof ClassInstanceCreation) {
			ClassInstanceCreation instance = (ClassInstanceCreation) expression;
			if (instance.getType() instanceof SimpleType) {
				SimpleType type = (SimpleType) instance.getType();
				if (type.getName().getFullyQualifiedName().equals(className()))
					return true;
			}
		}
		return false;
	}

	// Returns whether the java method has been visited in the source code
	public boolean visited() {
		return visited;
	}

	// Marks the java method element as visited
	public void setVisited() {
		visited = true;
	}

	// Returns whether the javaClass is optional
	public boolean optional() {
		return optional;
	}

	// Marks the javaClass as optional
	public void setOptional() {
		optional = true;
	}

	/**
	 * Sets the method name
	 * 
	 * @param name
	 *            the method name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the method type
	 * 
	 * @param type
	 *            the method type, either DECLARATION, IMPLEMENTATION or
	 *            INVOCATION
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the method archiMateTag
	 * 
	 * @param tag
	 *            the method archiMateTag
	 */
	public void setArchiMateTag(String tag) {
		archiMateTag = tag;
	}

	/**
	 * Sets the name of the class the method is implemented in
	 * 
	 * @param name
	 *            the name of the class the method is implemented in
	 */
	public void setClassName(String name) {
		className = name;
	}

	/**
	 * Returns the name of the method
	 * 
	 * @return the name of the method
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the method package
	 * 
	 * @return the method package
	 */
	public String packageName() {
		return packageName;
	}

	/**
	 * Returns the method type
	 * 
	 * @return the method type
	 */
	public String type() {
		return type;
	}

	/*
	 * (non-Javadoc) Returns whether the method has any archiMateTags
	 * 
	 * @see archimate.codegen.ICodeElement#archiMateTagsDefined()
	 */
	public boolean archiMateTagsDefined() {
		return archiMateTag.length() > 0;
	}

	/*
	 * (non-Javadoc) Returns the methods archiMateTag
	 * 
	 * @see archimate.codegen.ICodeElement#archiMateTag()
	 */
	public String archiMateTag() {
		return archiMateTag;
	}

	/**
	 * Returns the name of the invoking method
	 * 
	 * @return the name of the invoking method
	 */
	public String invocationMethod() {
		return name + "Invocation";
	}

	/**
	 * Returns the name of the class the method was implemented in
	 * 
	 * @return the name of the class the method was implemented in
	 */
	public String className() {
		return className;
	}

	/**
	 * Returns the object on which the method is invoked
	 * 
	 * @return the object on which the method is invoked
	 */
	public String invocationObject() {
		return JavaHelper.camelize(className);
	}

	/**
	 * Sets the comment going with the method
	 * 
	 * @param comment
	 *            the comment going with the method
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/*
	 * (non-Javadoc) Whether a comment is defined or not
	 * 
	 * @see archimate.codegen.ICodeElement#commentDefined()
	 */
	public boolean commentDefined() {
		return comment.length() > 0;
	}

	/*
	 * (non-Javadoc) Returns the comment going with the method
	 * 
	 * @see archimate.codegen.ICodeElement#comment()
	 */
	public String comment() {
		return comment;
	}

}
