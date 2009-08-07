package archimate.patterns.mvc;

import java.util.ArrayList;
import java.util.Iterator;

import archimate.Activator;
import archimate.codegen.IGenModel;
import archimate.codegen.JavaHelper;
import archimate.uml.*;

/**
 * Class modelling a Java MVC Pattern
 * 
 * @author Samuel Esposito
 * 
 */
public class MVCModel implements IGenModel {

	private String dataInterface = "";
	private String modelDataPort = "";
	private String viewDataPort = "";
	private String updateInterface = "";
	private String viewUpdatePort = "";
	private String controlUpdatePort = "";
	private String commandInterface = "";
	private String modelCommandPort = "";
	private String controlCommandPort = "";

	private ArrayList<String> dataMethods;
	private ArrayList<String> dataInvocationMethods = new ArrayList<String>();
	private ArrayList<String> updateMethods;
	private ArrayList<String> updateInvocationMethods = new ArrayList<String>();
	private ArrayList<String> commandMethods;
	private ArrayList<String> commandInvocationMethods = new ArrayList<String>();

	private UMLAdapter umlreader;

	public MVCModel(org.eclipse.uml2.uml.Package myPackage) {
		umlreader = new UMLAdapter(myPackage);
		initialize();
	}

	private void initialize() {
		dataInterface = umlreader.getElementName("DataInterface");
		if (dataInterface.equals("")) {
			dataInterface = "Data";
		}
		modelDataPort = umlreader.getElementName("ModelDataPort");
		if (modelDataPort.equals("")) {
			modelDataPort = "ModelData";
		}
		viewDataPort = umlreader.getElementName("ViewDataPort");
		if (viewDataPort.equals("")) {
			viewDataPort = "ViewData";
		}
		updateInterface = umlreader.getElementName("UpdateInterface");
		if (updateInterface.equals("")) {
			updateInterface = "Update";
		}
		controlUpdatePort = umlreader.getElementName("ControlUpdatePort");
		if (controlUpdatePort.equals("")) {
			controlUpdatePort = "ControlUpdate";
		}
		viewUpdatePort = umlreader.getElementName("ViewUpdatePort");
		if (viewUpdatePort.equals("")) {
			viewUpdatePort = "ViewUpdate";
		}
		commandInterface = umlreader.getElementName("CommandInterface");
		if (commandInterface.equals("")) {
			commandInterface = "Command";
		}
		controlCommandPort = umlreader.getElementName("ControlCommandPort");
		if (controlCommandPort.equals("")) {
			controlCommandPort = "ControlCommand";
		}
		modelCommandPort = umlreader.getElementName("ModelCommandPort");
		if (modelCommandPort.equals("")) {
			modelCommandPort = "ModelCommand";
		}
		dataMethods = umlreader.getElementNames("DataMessage");
		if (dataMethods.size() == 0) {
			dataMethods.add("getData");
		}
		setInvocation(dataMethods, dataInvocationMethods);
		updateMethods = umlreader.getElementNames("UpdateMessage");
		if (updateMethods.size() == 0) {
			updateMethods.add("triggerUpdate");
		}
		setInvocation(updateMethods, updateInvocationMethods);
		commandMethods = umlreader.getElementNames("CommandMessage");
		if (commandMethods.size() == 0) {
			commandMethods.add("executeCommand");
		}
		setInvocation(commandMethods, commandInvocationMethods);
	}

	private void setInvocation(ArrayList<String> methods,
			ArrayList<String> invocations) {
		for (Iterator<String> iter = methods.iterator(); iter.hasNext();) {
			invocations.add(iter.next() + "Invocation");
		}
	}
	
	// returns the type of source element
	public String sourceType(String archiMateTag){
		if (archiMateTag.equals(MVCPattern.DATA_MESSAGE)) {
			return JavaHelper.METHOD_DECLARATION;
		}
		if (archiMateTag.equals(MVCPattern.DATA_METHOD)) {
			return JavaHelper.METHOD_IMPLEMENTATION;
		}
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)) {
			return JavaHelper.METHOD_INVOCATION;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_MESSAGE)) {
			return JavaHelper.METHOD_DECLARATION;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_METHOD)) {
			return JavaHelper.METHOD_IMPLEMENTATION;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)) {
			return JavaHelper.METHOD_INVOCATION;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_MESSAGE)) {
			return JavaHelper.METHOD_DECLARATION;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_METHOD)) {
			return JavaHelper.METHOD_IMPLEMENTATION;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)) {
			return JavaHelper.METHOD_INVOCATION;
		}
		return "";
	}

	// returns the project source folder
	public String targetFolder() {
		return Activator.projectRoot + "/src";
	}

	// returns the source folder package base
	public String packageBase() {
		return "app";
	}

	// returns the file name for a source file
	public String targetFile(String archiMateTag) {
		return className(archiMateTag) + ".java";
	}

	// returns the package name for a source file
	public String packageName(String archiMateTag) {
		String packageBase = packageBase();
		if (archiMateTag.equals(MVCPattern.DATA_INTERFACE)) {
			return packageBase + ".model";
		}
		if (archiMateTag.equals(MVCPattern.MODEL_DATA)) {
			return packageBase + ".model";
		}
		if (archiMateTag.equals(MVCPattern.VIEW_DATA)) {
			return packageBase + ".view";
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INTERFACE)) {
			return packageBase + ".view";
		}
		if (archiMateTag.equals(MVCPattern.VIEW_UPDATE)) {
			return packageBase + ".view";
		}
		if (archiMateTag.equals(MVCPattern.CONTROL_UPDATE)) {
			return packageBase + ".controller";
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INTERFACE)) {
			return packageBase + ".model";
		}
		if (archiMateTag.equals(MVCPattern.MODEL_COMMAND)) {
			return packageBase + ".model";
		}
		if (archiMateTag.equals(MVCPattern.CONTROL_COMMAND)) {
			return packageBase + ".controller";
		}
		return "";
	}

	// returns the imports for a snippet of code
	public ArrayList<String> imports(String archiMateTag) {
		ArrayList<String> imports = new ArrayList<String>();
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)) {
			imports.add(packageName(MVCPattern.MODEL_DATA) + "."
					+ objectClass(archiMateTag));
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)) {
			imports.add(packageName(MVCPattern.VIEW_UPDATE) + "."
					+ objectClass(archiMateTag));
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)) {
			imports.add(packageName(MVCPattern.MODEL_COMMAND) + "."
					+ objectClass(archiMateTag));
		}
		return imports;
	}

	// returns the comments for a class
	public String classComment(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_INTERFACE)) {
			return "This interface specifies the Data interface of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.MODEL_DATA)) {
			return "This class implements the ModelDataPort of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.VIEW_DATA)) {
			return "This class implements the ViewDataPort of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INTERFACE)) {
			return "This interface specifies the Update interface of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.VIEW_UPDATE)) {
			return "This class implements the ViewUpdatePort of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.CONTROL_UPDATE)) {
			return "This class implements the ControlUpdatePort of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INTERFACE)) {
			return "This interface specifies the Command interface of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.MODEL_COMMAND)) {
			return "This class implements the ModelCommandPort of the MVC Pattern";
		}
		if (archiMateTag.equals(MVCPattern.CONTROL_COMMAND)) {
			return "This class implements the ControlCommandPort of the MVC Pattern";
		}
		return "";
	}

	// returns the class name for a source file
	public String className(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_INTERFACE)) {
			return dataInterface;
		}
		if (archiMateTag.equals(MVCPattern.MODEL_DATA)) {
			return modelDataPort;
		}
		if (archiMateTag.equals(MVCPattern.VIEW_DATA)) {
			return viewDataPort;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INTERFACE)) {
			return updateInterface;
		}
		if (archiMateTag.equals(MVCPattern.VIEW_UPDATE)) {
			return viewUpdatePort;
		}
		if (archiMateTag.equals(MVCPattern.CONTROL_UPDATE)) {
			return controlUpdatePort;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INTERFACE)) {
			return commandInterface;
		}
		if (archiMateTag.equals(MVCPattern.MODEL_COMMAND)) {
			return modelCommandPort;
		}
		if (archiMateTag.equals(MVCPattern.CONTROL_COMMAND)) {
			return controlCommandPort;
		}
		return "";
	}

	// returns a list of implemented interfaces
	public ArrayList<String> interfaces(String archiMateTag) {
		ArrayList<String> interfaces = new ArrayList<String>();
		if (archiMateTag.equals(MVCPattern.MODEL_DATA)) {
			interfaces.add(dataInterface);
		}
		if (archiMateTag.equals(MVCPattern.VIEW_UPDATE)) {
			interfaces.add(updateInterface);
		}
		if (archiMateTag.equals(MVCPattern.MODEL_COMMAND)) {
			interfaces.add(commandInterface);
		}
		return interfaces;
	}

	// returns whether a source file contains a class or an interface
	public boolean isInterface(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_INTERFACE)) {
			return true;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INTERFACE)) {
			return true;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INTERFACE)) {
			return true;
		}
		return false;
	}

	// returns the class of an object
	public String objectClass(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)) {
			return modelDataPort;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)) {
			return viewUpdatePort;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)) {
			return modelCommandPort;
		}
		return "";
	}

	// returns the name of an object
	public String objectName(String archiMateTag) {
		return JavaHelper.camelize(objectClass(archiMateTag));
	}

	// returns a list of methods in a source file
	public ArrayList<String> methods(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_MESSAGE)) {
			return dataMethods;
		}
		if (archiMateTag.equals(MVCPattern.DATA_METHOD)) {
			return dataMethods;
		}
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)) {
			return dataInvocationMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_MESSAGE)) {
			return updateMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_METHOD)) {
			return updateMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)) {
			return updateInvocationMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_MESSAGE)) {
			return commandMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_METHOD)) {
			return commandMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)) {
			return commandInvocationMethods;
		}
		return new ArrayList<String>();
	}

	// returns a list of methods invoking another method in a source file
	public ArrayList<String> methodInvocations(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)) {
			return dataMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)) {
			return updateMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)) {
			return commandMethods;
		}
		return new ArrayList<String>();
	}

}
