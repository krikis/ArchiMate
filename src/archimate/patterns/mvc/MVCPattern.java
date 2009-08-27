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

		// Create Data interface
		TagNode dataInterface = dataInterface(root);
		// Create Update interface
		TagNode updateInterface = updateInterface(root);
		// Create Command interface
		TagNode commandInterface = commandInterface(root);

		// Create Model Class
		TagNode modelClass = modelClass(root, dataInterface);
		// Create View Class
		TagNode viewClass = viewClass(root, updateInterface);
		// Create Controller Class
		TagNode controllerClass = controllerClass(root, commandInterface);

		// Create Data interface
		TagNode dataInterfaceInstance = dataInterfaceInstance(root,
				dataInterface);
		// Create Update interface
		TagNode updateInterfaceInstance = updateInterfaceInstance(root,
				updateInterface);
		// Create Command interface
		TagNode commandInterfaceInstance = commandInterfaceInstance(root,
				commandInterface);

		// Create Model Class
		TagNode modelInstanceClass = modelInstanceClass(root,
				dataInterfaceInstance, modelClass);
		// Create View Class
		TagNode viewInstanceClass = viewInstanceClass(root,
				updateInterfaceInstance, viewClass);
		// Create Controller Class
		TagNode controllerInstanceClass = controllerInstanceClass(root,
				commandInterfaceInstance, controllerClass);

		// Add updateInvocationMethods
		addUpdateInvocationMethods(modelInstanceClass, updateInterface, viewInstanceClass);

		// // Adds interface and classes for updating the data
		// constructDataCommunication(root);
		// // Adds interface and classes for updating the view
		// constructUpdateCommunication(root);
		// // Adds interface and classes for executing commands
		// constructCommandCommunication(root);
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
		return updateInterface;
	}

	// Create CommandInterface
	private TagNode commandInterface(TagNode root) {
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE);
		JavaClass commandInterfaceClass = createClass(commandInterface,
				controlPackage, null, JavaClass.INTERFACE, "ICommand", null,
				"This interface specifies the Command interface of the MVC Pattern");
		commandInterface.addSource(commandInterfaceClass);
		return commandInterface;
	}

	// Create Model class implementing DataInterface
	private TagNode modelClass(TagNode root, TagNode dataInterface) {
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		TagNode model = new TagNode(MODEL);
		ICodeElement element = dataInterface.getSourceByTag(DATA_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass dataInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceImpl(dataInterfaceClass.className(),
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
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		ICodeElement element = updateInterface.getSourceByTag(UPDATE_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass updateInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceImpl(updateInterfaceClass.className(),
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
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		ICodeElement element = commandInterface
				.getSourceByTag(COMMAND_INTERFACE);
		if (element instanceof JavaClass) {
			JavaClass commandInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceImpl(commandInterfaceClass.className(),
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
			dataInterfaceClass.setSuperClass(superInterfaceClass.className());
		}
		dataInterface.addSource(dataInterfaceClass);
		// Create interface method declarations
		TagNode dataMessage = new TagNode(DATA_MESSAGE);
		addMethods(dataMessage, "modifyData", JavaMethod.DECLARATION,
				"This method updates the data in the " + modelName
						+ (modelName.equals("") ? " " : "") + "model.");
		dataInterface.addChild(dataMessage);
		root.addChild(dataInterface);
		return dataInterface;
	}

	// Create UpdateInterface instance
	private TagNode updateInterfaceInstance(TagNode root, TagNode superInterface) {
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE_INSTANCE);
		String viewName = umlReader.getElementName(TagNode
				.inStereo(VIEW_INSTANCE));
		String interfaceName = (viewName.equals("") ? "IMyUpdate" : "I"
				+ viewName + "Update");
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
			updateInterfaceClass.setSuperClass(superInterfaceClass.className());
		}
		updateInterface.addSource(updateInterfaceClass);
		// Create interface method declarations
		TagNode updateMessage = new TagNode(UPDATE_MESSAGE);
		addMethods(updateMessage, "updateView", JavaMethod.DECLARATION,
				"This method updates the interface in the " + viewName
						+ (viewName.equals("") ? " " : "") + "view.");
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
			commandInterfaceClass
					.setSuperClass(superInterfaceClass.className());
		}
		commandInterface.addSource(commandInterfaceClass);
		// Create interface method declarations
		TagNode commandMessage = new TagNode(COMMAND_MESSAGE);
		addMethods(commandMessage, "executeCommand", JavaMethod.DECLARATION,
				"This method executes the commands in the " + controllerName
						+ (controllerName.equals("") ? " " : "")
						+ "controller.");
		commandInterface.addChild(commandMessage);
		root.addChild(commandInterface);
		return commandInterface;
	}

	// Create Model instance class implementing DataInterface instance
	private TagNode modelInstanceClass(TagNode root, TagNode dataInterface,
			TagNode superClass) {
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		TagNode model = new TagNode(MODEL_INSTANCE);
		ICodeElement element = dataInterface
				.getSourceByTag(DATA_INTERFACE_INSTANCE);
		if (element instanceof JavaClass) {
			JavaClass dataInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceImpl(dataInterfaceClass.className(),
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
			modelClass.setSuperClass(superJavaClass.className());
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
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		ICodeElement element = updateInterface
				.getSourceByTag(UPDATE_INTERFACE_INSTANCE);
		if (element instanceof JavaClass) {
			JavaClass updateInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceImpl(updateInterfaceClass.className(),
					updateInterfaceClass.packageName(), updateInterfaceClass
							.optional()));
		}
		JavaClass viewClass = createClass(view, viewPackage, null,
				JavaClass.CLASS, "MyView", interfaces,
				"This class implements a View of the MVC Pattern");
		// Set the superClass
		element = superClass.getSourceByTag(MODEL);
		if (element instanceof JavaClass) {
			JavaClass superJavaClass = (JavaClass) element;
			viewClass.setSuperClass(superJavaClass.className());
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
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		ICodeElement element = commandInterface
				.getSourceByTag(COMMAND_INTERFACE_INSTANCE);
		if (element instanceof JavaClass) {
			JavaClass commandInterfaceClass = (JavaClass) element;
			interfaces.add(new InterfaceImpl(commandInterfaceClass.className(),
					commandInterfaceClass.packageName(), commandInterfaceClass
							.optional()));
		}
		JavaClass controllerClass = createClass(controller, controlPackage,
				null, JavaClass.CLASS, "MyController", interfaces,
				"This class implements a Controller of the MVC Pattern");
		// Set the superClass
		element = superClass.getSourceByTag(MODEL);
		if (element instanceof JavaClass) {
			JavaClass superJavaClass = (JavaClass) element;
			controllerClass.setSuperClass(superJavaClass.className());
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

	// Create invocation methods
	private void updateInvocationMethods(TagNode model, TagNode view,
			TagNode updateInterface) {
		TagNode updateInvocation = new TagNode(UPDATE_INVOCATION);
		TagNode updateMessage = updateInterface.child(UPDATE_METHOD);
		if (updateMessage != null) {
			addMethods(updateInvocation, updateMessage.source(),
					JavaMethod.INVOCATION, viewUpdateClass.className(),
					viewUpdateClass.packageName(),
					"This method invokes updating of the interface in the view.");
		}
		model.addChild(updateInvocation);
	}

	// Adds interface and classes for updating the data
	private void constructDataCommunication(TagNode root) {
		// Create data interface
		TagNode dataInterface = new TagNode(DATA_INTERFACE);
		ArrayList<String> dataStereotypes = new ArrayList<String>();
		dataStereotypes.add("DataInterface");
		JavaClass dataInterfaceClass = createClass(dataInterface,
				dataStereotypes, modelPackage, null, JavaClass.INTERFACE,
				"IData", null,
				"This interface specifies the Data interface of the MVC Pattern");
		dataInterface.addSource(dataInterfaceClass);
		// Create interface method declarations
		TagNode dataMessage = new TagNode(DATA_MESSAGE);
		addMethods(dataMessage, "DataMessage", "updateData",
				JavaMethod.DECLARATION,
				"This method updates the data in the model.");
		dataInterface.addChild(dataMessage);
		root.addChild(dataInterface);
		// Create class implementing data interface
		TagNode modelData = new TagNode(MODEL_DATA);
		ArrayList<String> modelStereotypes = new ArrayList<String>();
		modelStereotypes.add("ModelDataPort");
		modelStereotypes.add("ModelDataInstance");
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		interfaces
				.add(new InterfaceImpl(dataInterfaceClass.className(),
						dataInterfaceClass.packageName(), dataInterfaceClass
								.optional()));
		JavaClass modelDataClass = createClass(modelData, modelStereotypes,
				modelPackage, null, JavaClass.CLASS, "ModelData", interfaces,
				"This class implements the ModelDataPort of the MVC Pattern");
		modelData.addSource(modelDataClass);
		// Create class methods
		TagNode dataMethod = new TagNode(DATA_METHOD);
		addMethods(dataMethod, dataMessage.source(), JavaMethod.IMPLEMENTATION,
				null, null,
				"This method implements updating the data in the model.");
		modelData.addChild(dataMethod);
		root.addChild(modelData);
		// Create class using data interface
		TagNode controlData = new TagNode(CONTROL_DATA);
		ArrayList<String> controlStereotypes = new ArrayList<String>();
		controlStereotypes.add("ControlDataPort");
		controlStereotypes.add("ControlDataInstance");
		ArrayList<String> imports = new ArrayList<String>();
		imports.add(modelDataClass.packageName() + "."
				+ modelDataClass.className());
		JavaClass controlDataClass = createClass(controlData,
				controlStereotypes, controlPackage, imports, JavaClass.CLASS,
				"ControlData", null,
				"This class implements the ControlDataPort of the MVC Pattern");
		controlData.addSource(controlDataClass);
		// Create invocation methods
		TagNode dataInvocation = new TagNode(DATA_INVOCATION);
		addMethods(dataInvocation, dataMessage.source(), JavaMethod.INVOCATION,
				modelDataClass.className(), modelDataClass.packageName(),
				"This method invokes updating the data in the model.");
		controlData.addChild(dataInvocation);
		root.addChild(controlData);
	}

	// Adds interface and classes for updating the view
	private void constructUpdateCommunication(TagNode root) {
		// Create update interface
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE);
		ArrayList<String> updateStereotypes = new ArrayList<String>();
		updateStereotypes.add("UpdateInterface");
		JavaClass updateInterfaceClass = createClass(updateInterface,
				updateStereotypes, viewPackage, null, JavaClass.INTERFACE,
				"IUpdate", null,
				"This interface specifies the Update interface of the MVC Pattern");
		updateInterface.addSource(updateInterfaceClass);
		// Create interface method declarations
		TagNode updateMessage = new TagNode(UPDATE_MESSAGE);
		addMethods(updateMessage, "UpdateMessage", "updateInterface",
				JavaMethod.DECLARATION,
				"This method updates the interface in the view.");
		updateInterface.addChild(updateMessage);
		root.addChild(updateInterface);
		// Create class implementing update interface
		TagNode viewUpdate = new TagNode(VIEW_UPDATE);
		ArrayList<String> viewStereotypes = new ArrayList<String>();
		viewStereotypes.add("ViewUpdatePort");
		viewStereotypes.add("ViewUpdateInstance");
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		interfaces.add(new InterfaceImpl(updateInterfaceClass.className(),
				updateInterfaceClass.packageName(), updateInterfaceClass
						.optional()));
		JavaClass viewUpdateClass = createClass(viewUpdate, viewStereotypes,
				viewPackage, null, JavaClass.CLASS, "ViewUpdate", interfaces,
				"This class implements the ViewUpdatePort of the MVC Pattern");
		viewUpdate.addSource(viewUpdateClass);
		// Create class methods
		TagNode updateMethod = new TagNode(UPDATE_METHOD);
		addMethods(updateMethod, updateMessage.source(),
				JavaMethod.IMPLEMENTATION, null, null,
				"This method implements updating the interface in the view.");
		viewUpdate.addChild(updateMethod);
		root.addChild(viewUpdate);
		// Create class using update interface
		TagNode modelUpdate = new TagNode(MODEL_UPDATE);
		ArrayList<String> modelStereotypes = new ArrayList<String>();
		modelStereotypes.add("ModelUpdatePort");
		modelStereotypes.add("ModelUpdateInstance");
		ArrayList<String> imports = new ArrayList<String>();
		imports.add(viewUpdateClass.packageName() + "."
				+ viewUpdateClass.className());
		JavaClass modelUpdateClass = createClass(modelUpdate, modelStereotypes,
				modelPackage, imports, JavaClass.CLASS, "ModelUpdate", null,
				"This class implements the ModelUpdatePort of the MVC Pattern");
		modelUpdate.addSource(modelUpdateClass);
		// Create invocation methods
		TagNode updateInvocation = new TagNode(UPDATE_INVOCATION);
		addMethods(updateInvocation, updateMessage.source(),
				JavaMethod.INVOCATION, viewUpdateClass.className(),
				viewUpdateClass.packageName(),
				"This method invokes updating of the interface in the view.");
		modelUpdate.addChild(updateInvocation);
		root.addChild(modelUpdate);
	}

	// Adds interface and classes for executing commands
	private void constructCommandCommunication(TagNode root) {
		// Create command interface
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE);
		ArrayList<String> commandStereotypes = new ArrayList<String>();
		commandStereotypes.add("CommandInterface");
		JavaClass commandInterfaceClass = createClass(commandInterface,
				commandStereotypes, controlPackage, null, JavaClass.INTERFACE,
				"ICommand", null,
				"This interface specifies the Command interface of the MVC Pattern");
		commandInterface.addSource(commandInterfaceClass);
		// Create interface method declarations
		TagNode commandMessage = new TagNode(COMMAND_MESSAGE);
		addMethods(commandMessage, "CommandMessage", "executeCommand",
				JavaMethod.DECLARATION,
				"This method executes the commands from the view.");
		commandInterface.addChild(commandMessage);
		root.addChild(commandInterface);
		// Create class implementing command interface
		TagNode controlCommand = new TagNode(CONTROL_COMMAND);
		ArrayList<String> controlStereotypes = new ArrayList<String>();
		controlStereotypes.add("ControlCommandPort");
		controlStereotypes.add("ControlCommandInstance");
		ArrayList<InterfaceImpl> interfaces = new ArrayList<InterfaceImpl>();
		interfaces.add(new InterfaceImpl(commandInterfaceClass.className(),
				commandInterfaceClass.packageName(), commandInterfaceClass
						.optional()));
		JavaClass controlCommandClass = createClass(controlCommand,
				controlStereotypes, controlPackage, null, JavaClass.CLASS,
				"ControlCommand", interfaces,
				"This class implements the ControlCommandPort of the MVC Pattern");
		controlCommand.addSource(controlCommandClass);
		// Create class methods
		TagNode commandMethod = new TagNode(COMMAND_METHOD);
		addMethods(commandMethod, commandMessage.source(),
				JavaMethod.IMPLEMENTATION, null, null,
				"This method implements execution of a command from the view.");
		controlCommand.addChild(commandMethod);
		root.addChild(controlCommand);
		// Create class using command interface
		TagNode viewCommand = new TagNode(VIEW_COMMAND);
		ArrayList<String> viewStereotypes = new ArrayList<String>();
		viewStereotypes.add("ViewCommandPort");
		viewStereotypes.add("ViewCommandInstance");
		ArrayList<String> imports = new ArrayList<String>();
		imports.add(controlCommandClass.packageName() + "."
				+ controlCommandClass.className());
		JavaClass viewCommandClass = createClass(viewCommand, viewStereotypes,
				viewPackage, imports, JavaClass.CLASS, "ViewCommand", null,
				"This class implements the ViewCommandPort of the MVC Pattern");
		viewCommand.addSource(viewCommandClass);
		// Create invocation methods
		TagNode commandInvocation = new TagNode(COMMAND_INVOCATION);
		addMethods(commandInvocation, commandMessage.source(),
				JavaMethod.INVOCATION, controlCommandClass.className(),
				controlCommandClass.packageName(),
				"This method invokes execution of a command from the view.");
		viewCommand.addChild(commandInvocation);
		root.addChild(viewCommand);
	}
}
