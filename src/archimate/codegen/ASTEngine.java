package archimate.codegen;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import archimate.util.FileHandler;
import archimate.util.TagTree;

public class ASTEngine {

	private IFile targetFile;
	private SourceInspector inspector;

	public ASTEngine(SourceInspector inspector) {
		this.inspector = inspector;
	}

	public ASTEngine(IFile targetFile, SourceInspector inspector) {
		this.targetFile = targetFile;
		this.inspector = inspector;
	}

	public void traverseSource() {
		FileHandler handler = new FileHandler();
		String text = handler.getSource(targetFile);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(text.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.recordModifications();
		JavaInspector visitor = new JavaInspector(inspector);
		unit.accept(visitor);
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
			handler.save(sourceCode, targetFile);
			handler.selectAndReveal(targetFile);
		}
	}

	public void createSourceFile(IGenModel model, String archiMateTag) {
		FileHandler handler = new FileHandler();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource("".toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.recordModifications();
		JavaHelper helper = new JavaHelper();
		helper.addClass(unit, model, archiMateTag);
		String sourceCode = "";
		Document doc = new Document("");
		TextEdit edits = unit.rewrite(doc, null);
		if (edits.hasChildren()) {
			try {
				edits.apply(doc);
			} catch (BadLocationException e) {
				System.out.println("Unable to apply changes to source.");
				e.printStackTrace();
			}
			sourceCode += doc.get();
			targetFile = handler.save(sourceCode, model
					.targetFolder(), model.packageName(archiMateTag), model
					.targetFile(archiMateTag));
		}
		traverseSource();
	}
}
