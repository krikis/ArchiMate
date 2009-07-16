package archimate.codegen;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class JavaHelper {

	public JavaHelper() {

	}

	public String getArchiMateTag(BodyDeclaration node) {
		List<TagElement> tags = node.getJavadoc().tags();
		for (Iterator<TagElement> iter = tags.iterator(); iter.hasNext();) {
			TagElement tag = iter.next();
			if (tag.getTagName() != null
					&& tag.getTagName().equals("@archiMateTag")) {
				List<TextElement> fragments = tag.fragments();
				String archiMateTag = "";
				for (Iterator<TextElement> ite2 = fragments.iterator(); ite2.hasNext();) {
					TextElement name = ite2.next();
					archiMateTag += name.getText().substring(1);
				}
				return archiMateTag;
			}
		}
		return null;
	}
		
	private void setModifier(AST ast, BodyDeclaration classType, int modifier){
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
