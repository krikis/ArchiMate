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

import archimate.codegen.*;
import archimate.templates.Pattern;

public class MVCPattern extends Pattern {

	final String DATA_INTERFACE = "MVC_DataInterface";
	final String MODEL_DATA = "MVC_ModelDataPort";
	final String VIEW_DATA = "MVC_ViewDataPort";
	final String UPDATE_INTERFACE = "MVC_UpdateInterface";
	final String VIEW_UPDATE = "MVC_ViewUpdatePort";
	final String CONTROL_UPDATE = "MVC_ControlUpdatePort";
	final String COMMAND_INTERFACE = "MVC_CommandInterface";
	final String CONTROL_COMMAND = "MVC_ControlCommandPort";
	final String MODEL_COMMAND = "MVC_ModelCommandPort";
	private IPath root;
	private static String pluginId = "ArchiMate";
	private Config config;
	private JETEngine engine;
	private String packageBase;
	private org.eclipse.uml2.uml.Package myPackage;

	public MVCPattern(org.eclipse.uml2.uml.Package myPackage, IPath root) {
		this.root = root;
		this.packageBase = "app";
		this.myPackage = myPackage;
	}

	public void generate_code() {
		// Read out UML package
		MVCModel mvcModel = new MVCModel(myPackage);
		// Check which code is present
		CodeState state = new CodeState(root);
		state.traverseCode();
		
		createSource(configDataInterface(mvcModel));
		createSource(configModelData(mvcModel));
		createSource(configViewData(mvcModel));
		createSource(configUpdateInterface(mvcModel));
		createSource(configViewUpdate(mvcModel));
		createSource(configControlUpdate(mvcModel));
		createSource(configCommandInterface(mvcModel));
		createSource(configModelCommand(mvcModel));
		createSource(configControlCommand(mvcModel));
//		testAST(mvcModel);
	}

	public void testAST(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "model";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(mvcmodel.dataInterface() + ".java");
		config.setTargetFolder(root + "/src");
		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");
		engine = new JETEngine(config);
		IContainer container = null;
		try {
			container = engine.findOrCreateContainer(new NullProgressMonitor(),
					config.getTargetFolder(), config.getPackageName());
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IFile targetFile = container.getFile(new Path(config.getTargetFile()));
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
//		parser.setSource("".toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.recordModifications();
//		AST ast = unit.getAST();

		JavaEdit editElements = new JavaEdit();
		JavaVisitor visitor = new JavaVisitor(editElements);
		unit.accept(visitor);
//		TestAST tester = new TestAST(unit);
//		tester.test();
		



		String sourceCode = "";
		try {
			Document doc = new Document(text);
//			Document doc = new Document();
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

	protected Config configDataInterface(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "model";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(mvcmodel.dataInterface() + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcmodel.dataInterface());
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(DATA_INTERFACE);
		model.addMethods(mvcmodel.dataMethods());
		config.setModel(model);
		return config;
	}

	protected Config configModelData(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "model";
		String className = "ModelDataPort";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setTemplateRelativeUri("src/archimate/templates/Class.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(MODEL_DATA);
		model.addInterface(mvcmodel.dataInterface());
		model.addMethods(mvcmodel.dataMethods());
		config.setModel(model);
		return config;
	}

	protected Config configViewData(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "view";
		String className = "ViewDataPort";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setTemplateRelativeUri("src/archimate/templates/Class.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(VIEW_DATA);
		config.setModel(model);
		return config;
	}

	protected Config configUpdateInterface(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "view";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(mvcmodel.updateInterface() + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcmodel.updateInterface());
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(UPDATE_INTERFACE);
		model.addMethods(mvcmodel.updateMethods());
		config.setModel(model);
		return config;
	}

	protected Config configViewUpdate(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "view";
		String className = "ViewUpdatePort";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setTemplateRelativeUri("src/archimate/templates/Class.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(VIEW_UPDATE);
		model.addInterface(mvcmodel.updateInterface());
		model.addMethods(mvcmodel.updateMethods());
		config.setModel(model);
		return config;
	}

	protected Config configControlUpdate(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "controller";
		String className = "ControlUpdatePort";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setTemplateRelativeUri("src/archimate/templates/Class.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(CONTROL_UPDATE);
		config.setModel(model);
		return config;
	}

	protected Config configCommandInterface(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "model";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(mvcmodel.commandInterface() + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcmodel.commandInterface());
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(COMMAND_INTERFACE);
		model.addMethods(mvcmodel.commandMethods());
		config.setModel(model);
		return config;
	}

	protected Config configModelCommand(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "model";
		String className = "ModelCommandPort";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setTemplateRelativeUri("src/archimate/templates/Class.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(MODEL_COMMAND);
		model.addInterface(mvcmodel.commandInterface());
		model.addMethods(mvcmodel.commandMethods());
		config.setModel(model);
		return config;
	}

	protected Config configControlCommand(MVCModel mvcmodel) {
		this.config = new Config();
		String packageName = "controller";
		String className = "ControlCommandPort";
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config.setTemplateRelativeUri("src/archimate/templates/Class.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(CONTROL_COMMAND);
		config.setModel(model);
		return config;
	}

}
