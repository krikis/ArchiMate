package archimate.codegen;

import java.util.ArrayList;

public class JavaState {

	private ArrayList<String> archiMateTags = new ArrayList<String>();

	public JavaState() {

	}

	public ArrayList<String> archiMateTags() {
		return archiMateTags;
	}

	public void addTag(String tag) {
		if (tag != null) {
			archiMateTags.add(tag);
		}
	}

}
