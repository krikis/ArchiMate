package archimate.codegen;

import java.util.ArrayList;

public class JavaState {

	private ArrayList<String> archiMateTags;

	public JavaState() {
		archiMateTags = new ArrayList<String>();
	}

	public ArrayList<String> archiMateTags() {
		return archiMateTags;
	}

	public void addTag(String tag) {
		if (tag != null) {
			archiMateTags.add(tag);
		}
	}

	public void addTags(ArrayList<String> tags) {
		archiMateTags.addAll(tags);
	}

}
