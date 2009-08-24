package archimate.patterns.mvc;

import java.util.ArrayList;

import archimate.codegen.*;
import archimate.patterns.Pattern;
import archimate.uml.UMLAdapter;
import archimate.util.*;

public class MVCPattern extends Pattern implements ICodeGenerator {

	// Constants for the key source elements of the MVC pattern
	public static final String DATA_INTERFACE = "MVC_DataInterface";
	public static final String DATA_MESSAGE = "MVC_DataMessage";
	public static final String MODEL_DATA = "MVC_ModelDataPort";
	public static final String DATA_METHOD = "MVC_DataMethod";
	public static final String CONTROL_DATA = "MVC_ControlDataPort";
	public static final String DATA_INVOCATION = "MVC_DataInvocation";
	public static final String UPDATE_INTERFACE = "MVC_UpdateInterface";
	public static final String UPDATE_MESSAGE = "MVC_UpdateMessage";
	public static final String VIEW_UPDATE = "MVC_ViewUpdatePort";
	public static final String UPDATE_METHOD = "MVC_UpdateMethod";
	public static final String MODEL_UPDATE = "MVC_ModelUpdatePort";
	public static final String UPDATE_INVOCATION = "MVC_UpdateInvocation";
	public static final String COMMAND_INTERFACE = "MVC_CommandInterface";
	public static final String COMMAND_MESSAGE = "MVC_CommandMessage";
	public static final String VIEW_COMMAND = "MVC_ViewCommandPort";
	public static final String COMMAND_METHOD = "MVC_CommandMethod";
	public static final String CONTROL_COMMAND = "MVC_ControlCommandPort";
	public static final String COMMAND_INVOCATION = "MVC_CommandInvocation";
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
		// Adds interface and classes for updating the data
		constructDataCommunication(root);
		// Adds interface and classes for updating the view
		constructUpdateCommunication(root);
		// Adds interface and classes for executing commands
		constructCommandCommunication(root);
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
