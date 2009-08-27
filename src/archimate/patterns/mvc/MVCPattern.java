package archimate.patterns.mvc;

import java.util.ArrayList;

import archimate.codegen.*;
import archimate.patterns.Pattern;
import archimate.uml.UMLAdapter;
import archimate.util.*;

public class MVCPattern extends Pattern implements ICodeGenerator {

	// Constants for the key source elements of the MVC pattern
	// Model Package
	public static final String DATA_INTERFACE = "MVC_DataInterface";
	public static final String DATA_INTERFACE_INSTANCE = "MVC_DataInterfaceInstance";
	public static final String DATA_MESSAGE = "MVC_DataMessage";
	public static final String MODEL = "MVC_Model";
	public static final String MODEL_INSTANCE = "MVC_ModelInstance";
	public static final String DATA_METHOD = "MVC_DataMethod";
	public static final String UPDATE_INVOCATION = "MVC_UpdateInvocation";
	// View Package
	public static final String UPDATE_INTERFACE = "MVC_UpdateInterface";
	public static final String UPDATE_INTERFACE_INSTANCE = "MVC_UpdateInterfaceInstance";
	public static final String UPDATE_MESSAGE = "MVC_UpdateMessage";
	public static final String VIEW = "MVC_View";
	public static final String VIEW_INSTANCE = "MVC_ViewInstance";
	public static final String UPDATE_METHOD = "MVC_UpdateMethod";
	public static final String COMMAND_INVOCATION = "MVC_CommandInvocation";
	// Controller Package
	public static final String COMMAND_INTERFACE = "MVC_CommandInterface";
	public static final String COMMAND_INTERFACE_INSTANCE = "MVC_CommandInterfaceInstance";
	public static final String COMMAND_MESSAGE = "MVC_CommandMessage";
	public static final String CONTROLLER = "MVC_Controller";
	public static final String CONTROLLER_INSTANCE = "MVC_ControllerInstance";
	public static final String COMMAND_METHOD = "MVC_CommandMethod";
	public static final String DATA_INVOCATION = "MVC_DataInvocation";
	// Names of the packages in the pattern
	private String modelPackage;
	private String viewPackage;
	private String controlPackage;

	/**
	 * Constructor for the MVC pattern. Initializes a <TagTree> object and a
	 * <code>IGenModel</code> object with all settings for the current Java
	 * Project.
	 * 
	 * @param umlPackage
	 *            The UML package in the open UML or GMF editor
	 */
	public MVCPattern(org.eclipse.uml2.uml.Package umlPackage) {
		// Set some configuration variables
		setVariables();
		// Set the UML reader
		umlReader = new UMLAdapter(umlPackage);
		// Set the pattern name
		name = "MVC Pattern";
		// Setup the tag tree
		constructTree();
	}

	// Sets the package names
	private void setVariables() {
		packageBase = "app";
		modelPackage = packageBase + ".model";
		viewPackage = packageBase + ".view";
		controlPackage = packageBase + ".controller";
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
		updateInvocationMethods(modelInstanceClass,
				viewInstanceClass, updateInterfaceInstance);
		// Add commandInvocationMethods
		commandInvocationMethods(viewInstanceClass,
				controllerInstanceClass, commandInterfaceInstance);
		// Add dataInvocationMethods
		dataInvocationMethods(controllerInstanceClass,
				modelInstanceClass, dataInterfaceInstance);
	}

	// Create DataInterface
	private TagNode dataInterface(TagNode root) {
		TagNode dataInterface = new TagNode(DATA_INTERFACE);
		JavaClass dataInterfaceClass = createClass(dataInterface, modelPackage,
				null, JavaClass.INTERFACE, "IData", null,
				"This interface specifies the Data interface of the MVC Pattern");
		dataInterface.addSource(dataInterfaceClass);
		root.addChild(dataInterface);
		return dataInterface;
	}

	// Create UpdateInterface
	private TagNode updateInterface(TagNode root) {
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE);
		JavaClass updateInterfaceClass = createClass(updateInterface,
				viewPackage, null, JavaClass.INTERFACE, "IUpdate", null,
				"This interface specifies the Update interface of the MVC Pattern");
		updateInterface.addSource(updateInterfaceClass);
		root.addChild(updateInterface);
		return updateInterface;
	}

	// Create CommandInterface
	private TagNode commandInterface(TagNode root) {
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE);
		JavaClass commandInterfaceClass = createClass(commandInterface,
				controlPackage, null, JavaClass.INTERFACE, "ICommand", null,
				"This interface specifies the Command interface of the MVC Pattern");
		commandInterface.addSource(commandInterfaceClass);
		root.addChild(commandInterface);
		return commandInterface;
	}

	// Create Model class implementing DataInterface
	private TagNode modelClass(TagNode root, TagNode dataInterface) {
		ArrayList<InterfaceType> interfaces = new ArrayList<InterfaceType>();
		TagNode model = new TagNode(MODEL);
		ICodeElement element = dataInterface.getSourceByTag(DATA_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass dataInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceType(dataInterfaceClass.className(),
					dataInterfaceClass.packageName(), dataInterfaceClass
							.optional()));
		}
		JavaClass modelClass = createClass(model, modelPackage, null,
				JavaClass.CLASS, "Model", interfaces,
				"This class implements the Model of the MVC Pattern");
		model.addSource(modelClass);
		root.addChild(model);
		return model;
	}

	// Create View class implementing UpdateInterface
	private TagNode viewClass(TagNode root, TagNode updateInterface) {
		TagNode view = new TagNode(VIEW);
		ArrayList<InterfaceType> interfaces = new ArrayList<InterfaceType>();
		ICodeElement element = updateInterface.getSourceByTag(UPDATE_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass updateInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceType(updateInterfaceClass.className(),
					updateInterfaceClass.packageName(), updateInterfaceClass
							.optional()));
		}
		JavaClass viewClass = createClass(view, viewPackage, null,
				JavaClass.CLASS, "View", interfaces,
				"This class implements the View of the MVC Pattern");
		view.addSource(viewClass);
		root.addChild(view);
		return view;
	}

	// Create Controller class implementing CommandInterface
	private TagNode controllerClass(TagNode root, TagNode commandInterface) {
		TagNode controller = new TagNode(CONTROLLER);
		ArrayList<InterfaceType> interfaces = new ArrayList<InterfaceType>();
		ICodeElement element = commandInterface
				.getSourceByTag(COMMAND_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass commandInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceType(commandInterfaceClass.className(),
					commandInterfaceClass.packageName(), commandInterfaceClass
							.optional()));
		}
		JavaClass controllerClass = createClass(controller, controlPackage,
				null, JavaClass.CLASS, "Controller", interfaces,
				"This class implements the Controller of the MVC Pattern");
		controller.addSource(controllerClass);
		root.addChild(controller);
		return controller;
	}

	// Create DataInterface instance
	private TagNode dataInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode dataInterface = new TagNode(DATA_INTERFACE_INSTANCE);
		String modelName = umlReader.getElementName(TagNode
				.inStereo(MODEL_INSTANCE));
		String interfaceName = (modelName.equals("") ? "IMyData" : "I"
				+ modelName + "Data");
		JavaClass dataInterfaceClass = createClass(dataInterface, modelPackage,
				null, JavaClass.INTERFACE, interfaceName, null,
				"This interface specifies the " + interfaceName
						+ " interface of the MVC Pattern");
		// Set whether the interface is optional
		if (!modelName.equals("")) {
			dataInterfaceClass.setOptional(false);
		}
		// Set the superClass
		ICodeElement element = superInterface.getSourceByTag(DATA_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass superInterfaceClass = (JavaClass) element;
			dataInterfaceClass.setSuperClass(new SuperClassType(
					superInterfaceClass.className(), superInterfaceClass
							.packageName(), superInterfaceClass.optional()));
		}
		dataInterface.addSource(dataInterfaceClass);
		// Create interface method declarations
		TagNode dataMessage = new TagNode(DATA_MESSAGE);
		addMethods(dataMessage, "modifyData", JavaMethod.DECLARATION,
				"This method updates the data in the " + modelName
						+ (modelName.equals("") ? "" : " ") + "model.");
		dataInterface.addChild(dataMessage);
		root.addChild(dataInterface);
		return dataInterface;
	}

	// Create UpdateInterface instance
	private TagNode updateInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE_INSTANCE);
		String viewName = umlReader.getElementName(TagNode
				.inStereo(VIEW_INSTANCE));
		String interfaceName = (viewName.equals("") ? "IMyUpdate" : "IUpdate"
				+ viewName);
		JavaClass updateInterfaceClass = createClass(updateInterface,
				viewPackage, null, JavaClass.INTERFACE, interfaceName, null,
				"This interface specifies the " + interfaceName
						+ " interface of the MVC Pattern");
		// Set whether the interface is optional
		if (!viewName.equals("")) {
			updateInterfaceClass.setOptional(false);
		}
		// Set the superClass
		ICodeElement element = superInterface.getSourceByTag(UPDATE_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass superInterfaceClass = (JavaClass) element;
			updateInterfaceClass.setSuperClass(new SuperClassType(
					superInterfaceClass.className(), superInterfaceClass
							.packageName(), superInterfaceClass.optional()));
		}
		updateInterface.addSource(updateInterfaceClass);
		// Create interface method declarations
		TagNode updateMessage = new TagNode(UPDATE_MESSAGE);
		addMethods(updateMessage, "updateView", JavaMethod.DECLARATION,
				"This method updates the interface in the " + viewName
						+ (viewName.equals("") ? "" : " ") + "view.");
		updateInterface.addChild(updateMessage);
		root.addChild(updateInterface);
		return updateInterface;
	}

	// Create CommandInterface instance
	private TagNode commandInterfaceInstance(TagNode root,
			TagNode superInterface) {
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE_INSTANCE);
		String controllerName = umlReader.getElementName(TagNode
				.inStereo(CONTROLLER_INSTANCE));
		String interfaceName = (controllerName.equals("") ? "IMyCommand" : "I"
				+ controllerName + "Command");
		JavaClass commandInterfaceClass = createClass(commandInterface,
				controlPackage, null, JavaClass.INTERFACE, interfaceName, null,
				"This interface specifies the " + interfaceName
						+ " interface of the MVC Pattern");
		// Set whether the interface is optional
		if (!controllerName.equals("")) {
			commandInterfaceClass.setOptional(false);
		}
		// Set the superClass
		ICodeElement element = superInterface.getSourceByTag(COMMAND_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass superInterfaceClass = (JavaClass) element;
			commandInterfaceClass.setSuperClass(new SuperClassType(
					superInterfaceClass.className(), superInterfaceClass
							.packageName(), superInterfaceClass.optional()));
		}
		commandInterface.addSource(commandInterfaceClass);
		// Create interface method declarations
		TagNode commandMessage = new TagNode(COMMAND_MESSAGE);
		addMethods(commandMessage, "executeCommand", JavaMethod.DECLARATION,
				"This method executes the commands in the " + controllerName
						+ (controllerName.equals("") ? "" : " ")
						+ "controller.");
		commandInterface.addChild(commandMessage);
		root.addChild(commandInterface);
		return commandInterface;
	}

	// Create Model instance class implementing DataInterface instance
	private TagNode modelInstanceClass(TagNode root, TagNode dataInterface,
			TagNode superClass) {
		ArrayList<InterfaceType> interfaces = new ArrayList<InterfaceType>();
		TagNode model = new TagNode(MODEL_INSTANCE);
		ICodeElement element = dataInterface
				.getSourceByTag(DATA_INTERFACE_INSTANCE);
		if (element instanceof JavaClass) {
			JavaClass dataInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceType(dataInterfaceClass.className(),
					dataInterfaceClass.packageName(), dataInterfaceClass
							.optional()));
		}
		JavaClass modelClass = createClass(model, modelPackage, null,
				JavaClass.CLASS, "MyModel", interfaces,
				"This class implements a Model of the MVC Pattern");
		// Set the superClass
		element = superClass.getSourceByTag(MODEL);
		if (element instanceof JavaClass) {
			JavaClass superJavaClass = (JavaClass) element;
			modelClass.setSuperClass(new SuperClassType(superJavaClass
					.className(), superJavaClass.packageName(), superJavaClass
					.optional()));
		}
		model.addSource(modelClass);
		// Create class methods
		TagNode dataMethods = new TagNode(DATA_METHOD);
		TagNode dataMessages = dataInterface.child(DATA_MESSAGE);
		if (dataMessages != null) {
			addMethods(dataMethods, dataMessages.source(),
					JavaMethod.IMPLEMENTATION, null, null,
					"This method implements updating the data in the model.");
		}
		model.addChild(dataMethods);
		root.addChild(model);
		return model;
	}

	// Create View instance class implementing UpdateInterface instance
	private TagNode viewInstanceClass(TagNode root, TagNode updateInterface,
			TagNode superClass) {
		TagNode view = new TagNode(VIEW_INSTANCE);
		ArrayList<InterfaceType> interfaces = new ArrayList<InterfaceType>();
		ICodeElement element = updateInterface
				.getSourceByTag(UPDATE_INTERFACE_INSTANCE);
		if (element instanceof JavaClass) {
			JavaClass updateInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceType(updateInterfaceClass.className(),
					updateInterfaceClass.packageName(), updateInterfaceClass
							.optional()));
		}
		JavaClass viewClass = createClass(view, viewPackage, null,
				JavaClass.CLASS, "MyView", interfaces,
				"This class implements a View of the MVC Pattern");
		// Set the superClass
		element = superClass.getSourceByTag(VIEW);
		if (element instanceof JavaClass) {
			JavaClass superJavaClass = (JavaClass) element;
			viewClass.setSuperClass(new SuperClassType(superJavaClass
					.className(), superJavaClass.packageName(), superJavaClass
					.optional()));
		}
		view.addSource(viewClass);
		TagNode updateMethod = new TagNode(UPDATE_METHOD);
		TagNode updateMessages = updateInterface.child(UPDATE_MESSAGE);
		if (updateMessages != null) {
			addMethods(updateMethod, updateMessages.source(),
					JavaMethod.IMPLEMENTATION, null, null,
					"This method implements updating the interface in the view.");
		}
		view.addChild(updateMethod);
		root.addChild(view);
		return view;
	}

	// Create Controller class implementing CommandInterface instance
	private TagNode controllerInstanceClass(TagNode root,
			TagNode commandInterface, TagNode superClass) {
		TagNode controller = new TagNode(CONTROLLER_INSTANCE);
		ArrayList<InterfaceType> interfaces = new ArrayList<InterfaceType>();
		ICodeElement element = commandInterface
				.getSourceByTag(COMMAND_INTERFACE_INSTANCE);
		if (element instanceof JavaClass) {
			JavaClass commandInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceType(commandInterfaceClass.className(),
					commandInterfaceClass.packageName(), commandInterfaceClass
							.optional()));
		}
		JavaClass controllerClass = createClass(controller, controlPackage,
				null, JavaClass.CLASS, "MyController", interfaces,
				"This class implements a Controller of the MVC Pattern");
		// Set the superClass
		element = superClass.getSourceByTag(CONTROLLER);
		if (element instanceof JavaClass) {
			JavaClass superJavaClass = (JavaClass) element;
			controllerClass.setSuperClass(new SuperClassType(superJavaClass
					.className(), superJavaClass.packageName(), superJavaClass
					.optional()));
		}
		controller.addSource(controllerClass);
		TagNode commandMethod = new TagNode(COMMAND_METHOD);
		TagNode commandMessages = commandInterface.child(COMMAND_MESSAGE);
		if (commandMessages != null) {
			addMethods(commandMethod, commandMessages.source(),
					JavaMethod.IMPLEMENTATION, null, null,
					"This method implements execution of a command from the view.");
		}
		controller.addChild(commandMethod);
		root.addChild(controller);
		return controller;
	}

	// Create update invocation methods
	private void updateInvocationMethods(TagNode model, TagNode view,
			TagNode updateInterface) {
		TagNode updateInvocation = new TagNode(UPDATE_INVOCATION);
		TagNode updateMessage = updateInterface.child(UPDATE_MESSAGE);
		ICodeElement element = view.getSourceByTag(VIEW_INSTANCE);
		if (updateMessage != null && element instanceof JavaClass) {
			JavaClass viewClass = (JavaClass) element;
			ICodeElement elem = model.getSourceByTag(MODEL_INSTANCE);
			if (elem instanceof JavaClass) {
				JavaClass modelClass = (JavaClass) elem;
				modelClass.addImport(viewClass.packageName() + "."
						+ viewClass.className());
			}
			addMethods(updateInvocation, updateMessage.source(),
					JavaMethod.INVOCATION, viewClass.className(), viewClass
							.packageName(),
					"This method invokes a method that updates the interface in the view.");
		}
		model.addChild(updateInvocation);
	}

	// Create command invocation methods
	private void commandInvocationMethods(TagNode view, TagNode controller,
			TagNode commandInterface) {
		TagNode commandInvocation = new TagNode(COMMAND_INVOCATION);
		TagNode commandMessage = commandInterface.child(COMMAND_MESSAGE);
		ICodeElement element = controller.getSourceByTag(CONTROLLER_INSTANCE);
		if (commandMessage != null && element instanceof JavaClass) {
			JavaClass controllerClass = (JavaClass) element;
			ICodeElement elem = view.getSourceByTag(VIEW_INSTANCE);
			if (elem instanceof JavaClass) {
				JavaClass viewClass = (JavaClass) elem;
				viewClass.addImport(controllerClass.packageName() + "."
						+ controllerClass.className());
			}
			addMethods(commandInvocation, commandMessage.source(),
					JavaMethod.INVOCATION, controllerClass.className(),
					controllerClass.packageName(),
					"This method invokes a method that executes a command in the controller.");
		}
		view.addChild(commandInvocation);
	}

	// Create data invocation methods
	private void dataInvocationMethods(TagNode controller, TagNode model,
			TagNode dataInterface) {
		TagNode dataInvocation = new TagNode(DATA_INVOCATION);
		TagNode dataMessage = dataInterface.child(DATA_MESSAGE);
		ICodeElement element = model.getSourceByTag(MODEL_INSTANCE);
		if (dataMessage != null && element instanceof JavaClass) {
			JavaClass modelClass = (JavaClass) element;
			ICodeElement elem = controller.getSourceByTag(CONTROLLER_INSTANCE);
			if (elem instanceof JavaClass) {
				JavaClass controllerClass = (JavaClass) elem;
				controllerClass.addImport(modelClass.packageName() + "."
						+ modelClass.className());
			}
			addMethods(dataInvocation, dataMessage.source(),
					JavaMethod.INVOCATION, modelClass.className(), modelClass
							.packageName(),
					"This method invokes a method that modifies the data in the model.");
		}
		controller.addChild(dataInvocation);
	}
}
