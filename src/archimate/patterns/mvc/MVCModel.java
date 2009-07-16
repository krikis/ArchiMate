package archimate.patterns.mvc;

import java.util.ArrayList;
import java.util.Iterator;

import archimate.uml.*;

/**
 * Class modelling a Java MVC Pattern
 * 
 * @author Samuel Esposito
 * 
 */
public class MVCModel {

	private String dataInterface = "";
	private String updateInterface = "";
	private String commandInterface = "";

	private ArrayList<String> dataMethods;
	private ArrayList<String> updateMethods;
	private ArrayList<String> commandMethods;

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
		updateInterface = umlreader.getElementName("UpdateInterface");
		if (updateInterface.equals("")) {
			updateInterface = "Update";
		}
		commandInterface = umlreader.getElementName("CommandInterface");
		if (commandInterface.equals("")) {
			commandInterface = "Command";
		}
		dataMethods = umlreader.getElementNames("DataMessage");
		if (dataMethods.size() == 0) {
			dataMethods.add("getData");
		}
		updateMethods = umlreader.getElementNames("UpdateMessage");
		if (updateMethods.size() == 0) {
			updateMethods.add("update");
		}
		commandMethods = umlreader.getElementNames("CommandMessage");
		if (commandMethods.size() == 0) {
			commandMethods.add("command");
		}
	}

	public String dataInterface() {
		return dataInterface;
	}

	public String updateInterface() {
		return updateInterface;
	}

	public String commandInterface() {
		return commandInterface;
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

}
