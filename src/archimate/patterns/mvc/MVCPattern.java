package archimate.patterns.mvc;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import archimate.Activator;
import archimate.codegen.*;
import archimate.templates.Pattern;
import archimate.util.*;

public class MVCPattern extends Pattern {

	// Cosntants for the key elements of the MVC pattern
	static final String DATA_INTERFACE = "MVC_DataInterface";
	static final String DATA_MESSAGE = "MVC_DataMessage";
	static final String MODEL_DATA = "MVC_ModelDataPort";
	static final String DATA_METHOD = "MVC_DataMethod";
	static final String VIEW_DATA = "MVC_ViewDataPort";
	static final String DATA_INVOCATION = "MVC_DataInvocation";
	static final String UPDATE_INTERFACE = "MVC_UpdateInterface";
	static final String UPDATE_MESSAGE = "MVC_UpdateMessage";
	static final String VIEW_UPDATE = "MVC_ViewUpdatePort";
	static final String UPDATE_METHOD = "MVC_UpdateMethod";
	static final String CONTROL_UPDATE = "MVC_ControlUpdatePort";
	static final String UPDATE_INVOCATION = "MVC_UpdateInvocation";
	static final String COMMAND_INTERFACE = "MVC_CommandInterface";
	static final String COMMAND_MESSAGE = "MVC_CommandMessage";
	static final String MODEL_COMMAND = "MVC_ModelCommandPort";
	static final String COMMAND_METHOD = "MVC_CommandMethod";
	static final String CONTROL_COMMAND = "MVC_ControlCommandPort";
	static final String COMMAND_INVOCATION = "MVC_CommandInvocation";
	// Tree defining the structure of the MVC pattern key elements
	private static TagTree tree = constructTree();
	// Data structure containing all settings of the current project
	private Config config;
	// Model containing all data for the MVC framework to create
	private MVCModel mvcModel;

	/**
	 * Constructs a tree defining the structure of the MVC pattern key elements
	 * 
	 * @return tree defining the structure of the MVC pattern key elements
	 */
	private static TagTree constructTree() {
		TagTree tree = new TagTree();
		TagNode root = tree.root();
		TagNode dataInterface = new TagNode(DATA_INTERFACE);
		dataInterface.addChild(new TagNode(DATA_MESSAGE));
		root.addChild(dataInterface);
		TagNode modelData = new TagNode(MODEL_DATA);
		modelData.addChild(new TagNode(DATA_METHOD));
		root.addChild(modelData);
		TagNode viewData = new TagNode(VIEW_DATA);
		viewData.addChild(new TagNode(DATA_INVOCATION));
		root.addChild(viewData);
		TagNode updateInterface = new TagNode(UPDATE_INTERFACE);
		updateInterface.addChild(new TagNode(UPDATE_MESSAGE));
		root.addChild(updateInterface);
		TagNode viewUpdate = new TagNode(VIEW_UPDATE);
		viewUpdate.addChild(new TagNode(UPDATE_METHOD));
		root.addChild(viewUpdate);
		TagNode controlUpdate = new TagNode(CONTROL_UPDATE);
		controlUpdate.addChild(new TagNode(UPDATE_INVOCATION));
		root.addChild(controlUpdate);
		TagNode commandInterface = new TagNode(COMMAND_INTERFACE);
		commandInterface.addChild(new TagNode(COMMAND_MESSAGE));
		root.addChild(commandInterface);
		TagNode modelCommand = new TagNode(MODEL_COMMAND);
		modelCommand.addChild(new TagNode(COMMAND_METHOD));
		root.addChild(modelCommand);
		TagNode controlCommand = new TagNode(CONTROL_COMMAND);
		controlCommand.addChild(new TagNode(COMMAND_INVOCATION));
		root.addChild(controlCommand);
		return tree;
	}

	/**
	 * Constructor for the MVC pattern. Initializes a <code>Config</code> object
	 * with all settings for the current Java Project.
	 * 
	 * @param myPackage
	 *            The UML package in the open UML or GMF editor
	 */
	public MVCPattern(org.eclipse.uml2.uml.Package myPackage) {
		// Configure settings for current Java project
		setupConfig();
		// Read out UML model
		mvcModel = new MVCModel(myPackage);
	}

	private void setupConfig() {
		config = new Config();
		config.setPackageBase("app");
		config.setTargetFolder(Activator.projectRoot + "/src");
		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(Activator.PLUGIN_ID);
		config.setInterfaceTemplateRelativeUri("src/archimate/"
				+ "templates/Interface.javajet");
		config.setClassTemplateRelativeUri("src/archimate/"
				+ "templates/Class.javajet");
	}

	/**
	 * Returns the <code>TagTree</code> object of the MVC pattern.
	 */
	public TagTree tree() {
		return tree;
	}

	/**
	 * Returns the <code>Config</code> object of the MVC pattern.
	 */
	public Config config() {
		return config;
	}

	/**
	 * Returns the <code>MVCModel</code> object containing all data for the MVC
	 * framework to create.
	 */
	public MVCModel model() {
		return mvcModel;
	}

	/**
	 * Generates code for the MVC pattern.
	 */
	@Override
	public void generate_code() {
		// Reset the tree containing MVC pattern key elements
		tree.resetVisited();

		// Traverses the source and calls back when source additions are needed
		SourceInspector inspector = new SourceInspector(this);
		inspector.inspect();

		// Adds the missing source files
		ArrayList<String> tags = tree.getUnvisited(tree.root());
		createSourceFiles(tags);

//		 createSource(configDataInterface(mvcModel));
//		 createSource(configModelData(mvcModel));
//		 createSource(configViewData(mvcModel));
//		 createSource(configUpdateInterface(mvcModel));
//		 createSource(configViewUpdate(mvcModel));
//		 createSource(configControlUpdate(mvcModel));
//		 createSource(configCommandInterface(mvcModel));
//		 createSource(configModelCommand(mvcModel));
//		 createSource(configControlCommand(mvcModel));
//		testAST(mvcModel);
	}

	/**
	 * Creates source files for every tag in the list.
	 */
	@Override
	public void createSourceFiles(ArrayList<String> tags) {
		for (Iterator<String> iter = tags.iterator(); iter.hasNext();) {
			String tag = iter.next();
			if (tag.equals(DATA_INTERFACE)) {
				new MVCDataInterface(this);
			}
		}
	}

	/**
	 * Creates source elements in the node for every tag in the list.
	 */
	@Override
	public void addSourceElements(TypeDeclaration node, ArrayList<String> tags) {
		JavaHelper helper = new JavaHelper();
		for (Iterator<String> iter = tags.iterator(); iter.hasNext();) {
			helper.addMethods(mvcModel, node, iter.next());
		}

	}

	public void testAST(MVCModel mvcmodel) {
		FileHandler handler = new FileHandler();
		Config conf = new Config(config);
		conf.setPackageBase("");
		conf.setPackageName("");
		conf.setTargetFile("Test" + ".java");
		IContainer container = null;
		container = handler.findOrCreateContainer(conf.getTargetFolder(), conf
				.getPackage());
		IFile targetFile = container.getFile(new Path(conf.getTargetFile()));
		InputStream contents = null;
		try {
			contents = targetFile.getContents();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				contents));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				contents.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String text = sb.toString();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(text.toCharArray());
		// parser.setSource("".toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.recordModifications();
		// AST ast = unit.getAST();

		// JavaInspector visitor = new JavaInspector(this);
		// unit.accept(visitor);
		TestAST tester = new TestAST(unit);
		tester.test();

		String sourceCode = "";
		Document doc = new Document(text);
		TextEdit edits = unit.rewrite(doc, null);
		if (edits.hasChildren()) {
			try {
				edits.apply(doc);
			} catch (BadLocationException e) {
				System.out.println("Unable to apply changes to source.");
				e.printStackTrace();
			}
			sourceCode += doc.get();
			handler.save(sourceCode.getBytes(), targetFile);
		}
	}

	protected void createSource(Config config) {
		ASTEngine engine = new ASTEngine();
		engine.createSourceFile(config);
	}

	/**
	 * Generates code for the MVC DataInterface
	 * 
	 * @param mvcmodel
	 * @return
	 */
	protected Config configDataInterface(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("model");
		conf.setModelType(Config.INTERFACE);

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcmodel.dataInterface());
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(DATA_INTERFACE);
		model.addMethods(Method.create(mvcModel.dataMethods(),
				MVCPattern.DATA_MESSAGE));

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configModelData(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("model");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass("ModelDataPort");
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(MODEL_DATA);
		model.addInterface(mvcmodel.dataInterface());
		model.addMethods(Method.create(mvcModel.dataMethods(),
				MVCPattern.DATA_METHOD));

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configViewData(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("view");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass("ViewDataPort");
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(VIEW_DATA);

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configUpdateInterface(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("view");
		conf.setModelType(Config.INTERFACE);

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcmodel.updateInterface());
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(UPDATE_INTERFACE);
		model.addMethods(Method.create(mvcModel.updateMethods(),
				MVCPattern.UPDATE_MESSAGE));

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configViewUpdate(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("view");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass("ViewUpdatePort");
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(VIEW_UPDATE);
		model.addInterface(mvcmodel.updateInterface());
		model.addMethods(Method.create(mvcModel.updateMethods(),
				MVCPattern.UPDATE_METHOD));

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configControlUpdate(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("controller");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass("ControlUpdatePort");
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(CONTROL_UPDATE);

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configCommandInterface(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("model");
		conf.setModelType(Config.INTERFACE);

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcmodel.commandInterface());
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(COMMAND_INTERFACE);
		model.addMethods(Method.create(mvcModel.commandMethods(),
				MVCPattern.COMMAND_MESSAGE));

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configModelCommand(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("model");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass("ModelCommandPort");
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(MODEL_COMMAND);
		model.addInterface(mvcmodel.commandInterface());
		model.addMethods(Method.create(mvcModel.commandMethods(),
				MVCPattern.COMMAND_METHOD));

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

	protected Config configControlCommand(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("controller");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass("ControlCommandPort");
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(CONTROL_COMMAND);

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

}
