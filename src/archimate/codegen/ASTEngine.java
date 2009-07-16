package archimate.codegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.jet.JETException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import archimate.codegen.resource.FileHandler;
import archimate.patterns.mvc.MVCModel;

public class ASTEngine {
	
	private IFile targetFile;
	private JavaState state;
	
	public ASTEngine(IFile targetFile, JavaState state){
		this.targetFile = targetFile;
		this.state = state;
	}	

	public void getArchimateTags() {
		FileHandler handler = new FileHandler();
		String text = handler.getSource(targetFile);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(text.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		JavaInspector visitor = new JavaInspector(state);
		unit.accept(visitor);
	}
}
