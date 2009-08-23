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

	public static final String DECLARATION = "declaration";
	public static final String IMPLEMENTATION = "implementation";
	public static final String INVOCATION = "invocation";

	private boolean visited;

	private boolean optional;

	private String name;

	private String type;

	private String packageName;

	private String archiMateTag;

	private String className;

	private String comment;

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
				status
						.add(new Status(
								IStatus.ERROR,
								status.getPlugin(),
								1,
								pattern
										+ ": The method \""
										+ invocationMethod()
										+ "()\" doesn't implement the invocation of the method \""
										+ name
										+ "()\".                          ",
								null));
			}
		}
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
				if (type.getName().getFullyQualifiedName().equals(
						invocationClass())) {
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
				if (type.getName().getFullyQualifiedName().equals(
						invocationClass()))
					return true;
			}
		}
		System.out.println(expression.getClass());
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

	// Returns the method package
	public String packageName() {
		return packageName;
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
