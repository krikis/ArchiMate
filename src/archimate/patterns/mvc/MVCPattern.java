package archimate.patterns.mvc;

import java.io.*;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.jet.JETException;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import archimate.Activator;
import archimate.codegen.*;
import archimate.templates.Pattern;

public class MVCPattern extends Pattern {

	static final String DATA_INTERFACE = "MVC_DataInterface";
	static final String MODEL_DATA = "MVC_ModelDataPort";
	static final String VIEW_DATA = "MVC_ViewDataPort";
	static final String UPDATE_INTERFACE = "MVC_UpdateInterface";
	static final String VIEW_UPDATE = "MVC_ViewUpdatePort";
	static final String CONTROL_UPDATE = "MVC_ControlUpdatePort";
	static final String COMMAND_INTERFACE = "MVC_CommandInterface";
	static final String CONTROL_COMMAND = "MVC_ControlCommandPort";
	static final String MODEL_COMMAND = "MVC_ModelCommandPort";
	private IPath root;
	private static String pluginId = "ArchiMate";
	private Config config;
	private JETEngine engine;
	private String packageBase;
	private org.eclipse.uml2.uml.Package myPackage;

	public MVCPattern(org.eclipse.uml2.uml.Package myPackage) {
		config = new Config();
		config.setPackageBase("app");
		config.setTargetFolder(Activator.projectRoot + "/src");
		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setInterfaceTemplateRelativeUri("src/archimate/"
				+ "templates/Interface.javajet");
		config.setClassTemplateRelativeUri("src/archimate/"
				+ "templates/Class.javajet");
		this.myPackage = myPackage;
	}

	public void generate_code() {
		// Check the state of the source code
		JavaState state = new JavaState();
		SourceInspector inspector = new SourceInspector(config, state);
		inspector.inspect();

		// Create the necessary source modifications
		JavaEdit edit = new JavaEdit(state);
		edit.compile();

		// Read out UML package
		MVCModel mvcModel = new MVCModel(myPackage);

		createSource(configDataInterface(mvcModel));
		createSource(configModelData(mvcModel));
		createSource(configViewData(mvcModel));
		createSource(configUpdateInterface(mvcModel));
		createSource(configViewUpdate(mvcModel));
		createSource(configControlUpdate(mvcModel));
		createSource(configCommandInterface(mvcModel));
		createSource(configModelCommand(mvcModel));
		createSource(configControlCommand(mvcModel));
		// testAST(mvcModel);
	}

	public void testAST(MVCModel mvcmodel) {
		Config conf = new Config(config);
		conf.setPackageName("model");
		conf.setTargetFile(mvcmodel.dataInterface() + ".java");
		conf.setTargetFolder(root + "/src");
		engine = new JETEngine(conf);
		IContainer container = null;
		try {
			container = engine.findOrCreateContainer(new NullProgressMonitor(),
					conf.getTargetFolder(), conf.getPackageName());
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		JavaState state = new JavaState();
		JavaInspector visitor = new JavaInspector(state);
		unit.accept(visitor);
		// TestAST tester = new TestAST(unit);
		// tester.test();

		String sourceCode = "";
		try {
			Document doc = new Document(text);
			// Document doc = new Document();
			TextEdit edits = unit.rewrite(doc, null);
			edits.apply(doc);
			sourceCode += doc.get();
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}

		try {
			engine.save(new NullProgressMonitor(), sourceCode.getBytes());
		} catch (JETException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void createSource(Config config) {
		engine = new JETEngine(config);

		try {
			engine.handleSourceFile();
		} catch (JETException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		model.addMethods(mvcmodel.dataMethods());

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
		model.addMethods(mvcmodel.dataMethods());

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
		model.addMethods(mvcmodel.updateMethods());

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
		model.addMethods(mvcmodel.updateMethods());

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
		model.addMethods(mvcmodel.commandMethods());

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
		model.addMethods(mvcmodel.commandMethods());

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
