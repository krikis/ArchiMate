package archimate.patterns.mvc;

import archimate.codegen.ASTEngine;
import archimate.codegen.Config;
import archimate.codegen.Method;
import archimate.codegen.Model;

public class MVCViewDataPort {
	
	private ASTEngine engine;
	private MVCModel mvcModel;
	private Config config;
	
	public MVCViewDataPort(MVCPattern mvc) {
		engine = new ASTEngine(mvc);
		this.mvcModel = mvc.model();
		this.config = configViewDataPort(mvc.config());
		engine.createSourceFile(config);
	}
	
	protected Config configViewDataPort(Config mvcConfig) {
		Config conf = new Config(mvcConfig);
		conf.setPackageName("view");

		Model model = new Model();
		model.setComment("This class implements the ViewDataPort of the MVC Pattern");
		model.setAuthor("Samuel Esposito");
		model.setClass("ViewDataPort");
		model.setPackage(conf.getPackage());
		model.addImport(conf.getPackageBase() + ".model." + mvcModel.modelDataPort());
		model.setArchiMateTag(MVCPattern.VIEW_DATA);

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}
}
