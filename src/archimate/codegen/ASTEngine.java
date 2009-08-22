package archimate.codegen;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import archimate.util.FileHandler;
import archimate.util.TagNode;
import archimate.util.JavaClass;

/**
 * Utility class providing methods for editing Java source files
 * 
 * @author Samuel Esposito
 * 
 */
public class ASTEngine {

	// the mode of the engine
	private String mode;
	// the file to edit
	private IFile targetFile;
	// the SourceInspector leading the editing
	private SourceInspector inspector;

	/**
	 * Creates new {@link ASTEngine} and sets the {@link SourceInspector}
	 * 
	 * @param inspector
	 */
	public ASTEngine(SourceInspector inspector, String mode) {
		this.mode = mode;
		this.inspector = inspector;
	}

	/**
	 * Creates new {@link ASTEngine} and sets the target file and
	 * {@link SourceInspector}
	 * 
	 * @param inspector
	 */
	public ASTEngine(IFile targetFile, SourceInspector inspector, String mode) {
		this.mode = mode;
		this.targetFile = targetFile;
		this.inspector = inspector;
	}

	/**
	 * Parses the source in the file, lets a {@link JavaInspector} visit it and
	 * saves the changes
	 */
	public void traverseSource() {
		FileHandler handler = new FileHandler();
		String text = handler.getSource(targetFile);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(text.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.recordModifications();
		ASTVisitor visitor = null;
		if (mode.equals(SourceInspector.GENERATE)) {
			visitor = new JavaInspector(inspector);
		} else {
			visitor = new JavaValidator(inspector);
		}
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

	/**
	 * Creates a new source file for the given archiMateTag
	 * 
	 * @param node
	 *            The {@link TagNode} to generate code for
	 */
	public void createSourceFile(TagNode node, MultiStatus status) {
		for (Iterator<ICodeElement> iter = node.source().iterator(); iter
				.hasNext();) {
			ICodeElement element = iter.next();
			if (element instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) element;
				FileHandler handler = new FileHandler();
				ASTParser parser = ASTParser.newParser(AST.JLS3);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setSource("".toCharArray());
				CompilationUnit unit = (CompilationUnit) parser.createAST(null);
				unit.recordModifications();
				JavaHelper helper = new JavaHelper();
				helper.addClass(unit, javaClass);
				String sourceCode = "";
				Document doc = new Document("");
				TextEdit edits = unit.rewrite(doc, null);
				if (edits.hasChildren()) {
					try {
						edits.apply(doc);
					} catch (BadLocationException e) {
						System.out
								.println("Unable to apply changes to source.");
						e.printStackTrace();
					}
					sourceCode += doc.get();
					targetFile = handler.save(sourceCode, javaClass
							.packageName(), javaClass.targetFile());
				}
				status.add(new Status(IStatus.INFO, status.getPlugin(), 1,
						"Sourcefile for "
								+ javaClass.className()
								+ (javaClass.isInterface() ? " interface"
										: " class") + " added."
								+ "                             ", null));
				traverseSource();
			}
		}
	}
}
