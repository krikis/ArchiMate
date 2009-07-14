package archimate.actions;

import java.util.*;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.domain.*;
import org.eclipse.ui.*;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.editor.UMLEditorPlugin;

import archimate.patterns.mvc.MVCPattern;
import archimate.templates.*;

import org.eclipse.emf.codegen.ecore.genmodel.impl.GenPackageImpl;

import org.eclipse.gmf.runtime.diagram.ui.editparts.*;
import org.eclipse.uml2.diagram.csd.edit.parts.*;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class GenerateCode extends ArchiMateAction {

	/**
	 * The constructor.
	 */
	public GenerateCode() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if (command != UnexecutableCommand.INSTANCE) {
			readProfiles(myPackage);
		}
	}

	public void readProfiles(org.eclipse.uml2.uml.Package myPack) {
		EList<Profile> profiles = myPack.getAppliedProfiles();
		for (int i = 0; i < profiles.size(); ++i) {
			Profile profile = profiles.get(i);
			Pattern pattern;

			// Get editor file path
			IPath root = getEditorFile().getProject().getFullPath();

			// IJavaProject jproject = (IJavaProject)
			// file.getProject().getAdapter(IJavaProject.class);
			// System.out.println(jproject);

			String name = profile.getName();
			if (name.equals("MVC")) {
				pattern = new MVCPattern(myPack, root);
			} else if (name.equals("MVCSeq")) {
				pattern = new MVCSeqPattern();
			} else if (name.equals("Callback")) {
				pattern = new CallbackPrimitive();
			} else if (name.equals("CallbackSeq")) {
				pattern = new CallbackSeqPrimitive(root);
			} else {
				pattern = new Pattern();
			}
			pattern.generate_code();
		}
	}
}