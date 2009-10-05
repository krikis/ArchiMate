package archimate.patterns.primitives.callback;

import java.util.ArrayList;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.uml2.uml.NamedElement;

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
	public static final String EVENT_INTERFACE = "Callback_Event" + INTERFACE;
	public static final String EVENT_INTERFACE_INSTANCE = "Callback_Event"
			+ INTERFACE + INSTANCE;
	public static final String EVENT_MESSAGE = "Callback_Event" + MESSAGE;
	public static final String CALLER = "Callback_Caller";
	public static final String CALLER_INSTANCE = "Callback_Caller" + INSTANCE;
	public static final String EVENT_METHOD = "Callback_Event" + METHOD;
	public static final String SUBSCRIPTION_INVOCATION = "Callback_Subscription"
			+ INVOCATION;
	// Callee Package
	public static final String SUBSCRIPTION_INTERFACE = "Callback_Subscription"
			+ INTERFACE;
	public static final String SUBSCRIPTION_INTERFACE_INSTANCE = "Callback_Subscription"
			+ INTERFACE + INSTANCE;
	public static final String SUBSCRIPTION_MESSAGE = "Callback_Subscription"
			+ MESSAGE;
	public static final String CALLEE = "Callback_Callee";
	public static final String CALLEE_INSTANCE = "Callback_Callee" + INSTANCE;
	public static final String SUBSCRIPTION_METHOD = "Callback_Subscription"
			+ METHOD;
	public static final String EVENT_INVOCATION = "Callback_Event" + INVOCATION;
	// Names of the packages in the primitive
	private String callerPackage;
	private String calleePackage;

	/**
	 * Constructor adding the Callback {@link TagNode} elements to the given
	 * tree
	 * 
	 * @param tree
	 *            the {@link TagTree} to add the {@link TagNode}s to
	 */
	public CallbackPrimitive(org.eclipse.uml2.uml.Package umlPackage,
			TagTree tree, MultiStatus status) {
		// Set the TagTree
		this.tree = tree;
		// Set the status
		this.status = status;
		// Set the UML reader
		umlReader = new UMLAdapter(umlPackage, "CallBack");
		// Set the pattern name
		name = "Callback primitive";
		// Set some configuration variables
		setVariables();
		// Update the TagTree
		updateTree();
	}

	// Updates the TagTree to make it contain the callback primitive
	private void updateTree() {
//		 System.out.println(tree);

		TagNode root = tree.root();
		// Find Caller Class and set the package
		TagNode callerClass = callerClass(tree);
		TagNode callerInstanceClass = null;
		if (callerClass != null) {
			callerPackage = getPackage(callerClass);
			callerInstanceClass = tree.getNode(root, callerClass.tag()
					+ INSTANCE);
		}
		// Find Callee Class and set the package
		TagNode calleeClass = calleeClass(tree);
		TagNode calleeInstanceClass = null;
		if (calleeClass != null) {
			calleePackage = getPackage(calleeClass);
			calleeInstanceClass = tree.getNode(root, calleeClass.tag()
					+ INSTANCE);
		}
		// Find Caller instance Class if undefined and set the package
		if (callerInstanceClass == null) {
			callerInstanceClass = callerInstanceClass(tree);
			if (callerInstanceClass != null) {
				callerPackage = getPackage(callerInstanceClass);
				callerClass = tree.getNode(root, callerInstanceClass.tag()
						.split(INSTANCE)[0]);
			}
		}
		// Find Callee instance Class if undefined and set the package
		if (calleeInstanceClass == null) {
			calleeInstanceClass = calleeInstanceClass(tree);
			if (calleeInstanceClass != null) {
				calleePackage = getPackage(calleeInstanceClass);
				calleeClass = tree.getNode(root, calleeInstanceClass.tag()
						.split(INSTANCE)[0]);
			}
		}

		// Find or create EventInterface
		TagNode eventInterface = eventInterface(tree);
		// Find or create SubscriptionInterface
		TagNode subscriptionInterface = subscriptionInterface(tree);
		// Update Caller Class
		updateCallerClass(callerClass, eventInterface);
		// Update Callee Class
		updateCalleeClass(calleeClass, subscriptionInterface);

		// Create EventInterface instance
		TagNode eventInterfaceInstance = eventInterfaceInstance(root,
				eventInterface);
		// Update Caller instance Class
		updateCallerInstanceClass(callerInstanceClass, eventInterfaceInstance,
				callerClass);

		// Create SubscriptionInterface instance
		TagNode subscriptionInterfaceInstance = subscriptionInterfaceInstance(
				root, subscriptionInterface, callerInstanceClass);
		// Update Callee instance Class
		updateCalleeInstanceClass(calleeInstanceClass,
				subscriptionInterfaceInstance, calleeClass);

		// Add subscriptionInvocationMethods
		addSubscriptionInvocationMethods(callerInstanceClass,
				calleeInstanceClass);
		// Add eventInvocationMethods
		addEventInvocationMethods(calleeInstanceClass, callerInstanceClass);

		 System.out.println(tree);
	}

	// Find EventInterface
	private TagNode eventInterface(TagTree tree) {
		TagNode eventInterface = tree.getNodeByStereotype(TagNode
				.inStereo(EVENT_INTERFACE));
		if (eventInterface != null)
			return eventInterface;
		return eventInterface(tree.root());
	}

	// Find SubscriptionInterface
	private TagNode subscriptionInterface(TagTree tree) {
		TagNode subscriptionInterface = tree.getNodeByStereotype(TagNode
				.inStereo(SUBSCRIPTION_INTERFACE));
		if (subscriptionInterface != null)
			return subscriptionInterface;
		return subscriptionInterface(tree.root());
	}

	// Find Caller Class
	private TagNode callerClass(TagTree tree) {
		return tree.getNodeByStereotype(TagNode.inStereo(CALLER));
	}

	// Find Callee Class
	private TagNode calleeClass(TagTree tree) {
		return tree.getNodeByStereotype(TagNode.inStereo(CALLEE));
	}

	// Find Caller instance Class
	private TagNode callerInstanceClass(TagTree tree) {
		return tree.getNodeByStereotype(TagNode.inStereo(CALLER_INSTANCE));
	}

	// Find Callee instance Class
	private TagNode calleeInstanceClass(TagTree tree) {
		return tree.getNodeByStereotype(TagNode.inStereo(CALLEE_INSTANCE));
	}

	// Helper method returning the package a TagNode was defined for
	private String getPackage(TagNode node) {
		if (node.sourceDefined())
			return node.source().get(0).packageName();
		return "";
	}

	/**
	 * Constructor for the Callback primitive. Initializes a <TagTree> object
	 * and a <code>IGenModel</code> object with all settings for the current
	 * Java Project.
	 * 
	 * @param umlPackage
	 *            The UML package in the open UML or GMF editor
	 */
	public CallbackPrimitive(org.eclipse.uml2.uml.Package umlPackage,
			MultiStatus status) {
		// Set the status
		this.status = status;
		// Set some configuration variables
		setVariables();
		// Set the UML reader
		umlReader = new UMLAdapter(umlPackage, "CallBack");
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
	 * Constructs a tree defining the structure of the Callback primitive key
	 * elements
	 * 
	 * @return Tree defining the structure of the Callback primitive key
	 *         elements
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
		// Create Caller instance Class
		TagNode callerInstanceClass = callerInstanceClass(root,
				eventInterfaceInstance, callerClass);

		// Create SubscriptionInterface instance
		TagNode subscriptionInterfaceInstance = subscriptionInterfaceInstance(
				root, subscriptionInterface, callerInstanceClass);
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

	// Update Caller class implementing EventInterface
	private void updateCallerClass(TagNode caller, TagNode eventInterface) {
		boolean hasrun = false;
		if (caller != null) {
			for (ICodeElement element : caller.getCodeByStereotype(TagNode
					.inStereo(CALLER))) {
				if (element instanceof JavaClass) {
					JavaClass javaClass = (JavaClass) element;
					ICodeElement code = eventInterface
							.getSourceByTag(EVENT_INTERFACE);
					if (code instanceof JavaClass) {
						javaClass.addInterface((JavaClass) code);
					}
					hasrun = true;
				}
			}
		}
		if (!hasrun) {
			ICodeElement element = caller.source().get(0);
			if (element instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) element;
				ICodeElement code = eventInterface
						.getSourceByTag(EVENT_INTERFACE);
				if (code instanceof JavaClass) {
					javaClass.addInterface((JavaClass) code);
				}
			}
		}
	}

	// Create Callee class implementing SubscriptionInterface
	private TagNode calleeClass(TagNode root, TagNode subscriptionInterface) {
		TagNode callee = new TagNode(CALLEE);
		ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
		ICodeElement element = subscriptionInterface
				.getSourceByTag(SUBSCRIPTION_INTERFACE);
		if (element instanceof JavaClass) {
			interfaces.add((JavaClass) element);
		}
		createClasses(callee, "", calleePackage, null, true, JavaClass.CLASS,
				"Callee", "", null, interfaces,
				"This class implements the Callee of the Callback primitive");
		root.addChild(callee);
		return callee;
	}

	// Update Callee class implementing SubscriptionInterface
	private void updateCalleeClass(TagNode callee, TagNode subscriptionInterface) {
		boolean hasrun = false;
		if (callee != null) {
			for (ICodeElement element : callee.getCodeByStereotype(TagNode
					.inStereo(CALLEE))) {
				if (element instanceof JavaClass) {
					JavaClass javaClass = (JavaClass) element;
					ICodeElement code = subscriptionInterface
							.getSourceByTag(SUBSCRIPTION_INTERFACE);
					if (code instanceof JavaClass) {
						javaClass.addInterface((JavaClass) code);
					}
					hasrun = true;
				}
			}
		}
		if (!hasrun) {
			ICodeElement element = callee.source().get(0);
			if (element instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) element;
				ICodeElement code = subscriptionInterface
						.getSourceByTag(SUBSCRIPTION_INTERFACE);
				if (code instanceof JavaClass) {
					javaClass.addInterface((JavaClass) code);
				}
			}
		}
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
	private TagNode subscriptionInterfaceInstance(TagNode root,
			TagNode superInterface, TagNode callerInstanceClass) {
		TagNode subscriptionInterface = new TagNode(
				SUBSCRIPTION_INTERFACE_INSTANCE);
		root.addChild(subscriptionInterface);
		JavaClass superClass = null;
		ICodeElement element = superInterface
				.getSourceByTag(SUBSCRIPTION_INTERFACE);
		if (element instanceof JavaClass) {
			superClass = (JavaClass) element;
		}
		createClasses(subscriptionInterface, TagNode.inStereo(CALLEE_INSTANCE),
				calleePackage, null, false, JavaClass.INTERFACE,
				"IMySubscription", "I#Subscription", superClass, null,
				"This interface specifies the #name#interface of the Callback primitive");
		// Create interface method declarations
		TagNode subscriptionMessage = new TagNode(SUBSCRIPTION_MESSAGE);
		subscriptionInterface.addChild(subscriptionMessage);
		addSubscriptionMethods(subscriptionMessage, callerInstanceClass,
				"subscribeToEvent", JavaMethod.DECLARATION,
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
				callerClass(eventInterfaceClass, count, superClass, caller,
						eventMethods);
				++count;
			}
		}
		return caller;
	}

	// Update Caller instance class implementing EventInterface instance
	private void updateCallerInstanceClass(TagNode caller,
			TagNode eventInterface, TagNode superClass) {
		if (caller != null && caller.hasChildren()) {
			TagNode eventMethods = new TagNode(EVENT_METHOD);
			caller.addChild(eventMethods);
			int count = 0;
			for (ICodeElement element : eventInterface.source()) {
				if (element instanceof JavaClass) {
					JavaClass eventInterfaceClass = (JavaClass) element;
					if (caller.onlyOptional()) {
						JavaClass javaClass = (JavaClass) caller
								.getSourceByTag(caller.tag());
						if (javaClass != null) {
							removeSuperfluousInterfaces(javaClass);
							javaClass.addInterface(eventInterfaceClass);
							// Create class methods
							addMethods(eventMethods, javaClass,
									eventInterfaceClass,
									JavaMethod.IMPLEMENTATION,
									"This method implements handling an event.");
						}
					} else {
						JavaClass javaClass = getExistingClass(
								eventInterfaceClass, caller.source());
						if (javaClass != null) {
							removeSuperfluousInterfaces(javaClass);
							javaClass.addInterface(eventInterfaceClass);
							// Create class methods
							addMethods(eventMethods, javaClass,
									eventInterfaceClass,
									JavaMethod.IMPLEMENTATION,
									"This method implements handling an event.");
						} else {
							callerClass(eventInterfaceClass, count, superClass,
									caller, eventMethods);
						}
					}
					++count;
				}
			}
		}
	}

	// Creates the Caller class and its methods
	private void callerClass(JavaClass eventInterfaceClass, int count,
			TagNode superClass, TagNode caller, TagNode eventMethods) {
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
		eventInterfaceClass.resetUmlElements();
		// Create class methods
		addMethods(eventMethods, javaClass, eventInterfaceClass,
				JavaMethod.IMPLEMENTATION,
				"This method implements handling an event.");
	}

	// Create Callee instance class implementing SubscriptionInterface instance
	private TagNode calleeInstanceClass(TagNode root,
			TagNode subscriptionInterface, TagNode superClass) {
		TagNode callee = new TagNode(CALLEE_INSTANCE);
		TagNode subscriptionMethods = new TagNode(SUBSCRIPTION_METHOD);
		callee.addChild(subscriptionMethods);
		root.addChild(callee);
		int count = 0;
		for (ICodeElement element : subscriptionInterface.source()) {
			if (element instanceof JavaClass) {
				JavaClass subscriptionInterfaceClass = (JavaClass) element;
				calleeClass(subscriptionInterfaceClass, count, superClass,
						callee, subscriptionMethods);
				++count;
			}
		}
		return callee;
	}

	// Update Callee instance class implementing SubscriptionInterface instance
	private void updateCalleeInstanceClass(TagNode callee,
			TagNode subscriptionInterface, TagNode superClass) {
		if (callee != null && callee.hasChildren()) {
			TagNode subscriptionMethods = new TagNode(SUBSCRIPTION_METHOD);
			callee.addChild(subscriptionMethods);
			int count = 0;
			for (ICodeElement element : subscriptionInterface.source()) {
				if (element instanceof JavaClass) {
					JavaClass subscriptionInterfaceClass = (JavaClass) element;
					if (callee.onlyOptional()) {
						JavaClass javaClass = (JavaClass) callee
								.getSourceByTag(callee.tag());
						if (javaClass != null) {
							javaClass.addInterface(subscriptionInterfaceClass);
							// Create class methods
							addMethods(subscriptionMethods, javaClass,
									subscriptionInterfaceClass,
									JavaMethod.CALLBACK_IMPL,
									"This method implements subscribing a caller to an event.");
						}
					} else {
						JavaClass javaClass = getExistingClass(
								subscriptionInterfaceClass, callee.source());
						if (javaClass != null) {
							javaClass.addInterface(subscriptionInterfaceClass);
							// Create class methods
							addMethods(subscriptionMethods, javaClass,
									subscriptionInterfaceClass,
									JavaMethod.CALLBACK_IMPL,
									"This method implements subscribing a caller to an event.");
						} else {
							calleeClass(subscriptionInterfaceClass, count,
									superClass, callee, subscriptionMethods);
						}
					}
					++count;
				}
			}
		}
	}

	// Creates the Callee class and its methods
	private void calleeClass(JavaClass subscriptionInterfaceClass, int count,
			TagNode superClass, TagNode callee, TagNode subscriptionMethods) {
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
				"This class implements a Callee of the Callback primitive",
				name.equals(""));
		subscriptionInterfaceClass.resetUmlElements();
		// Create class methods
		addMethods(subscriptionMethods, javaClass, subscriptionInterfaceClass,
				JavaMethod.CALLBACK_IMPL,
				"This method implements subscribing a caller to an event.");

	}

	// Create subscription invocation methods
	private void subscriptionInvocationMethods(TagNode caller, TagNode callee) {
		TagNode subscriptionInvocation = new TagNode(SUBSCRIPTION_INVOCATION);
		caller.addChild(subscriptionInvocation);
		for (ICodeElement element : caller.source()) {
			if (element instanceof JavaClass) {
				addMethods(
						subscriptionInvocation,
						TagNode.inStereo(SUBSCRIPTION_MESSAGE),
						(JavaClass) element,
						callee,
						JavaMethod.INVOCATION,
						"This method invokes a method that subscribes the caller to an event at the callee.");
			}
		}
	}

	// Add subscription invocation methods
	private void addSubscriptionInvocationMethods(TagNode caller, TagNode callee) {
		TagNode subscriptionInvocation = new TagNode(SUBSCRIPTION_INVOCATION);
		caller.addChild(subscriptionInvocation);
		for (ICodeElement element : caller.source()) {
			if (element instanceof JavaClass) {
				addMethods(
						subscriptionInvocation,
						TagNode.inStereo(SUBSCRIPTION_MESSAGE),
						(JavaClass) element,
						callee,
						JavaMethod.INVOCATION,
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
				addMethods(eventInvocation, TagNode.inStereo(EVENT_MESSAGE),
						(JavaClass) element, caller, JavaMethod.CALLBACK_INV,
						"This method calls back to all objects that handle an event from the callee.");
			}
		}
	}

	// Add event invocation methods
	private void addEventInvocationMethods(TagNode callee, TagNode caller) {
		TagNode eventInvocation = new TagNode(EVENT_INVOCATION);
		callee.addChild(eventInvocation);
		for (ICodeElement element : callee.source()) {
			if (element instanceof JavaClass) {
				addMethods(eventInvocation, TagNode.inStereo(EVENT_MESSAGE),
						(JavaClass) element, caller, JavaMethod.CALLBACK_INV,
						"This method calls back to all objects that handle an event from the callee.");
			}
		}
	}

	// Creates a list of subscription methods with the given settings for every
	// source element of the nodes parent and adds it to the TagNodes source
	// list
	protected void addSubscriptionMethods(TagNode node, TagNode invoker,
			String defaultName, String type, String comment) {
		if (node.parent() != null) {
			for (ICodeElement element : node.parent().source()) {
				if (element instanceof JavaClass) {
					addSubscriptionMethods(node, invoker, (JavaClass) element,
							defaultName, type, comment);
				}
			}
		}
	}

	// Creates a list of subscription methods with the given settings and adds
	// it to the TagNodes source list
	private void addSubscriptionMethods(TagNode node, TagNode invoker,
			JavaClass javaClass, String defaultName, String type, String comment) {
		ArrayList<NamedElement> messages = new ArrayList<NamedElement>();
		for (NamedElement umlElement : javaClass.umlElements()) {
			messages.addAll(umlReader
					.getReceived(umlElement, node.stereotype()));
		}
		ArrayList<NamedElement> usedMessages = new ArrayList<NamedElement>();
		ArrayList<JavaClass> args = new ArrayList<JavaClass>();
		for (NamedElement message : messages) {
			for (ICodeElement sourceElement : invoker.source()) {
				if (sourceElement instanceof JavaClass) {
					JavaClass invokerClass = (JavaClass) sourceElement;
					if (invokerClass.interfacesDefined()) {
						ArrayList<NamedElement> sentMessages = new ArrayList<NamedElement>();
						for (NamedElement umlElement : invokerClass
								.umlElements()) {
							sentMessages.addAll(umlReader.getSent(umlElement,
									node.stereotype()));
						}
						if (sentMessages.contains(message)) {
							JavaClass interfaceClass = ((JavaClass) sourceElement)
									.interfaces().get(0);
							args.add(interfaceClass);
							usedMessages.add(message);
						}
					}
				}
			}
		}
		// Add the methods which have a valid argument list
		if (invoker.sourceDefined()
				&& invoker.source().get(0) instanceof JavaClass) {
			JavaClass defaultClass = (JavaClass) invoker.source().get(0);
			if (defaultClass.interfacesDefined()) {
				JavaClass interfaceClass = defaultClass.interfaces().get(0);
				addMethods(node, javaClass, usedMessages, args, defaultName,
						interfaceClass, type, comment);
			}
		}
	}

	// Searches the code for a class implementing the same UML element as the
	// given class
	private JavaClass getExistingClass(JavaClass javaClass,
			ArrayList<ICodeElement> code) {
		for (ICodeElement classElement : code) {
			if (classElement instanceof JavaClass
					&& classElement.umlElements().containsAll(
							javaClass.umlElements())) {
				JavaClass foundClass = (JavaClass) classElement;
				return foundClass;
			}
		}
		return null;
	}

	// Removes optional interfaces from the TagTree and from a JavaClass
	private void removeSuperfluousInterfaces(JavaClass javaClass) {
		ArrayList<JavaClass> toRemove = new ArrayList<JavaClass>();
		for (JavaClass interfaceClass : javaClass.interfaces()) {
			// Remove interface from restricted interfaces
			tree.restrictedInterfaces().remove(interfaceClass);
			// Remove method declarations from restricted methods
			ArrayList<ICodeElement> methods2Remove = new ArrayList<ICodeElement>();
			for (JavaMethod method : tree.restrictedMethods()) {
				for (ICodeElement child : interfaceClass.children()) {
					if (method.isInstanceof(child)) {
						methods2Remove.add(method);
					}
				}
			}
			tree.restrictedMethods().removeAll(methods2Remove);
			// Remove the interface node
			tree.dropNode(tree.getNode(tree.root(), interfaceClass
					.archiMateTag()));
			// Remove the methods
			for (ICodeElement declaration : interfaceClass.children()) {
				methods2Remove = new ArrayList<ICodeElement>();
				for (ICodeElement implementation : javaClass.children()) {
					if (implementation instanceof JavaMethod
							&& declaration.isInstanceof(implementation)) {
						if (((JavaMethod) implementation).type().equals(
								JavaMethod.IMPLEMENTATION)) {
							// Remove the method implementation
							methods2Remove.add(implementation);
							// Remove the method invocation
							tree.dropNode(implementation.archiMateTag().split(
									METHOD)[0]
									+ INVOCATION);
						}
						TagNode node = tree.getNode(tree.root(), javaClass
								.archiMateTag());
						ArrayList<TagNode> nodes2Drop = new ArrayList<TagNode>();
						for (TagNode child : node.children()) {
							if (child.source().remove(implementation)
									&& !child.sourceDefined()) {
								// Remove the method node
								nodes2Drop.add(child);
							}
						}
						node.children().removeAll(nodes2Drop);
					}
				}
				javaClass.children().removeAll(methods2Remove);
			}
			// Remove the interface class
			toRemove.add(interfaceClass);
		}
		javaClass.interfaces().removeAll(toRemove);
	}
}
