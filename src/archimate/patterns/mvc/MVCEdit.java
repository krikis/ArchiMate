package archimate.patterns.mvc;

import java.util.ArrayList;
import java.util.Iterator;

import archimate.codegen.EditAction;
import archimate.codegen.JavaEdit;
import archimate.codegen.JavaState;

public class MVCEdit extends JavaEdit {

	private JavaState state;
	private ArrayList<EditAction> actions;
	private ArrayList<String> archiMateTags;

	public MVCEdit(JavaState state) {
		this.state = state;
		actions = new ArrayList<EditAction>();
		archiMateTags = new ArrayList<String>();
		archiMateTags.add(MVCPattern.COMMAND_INTERFACE);
		archiMateTags.add(MVCPattern.CONTROL_COMMAND);
		archiMateTags.add(MVCPattern.CONTROL_UPDATE);
		archiMateTags.add(MVCPattern.DATA_INTERFACE);
		archiMateTags.add(MVCPattern.MODEL_COMMAND);
		archiMateTags.add(MVCPattern.MODEL_DATA);
		archiMateTags.add(MVCPattern.UPDATE_INTERFACE);
		archiMateTags.add(MVCPattern.VIEW_DATA);
		archiMateTags.add(MVCPattern.VIEW_UPDATE);
	}

	public ArrayList<EditAction> actions() {
		return actions;
	}

	public void addAction(EditAction action) {
		actions.add(action);
	}

	public void compile() {
		archiMateTags.removeAll(state.archiMateTags());
		for (Iterator<String> iter = archiMateTags.iterator(); iter.hasNext();){
			
		}
	}

}
