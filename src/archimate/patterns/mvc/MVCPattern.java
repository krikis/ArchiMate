package archimate.patterns.mvc;

import java.util.ArrayList;

import org.eclipse.core.runtime.MultiStatus;

import archimate.codegen.ICodeElement;
import archimate.codegen.ICodeGenerator;
import archimate.patterns.Pattern;
import archimate.uml.UMLAdapter;
import archimate.util.JavaClass;
import archimate.util.JavaMethod;
import archimate.util.TagNode;
import archimate.util.TagTree;

/**
 * This class models the MVC design pattern.
 * 
 * @author Samuel Esposito
 */
public class MVCPattern extends Pattern implements ICodeGenerator {

	// Constants for the key source elements of the MVC pattern
	// Model Package
	public static final String DATA_INTERFACE = "MVC_Data" + INTERFACE;
	public static final String DATA_INTERFACE_INSTANCE = "MVC_Data" + INTERFACE
			+ INSTANCE;
	public static final String DATA_MESSAGE = "MVC_Data" + MESSAGE;
	public static final String MODEL = "MVC_Model";
	public static final String MODEL_INSTANCE = "MVC_Model" + INSTANCE;
	public static final String DATA_METHOD = "MVC_Data" + METHOD;
	public static final String UPDATE_INVOCATION = "MVC_Update" + INVOCATION;
	// View Package
	public static final String UPDATE_INTERFACE = "MVC_Update" + INTERFACE;
	public static final String UPDATE_INTERFACE_INSTANCE = "MVC_Update"
			+ INTERFACE + INSTANCE;
	public static final String UPDATE_MESSAGE = "MVC_Update" + MESSAGE;
	public static final String VIEW = "MVC_View";
	public static final String VIEW_INSTANCE = "MVC_View" + INSTANCE;
	public static final String UPDATE_METHOD = "MVC_Update" + METHOD;
	public static final String COMMAND_INVOCATION = "MVC_Command" + INVOCATION;
	// Controller Package
	public static final String COMMAND_INTERFACE = "MVC_Command" + INTERFACE;
	public static final String COMMAND_INTERFACE_INSTANCE = "MVC_Command"
			+ INTERFACE + INSTANCE;
	public static final String COMMAND_MESSAGE = "MVC_Command" + MESSAGE;
	public static final String CONTROLLER = "MVC_Controller";
	public static final String CONTROLLER_INSTANCE = "MVC_Controller"
			+ INSTANCE;
	public static final String COMMAND_METHOD = "MVC_Command" + METHOD;
	public static final String DATA_INVOCATION = "MVC_Data" + INVOCATION;
	// Names of the packages in the pattern
	private String modelPackage;
	private String viewPackage;
	private String controllerPackage;

	/**
	 * Constructor for the MVC pattern. Initializes a <TagTree> object and a
	 * <code>IGenModel</code> object with all settings for the current Java
	 * Project.
	 * 
	 * @param umlPackage
	 *            The UML package in the open UML or GMF editor
	 */
	public MVCPattern(org.eclipse.uml2.uml.Package umlPackage,
			MultiStatus status) {
		// Set the status
		this.status = status;
		// Set some configuration variables
		setVariables();
		// Set the UML reader
		umlReader = new UMLAdapter(umlPackage, "MVC");
		// Set the pattern name
		name = "MVC Pattern";
		// Setup the tag tree
		constructTree();
		addPrimitives(umlPackage);
	}

	// Sets the package names
	private void setVariables() {
		packageBase = "app";
		modelPackage = packageBase + ".model";
		viewPackage = packageBase + ".view";
		controllerPackage = packageBase + ".controller";
	}

	/**
	 * Constructs a tree defining the structure of the MVC pattern key elements
	 * 
	 * @return Tree defining the structure of the MVC pattern key elements
	 */
	private void constructTree() {
		tree = new TagTree();
		TagNode root = tree.root();

		// Create DataInterface
		TagNode dataInterface = dataInterface(root);
		// Create UpdateInterface
		TagNode updateInterface = updateInterface(root);
		// Create CommandInterface
		TagNode commandInterface = commandInterface(root);

		// Create Model Class
		TagNode modelClass = modelClass(root, dataInterface);
		// Create View Class
		TagNode viewClass = viewClass(root, updateInterface);
		// Create Controller Class
		TagNode controllerClass = controllerClass(root, commandInterface);

		// Create DataInterface instance
		TagNode dataInterfaceInstance = dataInterfaceInstance(root,
				dataInterface);
		// Create UpdateInterface instance
		TagNode updateInterfaceInstance = updateInterfaceInstance(root,
				updateInterface);
		// Create CommandInterface instance
		TagNode commandInterfaceInstance = commandInterfaceInstance(root,
				commandInterface);

		// Create Model instance Class
		TagNode modelInstanceClass = modelInstanceClass(root,
				dataInterfaceInstance, modelClass);
		// Create View instance Class
		TagNode viewInstanceClass = viewInstanceClass(root,
				updateInterfaceInstance, viewClass);
		// Create Controller instance Class
		TagNode controllerInstanceClass = controllerInstanceClass(root,
				commandInterfaceInstance, controllerClass);

		// Add updateInvocationMethods
		updateInvocationMethods(modelInstanceClass, viewInstanceClass,
				updateInterfaceInstance);
		// Add commandInvocationMethods
		commandInvocationMethods(viewInstanceClass, controllerInstanceClass,
				commandInterfaceInstance);
		// Add dataInvocationMethods
		dataInvocationMethods(controllerInstanceClass, modelInstanceClass,
				dataInterfaceInstance);
	}

	// Create DataInterface
	private TagNode dataInterface(TagNode root) {
		TagNode dataInterface = new TagNode(DATA_INTERFACE);
		createClasses(dataInterface, "", modelPackage, null, false,
				JavaClass.INTERFACE, "IData", "", null, null,
				"This interface specifies the Data interface of the MVC Pattern");
		root.addChild(dataInterface);
		return dataInterface;
	}

	// Create UpdateInterface
	private TagNode updateInterface(TagNode root) {
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE);
		createClasses(updateInterface, "", viewPackage, null, false,
				JavaClass.INTERFACE, "IUpdate", "", null, null,
				"This interface specifies the Update interface of the MVC Pattern");
		root.addChild(updateInterface);
		return updateInterface;
	}

	// Create CommandInterface
	private TagNode commandInterface(TagNode root) {
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE);
		createClasses(commandInterface, "", controllerPackage, null, false,
				JavaClass.INTERFACE, "ICommand", "", null, null,
				"This interface specifies the Command interface of the MVC Pattern");
		root.addChild(commandInterface);
		return commandInterface;
	}

	// Create Model class implementing DataInterface
	private TagNode modelClass(TagNode root, TagNode dataInterface) {
		ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
		TagNode model = new TagNode(MODEL);
		ICodeElement element = dataInterface.getSourceByTag(DATA_INTERFACE);
		if (element instanceof JavaClass) {
			interfaces.add((JavaClass) element);
		}
		createClasses(model, "", modelPackage, null, true, JavaClass.CLASS,
				"Model", "", null, interfaces,
				"This class implements the Model of the MVC Pattern");
		root.addChild(model);
		return model;
	}

	// Create View class implementing UpdateInterface
	private TagNode viewClass(TagNode root, TagNode updateInterface) {
		TagNode view = new TagNode(VIEW);
		ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
		ICodeElement element = updateInterface.getSourceByTag(UPDATE_INTERFACE);
		if (element instanceof JavaClass) {
			interfaces.add((JavaClass) element);
		}
		createClasses(view, "", viewPackage, null, true, JavaClass.CLASS,
				"View", "", null, interfaces,
				"This class implements the View of the MVC Pattern");
		root.addChild(view);
		return view;
	}

	// Create Controller class implementing CommandInterface
	private TagNode controllerClass(TagNode root, TagNode commandInterface) {
		TagNode controller = new TagNode(CONTROLLER);
		ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
		ICodeElement element = commandInterface
				.getSourceByTag(COMMAND_INTERFACE);
		if (element instanceof JavaClass) {
			interfaces.add((JavaClass) element);
		}
		createClasses(controller, "", controllerPackage, null, true,
				JavaClass.CLASS, "Controller", "", null, interfaces,
				"This class implements the Controller of the MVC Pattern");
		root.addChild(controller);
		return controller;
	}

	// Create DataInterface instance
	private TagNode dataInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode dataInterface = new TagNode(DATA_INTERFACE_INSTANCE);
		root.addChild(dataInterface);
		JavaClass superClass = null;
		ICodeElement element = superInterface.getSourceByTag(DATA_INTERFACE);
		if (element instanceof JavaClass) {
			superClass = (JavaClass) element;
		}
		createClasses(dataInterface, TagNode.inStereo(MODEL_INSTANCE),
				modelPackage, null, false, JavaClass.INTERFACE, "IMyData",
				"I#Data", superClass, null,
				"This interface specifies the #name#interface of the MVC Pattern");
		// Create interface method declarations
		TagNode dataMessage = new TagNode(DATA_MESSAGE);
		dataInterface.addChild(dataMessage);
		addMethods(dataMessage, "modifyData", JavaMethod.DECLARATION,
				"This method updates the data in the model.");
		return dataInterface;
	}

	// Create UpdateInterface instance
	private TagNode updateInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE_INSTANCE);
		root.addChild(updateInterface);
		JavaClass superClass = null;
		ICodeElement element = superInterface.getSourceByTag(UPDATE_INTERFACE);
		if (element instanceof JavaClass) {
			superClass = (JavaClass) element;
		}
		createClasses(updateInterface, TagNode.inStereo(VIEW_INSTANCE),
				viewPackage, null, false, JavaClass.INTERFACE, "IMyUpdate",
				"IUpdate#", superClass, null,
				"This interface specifies the #name#interface of the MVC Pattern");
		// Create interface method declarations
		TagNode updateMessage = new TagNode(UPDATE_MESSAGE);
		updateInterface.addChild(updateMessage);
		addMethods(updateMessage, "updateView", JavaMethod.DECLARATION,
				"This method updates the interface in the view.");
		return updateInterface;
	}

	// Create CommandInterface instance
	private TagNode commandInterfaceInstance(TagNode root,
			TagNode superInterface) {
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE_INSTANCE);
		root.addChild(commandInterface);
		JavaClass superClass = null;
		ICodeElement element = superInterface.getSourceByTag(COMMAND_INTERFACE);
		if (element instanceof JavaClass) {
			superClass = (JavaClass) element;
		}
		createClasses(commandInterface, TagNode.inStereo(CONTROLLER_INSTANCE),
				controllerPackage, null, false, JavaClass.INTERFACE,
				"IMyCommand", "I#Command", superClass, null,
				"This interface specifies the #name#interface of the MVC Pattern");
		// Create interface method declarations
		TagNode commandMessage = new TagNode(COMMAND_MESSAGE);
		commandInterface.addChild(commandMessage);
		addMethods(commandMessage, "executeCommand", JavaMethod.DECLARATION,
				"This method executes the commands in the controller.");
		return commandInterface;
	}

	// Create Model instance class implementing DataInterface instance
	private TagNode modelInstanceClass(TagNode root, TagNode dataInterface,
			TagNode superClass) {
		TagNode model = new TagNode(MODEL_INSTANCE);
		TagNode dataMethods = new TagNode(DATA_METHOD);
		model.addChild(dataMethods);
		root.addChild(model);
		int count = 0;
		for (ICodeElement element : dataInterface.source()) {
			if (element instanceof JavaClass) {
				JavaClass dataInterfaceClass = (JavaClass) element;
				String name = "";
				if (dataInterfaceClass.umlElements().size() > 0) {
					name = dataInterfaceClass.umlElements().get(0).getName();
				}
				String className = (name.equals("") ? "MyModel"
						+ (count == 0 ? "" : count) : name);
				ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
				interfaces.add(dataInterfaceClass);
				JavaClass superClassType = null;
				ICodeElement codeElement = superClass.getSourceByTag(MODEL);
				if (codeElement instanceof JavaClass) {
					superClassType = (JavaClass) codeElement;
				}
				JavaClass javaClass = createClass(model, dataInterfaceClass
						.umlElements(), modelPackage, null, JavaClass.CLASS,
						className, superClassType, interfaces,
						"This class implements a Model of the MVC Pattern",
						name.equals(""));
				// Create class methods
				addMethods(dataMethods, javaClass, dataInterfaceClass,
						JavaMethod.IMPLEMENTATION,
						"This method implements updating the data in the model.");
				++count;
			}
		}
		return model;
	}

	// Create View instance class implementing UpdateInterface instance
	private TagNode viewInstanceClass(TagNode root, TagNode updateInterface,
			TagNode superClass) {
		TagNode view = new TagNode(VIEW_INSTANCE);
		TagNode updateMethod = new TagNode(UPDATE_METHOD);
		view.addChild(updateMethod);
		root.addChild(view);
		int count = 0;
		for (ICodeElement element : updateInterface.source()) {
			if (element instanceof JavaClass) {
				JavaClass updateInterfaceClass = (JavaClass) element;
				String name = "";
				if (updateInterfaceClass.umlElements().size() > 0) {
					name = updateInterfaceClass.umlElements().get(0).getName();
				}
				String className = (name.equals("") ? "MyView"
						+ (count == 0 ? "" : count) : name);
				ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
				interfaces.add(updateInterfaceClass);
				JavaClass superClassType = null;
				ICodeElement codeElement = superClass.getSourceByTag(VIEW);
				if (codeElement instanceof JavaClass) {
					superClassType = (JavaClass) codeElement;
				}
				JavaClass javaClass = createClass(view, updateInterfaceClass
						.umlElements(), viewPackage, null, JavaClass.CLASS,
						className, superClassType, interfaces,
						"This class implements a View of the MVC Pattern", name
								.equals(""));
				// Create class methods
				addMethods(updateMethod, javaClass, updateInterfaceClass,
						JavaMethod.IMPLEMENTATION,
						"This method implements updating the interface in the view.");
				++count;
			}
		}
		return view;
	}

	// Create Controller class implementing CommandInterface instance
	private TagNode controllerInstanceClass(TagNode root,
			TagNode commandInterface, TagNode superClass) {
		TagNode controller = new TagNode(CONTROLLER_INSTANCE);
		TagNode commandMethod = new TagNode(COMMAND_METHOD);
		controller.addChild(commandMethod);
		root.addChild(controller);
		int count = 0;
		for (ICodeElement element : commandInterface.source()) {
			if (element instanceof JavaClass) {
				JavaClass commandInterfaceClass = (JavaClass) element;
				String name = "";
				if (commandInterfaceClass.umlElements().size() > 0) {
					name = commandInterfaceClass.umlElements().get(0).getName();
				}
				String className = (name.equals("") ? "MyController"
						+ (count == 0 ? "" : count) : name);
				ArrayList<JavaClass> interfaces = new ArrayList<JavaClass>();
				interfaces.add(commandInterfaceClass);
				JavaClass superClassType = null;
				ICodeElement codeElement = superClass
						.getSourceByTag(CONTROLLER);
				if (codeElement instanceof JavaClass) {
					superClassType = (JavaClass) codeElement;
				}
				JavaClass javaClass = createClass(
						controller,
						commandInterfaceClass.umlElements(),
						controllerPackage,
						null,
						JavaClass.CLASS,
						className,
						superClassType,
						interfaces,
						"This class implements a Controller of the MVC Pattern",
						name.equals(""));
				// Create class methods
				addMethods(commandMethod, javaClass, commandInterfaceClass,
						JavaMethod.IMPLEMENTATION,
						"This method implements execution of a command from the view.");
				++count;
			}
		}
		return controller;
	}

	// Create update invocation methods
	private void updateInvocationMethods(TagNode model, TagNode view,
			TagNode updateInterface) {
		TagNode updateInvocation = new TagNode(UPDATE_INVOCATION);
		model.addChild(updateInvocation);
		for (ICodeElement element : model.source()) {
			if (element instanceof JavaClass) {
				addMethods(updateInvocation, TagNode.inStereo(UPDATE_MESSAGE),
						(JavaClass) element, view, JavaMethod.INVOCATION,
						"This method invokes a method that updates the interface in the view.");
			}
		}
	}

	// Create command invocation methods
	private void commandInvocationMethods(TagNode view, TagNode controller,
			TagNode commandInterface) {
		TagNode commandInvocation = new TagNode(COMMAND_INVOCATION);
		view.addChild(commandInvocation);
		for (ICodeElement element : view.source()) {
			if (element instanceof JavaClass) {
				addMethods(commandInvocation,
						TagNode.inStereo(COMMAND_MESSAGE), (JavaClass) element,
						controller, JavaMethod.INVOCATION,
						"This method invokes a method that executes a command in the controller.");
			}
		}
	}

	// Create data invocation methods
	private void dataInvocationMethods(TagNode controller, TagNode model,
			TagNode dataInterface) {
		TagNode commandInvocation = new TagNode(DATA_INVOCATION);
		controller.addChild(commandInvocation);
		for (ICodeElement element : controller.source()) {
			if (element instanceof JavaClass) {
				addMethods(commandInvocation, TagNode.inStereo(DATA_MESSAGE),
						(JavaClass) element, model, JavaMethod.INVOCATION,
						"This method invokes a method that modifies the data in the model.");
			}
		}
	}
}
