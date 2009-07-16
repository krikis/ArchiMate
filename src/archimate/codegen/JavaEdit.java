package archimate.codegen;

import java.util.ArrayList;

public class JavaEdit {

	private JavaState state;
	private ArrayList<EditAction> actions;
	
	public JavaEdit() {}

	public JavaEdit(JavaState state) {
		this.state = state;
		actions = new ArrayList<EditAction>();
	}

	public ArrayList<EditAction> actions() {
		return actions;
	}

	public void addAction(EditAction action) {
		actions.add(action);
	}

	public void compile() {
		
	}

}
