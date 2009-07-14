package archimate.patterns.mvc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.codegen.jet.JETException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.*;

import archimate.jet.Config;
import archimate.jet.JETEngine;
import archimate.jet.Model;
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
		createSource(configDataInterface());
		createSource(configModelData());
		createSource(configViewData());
		createSource(configUpdateInterface());
		createSource(configViewUpdate());
		createSource(configControlUpdate());
		createSource(configCommandInterface());
		createSource(configModelCommand());
		createSource(configControlCommand());
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

	protected String getElementName(String stereotypeName) {
		String name = "";
		EList<NamedElement> elements = myPackage.getOwnedMembers();
		for (int index = 0; index < elements.size(); index++) {
			NamedElement element = elements.get(index);
			if (!(element.getName() == null || element.getName().equals(""))) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (int inde2 = 0; inde2 < stereotypes.size(); inde2++) {
					Stereotype stereotype = stereotypes.get(inde2);
					if (stereotype.getName().equals(stereotypeName)) {
						name = element.getName();
					}
				}
			}
		}
		return name;
	}

	protected Config configDataInterface() {
		this.config = new Config();
		String packageName = "model";
		String className = getElementName("DataInterface");
		if (className.equals("")) {
			className = "Data";
		}
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(DATA_INTERFACE);
		config.setModel(model);
		return config;
	}

	protected Config configModelData() {
		this.config = new Config();
		String packageName = "model";
		String className = "ModelDataPort";
		String interfaceName = getElementName("DataInterface");		
		if (interfaceName.equals("")) {
			interfaceName = "Data";
		}
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
		model.addInterface(interfaceName);
		config.setModel(model);
		return config;
	}

	protected Config configViewData() {
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

	protected Config configUpdateInterface() {
		this.config = new Config();
		String packageName = "view";
		String className = getElementName("UpdateInterface");
		if (className.equals("")) {
			className = "Update";
		}
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(UPDATE_INTERFACE);
		config.setModel(model);
		return config;
	}

	protected Config configViewUpdate() {
		this.config = new Config();
		String packageName = "view";
		String className = "ViewUpdatePort";
		String interfaceName = getElementName("UpdateInterface");		
		if (interfaceName.equals("")) {
			interfaceName = "Update";
		}
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
		model.addInterface(interfaceName);
		config.setModel(model);
		return config;
	}

	protected Config configControlUpdate() {
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

	protected Config configCommandInterface() {
		this.config = new Config();
		String packageName = "model";
		String className = getElementName("CommandInterface");
		if (className.equals("")) {
			className = "Command";
		}
		config.setPackageName(packageBase + "." + packageName);
		config.setTargetFile(className + ".java");
		config.setTargetFolder(root + "/src");

		config.setClasspathVariable("ARCHIMATE");
		config.setPluginId(pluginId);
		config
				.setTemplateRelativeUri("src/archimate/templates/Interface.javajet");

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(className);
		model.setPackage(packageBase + "." + packageName);
		model.setArchiMateTag(COMMAND_INTERFACE);
		config.setModel(model);
		return config;
	}

	protected Config configModelCommand() {
		this.config = new Config();
		String packageName = "model";
		String className = "ModelCommandPort";
		String interfaceName = getElementName("CommandInterface");		
		if (interfaceName.equals("")) {
			interfaceName = "Command";
		}
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
		model.addInterface(interfaceName);
		config.setModel(model);
		return config;
	}

	protected Config configControlCommand() {
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
