package archimate.patterns.mvc;

import java.util.ArrayList;
import java.util.Iterator;

import archimate.codegen.Config;
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
	private Config config;

	public MVCModel(org.eclipse.uml2.uml.Package myPackage, Config config) {
		umlreader = new UMLAdapter(myPackage);
		this.config = config;
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
	
	private void setInvocation(ArrayList<String> methods, ArrayList<String> invocations) {
		for (Iterator<String> iter = methods.iterator(); iter.hasNext();) {
			invocations.add(iter.next() + "Invocation");
		}
	}

	public String dataInterface() {
		return dataInterface;
	}

	public String modelDataPort() {
		return modelDataPort;
	}

	public String viewDataPort() {
		return viewDataPort;
	}

	public String updateInterface() {
		return updateInterface;
	}

	public String viewUpdatePort() {
		return viewUpdatePort;
	}

	public String controlUpdatePort() {
		return controlUpdatePort;
	}

	public String commandInterface() {
		return commandInterface;
	}

	public String modelCommandPort() {
		return modelCommandPort;
	}

	public String controlCommandPort() {
		return controlCommandPort;
	}

	public ArrayList<String> dataMethods() {
		return dataMethods;
	}

	public ArrayList<String> updateMethods() {
		return updateMethods;
	}

	public ArrayList<String> commandMethods() {
		return commandMethods;
	}
		
	public ArrayList<String> imports(String archiMateTag){
		ArrayList<String> imports = new ArrayList<String>();
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)){
			imports.add(config.getPackageBase() + ".model." + objectClass(archiMateTag));
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)){
			imports.add(config.getPackageBase() + ".update." + objectClass(archiMateTag));
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)){
			imports.add(config.getPackageBase() + ".command." + objectClass(archiMateTag));
		}
		return imports;
	}
	
	public String objectClass(String archiMateTag){
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)){
			return modelDataPort;
		}
		return "";
	}
	
	public String objectName(String archiMateTag){
		return JavaHelper.camelize(objectClass(archiMateTag));
	}
	
	public ArrayList<String> methods(String archiMateTag){
		if (archiMateTag.equals(MVCPattern.DATA_MESSAGE)){
			return dataMethods;
		}
		if (archiMateTag.equals(MVCPattern.DATA_METHOD)){
			return dataMethods;
		}
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)){
			return dataInvocationMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_MESSAGE)){
			return updateMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_METHOD)){
			return updateMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)){
			return updateInvocationMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_MESSAGE)){
			return commandMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_METHOD)){
			return commandMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)){
			return commandInvocationMethods;
		}
		return new ArrayList<String>();
	}
	
	public ArrayList<String> methodInvocations(String archiMateTag){
		if (archiMateTag.equals(MVCPattern.DATA_INVOCATION)){
			return dataMethods;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INVOCATION)){
			return updateMethods;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INVOCATION)){
			return commandMethods;
		}
		return new ArrayList<String>();
	}

}
