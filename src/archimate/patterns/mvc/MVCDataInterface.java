package archimate.patterns.mvc;

import archimate.codegen.ASTEngine;
import archimate.codegen.Config;
import archimate.codegen.Model;

public class MVCDataInterface {
	
	private ASTEngine engine;
	private MVCModel mvcModel;
	private Config config;
	
	public MVCDataInterface(MVCPattern mvc) {
		engine = new ASTEngine();
		this.mvcModel = mvc.model();
		this.config = configDataInterface(mvc.config());
		engine.createSourceFile(config);
	}
	
	protected Config configDataInterface(Config mvcConfig) {
		Config conf = new Config(mvcConfig);
		conf.setPackageName("model");
		conf.setModelType(Config.INTERFACE);

		Model model = new Model();
		model.setAuthor("Samuel Esposito");
		model.setClass(mvcModel.dataInterface());
		model.setPackage(conf.getPackage());
		model.setArchiMateTag(MVCPattern.DATA_INTERFACE);
		model.addMethods(mvcModel.dataMethods());

		conf.setTargetFile(model.className() + ".java");
		conf.setModel(model);
		return conf;
	}

}
