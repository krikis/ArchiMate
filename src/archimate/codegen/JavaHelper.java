package archimate.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class JavaHelper {
	
	public static final String ARCHIMATETAG = "@archiMateTag";

	public JavaHelper() {

	}

	public String getArchiMateTag(BodyDeclaration node) {
		if (node.getJavadoc() != null) {
			List<TagElement> tags = node.getJavadoc().tags();
			for (Iterator<TagElement> iter = tags.iterator(); iter.hasNext();) {
				TagElement tag = iter.next();
				if (tag.getTagName() != null
						&& tag.getTagName().equals(ARCHIMATETAG)) {
					List<TextElement> fragments = tag.fragments();
					String archiMateTag = "";
					for (Iterator<TextElement> ite2 = fragments.iterator(); ite2
							.hasNext();) {
						TextElement name = ite2.next();
						if (name.getText().length() > 0)
							archiMateTag += name.getText().substring(1);
					}
					return archiMateTag;
				}
			}
		}
		return "";
	}
	
	public void addMethods(IGenModel model, TypeDeclaration node,
			String archiMateTag){
		ArrayList<String> methods = model.methods(archiMateTag);
		AST ast = node.getAST(); 
		MethodDeclaration md;
		for (Iterator<String> iter = methods.iterator(); iter.hasNext();) {
			md = ast.newMethodDeclaration();
			md.setConstructor(false);
			setModifier(ast, md, Modifier.PUBLIC);
			md.setName(ast.newSimpleName(iter.next()));
			node.bodyDeclarations().add(md);
			Block methodBlock = ast.newBlock();
			md.setBody(methodBlock);
			Javadoc jc = ast.newJavadoc();
			TagElement tag = ast.newTagElement();
			tag.setTagName(ARCHIMATETAG);
			tag.fragments().add(ast.newSimpleName(archiMateTag));
			jc.tags().add(tag);
			md.setJavadoc(jc);
		}
	}
	
	public void addMethod(TypeDeclaration node, String name, String tag){
		AST ast = node.getAST(); 
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setConstructor(false);
		setModifier(ast, md, Modifier.PUBLIC);
		md.setName(ast.newSimpleName("getData"));
		node.bodyDeclarations().add(md);
		Block methodBlock = ast.newBlock();
		md.setBody(methodBlock);
	}

	private void setModifier(AST ast, BodyDeclaration classType, int modifier) {
		switch (modifier) {
		case Modifier.PUBLIC:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			break;
		case Modifier.PRIVATE:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
			break;
		case Modifier.STATIC:
			classType.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
			break;
		}
	}

}
