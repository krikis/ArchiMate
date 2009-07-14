package archimate.actions;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.window.WindowManager;
import org.eclipse.ocl.uml.*;
import org.eclipse.ocl.uml.OCL.*;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.edit.domain.*;
import org.eclipse.ui.*;
import org.eclipse.uml2.uml.*;
import archimate.Activator;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class Validate extends ArchiMateAction {

	public Validate() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if (command != UnexecutableCommand.INSTANCE) {
			readPack(myPackage);
		}
	}

	public void readPack(org.eclipse.uml2.uml.Package myPack) {
		String result = "";
		result += readProfiles(myPack);
		result += readStereotypes(myPack);
		if (result.equals("")) {
			MessageDialog.openInformation(window.getShell(),
					"Archimate Validation Success",
					"No errors encountered during validation.");
		} else {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, result);
			ErrorDialog dialog = new ErrorDialog(window.getShell(),
					"Archimate Validation Error(s)", "Error(s) encountered during validation!", status, IStatus.ERROR);			
			dialog.open();
		}
	}

	public String readProfiles(org.eclipse.uml2.uml.Package myPack) {
		EList<Profile> profiles = myPack.getAppliedProfiles();
		String output = "";
		for (int i = 0; i < profiles.size(); ++i) {
			Profile profile = profiles.get(i);
			EList<Constraint> rules = profile.getOwnedRules();
			for (int j = 0; j < rules.size(); ++j) {
				Constraint rule = rules.get(j);
				ValueSpecification spec = rule.getSpecification();
				String ocl = spec.stringValue();
				output += checkOCL(myPack, ocl);
			}
		}
		return output;
	}

	public String readStereotypes(org.eclipse.uml2.uml.Package myPack) {
		EList<Element> elements = myPack.allOwnedElements();
		String output = "";
		for (int i = 0; i < elements.size(); ++i) {
			Element element = elements.get(i);
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (int j = 0; j < stereotypes.size(); ++j) {
				Stereotype stereotype = stereotypes.get(j);
				EList<Constraint> rules = stereotype.getOwnedRules();
				for (int k = 0; k < rules.size(); ++k) {
					Constraint rule = rules.get(k);
					ValueSpecification spec = rule.getSpecification();
					String ocl = spec.stringValue();
					output += checkOCL(element, ocl);
				}
			}
		}
		return output;
	}

	public String checkOCL(Element element, String oclExpr) {
		boolean valid = false;

		OCL myOcl = Activator.getOCL();

		Helper oclHelper = Activator.getOCLHelper();

		oclHelper.setInstanceContext(element);

		OCLExpression oclInv = null;
		try {
			oclInv = (OCLExpression) oclHelper.createQuery(oclExpr);
		} catch (Exception e) {
			System.out.println("Invalid OCL!\n");
			e.printStackTrace();
		}

		valid = myOcl.check(element, oclInv);
		oclInv.destroy();

		 if (valid) {
			return ""; //"success for :: " + oclExpr + "\n";
		} else {
			return "The model doesn't meet the constraint \"" + oclExpr + "\"\n";
		}
	}
}