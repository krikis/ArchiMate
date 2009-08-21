package archimate.patterns.mvc;

import org.eclipse.core.runtime.IProgressMonitor;

import archimate.codegen.*;
import archimate.patterns.Pattern;
import archimate.util.*;

public class MVCPattern extends Pattern implements ICodeGenerator {

	// Constants for the key source elements of the MVC pattern
	static final String DATA_INTERFACE = "MVC_DataInterface";
	static final String DATA_MESSAGE = "MVC_DataMessage";
	static final String MODEL_DATA = "MVC_ModelDataPort";
	static final String DATA_METHOD = "MVC_DataMethod";
	static final String CONTROL_DATA = "MVC_ControlDataPort";
	static final String DATA_INVOCATION = "MVC_DataInvocation";
	static final String UPDATE_INTERFACE = "MVC_UpdateInterface";
	static final String UPDATE_MESSAGE = "MVC_UpdateMessage";
	static final String VIEW_UPDATE = "MVC_ViewUpdatePort";
	static final String UPDATE_METHOD = "MVC_UpdateMethod";
	static final String MODEL_UPDATE = "MVC_ModelUpdatePort";
	static final String UPDATE_INVOCATION = "MVC_UpdateInvocation";
	static final String COMMAND_INTERFACE = "MVC_CommandInterface";
	static final String COMMAND_MESSAGE = "MVC_CommandMessage";
	static final String VIEW_COMMAND = "MVC_ViewCommandPort";
	static final String COMMAND_METHOD = "MVC_CommandMethod";
	static final String CONTROL_COMMAND = "MVC_ControlCommandPort";
	static final String COMMAND_INVOCATION = "MVC_CommandInvocation";

	/**
	 * Constructs a tree defining the structure of the MVC pattern key elements
	 * 
	 * @return Tree defining the structure of the MVC pattern key elements
	 */
	private TagTree constructTree() {
		TagTree tree = new TagTree();
		TagNode root = tree.root();
		TagNode dataInterface = new TagNode(DATA_INTERFACE);
		dataInterface.addChild(new TagNode(DATA_MESSAGE));
		root.addChild(dataInterface);
		TagNode modelData = new TagNode(MODEL_DATA);
		modelData.addChild(new TagNode(DATA_METHOD));
		root.addChild(modelData);
		TagNode viewData = new TagNode(CONTROL_DATA);
		viewData.addChild(new TagNode(DATA_INVOCATION));
		root.addChild(viewData);
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE);
		updateInterface.addChild(new TagNode(UPDATE_MESSAGE));
		root.addChild(updateInterface);
		TagNode viewUpdate = new TagNode(VIEW_UPDATE);
		viewUpdate.addChild(new TagNode(UPDATE_METHOD));
		root.addChild(viewUpdate);
		TagNode controlUpdate = new TagNode(MODEL_UPDATE);
		controlUpdate.addChild(new TagNode(UPDATE_INVOCATION));
		root.addChild(controlUpdate);
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE);
		commandInterface.addChild(new TagNode(COMMAND_MESSAGE));
		root.addChild(commandInterface);
		TagNode modelCommand = new TagNode(CONTROL_COMMAND);
		modelCommand.addChild(new TagNode(COMMAND_METHOD));
		root.addChild(modelCommand);
		TagNode controlCommand = new TagNode(VIEW_COMMAND);
		controlCommand.addChild(new TagNode(COMMAND_INVOCATION));
		root.addChild(controlCommand);
		return tree;
	}

	/**
	 * Constructor for the MVC pattern. Initializes a <TagTree> object and a
	 * <code>IGenModel</code> object with all settings for the current Java
	 * Project.
	 * 
	 * @param myPackage
	 *            The UML package in the open UML or GMF editor
	 */
	public MVCPattern(org.eclipse.uml2.uml.Package myPackage) {
		super.name = "MVC Pattern";
		// Setup the tag tree
		super.tree = constructTree();
		// Read out UML model
		super.model = new MVCModel(myPackage);
	}
}
