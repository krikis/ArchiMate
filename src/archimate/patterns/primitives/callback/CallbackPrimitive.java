package archimate.patterns.primitives.callback;

import java.util.ArrayList;

import archimate.codegen.ICodeElement;
import archimate.codegen.ICodeGenerator;
import archimate.patterns.Pattern;
import archimate.uml.UMLAdapter;
import archimate.util.JavaClass;
import archimate.util.JavaMethod;
import archimate.util.TagNode;
import archimate.util.TagTree;

/**
 * This class models the Callback design primitives.
 * 
 * @author Samuel Esposito
 */
public class CallbackPrimitive extends Pattern implements ICodeGenerator {

	// Constants for the key source elements of the Callback primitive
	// Caller Package
	public static final String EVENT_INTERFACE = "Callback_EventInterface";
	public static final String EVENT_INTERFACE_INSTANCE = "Callback_EventInterfaceInstance";
	public static final String EVENT_MESSAGE = "Callback_EventMessage";
	public static final String CALLER = "Callback_Caller";
	public static final String CALLER_INSTANCE = "Callback_CallerInstance";
	public static final String EVENT_METHOD = "Callback_EventMethod";
	public static final String SUBSCRIPTION_INVOCATION = "Callback_SubscriptionInvocation";
	// Callee Package
	public static final String SUBSCRIPTION_INTERFACE = "Callback_SubscriptionInterface";
	public static final String SUBSCRIPTION_INTERFACE_INSTANCE = "Callback_SubscriptionInterfaceInstance";
	public static final String SUBSCRIPTION_MESSAGE = "Callback_SubscriptionMessage";
	public static final String CALLEE = "Callback_Callee";
	public static final String CALLEE_INSTANCE = "Callback_CalleeInstance";
	public static final String SUBSCRIPTION_METHOD = "Callback_SubscriptionMethod";
	public static final String EVENT_INVOCATION = "Callback_EventInvocation";
	// Names of the packages in the primitive
	private String callerPackage;
	private String calleePackage;

	/**
	 * Constructor for the Callback primitive. Initializes a <TagTree> object and a
	 * <code>IGenModel</code> object with all settings for the current Java
	 * Project.
	 * 
	 * @param umlPackage
	 *            The UML package in the open UML or GMF editor
	 */
	public CallbackPrimitive(org.eclipse.uml2.uml.Package umlPackage) {
		// Set some configuration variables
		setVariables();
		// Set the UML reader
		umlReader = new UMLAdapter(umlPackage, "MVC");
		// Set the pattern name
		name = "Callback primitive";
		// Setup the tag tree
		constructTree();
	}

	// Sets the package names
	private void setVariables() {
		packageBase = "callback";
		callerPackage = packageBase + ".caller";
		calleePackage = packageBase + ".callee";
	}

	/**
	 * Constructs a tree defining the structure of the Callback primitive key elements
	 * 
	 * @return Tree defining the structure of the Callback primitive key elements
	 */
	private void constructTree() {
		tree = new TagTree();
		TagNode root = tree.root();

		// Create EventInterface
		TagNode eventInterface = eventInterface(root);
		// Create SubscriptionInterface
		TagNode subscriptionInterface = subscriptionInterface(root);

		// Create Caller Class
		TagNode callerClass = callerClass(root, eventInterface);
		// Create Callee Class
		TagNode calleeClass = calleeClass(root, subscriptionInterface);

		// Create EventInterface instance
		TagNode eventInterfaceInstance = eventInterfaceInstance(root,
				eventInterface);
		// Create SubscriptionInterface instance
		TagNode subscriptionInterfaceInstance = subscriptionInterfaceInstance(root,
				subscriptionInterface);

		// Create Caller instance Class
		TagNode callerInstanceClass = callerInstanceClass(root,
				eventInterfaceInstance, callerClass);
		// Create Callee instance Class
		TagNode calleeInstanceClass = calleeInstanceClass(root,
				subscriptionInterfaceInstance, calleeClass);

		// Add subscriptionInvocationMethods
		subscriptionInvocationMethods(callerInstanceClass, calleeInstanceClass);
		// Add eventInvocationMethods
		eventInvocationMethods(calleeInstanceClass, callerInstanceClass);
	}

	// Create EventInterface
	private TagNode eventInterface(TagNode root) {
		TagNode eventInterface = new TagNode(EVENT_INTERFACE);
		createClasses(eventInterface, "", callerPackage, null, false,
				JavaClass.INTERFACE, "IEvent", "", null, null,
				"This interface specifies the Event interface of the Callback primitive");
		root.addChild(eventInterface);
		return eventInterface;
	}

	// Create SubscriptionInterface
	private TagNode subscriptionInterface(TagNode root) {
		TagNode subscriptionInterface = new TagNode(SUBSCRIPTION_INTERFACE);
		createClasses(subscriptionInterface, "", calleePackage, null, false,
				JavaClass.INTERFACE, "ISubscription", "", null, null,
				"This interface specifies the Subscription interface of the Callback primitive");
		root.addChild(subscriptionInterface);
		return subscriptionInterface;
	}

	// Create Caller class implementing EventInterface
	private TagNode callerClass(TagNode root, TagNode eventInterface) {
		ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
		TagNode caller = new TagNode(CALLER);
		ICodeElement element = eventInterface.getSourceByTag(EVENT_INTERFACE);
		if (element instanceof JavaClass) {
			interfaces.add((JavaClass) element);
		}
		createClasses(caller, "", callerPackage, null, true, JavaClass.CLASS,
				"Caller", "", null, interfaces,
				"This class implements the Caller of the Callback primitive");
		root.addChild(caller);
		return caller;
	}

	// Create Callee class implementing SubscriptionInterface
	private TagNode calleeClass(TagNode root, TagNode subscriptionInterface) {
		TagNode Callee = new TagNode(CALLEE);
		ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
		ICodeElement element = subscriptionInterface.getSourceByTag(SUBSCRIPTION_INTERFACE);
		if (element instanceof JavaClass) {
			interfaces.add((JavaClass) element);
		}
		createClasses(Callee, "", calleePackage, null, true, JavaClass.CLASS,
				"Callee", "", null, interfaces,
				"This class implements the Callee of the Callback primitive");
		root.addChild(Callee);
		return Callee;
	}

	// Create EventInterface instance
	private TagNode eventInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode eventInterface = new TagNode(EVENT_INTERFACE_INSTANCE);
		root.addChild(eventInterface);
		JavaClass superClass = null;
		ICodeElement element = superInterface.getSourceByTag(EVENT_INTERFACE);
		if (element instanceof JavaClass) {
			superClass = (JavaClass) element;
		}
		createClasses(eventInterface, TagNode.inStereo(CALLER_INSTANCE),
				callerPackage, null, false, JavaClass.INTERFACE, "IMyEvent",
				"I#Event", superClass, null,
				"This interface specifies the #name#interface of the Callback primitive");
		// Create interface method declarations
		TagNode eventMessage = new TagNode(EVENT_MESSAGE);
		eventInterface.addChild(eventMessage);
		addMethods(eventMessage, "handleEvent", JavaMethod.DECLARATION,
				"This method handles the event passed from the Callee.");
		return eventInterface;
	}

	// Create SubscriptionInterface instance
	private TagNode subscriptionInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode subscriptionInterface = new TagNode(SUBSCRIPTION_INTERFACE_INSTANCE);
		root.addChild(subscriptionInterface);
		JavaClass superClass = null;
		ICodeElement element = superInterface.getSourceByTag(SUBSCRIPTION_INTERFACE);
		if (element instanceof JavaClass) {
			superClass = (JavaClass) element;
		}
		createClasses(subscriptionInterface, TagNode.inStereo(CALLEE_INSTANCE),
				calleePackage, null, false, JavaClass.INTERFACE, "IMySubscription",
				"ISubscription#", superClass, null,
				"This interface specifies the #name#interface of the Callback primitive");
		// Create interface method declarations
		TagNode subscriptionMessage = new TagNode(SUBSCRIPTION_MESSAGE);
		subscriptionInterface.addChild(subscriptionMessage);
		addMethods(subscriptionMessage, "subscribe", JavaMethod.DECLARATION,
				"This method subscribes the caller to an event.");
		return subscriptionInterface;
	}

	// Create Caller instance class implementing EventInterface instance
	private TagNode callerInstanceClass(TagNode root, TagNode eventInterface,
			TagNode superClass) {
		TagNode caller = new TagNode(CALLER_INSTANCE);
		TagNode eventMethods = new TagNode(EVENT_METHOD);
		caller.addChild(eventMethods);
		root.addChild(caller);
		int count = 0;
		for (ICodeElement element : eventInterface.source()) {
			if (element instanceof JavaClass) {
				JavaClass eventInterfaceClass = (JavaClass) element;
				String name = "";
				if (eventInterfaceClass.umlElements().size() > 0) {
					name = eventInterfaceClass.umlElements().get(0).getName();
				}
				String className = (name.equals("") ? "MyCaller"
						+ (count == 0 ? "" : count) : name);
				ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
				interfaces.add(eventInterfaceClass);
				JavaClass superClassType = null;
				ICodeElement codeElement = superClass.getSourceByTag(CALLER);
				if (codeElement instanceof JavaClass) {
					superClassType = (JavaClass) codeElement;
				}
				JavaClass javaClass = createClass(caller, eventInterfaceClass
						.umlElements(), callerPackage, null, JavaClass.CLASS,
						className, superClassType, interfaces,
						"This class implements a Caller of the Callback primitive",
						name.equals(""));
				// Create class methods
				addMethods(eventMethods, javaClass, eventInterfaceClass,
						JavaMethod.IMPLEMENTATION,
						"This method implements handling an event.");
				++count;
			}
		}
		return caller;
	}

	// Create Callee instance class implementing SubscriptionInterface instance
	private TagNode calleeInstanceClass(TagNode root, TagNode subscriptionInterface,
			TagNode superClass) {
		TagNode callee = new TagNode(CALLEE_INSTANCE);
		TagNode subscriptionMethod = new TagNode(SUBSCRIPTION_METHOD);
		callee.addChild(subscriptionMethod);
		root.addChild(callee);
		int count = 0;
		for (ICodeElement element : subscriptionInterface.source()) {
			if (element instanceof JavaClass) {
				JavaClass subscriptionInterfaceClass = (JavaClass) element;
				String name = "";
				if (subscriptionInterfaceClass.umlElements().size() > 0) {
					name = subscriptionInterfaceClass.umlElements().get(0).getName();
				}
				String className = (name.equals("") ? "MyCallee"
						+ (count == 0 ? "" : count) : name);
				ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
				interfaces.add(subscriptionInterfaceClass);
				JavaClass superClassType = null;
				ICodeElement codeElement = superClass.getSourceByTag(CALLEE);
				if (codeElement instanceof JavaClass) {
					superClassType = (JavaClass) codeElement;
				}
				JavaClass javaClass = createClass(callee, subscriptionInterfaceClass
						.umlElements(), calleePackage, null, JavaClass.CLASS,
						className, superClassType, interfaces,
						"This class implements a Callee of the Callback primitive", name
								.equals(""));
				// Create class methods
				addMethods(subscriptionMethod, javaClass, subscriptionInterfaceClass,
						JavaMethod.IMPLEMENTATION,
						"This method implements subscribing a caller to an event.");
				++count;
			}
		}
		return callee;
	}

	// Create subscription invocation methods
	private void subscriptionInvocationMethods(TagNode caller, TagNode callee) {
		TagNode subscriptionInvocation = new TagNode(SUBSCRIPTION_INVOCATION);
		caller.addChild(subscriptionInvocation);
		for (ICodeElement element : caller.source()) {
			if (element instanceof JavaClass) {
				addMethods(subscriptionInvocation, TagNode.inStereo(SUBSCRIPTION_MESSAGE),
						(JavaClass) element, callee, JavaMethod.INVOCATION,
						"This method invokes a method that subscribes the caller to an event at the callee.");
			}
		}
	}

	// Create event invocation methods
	private void eventInvocationMethods(TagNode callee, TagNode caller) {
		TagNode eventInvocation = new TagNode(EVENT_INVOCATION);
		callee.addChild(eventInvocation);
		for (ICodeElement element : callee.source()) {
			if (element instanceof JavaClass) {
				addMethods(eventInvocation,
						TagNode.inStereo(EVENT_MESSAGE), (JavaClass) element,
						caller, JavaMethod.INVOCATION,
						"This method invokes a method that handles an event a the caller.");
			}
		}
	}
}
