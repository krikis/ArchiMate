package archimate.codegen;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import archimate.util.FileHandler;

public class ASTEngine {

	private IFile targetFile;
	private ICodeGenerator generator;

	public ASTEngine() {

	}

	public ASTEngine(IFile targetFile, ICodeGenerator generator) {
		this.targetFile = targetFile;
		this.generator = generator;
	}

	public void traverseSource() {
		FileHandler handler = new FileHandler();
		String text = handler.getSource(targetFile);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(text.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		JavaInspector visitor = new JavaInspector(generator);
		unit.accept(visitor);
	}

	public void createSourceFile(Config config) {
		FileHandler handler = new FileHandler();
		JETEngine engine = new JETEngine(config);
		String source = engine.generate();
		IFile file = handler.save(source.getBytes(), config.getTargetFolder(),
				config.getPackage(), config.getTargetFile());
		handler.selectAndReveal(file);
	}
}
